package com.craftingdead.core.client.renderer.item;

import com.craftingdead.core.CraftingDead;
import com.craftingdead.core.capability.gun.IGun;
import com.craftingdead.core.client.renderer.item.model.ModelAKMIS1;
import com.craftingdead.core.client.renderer.item.model.ModelAKMIS2;
import com.craftingdead.core.item.AttachmentItem;
import com.craftingdead.core.item.ModItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AK47Renderer extends RenderGun {

  private final Model ironSight1 = new ModelAKMIS1();
  private final Model ironSight2 = new ModelAKMIS2();

  public AK47Renderer() {
    super(ModItems.AK47);
  }

  @Override
  protected void renderGunThirdPerson(LivingEntity livingEntity, IGun gun,
      MatrixStack matrixStack) {
    matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(-15));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(77));

    matrixStack.translate(0.9F, -0.7F, 0.4F);

    matrixStack.rotate(Vector3f.ZP.rotationDegrees(15));

    matrixStack.translate(0.3F, 0.6F, 0.0F);

    float scale = 0.35F;
    matrixStack.scale(scale, scale, scale);
  }

  @Override
  protected void renderGunFirstPerson(PlayerEntity entityplayer, IGun gun,
      MatrixStack matrixStack) {

    this.muzzleFlashX = 0.1F;
    this.muzzleFlashY = -0.1F;
    this.muzzleFlashZ = -2.19F;
    this.muzzleScale = 1.2F;

    matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(-30));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(5));

    matrixStack.translate(1.8F, -0.14F, 0.1F);

    float scale = 0.35F;
    matrixStack.scale(scale, scale, scale);
  }

  @Override
  protected void renderGunFirstPersonAiming(PlayerEntity playerEntity, IGun gun,
      MatrixStack matrixStack) {

    matrixStack.translate(0F, 0F, -0.002F);

    matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(-35));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(5));

    matrixStack.translate(0.6F, -0.48F, 1F);

    float scale = 0.15F;
    matrixStack.scale(scale, scale, scale);

    matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
    matrixStack.rotate(Vector3f.XP.rotationDegrees(-1));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(0.25F));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(1.25F));

    matrixStack.translate(-0.3F, -0.2F, 0.005F);

    if (gun.getAttachments().contains(ModItems.RED_DOT_SIGHT.get())) {
      matrixStack.translate(0F, 0.075F, 0.00625F);
    } else if (gun.getAttachments().contains(ModItems.ACOG_SIGHT.get())) {
      matrixStack.translate(0F, 0.075F, 0.00625F);
    } else if (gun.getAttachments().contains(ModItems.EOTECH_SIGHT.get())) {
      matrixStack.translate(0F, 0.0625F, 0.00625F);
    }
  }

  @Override
  protected void renderIronSights(LivingEntity livingEntity, IGun gun,
      MatrixStack matrixStack,
      IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay) {
    this.renderIronSight1(matrixStack, renderTypeBuffer, packedLight, packedOverlay);
    this.renderIronSight2(matrixStack, renderTypeBuffer, packedLight, packedOverlay);
  }

  @Override
  protected void renderGunOnPlayerBack(LivingEntity livingEntity, IGun gun,
      MatrixStack matrixStack) {
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
    matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(180));

    float scale = 0.18F;
    matrixStack.scale(scale, scale, scale);
    matrixStack.translate(0F, 0.8F, 0.9F);
  }

  @Override
  protected void renderGunAmmo(LivingEntity livingEntity, ItemStack itemStack,
      MatrixStack matrixStack) {}

  @Override
  protected void renderGunAttachment(LivingEntity livingEntity, AttachmentItem attachmentItem,
      MatrixStack matrixStack) {

    if (attachmentItem == ModItems.LP_SCOPE.get()) {
      matrixStack.translate(-28F, -9F, -0.35F);
      float scale = 2F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.HP_SCOPE.get()) {
      matrixStack.translate(-28F, -9F, -0.35F);
      float scale = 2F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.RED_DOT_SIGHT.get()) {
      matrixStack.translate(-30F, -5.4F, -0.35F);
      float scale = 2F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.ACOG_SIGHT.get()) {
      matrixStack.translate(-27F, -5F, 0.3F);
      float scale = 1.8F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.SUPPRESSOR.get()) {
      matrixStack.translate(30F, -0.2F, 2F);
      float scale = 1.9F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.TACTICAL_GRIP.get()) {
      matrixStack.translate(-3F, 3.2F, 2F);
      float scale = 3F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.BIPOD.get()) {
      matrixStack.translate(-15F, 3.7F, -2F);
      float scale = 3F;
      matrixStack.scale(scale, scale, scale);
    }

    if (attachmentItem == ModItems.EOTECH_SIGHT.get()) {
      matrixStack.translate(-23D, -8.3D, 1D);
      float scale = 0.4F;
      matrixStack.scale(scale, scale, scale);
    }
  }

  @Override
  public void renderHandLocation(PlayerEntity playerEntity, IGun gun,
      boolean rightHand, MatrixStack matrixStack) {
    if (rightHand) {
      matrixStack.translate(-0.1F, -0.23F, -0.3F);
    } else {
      matrixStack.translate(-0.1F, 0.3F, 0.04F);
    }
  }

  @Override
  public void renderWhileSprinting(MatrixStack matrixStack) {
    matrixStack.rotate(Vector3f.YP.rotationDegrees(-70));
    matrixStack.translate(4F, 0.8F, 2.5F);
  }

  private void renderIronSight1(MatrixStack matrixStack,
      IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay) {
    matrixStack.push();
    {
      float scale = 1.1F;
      matrixStack.scale(scale, scale, scale);
      matrixStack.translate(-3.2F, -0.48F, 0.02F);

      IVertexBuilder vertexBuilder =
          renderTypeBuffer.getBuffer(this.ironSight1.getRenderType(
              new ResourceLocation(CraftingDead.ID, "textures/attachment/akm_is1.png")));
      this.ironSight1.render(matrixStack, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F,
          1.0F,
          1.0F);
    }
    matrixStack.pop();
  }

  private void renderIronSight2(MatrixStack matrixStack,
      IRenderTypeBuffer renderTypeBuffer, int packedLight, int packedOverlay) {
    matrixStack.push();
    {
      matrixStack.translate(0.7F, -0.6F, 0.01F);

      float scale = 0.85F;
      matrixStack.scale(scale, scale, scale);

      IVertexBuilder vertexBuilder =
          renderTypeBuffer
              .getBuffer(this.ironSight2.getRenderType(new ResourceLocation(CraftingDead.ID,
                  "textures/attachment/akm_is2.png")));
      this.ironSight2.render(matrixStack, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F,
          1.0F,
          1.0F);
    }
    matrixStack.pop();
  }
}