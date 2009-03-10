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



class Missile(_target: Body) {

	val body = {
		val body = new Body
		body.mass = 150
		body.shape = Circle(1)

		body
	}



	def target = _target



	/**
	 * Missile guidance logic.
	 * Returns true if the missile explodes.
	 */

	def update: Boolean = {
		val maxAccelerationForce = 5000.0
		val maxManeuveringForce = 3000.0

		val nominalHeading = (_target.position - body.position).normalize
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

		val targetRadius = target.shape.asInstanceOf[Circle].radius
		val missileRadius = body.shape.asInstanceOf[Circle].radius
		(target.position - body.position).length - targetRadius - missileRadius <= 10
	}
}
