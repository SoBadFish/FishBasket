package org.sobadfish.fishbasket.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.fishbasket.FishBasketMainClass;
import org.sobadfish.fishbasket.config.WaterDBArea;
import org.sobadfish.fishbasket.form.CustomButtonForm;

import java.util.List;

public class PlayerDeleteWaterAreaForm extends CustomButtonForm {

    public List<WaterDBArea> waterDBAreas;

    public PlayerDeleteWaterAreaForm(List<WaterDBArea> waterDBAreas, Player playerInfo) {
        super("移除水域", "", playerInfo);
        this.waterDBAreas = waterDBAreas;
    }

    @Override
    public void callback(FormResponseSimple response) {
        int index = response.getClickedButtonId();
        if(index >= waterDBAreas.size()){
            return;
        }
        WaterDBArea waterDBArea = waterDBAreas.get(index);
        PlayerDeleteModelForm form = new PlayerDeleteModelForm(waterDBArea, playerInfo);
        FishBasketMainClass.INSTANCE.formManager.addForm(playerInfo, form);
    }

    @Override
    public void onCreateView() {
        setContent("请选择要移除的水域");
        for (WaterDBArea area : waterDBAreas) {
            addButton(new ElementButton(TextFormat.colorize('&',
                    area.display_name+"&r\nx: "
                    +area.start_x+"z: "+area.start_z+" ~ x: "
                    +area.end_x+"z: "+area.end_z)
                    ,new ElementButtonImageData("path","textures/blocks/water_placeholder")));
        }

    }

    @Override
    public boolean isCanRemove() {
        return false;
    }
}
