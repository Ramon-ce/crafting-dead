package com.craftingdead.mod.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.craftingdead.mod.capability.ModCapabilities;
import com.craftingdead.mod.capability.animation.DefaultAnimationController;
import com.craftingdead.mod.capability.animation.IAnimation;
import com.craftingdead.mod.capability.animation.IAnimationController;
import com.craftingdead.mod.capability.gun.IGunController;
import com.craftingdead.mod.capability.gun.ItemGunController;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class GunItem extends ShootableItem {

  /**
   * Time between shots in milliseconds.
   */
  private final int fireRate;

  private final int damage;

  private final int reloadDurationTicks;

  /**
   * Accuracy as percentage.
   */
  private final float accuracy;

  /**
   * Amount of "pellets" to be fired in a single shot. It is used by shotguns.
   */
  private final int bulletAmountToFire;

  /**
   * Whether the player can aim with this gun or not
   */
  private final boolean aimable;

  /**
   * Whether the crosshair should be rendered or not while holding this item
   */
  private final boolean crosshair;

  /**
   * {@link FireMode}s the gun can cycle through.
   */
  private final List<FireMode> fireModes;

  private final Supplier<SoundEvent> shootSound;

  private final Supplier<SoundEvent> silencedShootSound;

  private final Supplier<SoundEvent> reloadSound;

  private final Map<AnimationType, Supplier<IAnimation>> animations;

  private final Set<Supplier<MagazineItem>> acceptedMagazines;

  private final Set<Supplier<AttachmentItem>> acceptedAttachments;

  private final Set<Supplier<PaintItem>> acceptedPaints;

  private final Set<Supplier<AttachmentItem>> defaultAttachments;

  public GunItem(Properties properties) {
    super(properties);
    this.fireRate = properties.fireRate;
    this.damage = properties.damage;
    this.reloadDurationTicks = properties.reloadDurationTicks;
    this.accuracy = properties.accuracy;
    this.bulletAmountToFire = properties.bulletAmountToFire;
    this.aimable = properties.aimable;
    this.crosshair = properties.crosshair;
    this.fireModes = properties.fireModes;
    this.shootSound = properties.shootSound;
    this.silencedShootSound = properties.silencedShootSound;
    this.reloadSound = properties.reloadSound;
    this.animations = properties.animations;
    this.acceptedMagazines = properties.acceptedMagazines;
    this.acceptedAttachments = properties.acceptedAttachments;
    this.defaultAttachments = properties.defaultAttachments;
    this.acceptedPaints = properties.acceptedPaints;
    this
        .addPropertyOverride(new ResourceLocation("aiming"),
            (itemStack, world, entity) -> entity != null ? entity
                .getCapability(ModCapabilities.PLAYER)
                .map(player -> entity.getHeldItemMainhand() == itemStack && player.isAiming() ? 1.0F
                    : 0.0F)
                .orElse(0.0F) : 0.0F);
  }

  public int getFireRate() {
    return this.fireRate;
  }

  public int getDamage() {
    return this.damage;
  }

  public int getReloadDurationTicks() {
    return this.reloadDurationTicks;
  }

  public float getAccuracy() {
    return this.accuracy;
  }

  public int getBulletAmountToFire() {
    return this.bulletAmountToFire;
  }

  public boolean isAimable() {
    return this.aimable;
  }

  public boolean hasCrosshair() {
    return this.crosshair;
  }

  public List<FireMode> getFireModes() {
    return this.fireModes;
  }

  public Supplier<SoundEvent> getShootSound() {
    return this.shootSound;
  }

  public Optional<SoundEvent> getSilencedShootSound() {
    return Optional.ofNullable(this.silencedShootSound.get());
  }

  public Optional<SoundEvent> getReloadSound() {
    return Optional.ofNullable(this.reloadSound.get());
  }

  public Map<AnimationType, Supplier<IAnimation>> getAnimations() {
    return this.animations;
  }

  public Set<MagazineItem> getAcceptedMagazines() {
    return this.acceptedMagazines.stream().map(Supplier::get).collect(Collectors.toSet());
  }

  public Set<AttachmentItem> getAcceptedAttachments() {
    return this.acceptedAttachments.stream().map(Supplier::get).collect(Collectors.toSet());
  }

  public Set<PaintItem> getAcceptedPaints() {
    return this.acceptedPaints.stream().map(Supplier::get).collect(Collectors.toSet());
  }

  public Set<AttachmentItem> getDefaultAttachments() {
    return this.defaultAttachments.stream().map(Supplier::get).collect(Collectors.toSet());
  }

  @Override
  public Predicate<ItemStack> getInventoryAmmoPredicate() {
    return itemStack -> this.acceptedMagazines
        .stream()
        .map(Supplier::get)
        .anyMatch(itemStack.getItem()::equals);
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundNBT nbt) {
    return new ICapabilitySerializable<CompoundNBT>() {
      private final IAnimationController animationController = new DefaultAnimationController();
      private final IGunController gunController = new ItemGunController(GunItem.this);

      @Override
      public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ModCapabilities.ANIMATION_CONTROLLER
            ? LazyOptional.of(() -> this.animationController).cast()
            : cap == ModCapabilities.GUN_CONTROLLER || cap == ModCapabilities.ACTION
                ? LazyOptional.of(() -> this.gunController).cast()
                : LazyOptional.empty();
      }

      @Override
      public CompoundNBT serializeNBT() {
        return this.gunController.serializeNBT();
      }

      @Override
      public void deserializeNBT(CompoundNBT nbt) {
        this.gunController.deserializeNBT(nbt);
      }
    };
  }

  @Override
  public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
    return true;
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack,
      boolean slotChanged) {
    return oldStack.getItem() != newStack.getItem();
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity,
      Hand hand) {
    if (world.isRemote()) {
      playerEntity
          .getCapability(ModCapabilities.PLAYER)
          .ifPresent(player -> player.toggleAiming(true));
    }
    return super.onItemRightClick(world, playerEntity, hand);
  }

  @Override
  public CompoundNBT getShareTag(ItemStack stack) {
    return stack.serializeNBT();
  }

  @Override
  public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
    stack.deserializeNBT(nbt);
  }

  public static enum AnimationType {
    SHOOT;
  }

  public static class Properties extends Item.Properties {

    private int fireRate;

    private int damage;

    private int reloadDurationTicks;

    private int bulletAmountToFire = 1;

    private float accuracy;

    private boolean aimable;

    private boolean crosshair = true;

    private final List<FireMode> fireModes = new ArrayList<>();

    private Supplier<SoundEvent> shootSound;

    private Supplier<SoundEvent> silencedShootSound = () -> null;

    private Supplier<SoundEvent> reloadSound = () -> null;

    private final Map<AnimationType, Supplier<IAnimation>> animations =
        new EnumMap<>(AnimationType.class);

    private final Set<Supplier<MagazineItem>> acceptedMagazines = new HashSet<>();

    private final Set<Supplier<AttachmentItem>> acceptedAttachments = new HashSet<>();

    private final Set<Supplier<PaintItem>> acceptedPaints = new HashSet<>();

    private final Set<Supplier<AttachmentItem>> defaultAttachments = new HashSet<>();

    public Properties setFireRate(int fireRate) {
      this.fireRate = fireRate;
      return this;
    }

    public Properties setDamage(int damage) {
      this.damage = damage;
      return this;
    }

    public Properties setReloadDurationTicks(int reloadDurationTicks) {
      this.reloadDurationTicks = reloadDurationTicks;
      return this;
    }

    public Properties setBulletAmountToFire(int amount) {
      this.bulletAmountToFire = amount;
      return this;
    }

    public Properties setAimable(boolean aimable) {
      this.aimable = aimable;
      return this;
    }

    public Properties setCrosshair(boolean crosshair) {
      this.crosshair = crosshair;
      return this;
    }

    public Properties setAccuracy(float accuracy) {
      this.accuracy = accuracy;
      return this;
    }

    public Properties addFireMode(FireMode fireMode) {
      this.fireModes.add(fireMode);
      return this;
    }

    public Properties setShootSound(Supplier<SoundEvent> shootSound) {
      this.shootSound = shootSound;
      return this;
    }

    public Properties setSilencedShootSound(Supplier<SoundEvent> silencedShootSound) {
      this.silencedShootSound = silencedShootSound;
      return this;
    }

    public Properties setReloadSound(Supplier<SoundEvent> reloadSound) {
      this.reloadSound = reloadSound;
      return this;
    }

    public Properties addAnimation(AnimationType type, Supplier<IAnimation> animation) {
      this.animations.put(type, animation);
      return this;
    }

    public Properties addAcceptedMagazine(Supplier<MagazineItem> acceptedMagazine) {
      this.acceptedMagazines.add(acceptedMagazine);
      return this;
    }

    public Properties addAcceptedAttachment(Supplier<AttachmentItem> acceptedAttachment) {
      this.acceptedAttachments.add(acceptedAttachment);
      return this;
    }

    public Properties addDefaultAttachment(Supplier<AttachmentItem> defaultAttachment) {
      // Default attachments are also accepted attachments
      this.addAcceptedAttachment(defaultAttachment);
      this.defaultAttachments.add(defaultAttachment);
      return this;
    }

    public Properties addAcceptedPaint(Supplier<PaintItem> acceptedPaint) {
      this.acceptedPaints.add(acceptedPaint);
      return this;
    }
  }
}