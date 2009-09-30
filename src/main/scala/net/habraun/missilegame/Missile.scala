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



import net.habraun.sd._
import net.habraun.sd.collision._



abstract class Missile(target: () => Option[GameEntity], val hostile: Boolean, maxAccelerationForce: Double,
		maxManeuveringForce: Double) extends GameEntity {

	val body = {
		val body = new Body
		body.mass = 150
		body.shape = Circle(1)

		body
	}



	private var killed = false
	
	def active = !killed



	private var damage = 0

	def damage(amount: Int) {
		damage += amount
	}



	/**
	 * Missile guidance logic.
	 */

	def update = {
		for (t <- target()) {
			val nominalHeading = (t.body.position - body.position).normalize
			val deviatingVelocity = body.velocity.projectOn(nominalHeading.orthogonal)
		
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

			val targetRadius = t.body.shape.asInstanceOf[Circle].radius
			val missileRadius = body.shape.asInstanceOf[Circle].radius
			if ((t.body.position - body.position).length - targetRadius - missileRadius <= 50) {
				killed = true
				t.damage(1)
			}
		}

		if (target() == None || damage >= 1)
			killed = true

		!killed
	}
}
