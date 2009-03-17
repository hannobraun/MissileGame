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



import java.awt._
import javax.swing._

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

	// Player ship
	val shipView = new ShipView(ship)
	scannerDisplay.node.addChild(shipView.node)
}



object View {
	val backgroundSize = 600
	val scannerRadius = 230

	val defaultScanRange = 10000
}
