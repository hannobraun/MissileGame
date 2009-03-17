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
		val point1 = new Point2D.Double(-0.5, 0.289)
		val point2 = new Point2D.Double(0.5, 0.289)
		val point3 = new Point2D.Double(0, -0.577)
		val node = PPath.createPolyline(Array(point1, point2, point3, point1))

		node.setPaint(Color.YELLOW)
		node.setStrokePaint(Color.YELLOW)
		node.setStroke(Main.defaultStroke)

		node.setTransform(AffineTransform.getTranslateInstance(0, 0))
		node.scale(20)

		node
	}
}
