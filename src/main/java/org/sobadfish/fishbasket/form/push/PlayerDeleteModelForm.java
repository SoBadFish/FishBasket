package org.sobadfish.fishbasket.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponseModal;
import org.sobadfish.fishbasket.FishBasketMainClass;
import org.sobadfish.fishbasket.config.WaterDBArea;
import org.sobadfish.fishbasket.form.CustomModelForm;

public class PlayerDeleteModelForm extends CustomModelForm {

    public WaterDBArea waterDBArea;

    public PlayerDeleteModelForm(WaterDBArea waterDBArea, Player playerInfo) {
        super("删除水域", "", "确认删除", "我再想想", playerInfo);
        this.waterDBArea = waterDBArea;
    }

    @Override
    public void callback(FormResponseModal response) {
        if(response.getClickedButtonId() == 0){
            FishBasketMainClass.getINSTANCE().waterAreaManager.deleteWaterArea(waterDBArea);
        }
    }

    @Override
    public void onCreateView() {
        setContent("确定要删除水域 "+waterDBArea.name+" 吗?");
    }

    @Override
    public boolean isCanRemove() {
        return true;
    }
}
