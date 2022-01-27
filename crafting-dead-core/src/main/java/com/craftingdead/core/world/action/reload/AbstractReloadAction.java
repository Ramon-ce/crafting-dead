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

package com.craftingdead.core.world.action.reload;

import javax.annotation.Nullable;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.client.animation.Animation;
import com.craftingdead.core.world.action.ActionType;
import com.craftingdead.core.world.action.TimedAction;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.gun.Gun;
import com.craftingdead.core.world.item.gun.GunAnimationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public abstract class AbstractReloadAction extends TimedAction<ActionType> {

  protected final ItemStack gunStack;

  protected final Gun gun;

  protected final ItemStack oldMagazineStack;

  @Nullable
  private Animation animation;

  public AbstractReloadAction(ActionType type, LivingExtension<?, ?> performer,
      @Nullable LivingExtension<?, ?> target) {
    super(type, performer, target);
    this.gunStack = performer.getMainHandItem();
    this.gun = this.gunStack.getCapability(Capabilities.GUN)
        .orElseThrow(() -> new IllegalStateException("Performer not holding gun"));
    this.oldMagazineStack = this.gun.getAmmoProvider().getMagazineStack();
  }

  @Override
  protected int getTotalDurationTicks() {
    return this.gun.getReloadDurationTicks();
  }

  @Override
  public boolean start() {
    if (this.getPerformer().getEntity().isSprinting()) {
      return false;
    }

    if (this.gun.isPerformingSecondaryAction()) {
      this.gun.setPerformingSecondaryAction(this.getPerformer(), false, false);
    }

    this.gun.getReloadSound()
        .ifPresent(sound -> this.getPerformer().getLevel().playSound(null,
            this.getPerformer().getEntity(), sound, SoundCategory.PLAYERS, 1.0F, 1.0F));

    if (this.getPerformer().getLevel().isClientSide()) {
      this.animation = this.gun.getClient().getAnimation(GunAnimationEvent.RELOAD);
      this.gun.getClient().getAnimationController().addAnimation(this.animation);
    }

    return true;
  }

  protected abstract void loadNewMagazineStack(boolean displayOnly);

  @Override
  public boolean tick() {
    if (!this.getPerformer().getLevel().isClientSide()
        && this.getPerformer().getEntity().isSprinting()) {
      this.getPerformer().cancelAction(true);
      return false;
    }
    return super.tick();
  }

  @Override
  protected void finish() {
    if (this.getPerformer().getLevel().isClientSide()) {
      return;
    }
    // This will be synced to the client by the gun.
    this.loadNewMagazineStack(false);
  }

  @Override
  public void cancel() {
    super.cancel();
    if (this.getPerformer().getLevel().isClientSide()) {
      if (this.gun.getReloadSound().isPresent()) {
        // Stop reload sound
        Minecraft.getInstance().getSoundManager()
            .stop(this.gun.getReloadSound().get().getRegistryName(), SoundCategory.PLAYERS);
      }
      if (this.animation != null) {
        this.animation.remove();
      }
    }

    // Revert all changes as we've been cancelled
    this.revert();
  }

  protected abstract void revert();
}
