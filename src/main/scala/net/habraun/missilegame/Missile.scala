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



import net.habraun.sd.collision.shape.Circle
import net.habraun.sd.core.Body



class Missile(target: () => Option[GameEntity], val hostile: Boolean, attributes: MissileAttributes)
		extends GameEntity {

	// Set physical attributes
	mass = attributes.mass
	radius = attributes.radius



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
			val nominalHeading = ( t.position - position ).normalize
			val deviatingVelocity = velocity.projectOn( nominalHeading.orthogonal )
		
			val accelerationForce = nominalHeading * attributes.maxAccelerationForce
			val maneuveringForce = {
				val correctionForce = -deviatingVelocity * mass / Main.timeStep
				if ( correctionForce * correctionForce <= attributes.maxManeuveringForce ) {
					correctionForce
				}
				else {
					correctionForce * ( attributes.maxManeuveringForce / correctionForce.length )
				}
			}

			applyForce( accelerationForce )
			applyForce( maneuveringForce )

			val targetRadius = t.radius
			val missileRadius = radius
			if ( ( t.position - position ).length - targetRadius - missileRadius <= 50 ) {
				killed = true
				t.damage( 1 )
			}
		}

		if ( target() == None || damage >= 1 )
			killed = true

		!killed
	}
}
