/*******************************************************************************
 * Copyright (C) 2015 BOULMIER Jérôme, CORTIER Benoît
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 *******************************************************************************/
package fr.utbm.tc.qlearningmario.mario.entity;

import fr.utbm.tc.qlearningmario.mario.common.Hitbox;

public class Coin extends Entity<Coin> implements Collectable {
	private static final Hitbox hitbox = new Hitbox(1, 1);

	private boolean isCollected = false;
	private Entity<?> collector = null;

	public Coin() {
		this.currentHitbox = Coin.hitbox;
	}

	@Override
	public void collect(Entity<?> entity) {
		this.isCollected = true;
		this.collector = entity;
	}

	@Override
	public boolean isCollected() {
		return this.isCollected;
	}

	@Override
	public Entity<?> getCollector() {
		return this.collector;
	}

}
