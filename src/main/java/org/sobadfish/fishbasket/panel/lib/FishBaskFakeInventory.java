package org.sobadfish.fishbasket.panel.lib;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import org.sobadfish.fishbasket.entitys.FishBasketEntity;


/**
 * @author Sobadfish
 * @date 2024/4/6
 */
public class FishBaskFakeInventory extends ChestFakeInventory implements InventoryHolder {

    public long id;


    public FishBasketEntity fishBasketEntity;




    public FishBaskFakeInventory(Player player, InventoryHolder holder, String name) {
        super(InventoryType.CHEST,holder,name);
        this.player = player;
        this.setName(name);

    }



    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.DOUBLE_CHEST.getNetworkType();
        who.dataPacket(pk);

    }







    @Override
    public void onClose(Player who) {
        fishBasketEntity.inventory.setContents(this.getContents());
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);



    }

    @Override
    public Inventory getInventory() {
        return this;
    }

}
