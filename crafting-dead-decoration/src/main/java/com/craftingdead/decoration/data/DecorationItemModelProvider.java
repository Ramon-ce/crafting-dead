package com.craftingdead.decoration.data;

import com.craftingdead.decoration.world.item.DecorationItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DecorationItemModelProvider extends ItemModelProvider {

  public DecorationItemModelProvider(DataGenerator generator, String modid,
      ExistingFileHelper existingFileHelper) {
    super(generator, modid, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    this.withExistingParent(DecorationItems.CLOTHING_RACK.getId().toString(),
        this.modLoc("block/clothing_rack_inventory"));
  }
}
