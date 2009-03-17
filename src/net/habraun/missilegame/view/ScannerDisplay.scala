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



import java.awt.BasicStroke
import java.awt.Color
import java.awt.geom._

import edu.umd.cs.piccolo.nodes._



class ScannerDisplay(radius: Float, defaultRange: Double) extends ScannerDisplayConstants {

	val node = {
		// The blue background color of the scanner display. Any other scanner graphics will be added to this
		// node.
		val node = PPath.createEllipse(-radius, -radius, radius * 2, radius * 2)
		node.setPaint(background)
		node.setStroke(stroke)
		node.setStrokePaint(markings)

		// The circle around the scanner display with the direction markings.
		var directionMarkings = Nil
		for ( i <- 0 until 24) {
			val displayedAngle = i * 15
			val actualAngle = Math.toRadians(i * 15 - 90)

			val startRadius = radius - directionMarkingOffset
			val endRadius = radius + directionMarkingOffset
			val startX = startRadius * Math.cos(actualAngle)
			val startY = startRadius * Math.sin(actualAngle)
			val endX = endRadius * Math.cos(actualAngle)
			val endY = endRadius * Math.sin(actualAngle)
			val lineStart = new Point2D.Double(startX, startY)
			val lineEnd = new Point2D.Double(endX, endY)
			val line = PPath.createPolyline(Array(lineStart, lineEnd))
			line.setStrokePaint(markings)
			line.setStroke(stroke)

			val angleText = new PText(displayedAngle.toString)
			angleText.scale(0.8)
			val angleX = Math.cos(actualAngle) * (radius + directionOffset)
			val angleY = Math.sin(actualAngle) * (radius + directionOffset)
			val angleXOffset = -(angleText.getWidth * angleText.getScale / 2)
			val angleYOffset = -(angleText.getHeight * angleText.getScale / 2)

			angleText.setTextPaint(markings)
			angleText.setOffset(angleX + angleXOffset, angleY + angleYOffset)

			node.addChild(line)
			node.addChild(angleText)
		}

		// A small circle around the player's ship.
		val innerCircle = PPath.createEllipse(-innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2)
		innerCircle.setPaint(background)
		innerCircle.setStroke(fineStroke)
		innerCircle.setStrokePaint(innerMarkings)
		node.addChild(innerCircle)

		// Lines each 45 degrees from the inner to the outer circle.
		for ( i <- 0 until 4 ) {
			val angle = i * (Math.Pi / 2)
			val startX = innerRadius * Math.cos(angle)
			val startY = innerRadius * Math.sin(angle)
			val endX = (radius - directionMarkingOffset - 1.5) * Math.cos(angle)
			val endY = (radius - directionMarkingOffset - 1.5) * Math.sin(angle)
			val lineStart = new Point2D.Double(startX, startY)
			val lineEnd = new Point2D.Double(endX, endY)
			val line = PPath.createPolyline(Array(lineStart, lineEnd))
			line.setStrokePaint(innerMarkings)
			line.setStroke(fineStroke)

			node.addChild(line)
		}

		node
	}
	
	// More circles around the player ship that are supposed to help with judging distance.
	var distances: List[PText] = Nil
	for ( i <- 0 until distanceCircleNumber ) {
		val r = radius * ((i + 1) / (distanceCircleNumber + 1).toFloat)
		val circle = PPath.createEllipse(-r, -r, r * 2, r * 2)
		circle.setPaint(transparency)
		circle.setStroke(fineStroke)
		circle.setStrokePaint(innerMarkings)

		val distance = new PText("???")
		distance.setTextPaint(innerMarkings)
		distance.scale(0.8)
		distance.setOffset(5, -r - 13)

		node.addChild(circle)
		node.addChild(distance)

		distances = distances ::: List(distance)
	}



	def update(zoom: Double) {
		for ( i <- 0 until distances.length ) {
			val distance = (defaultRange * (i + 1) / (distances.length + 1).toDouble).toInt
			distances(i).setText(distance.toString)
		}
	}
}



trait ScannerDisplayConstants {
	// Colors
	val background = new Color(0, 0, 120)
	val markings = Color.WHITE
	val innerMarkings = new Color(0, 0, 200)
	val transparency = new Color(0, 0, 0, 0)

	val stroke = new BasicStroke(2)
	val fineStroke = new BasicStroke(1)

	val directionMarkingOffset = 3
	val directionOffset = 20
	val innerRadius = 20

	val distanceCircleNumber = 3
}
