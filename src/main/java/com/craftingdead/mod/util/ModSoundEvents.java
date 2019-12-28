package com.craftingdead.mod.util;

import com.craftingdead.mod.CraftingDead;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSoundEvents {

  public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
      new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, CraftingDead.ID);

  public static final RegistryObject<SoundEvent> ACR_SHOOT = register("acr_shoot");
  public static final RegistryObject<SoundEvent> AK47_SHOOT = register("ak47_shoot");
  public static final RegistryObject<SoundEvent> DESERT_EAGLE_SHOOT =
      register("desert_eagle_shoot");
  public static final RegistryObject<SoundEvent> M4A1_SHOOT = register("m4a1_shoot");
  public static final RegistryObject<SoundEvent> M9_SHOOT = register("m9_shoot");
  public static final RegistryObject<SoundEvent> TASER_SHOOT = register("taser_shoot");
  public static final RegistryObject<SoundEvent> MAGNUM_SHOOT = register("magnum_shoot");
  public static final RegistryObject<SoundEvent> FN57_SHOOT = register("fn57_shoot");
  public static final RegistryObject<SoundEvent> DRY_FIRE = register("dry_fire");
  public static final RegistryObject<SoundEvent> AK47_RELOAD = register("ak47_reload");

  private static RegistryObject<SoundEvent> register(String name) {
    ResourceLocation registryName = new ResourceLocation(CraftingDead.ID, name);
    return SOUND_EVENTS.register(name, () -> new SoundEvent(registryName));
  }
}
