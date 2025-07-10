package org.sobadfish.fishbasket.panel.lib;

import cn.nukkit.Player;
import org.sobadfish.fishbasket.entitys.FishBasketEntity;

/**
 * @author Sobadfish
 * @date 2024/4/6
 */
public interface IDisplayPanel {

    void close();

    void open(Player player);

    FishBasketEntity getFishBaskEntity();

}
