package org.sobadfish.fishbasket.items;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.fishbasket.entitys.FishBasketEntity;

public class FishBasketItem extends ItemCustom {
    public FishBasketItem() {
        super("minecraft:fish_basket", "鱼篓", "fb_basket");
    }


    public int scaleOffset() {
        return 16; // 需要是16的倍数，如 32、64、128
    }

    @Override
    public CustomItemDefinition getDefinition() {

        return CustomItemDefinition
                .simpleBuilder(this, ItemCreativeCategory.ITEMS)
                .creativeGroup(ItemCreativeGroup.CHEST)
                .handEquipped(true)
                .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
                .build();
    }



    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (block instanceof BlockWater) {
            CompoundTag tag = Entity.getDefaultNBT(block);
            tag.putString("master", player.getName());
            FishBasketEntity fishBasket = new FishBasketEntity(player.getChunk(),tag
                            );
             if (!player.isCreative()) {
                --this.count;
            }
            fishBasket.spawnToAll();
            return true;
        } else {
            return false;
        }
    }
}
