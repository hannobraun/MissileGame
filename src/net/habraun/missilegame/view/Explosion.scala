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
import java.awt.geom._

import edu.umd.cs.piccolo._
import edu.umd.cs.piccolo.nodes._
import net.habraun.sd.math._



class Explosion(position: Vec2D, diameter: Int, scannerRadius: Double) {

	val node = {
		val node = new PNode

		for ( i <- 0 until Explosion.circleCount) {
			node.addChild {
				val r = diameter * (1 - (i.toFloat / Explosion.circleCount)) / 2
				val node = PPath.createEllipse(-r, -r, r * 2, r * 2)
				node.setPaint(new Color(255, 255, 255, 80))
				node.setStrokePaint(Explosion.strokePaint)
				node.setStroke(Explosion.stroke)

				node
			}
		}

		node
	}

	private[this] var ttl = Explosion.timeToLive



	def update(scanRange: Double, center: Vec2D): Boolean = {
		val x = (position.x - center.x) * scannerRadius / scanRange
		val y = (position.y - center.y) * scannerRadius / scanRange
		node.setTransform(AffineTransform.getTranslateInstance(x, y))

		val scale = node.getScale
		node.setScale(scale - (1.0 / Explosion.timeToLive))
		ttl -= 1

		ttl <= 0
	}
}



object Explosion {
	val strokePaint = new Color(0, 0, 0, 0)
	val stroke = new BasicStroke(0)
	val circleCount = 10
	val timeToLive = 1000
}
