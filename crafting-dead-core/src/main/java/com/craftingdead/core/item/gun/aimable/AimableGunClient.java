/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.craftingdead.core.item.gun.aimable;

import com.craftingdead.core.item.gun.simple.SimpleGunClient;
import com.craftingdead.core.living.ILiving;

public class AimableGunClient extends SimpleGunClient<AimableGun> {

  public AimableGunClient(AimableGun gun) {
    super(gun);
  }

  @Override
  protected boolean canFlash(ILiving<?, ?> living) {
    return super.canFlash(living) && !this.gun.isAiming(living.getEntity());
  }
}