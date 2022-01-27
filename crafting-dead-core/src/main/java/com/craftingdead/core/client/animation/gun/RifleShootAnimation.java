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

package com.craftingdead.core.client.animation.gun;

import com.craftingdead.core.client.animation.TimedAnimation;
import com.craftingdead.core.util.EasingFunction;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RifleShootAnimation extends TimedAnimation {

  public RifleShootAnimation() {
    super(5);
  }

  @Override
  public void apply(float partialTicks, MatrixStack matrixStack) {
    float lerpProgress = this.lerpProgress(partialTicks);
    matrixStack.translate(0, 0, getPushbackTranslation(lerpProgress));
    matrixStack.mulPose(getBounceRotation(lerpProgress));
  }

  private static Quaternion getBounceRotation(float lerpProgress) {
    final float amplification = 3.0F;
    float easedProgress =
        EasingFunction.SINE_IN.andThen(EasingFunction.ELASTIC_OUT).apply(lerpProgress);
    return Vector3f.XP
        .rotationDegrees(amplification * MathHelper.sin(easedProgress * ((float) Math.PI)));
  }

  private static float getPushbackTranslation(float lerpProgress) {
    float easedProgress = EasingFunction.EXPO_OUT.apply(lerpProgress);
    return MathHelper.sin(easedProgress * ((float) Math.PI)) * 0.5F;
  }

  @Override
  public void applyHand(Hand hand, HandSide handSide, float partialTicks, MatrixStack matrixStack) {
    float lerpProgress = this.lerpProgress(partialTicks);
    float translation = getPushbackTranslation(lerpProgress);
    switch (handSide) {
      case LEFT:
        matrixStack.translate(0, -translation * 0.5F, translation);
        break;
      case RIGHT:
        matrixStack.translate(0, 0, translation);
        break;
    }
  }

  @Override
  public void applyCamera(float partialTicks, Vector3f rotations) {}
}
