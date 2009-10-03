/*
	Copyright (c) 2009 Hanno Braun <hanno@habraun.net>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/



package net.habraun.missilegame



import input._
import view._

import java.awt._
import java.awt.geom._
import javax.swing._
import scala.collection.mutable._

import edu.umd.cs.piccolo._
import edu.umd.cs.piccolo.nodes._
import net.habraun.sd._
import net.habraun.sd.math._



object Main {

	val timeStep = 1.0 / 50.0

	val screenSizeX = 800
	val screenSizeY = 600

	val scannerRadius = 230

	val defaultStroke = new BasicStroke(0)



	def main(args: Array[String]) {
		// Configure the main window.
		val frame = new JFrame("Missile Game 0.1")
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		frame.setSize(screenSizeX, screenSizeY)

		// Configure the canvas where the scene graph is painted on.
		val canvas = new PCanvas
		canvas.removeInputEventListener(canvas.getZoomEventHandler)
		canvas.removeInputEventListener(canvas.getPanEventHandler)
		frame.add(canvas)

		// Adjust the camera.
		canvas.getCamera.setViewOffset(screenSizeX / 2, screenSizeY / 2)

		// Set up input handling.
		val mouseHandler = new MouseHandler
		canvas.getCamera.addInputEventListener(mouseHandler.handler)

		// Create a world for the physics simulation and a container for the game entities.
		var world = new World[GameEntity]

		// Create the player's ship.
		val ship = new Ship
		world.add(ship)

		// Initialize the display.
		val view = new View(canvas.getLayer, ship)

		// Make window visible.
		frame.setVisible(true)
		canvas.requestFocusInWindow

		// Missile spawn timers.
		var attackTimer1 = 250
		var attackTimer2 = 0

		// Stuff for managing defensive missiles
		val launchedMissiles = new HashMap[GameEntity, GameEntity]
		var tube = 0
		val tubeData = Array((Vec2D(-200, 0), Vec2D(-100, 0)),
							 (Vec2D(0, -200), Vec2D(0, -100)),
							 (Vec2D(200, 0), Vec2D(100, 0)))
		var cooldownTimer = 0


		var zoom = 1.0

		while (true) {
			val timeBefore = System.currentTimeMillis

			// Adjust zoom according to mouse wheel rotation.
			zoom += -mouseHandler.wheelRotation * 0.1
			zoom = Math.max(0.01, zoom)
			zoom = Math.min(5, zoom)

			// Update game entities.
			for ( entity <- world.bodies ) {
				if ( !entity.update ) {
					view.addView( new Explosion( entity.position, 10, scannerRadius ) )
					world.remove( entity )
				}
			}

			// Step the physics simulation.
			world.step(timeStep)

			// Update the display.
			view.update(zoom)

			// Spawn a missile if a timer has run out.
			attackTimer1 -= 1
			attackTimer2 -= 1
			if (attackTimer1 <= 0) {
				spawnOffensiveMissile(() => Some(ship), Vec2D(10, -10000), Vec2D(100, -100), world, view,
						true)
				attackTimer1 = 500
			}
			if (attackTimer2 <= 0) {
				spawnOffensiveMissile(() => Some(ship), Vec2D(-10, -10000), Vec2D(-100, -100), world, view,
						true)
				attackTimer2 = 500
			}

			// Launch defensive missiles if something comes near the ship.
			if (cooldownTimer > 0) cooldownTimer -= 1
			world.bodies.foreach( ( entity ) => {
				if ((entity.position - ship.position).length < 9000 && entity != ship
						&& !launchedMissiles.contains(entity) && !launchedMissiles.values.contains(entity)
						&& cooldownTimer == 0) {
					val (position, velocity) = tubeData(tube)
					val target = () => if (entity.active) Some(entity) else None
					val missile = spawnDefensiveMissile(target, position, velocity, world, view, false)

					launchedMissiles.put(missile, entity)

					tube += 1
					if (tube > 2) tube = 0

					cooldownTimer = 300
				}
			})

			val delta = System.currentTimeMillis - timeBefore
			val missing = (timeStep * 1000).toLong - delta
			if (missing > 0) {
				Thread.sleep(missing)
			}
			else {
				Console.println("Warning: Last step took " + -missing + "ms longer than allowed.")
			}
		}
	}



	def spawnOffensiveMissile(target: () => Option[GameEntity], position: Vec2D, velocity: Vec2D,
			world: World[GameEntity], view: View, hostile: Boolean): Missile = {
		val missile = new OffensiveMissile(target, hostile)
		val missileView = new MissileView(missile, scannerRadius)

		missile.position = position
		missile.velocity = velocity

		world.add(missile)

		view.addView(missileView)

		missile
	}



	def spawnDefensiveMissile(target: () => Option[GameEntity], position: Vec2D, velocity: Vec2D,
			world: World[GameEntity], view: View, hostile: Boolean): Missile = {
		val missile = new DefensiveMissile(target, hostile)
		val missileView = new MissileView(missile, scannerRadius)

		missile.position = position
		missile.velocity = velocity

		world.add(missile)

		view.addView(missileView)

		missile
	}
}
