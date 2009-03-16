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

import edu.umd.cs.piccolo.nodes._



class ScannerDisplay(radius: Float) extends ScannerDisplayConstants {

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

		node
	}
}



trait ScannerDisplayConstants {
	// Colors
	val background = new Color(0, 0, 120)
	val markings = Color.WHITE

	val stroke = new BasicStroke(2)

	val directionMarkingOffset = 3
	val directionOffset = 20
}
