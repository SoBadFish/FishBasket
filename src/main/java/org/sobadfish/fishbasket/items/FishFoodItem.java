package org.sobadfish.fishbasket.items;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;

public class FishFoodItem extends ItemCustom {
    public FishFoodItem() {
        super("minecraft:fish_food", "鱼饵", "fb_yver");
    }


    public int scaleOffset() {
        return 32; // 需要是16的倍数，如 32、64、128
    }

    @Override
    public CustomItemDefinition getDefinition() {

        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.ITEMS)
                .creativeGroup(ItemCreativeGroup.MISC_FOOD)
                .handEquipped(true)
                .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
                .build();
    }



    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
