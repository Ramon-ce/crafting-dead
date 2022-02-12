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

package com.craftingdead.survival.world.action;

import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.world.action.ActionType;
import com.craftingdead.core.world.action.TargetSelector;
import com.craftingdead.core.world.action.item.BlockItemActionType;
import com.craftingdead.core.world.action.item.EntityItemActionType;
import com.craftingdead.core.world.item.ModItems;
import com.craftingdead.core.world.item.clothing.Clothing;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.effect.SurvivalMobEffects;
import com.craftingdead.survival.world.item.SurvivalItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SurvivalActionTypes {

  @SuppressWarnings("unchecked")
  public static final DeferredRegister<ActionType<?>> ACTION_TYPES =
      DeferredRegister.create((Class<ActionType<?>>) (Object) ActionType.class,
          CraftingDeadSurvival.ID);

  public static final RegistryObject<EntityItemActionType<?>> SHRED_CLOTHING =
      ACTION_TYPES.register("shred_clothing",
          () -> EntityItemActionType.builder(TargetSelector.SELF_ONLY)
              .forItem(CapabilityUtil.capabilityPresent(Clothing.CAPABILITY))
              .customAction((performer, target) -> {
                var random = target.getRandom();
                int randomRagAmount = random.nextInt(3) + 3;

                for (int i = 0; i < randomRagAmount; i++) {
                  if (random.nextBoolean()) {
                    target.getEntity().spawnAtLocation(
                        new ItemStack(SurvivalItems.CLEAN_RAG::get));
                  } else {
                    target.getEntity().spawnAtLocation(
                        new ItemStack(SurvivalItems.DIRTY_RAG::get));
                  }
                }
              }, 1.0F)
              .build());

  public static final RegistryObject<EntityItemActionType<?>> USE_SPLINT =
      ACTION_TYPES.register("use_splint",
          () -> EntityItemActionType
              .builder(TargetSelector.SELF_OR_OTHERS.hasEffect(SurvivalMobEffects.BROKEN_LEG))
              .forItem(SurvivalItems.SPLINT)
              .build());

  public static final RegistryObject<EntityItemActionType<?>> USE_CLEAN_RAG =
      ACTION_TYPES.register("use_clean_rag",
          () -> EntityItemActionType
              .builder(TargetSelector.SELF_OR_OTHERS.hasEffect(SurvivalMobEffects.BLEEDING))
              .forItem(SurvivalItems.CLEAN_RAG)
              .duration(16)
              .returnItem(SurvivalItems.BLOODY_RAG)
              .build());

  public static final RegistryObject<BlockItemActionType> WASH_RAG =
      ACTION_TYPES.register("wash_rag",
          () -> BlockItemActionType.builder()
              .forItem(itemStack -> itemStack.is(SurvivalItems.DIRTY_RAG.get())
                  || itemStack.is(SurvivalItems.BLOODY_RAG.get()))
              .returnItem(SurvivalItems.CLEAN_RAG)
              .finishSound(SoundEvents.BUCKET_FILL)
              .forBlock(blockState -> blockState.getFluidState().is(Fluids.WATER))
              .build());

  public static final RegistryObject<EntityItemActionType<?>> USE_SYRINGE_ON_ZOMBIE =
      ACTION_TYPES.register("use_syringe_on_zombie",
          () -> EntityItemActionType.builder(TargetSelector.OTHERS_ONLY.ofEntityType(Zombie.class))
              .forItem(ModItems.SYRINGE)
              .duration(16)
              .customAction((performer, target) -> target.getEntity().hurt(
                  DamageSource.mobAttack(target.getEntity()), 2.0F), 0.25F)
              .returnItem(SurvivalItems.RBI_SYRINGE)
              .build());

  public static final RegistryObject<EntityItemActionType<?>> USE_CURE_SYRINGE =
      ACTION_TYPES.register("use_cure_syringe",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(SurvivalItems.CURE_SYRINGE)
              .duration(16)
              .returnItem(ModItems.SYRINGE)
              .build());

  public static final RegistryObject<EntityItemActionType<?>> USE_RBI_SYRINGE =
      ACTION_TYPES.register("use_rbi_syringe",
          () -> EntityItemActionType.builder(TargetSelector.SELF_OR_OTHERS)
              .forItem(SurvivalItems.RBI_SYRINGE)
              .duration(16)
              .effect(() -> new MobEffectInstance(SurvivalMobEffects.INFECTION.get(), 9999999))
              .returnItem(ModItems.SYRINGE)
              .build());
}
