package org.sobadfish.fishbasket.panel;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.fishbasket.entitys.FishBasketEntity;
import org.sobadfish.fishbasket.panel.lib.AbstractFakeInventory;
import org.sobadfish.fishbasket.panel.lib.ChestFakeInventory;
import org.sobadfish.fishbasket.panel.lib.FishBaskFakeInventory;
import org.sobadfish.fishbasket.panel.lib.IDisplayPanel;

/**
 * @author Sobadfish
 * @date 2023/11/20
 */
public class DisplayPlayerPanel implements InventoryHolder, IDisplayPanel {

    public AbstractFakeInventory inventory;


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public FishBasketEntity fishbasket;

    public DisplayPlayerPanel(FishBasketEntity fishbasket){
        this.fishbasket = fishbasket;
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public FishBasketEntity getFishBaskEntity() {
        return fishbasket;
    }




    @Override
    public void close() {
        if (inventory != null) {
            //保存当前物品到配置
            ChestFakeInventory chestPanel = (ChestFakeInventory) inventory;
            chestPanel.close(chestPanel.getPlayer());

        }
    }


    public void displayPlayer(Player player, String name) {

        FishBaskFakeInventory panel = new FishBaskFakeInventory(player, this, name);
        panel.setContents(getFishBaskEntity().inventory.getContents());
        panel.setPlayer(player);
        panel.id = ++Entity.entityCount;
        panel.fishBasketEntity = fishbasket;
        inventory = panel;
        player.addWindow(panel);

    }
}

