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

package com.craftingdead.core.world.entity.extension;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.world.action.Action;
import com.craftingdead.core.world.action.ActionObserver;
import com.craftingdead.core.world.item.gun.Gun;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface LivingExtension<E extends LivingEntity, H extends LivingHandler>
    extends LivingHandler {

  Capability<LivingExtension<?, ?>> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  /**
   * @see {@link net.minecraftforge.event.AttachCapabilitiesEvent}
   */
  static final ResourceLocation CAPABILITY_KEY = new ResourceLocation(CraftingDead.ID, "living");

  static <E extends LivingEntity> LivingExtension<E, ?> create(E entity) {
    return new LivingExtensionImpl<>(entity);
  }

  @SuppressWarnings("unchecked")
  static <E extends LivingEntity> LivingExtension<E, ?> getOrThrow(E entity) {
    return CapabilityUtil.getOrThrow(LivingExtension.CAPABILITY, entity, LivingExtension.class);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  static <E extends LivingEntity> LivingExtension<E, ?> get(E entity) {
    return CapabilityUtil.get(LivingExtension.CAPABILITY, entity, LivingExtension.class);
  }

  /**
   * Load this {@link LivingExtension} - called upon capability attachment.
   */
  void load();

  /**
   * Get a handler by its ID.
   * 
   * @param id - the handler's ID
   * @return an {@link Optional} handler
   */
  Optional<H> getHandler(ResourceLocation id);

  /**
   * Get a handler or throw an exception if not present.
   * 
   * @param id - the handler's ID
   * @throws IllegalStateException if not present
   * @return the handler
   */
  @Nonnull
  H getHandlerOrThrow(ResourceLocation id) throws IllegalStateException;

  /**
   * Register a handler.
   * 
   * @param id - the handler's ID
   * @param handler - the handler to attach
   */
  void registerHandler(ResourceLocation id, H handler);

  Optional<Action> getAction();

  /**
   * Perform an unforced action.
   * 
   * @param action - the {@link Action} to perform
   * @param sendUpdate - alert the other side (client/server) of this change
   * @return whether the {@link Action} is being performed
   */
  default boolean performAction(Action action, boolean sendUpdate) {
    return this.performAction(action, false, sendUpdate);
  }

  /**
   * Perform the specified {@link Action}.
   * 
   * @param action - the {@link Action} to perform
   * @param force - whether this {@link Action} will be forced (will override an existing action)
   * @param sendUpdate - alert the other side (client/server) of this change
   * @return whether the {@link Action} is being performed
   */
  <T extends Action> boolean performAction(T action, boolean force, boolean sendUpdate);

  /**
   * Cancel the current {@link Action} being performed.
   * 
   * @param sendUpdate- alert the other side (client/server) of this change
   */
  void cancelAction(boolean sendUpdate);

  /**
   * Set the action observer.
   * 
   * @param actionObserver - the {@link ActionObserver} to attach
   */
  void setActionObserver(@Nullable ActionObserver actionObserver);

  /**
   * Get the action observer.
   * 
   * @return an optional {@link ActionObserver}
   * @see #setActionObserver(ActionObserver)
   */
  Optional<ActionObserver> getActionObserver();

  /**
   * Whether this {@link LivingExtension} is currently monitoring an action.
   * 
   * @return true if it is monitoring an action.
   */
  default boolean isObservingAction() {
    return this.getActionObserver().isPresent();
  }

  /**
   * Prevent this {@link LivingExtension} from moving (for the current tick).
   * 
   * @param movementBlocked - if the movement should be blocked
   */
  void setMovementBlocked(boolean movementBlocked);

  /**
   * Whether this {@link LivingExtension} is currently moving.
   * 
   * @return true if moving
   */
  boolean isMoving();

  /**
   * Gets the {@link IItemHandler} associated with this {@link LivingExtension} (used for the mod's
   * equipment storage)
   * 
   * @return the {@link IItemHandler}
   * @see {@link com.craftingdead.core.world.inventory.ModEquipmentSlot}
   */
  IItemHandlerModifiable getItemHandler();

  /**
   * Get an {@link EntitySnapshot} for the specified tick time.
   * 
   * @param tick - the tick in which to retrieve the {@link EntitySnapshot} for
   * @return the snapshot
   * @throws IndexOutOfBoundsException if the tick is too small
   */
  EntitySnapshot getSnapshot(int tick) throws IndexOutOfBoundsException;

  /**
   * Whether this {@link LivingExtension} is crouching.
   * 
   * @return true if crouching
   */
  boolean isCrouching();

  /**
   * Makes this {@link LivingExtension} crouch.
   * 
   * @param crouching - crouching state
   * @param sendUpdate - alert the other side (client/server) of this change
   */
  void setCrouching(boolean crouching, boolean sendUpdate);

  /**
   * Get a modified gun accuracy for this {@link LivingExtension}. E.g. walking will reduce
   * accuracy.
   * 
   * @param accuracy - the accuracy to modify
   * @param random - a {@link Random} instance
   * @return the modified accuracy
   */
  default float getModifiedAccuracy(float accuracy, Random random) {
    if (this.isMoving()) {
      accuracy -= 0.15F;
    } else if (this.isCrouching()) {
      accuracy += 0.15F;
    }

    // Apply some random accuracy for non-players (bots)
    if (!(this.getEntity() instanceof Player)) {
      accuracy -= random.nextFloat();
    }

    return accuracy;
  }

  /**
   * Get the {@link LivingEntity} associated with this {@link LivingExtension}.
   * 
   * @return the {@link LivingEntity}
   */
  E getEntity();

  /**
   * Shorthand for {@link LivingEntity#getLevel()}.
   * 
   * @return the {@link Level}
   */
  default Level getLevel() {
    return this.getEntity().getLevel();
  }

  /**
   * Shorthand for {@link LivingEntity#getRandom()}.
   * 
   * @return the {@link Random}
   */
  default Random getRandom() {
    return this.getEntity().getRandom();
  }

  /**
   * Shorthand for {@link LivingEntity#getMainHandItem}.
   * 
   * @return the main hand {@link ItemStack}
   */
  default ItemStack getMainHandItem() {
    final var item = this.getEntity().getMainHandItem();
    return item.isEmpty() ? ItemStack.EMPTY : item;
  }

  /**
   * Helper method to retrieve a gun in the entity's main hand.
   * 
   * @return a {@link LazyOptional} gun.
   */
  default LazyOptional<Gun> getMainHandGun() {
    return this.getMainHandItem().getCapability(Gun.CAPABILITY);
  }

  default void breakItem(ItemStack itemStack) {
    if (!itemStack.isEmpty()) {
      if (!this.getEntity().isSilent()) {
        this.getLevel().playLocalSound(this.getEntity().getX(), this.getEntity().getY(),
            this.getEntity().getZ(), SoundEvents.ITEM_BREAK,
            this.getEntity().getSoundSource(), 0.8F,
            0.8F + this.getLevel().getRandom().nextFloat() * 0.4F, false);
      }

      this.spawnItemParticles(itemStack, 5);
    }
  }

  default void spawnItemParticles(ItemStack itemStack, int count) {
    for (int i = 0; i < count; ++i) {
      var velocity = new Vec3((this.getRandom().nextFloat() - 0.5D) * 0.1D,
          Math.random() * 0.1D + 0.1D, 0.0D);
      velocity = velocity.xRot(-this.getEntity().getXRot() * ((float) Math.PI / 180F));
      velocity = velocity.yRot(-this.getEntity().getYRot() * ((float) Math.PI / 180F));
      var yPos = -this.getRandom().nextFloat() * 0.6D - 0.3D;
      var pos = new Vec3((this.getRandom().nextFloat() - 0.5D) * 0.3D, yPos, 0.6D);
      pos = pos.xRot(-this.getEntity().getXRot() * ((float) Math.PI / 180F));
      pos = pos.yRot(-this.getEntity().getYRot() * ((float) Math.PI / 180F));
      pos =
          pos.add(this.getEntity().getX(), this.getEntity().getEyeY(), this.getEntity().getZ());
      if (this.getLevel() instanceof ServerLevel level) {
        level.sendParticles(
            new ItemParticleOption(ParticleTypes.ITEM, itemStack), pos.x, pos.y, pos.z, 1,
            velocity.x, velocity.y + 0.05D, velocity.z, 0.0D);
      } else {
        this.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), pos.x,
            pos.y, pos.z, velocity.x, velocity.y + 0.05D, velocity.z);
      }
    }
  }
}
