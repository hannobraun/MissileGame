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



package net.habraun.missilegame.view



import View._

import java.awt._
import javax.swing._
import scala.collection.mutable._

import edu.umd.cs.piccolo._
import edu.umd.cs.piccolo.nodes._



class View(layer: PLayer, ship: Ship) {
	
	// Background color
	val background = PPath.createRectangle(-(View.backgroundSize / 2), -(View.backgroundSize / 2),
			View.backgroundSize, View.backgroundSize)
	background.setPaint(Color.BLACK)
	layer.addChild(background)

	// Scanner display
	val scannerDisplay = new ScannerDisplay(View.scannerRadius, View.defaultScanRange)
	layer.addChild(scannerDisplay.node)

	// Data structure containing all entity views.
	val entityViews = new HashSet[GameEntityView]

	// Player ship
	val shipView = new ShipView(ship)
	addView(shipView)



	/**
	 * Adds the given view to the scene graph.
	 */

	def addView(view: GameEntityView) {
		entityViews.addEntry(view)

		updateSG {
			scannerDisplay.node.addChild(view.node)
		}
	}



	/**
	 * Updates the entity views.
	 */

	def update(zoom: Double) {
		updateSG {
			scannerDisplay.update(zoom)

			entityViews.foreach((entityView) => {
				if (!entityView.update(10000 / zoom, ship.position)) {
					entityViews.removeEntry(entityView)
					scannerDisplay.node.removeChild(entityView.node)
				}
			})
		}
	}
}



object View {
	val backgroundSize = 600
	val scannerRadius = 230

	val defaultScanRange = 10000

	def updateSG(f: => Unit) {
		SwingUtilities.invokeAndWait(new Runnable {
			def run {
				f
			}
		})
	}
}
