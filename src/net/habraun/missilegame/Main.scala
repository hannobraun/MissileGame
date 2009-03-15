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
	val directionMarkingOffset = 3
	val directionOffset = 20

	val scannerBackground = new Color(0, 0, 120)

	val defaultStroke = new BasicStroke(0)



	def main(args: Array[String]) {
		// Configure the main window.
		val frame = new JFrame("Missile Game 0.1")
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		frame.setSize(screenSizeX, screenSizeY)

		// Configure the canvas where the scene graph is painted on.
		val canvas = new PCanvas
		//canvas.removeInputEventListener(canvas.getZoomEventHandler)
		//canvas.removeInputEventListener(canvas.getPanEventHandler)
		frame.add(canvas)

		// Adjust the camera.
		canvas.getCamera.setViewOffset(screenSizeX / 2, screenSizeY / 2)

		// Configure the background color.
		val background = PPath.createRectangle(-300, -300, 600, 600)
		background.setPaint(Color.BLACK)
		canvas.getLayer.addChild(background)

		// Configure scanner display.
		val scannerDisplay = PPath.createEllipse(-scannerRadius, -scannerRadius, scannerRadius * 2,
				scannerRadius * 2)
		scannerDisplay.setPaint(scannerBackground)
		scannerDisplay.setStroke(new BasicStroke(2))
		scannerDisplay.setStrokePaint(Color.WHITE)
		canvas.getLayer.addChild(scannerDisplay)

		var directionMarkings = Nil
		for ( i <- 0 until 24) {
			val displayedAngle = i * 15
			val actualAngle = Math.toRadians(i * 15 - 90)

			val startRadius = scannerRadius - directionMarkingOffset
			val endRadius = scannerRadius + directionMarkingOffset
			val startX = startRadius * Math.cos(actualAngle)
			val startY = startRadius * Math.sin(actualAngle)
			val endX = endRadius * Math.cos(actualAngle)
			val endY = endRadius * Math.sin(actualAngle)
			val lineStart = new Point2D.Double(startX, startY)
			val lineEnd = new Point2D.Double(endX, endY)
			val line = PPath.createPolyline(Array(lineStart, lineEnd))
			line.setStrokePaint(Color.WHITE)
			line.setStroke(new BasicStroke(2))

			val angleText = new PText(displayedAngle.toString)
			angleText.scale(0.8)
			val angleX = Math.cos(actualAngle) * (scannerRadius + directionOffset)
			val angleY = Math.sin(actualAngle) * (scannerRadius + directionOffset)
			val angleXOffset = -(angleText.getWidth * angleText.getScale / 2)
			val angleYOffset = -(angleText.getHeight * angleText.getScale / 2)
			
			angleText.setTextPaint(Color.WHITE)
			angleText.setOffset(angleX + angleXOffset, angleY + angleYOffset)
			
			scannerDisplay.addChild(line)
			scannerDisplay.addChild(angleText)
		}

		// Create a world for the physics simulation.
		val world = new World

		// Create the player's ship.
		val ship = new Ship
		world.add(ship.body)
		val shipView = new ShipView(ship)
		canvas.getLayer.addChild(shipView.node)

		val missiles = new HashMap[Missile, MissileView]
		
		// Make window visible.
		frame.setVisible(true)
		canvas.requestFocusInWindow

		// Missile spawn timers.
		var timer1 = 250
		var timer2 = 0

		while (true) {
			val timeBefore = System.currentTimeMillis

			missiles.foreach((missile) => {
				if (missile._1.update) {
					world.remove(missile._1.body)
					missiles -= missile._1

					SwingUtilities.invokeLater(new Runnable { def run {
						canvas.getLayer.removeChild(missile._2.node)
					}})
				}
			})
			world.step(timeStep)

			SwingUtilities.invokeLater(new Runnable { def run {
				shipView.update
				missiles.foreach((missile) => {
					missile._2.update
				})
			}})

			timer1 -= 1
			timer2 -= 1
			if (timer1 <= 0) {
				val missile = new Missile(ship.body)
				val missileView = new MissileView(missile)

				missile.body.position = Vec2D(10, -10000)
				missile.body.velocity = Vec2D(100, -100)
				timer1 = 500

				world.add(missile.body)
				missiles.put(missile, missileView)

				SwingUtilities.invokeLater(new Runnable { def run {
					canvas.getLayer.addChild(missileView.node)
				}})
			}
			if (timer2 <= 0) {
				val missile = new Missile(ship.body)
				val missileView = new MissileView(missile)

				missile.body.position = Vec2D(-10, -10000)
				missile.body.velocity = Vec2D(-100, -100)
				timer2 = 500

				world.add(missile.body)
				missiles.put(missile, missileView)

				SwingUtilities.invokeLater(new Runnable { def run {
					canvas.getLayer.addChild(missileView.node)
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
