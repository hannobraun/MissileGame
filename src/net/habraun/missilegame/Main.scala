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

import edu.umd.cs.piccolo._
import edu.umd.cs.piccolo.nodes._



object Main {

	val timeStep = 1.0 / 50.0

	val screenSizeX = 800
	val screenSizeY = 600

	val backgroundColor = new Color(0, 0, 120)

	val defaultStroke = new BasicStroke(0)

	val cameraScale = 1.0 / 40.0



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
		canvas.getCamera.setViewScale(cameraScale)

		// Configure the background color.
		val background = PPath.createRectangle(-20000, -20000, 40000, 40000)
		background.setPaint(backgroundColor)
		canvas.getLayer.addChild(background)

		// Create the player's ship.
		val ship = new Ship
		val shipView = new ShipView(ship)
		canvas.getLayer.addChild(shipView.node)

		// Make window visible.
		frame.setVisible(true)
		canvas.requestFocusInWindow

		while (true) {
			val timeBefore = System.currentTimeMillis

			shipView.update

			val delta = System.currentTimeMillis - timeBefore
			val missing = (timeStep * 1000).toLong - delta
			if (missing > 0) {
				Thread.sleep(missing)
			}
		}
	}
}
