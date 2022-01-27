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

package com.craftingdead.core.client.renderer.item;

import java.util.Collection;
import javax.annotation.Nullable;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Provides custom {@link Item} rendering abilities.
 */
public interface CustomItemRenderer {

  Codec<? extends CustomItemRenderer> CODEC =
      ItemRendererType.CODEC.dispatch(CustomItemRenderer::getType, ItemRendererType::getCodec);

  /**
   * Checks if this renderer should handle a specific item's render type.
   * 
   * @param itemStack - {@link ItemStack} being rendered
   * @param transformType - the perspective
   * @return true if this renderer should handle the given render type, otherwise false
   */
  boolean handlePerspective(ItemStack itemStack, ItemCameraTransforms.TransformType transformType);

  /**
   * Called to do the actual rendering.
   * 
   * @param transformType - the perspective
   * @param itemStack - {@link ItemStack} being rendered
   */
  void renderItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType,
      @Nullable LivingEntity livingEntity, MatrixStack matrixStack,
      IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay);

  void rotateCamera(ItemStack itemStack, LivingEntity livingEntity, float partialTicks,
      Vector3f rotations);

  /**
   * Get all models this {@link CustomItemRenderer} depends on. This is used to resolve and stitch
   * model texture dependencies as well as auto baking models upon resource reload.
   * 
   * @return a {@link Collection} of model locations.
   */
  Collection<ResourceLocation> getModelDependencies(Item item);

  /**
   * Get any additional textures that need to be stitched to the block texture atlas. Use this if
   * you want to re-bake models with different textures.
   * 
   * @return a {@link Collection} of texture locations.
   */
  Collection<RenderMaterial> getMaterials();

  /**
   * Clear any cached models this {@link CustomItemRenderer} may of stored.
   */
  void refreshCachedModels();

  ItemRendererType getType();
}
