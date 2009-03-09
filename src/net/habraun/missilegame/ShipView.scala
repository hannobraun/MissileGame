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

import edu.umd.cs.piccolo.nodes._
import net.habraun.scd._



class ShipView(ship: Ship) {

	val node = {
		val r = ship.body.shape.asInstanceOf[Circle].radius
		val nodeShape = new Ellipse2D.Double(-r, -r, 2 * r, 2 * r)
		
		val node = new PPath(nodeShape)
		node.setPaint(Color.BLACK)
		node.setStroke(Main.defaultStroke)

		node
	}



	def update {
		node.setTransform(AffineTransform.getTranslateInstance(ship.body.position.x, ship.body.position.y))
	}
}
