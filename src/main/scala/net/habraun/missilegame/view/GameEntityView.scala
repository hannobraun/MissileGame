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



import edu.umd.cs.piccolo._
import net.habraun.sd.math._



abstract class GameEntityView(entity: GameEntity, scannerRadius: Double) {

	/**
	 * Returns the Piccolo2D scene graph node this object manages.
	 */

	def node: PNode



	/**
	 * Updates the node.
	 * Returns true if the view remains active, false if it should not be displayed any longer.
	 */

	def update(scanRange: Double, center: Vec2D): Boolean
}
