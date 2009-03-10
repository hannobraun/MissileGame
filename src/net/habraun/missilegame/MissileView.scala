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

import edu.umd.cs.piccolo._
import edu.umd.cs.piccolo.nodes._
import net.habraun.scd._



class MissileView(missile: Missile) {

	val node = new PNode
	
	val symbolNode = {
		val point1 = new Point2D.Double(-0.333, 0.333)
		val point2 = new Point2D.Double(0.333, 0.333)
		val point3 = new Point2D.Double(0, -0.666)
		val node = PPath.createPolyline(Array(point1, point2, point3, point1))

		node.setPaint(Color.RED)
		node.setStrokePaint(Color.RED)
		node.setStroke(Main.defaultStroke)

		node.setRotation(-Math.Pi / 4)
		
		node
	}
	node.addChild(symbolNode)

	val speedNode = {
		val node = new PText
		node.setTextPaint(Color.WHITE)
		node.setOffset(0.8, -0.9)
		node.setScale(0.05)

		node
	}
	node.addChild(speedNode)

	val orientationNode = {
		val node = new PText
		node.setTextPaint(Color.WHITE)
		node.setOffset(0.8, -0.3)
		node.setScale(0.05)

		node
	}
	node.addChild(orientationNode)

	val energyNode = {
		val node = new PText
		node.setTextPaint(Color.WHITE)
		node.setOffset(0.8, 0.3)
		node.setScale(0.05)

		node
	}
	node.addChild(energyNode)



	def update {
		val x = missile.body.position.x
		val y = missile.body.position.y
		node.setTransform(AffineTransform.getTranslateInstance(x, y))
		node.scale(1 / Main.cameraScale * 15)

		speedNode.setText(missile.body.velocity.length.toInt.toString)

		val r = Vec2D(0, -1)
		val v = missile.body.velocity
		val angle = Math.toDegrees(Math.acos((r * v) / (r.length * v.length)))
		val orientation = if (v.x < 0) angle else 360 - angle
		orientationNode.setText(orientation.toInt.toString)

		energyNode.setText("???")
	}
}
