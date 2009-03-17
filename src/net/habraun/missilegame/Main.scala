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
import net.habraun.scd._



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

		// Create a world for the physics simulation.
		val world = new World

		// Create the player's ship.
		val ship = new Ship
		world.add(ship.body)
		
		val missiles = new HashMap[Missile, MissileView]

		// Initialize the display.
		val view = new View(canvas.getLayer, ship)

		// Make window visible.
		frame.setVisible(true)
		canvas.requestFocusInWindow

		// Missile spawn timers.
		var timer1 = 250
		var timer2 = 0

		var zoom = 1.0

		while (true) {
			val timeBefore = System.currentTimeMillis

			zoom += -mouseHandler.wheelRotation * 0.1
			zoom = Math.max(0.01, zoom)
			zoom = Math.min(5, zoom)

			missiles.foreach((missile) => {
				if (missile._1.update) {
					world.remove(missile._1.body)
					missiles -= missile._1

					SwingUtilities.invokeLater(new Runnable { def run {
						view.scannerDisplay.node.removeChild(missile._2.node)
					}})
				}
			})
			world.step(timeStep)

			SwingUtilities.invokeLater(new Runnable { def run {
				missiles.foreach((missile) => {
					missile._2.update(10000 / zoom, ship.body.position)
				})
				view.scannerDisplay.update(zoom)
			}})

			timer1 -= 1
			timer2 -= 1
			if (timer1 <= 0) {
				val missile = new Missile(ship.body)
				val missileView = new MissileView(missile, scannerRadius)

				missile.body.position = Vec2D(10, -10000)
				missile.body.velocity = Vec2D(100, -100)
				timer1 = 500

				world.add(missile.body)
				missiles.put(missile, missileView)

				SwingUtilities.invokeLater(new Runnable { def run {
					view.scannerDisplay.node.addChild(missileView.node)
				}})
			}
			if (timer2 <= 0) {
				val missile = new Missile(ship.body)
				val missileView = new MissileView(missile, scannerRadius)

				missile.body.position = Vec2D(-10, -10000)
				missile.body.velocity = Vec2D(-100, -100)
				timer2 = 500

				world.add(missile.body)
				missiles.put(missile, missileView)

				SwingUtilities.invokeLater(new Runnable { def run {
					view.scannerDisplay.node.addChild(missileView.node)
				}})
			}
			

			val delta = System.currentTimeMillis - timeBefore
			val missing = (timeStep * 1000).toLong - delta
			if (missing > 0) {
				Thread.sleep(missing)
			}
		}
	}
}
