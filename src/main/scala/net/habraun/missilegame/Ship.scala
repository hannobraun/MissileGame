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
import net.habraun.sd.collision._



class Ship extends GameEntity {

	// Set physical attributes.
	mass = 100000000
	radius = 150



	def update = true

	def active = true

	def damage(amount: Int) {}
}
