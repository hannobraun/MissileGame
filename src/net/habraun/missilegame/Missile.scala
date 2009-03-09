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



import net.habraun.scd._



class Missile(target: Body) {

	val body = {
		val body = new Body
		body.mass = 150
		body.shape = Circle(1)

		body
	}
	Console.println("body.position: " + body.position)



	def update {
		val maxAccelerationForce = 50000.0
		val maxManeuveringForce = 30000.0

		val nominalHeading = (target.position - body.position).normalize
		val deviatingVelocity = body.velocity.project(nominalHeading.orthogonal)
		
		val accelerationForce = nominalHeading * maxAccelerationForce
		val maneuveringForce = {
			val correctionForce = -deviatingVelocity * body.mass / Main.timeStep
			if (correctionForce * correctionForce <= maxManeuveringForce) {
				correctionForce
			}
			else {
				correctionForce * (maxManeuveringForce / correctionForce.length)
			}
		}

		body.applyForce(accelerationForce)
		body.applyForce(maneuveringForce)
	}
}
