package org.sobadfish.fishbasket.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.fishbasket.FishBasketMainClass;
import org.sobadfish.fishbasket.config.WaterDBArea;
import org.sobadfish.fishbasket.form.push.PlayerDeleteWaterAreaForm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 水域相关设置
 * */
public class FishBasketCommand extends Command {

    public FishBasketMainClass mainClass;

    public FishBasketCommand(FishBasketMainClass mainClass) {
        super("fsh");
        this.mainClass = mainClass;
    }

    public Map<String, WaterDBArea> waterDBAreaMap = new LinkedHashMap<>();


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player player){
            if(player.isOp()){
                if(args.length > 0){
                    switch (args[0]){
                        case "help":
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ehelp &a查看指令帮助",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &esave [名称] &a将手持物品保存到tag文件",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ewa pos1 &a设置当前位置为水域起点坐标",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ewa pos2 &a设置当前位置为水域终点坐标",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ewa create [名称] &a创建水域",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ewa remove [名称] &a移除水域",player);
                            FishBasketMainClass.sendMessageToObject("&7/"+getName()+" &ewa see &a查看所有水域",player);
                            break;
                        case "save":
                            if(args.length > 1){
                                String name = args[1];
                                Item item = player.getInventory().getItemInHand();
                                if(item.getId() != 0){
                                    mainClass.configManager.saveItemToTagFile(name,item);
                                    FishBasketMainClass.sendMessageToObject("&a已保存物品至"+name,player);
                                }else{
                                    FishBasketMainClass.sendMessageToObject("&c无法保存空气",player);
                                }
                            }else{
                                FishBasketMainClass.sendMessageToObject("&c/fsh help 查看帮助",player);
                            }
                            break;
                        case "wa":
                            if(args.length > 1){
                                switch (args[1]){
                                    case "pos1":
                                        WaterDBArea waterDBArea = new WaterDBArea();
                                        waterDBArea.start_x = player.getPosition().getFloorX();
                                        waterDBArea.start_z = player.getPosition().getFloorZ();
                                        waterDBArea.level_name = player.getLevel().getFolderName();
                                        waterDBAreaMap.put(player.getName(),waterDBArea);
                                        FishBasketMainClass.sendMessageToObject("&a已设置起点坐标 x="+waterDBArea.start_x+" z="+waterDBArea.start_z,player);
                                        break;
                                    case "pos2":
                                        if(waterDBAreaMap.containsKey(player.getName())){
                                            WaterDBArea wb = waterDBAreaMap.get(player.getName());
                                            if(!wb.level_name.equalsIgnoreCase(player.level.getFolderName())){
                                                FishBasketMainClass.sendMessageToObject("&c两个点不在同一张地图",player);
                                                return true;
                                            }

                                            wb.end_x = player.getPosition().getFloorX();
                                            wb.end_z = player.getPosition().getFloorZ();
                                            wb.id = UUID.randomUUID().toString();
                                            FishBasketMainClass.sendMessageToObject("&a已设置终点坐标 x="+wb.end_x + " z="+wb.end_z,player);
                                        }else{
                                            FishBasketMainClass.sendMessageToObject("&c请先设置起点坐标",player);
                                        }
                                        break;
                                    case "create":
                                        if(args.length > 2){
                                            if(waterDBAreaMap.containsKey(player.getName())){
                                                WaterDBArea wb = waterDBAreaMap.get(player.getName());
                                                if(wb.id == null || wb.id.isEmpty()){
                                                    FishBasketMainClass.sendMessageToObject("&c请先设置终点坐标",player);
                                                    return true;
                                                }
                                                Position ps = new Position(wb.start_x,player.getPosition().getY(),wb.start_z,
                                                        Server.getInstance().getLevelByName(wb.level_name));
                                                WaterDBArea waterDBArea1 = mainClass.waterAreaManager.getWaterAreaOnLocation(ps);
                                                if(waterDBArea1 == null){
                                                    wb.name = args[2];
                                                    wb.display_name = args[2];
                                                    mainClass.waterAreaManager.saveWaterDBArea(wb);
                                                    FishBasketMainClass.sendMessageToObject("&a已创建水域 "+args[2],player);

                                                }else{
                                                    FishBasketMainClass.sendMessageToObject("存在重叠水域 "+waterDBArea1.name,player);
                                                }

                                            }else{
                                                FishBasketMainClass.sendMessageToObject("&c请先设置起点坐标",player);
                                            }
                                        }else{
                                            FishBasketMainClass.sendMessageToObject("&c/fsh help 查看帮助",player);
                                        }
                                        break;
                                    case "remove":
                                        if(args.length > 2){
                                            List<WaterDBArea> waterDBAreas = mainClass.waterAreaManager.getWaterAreasByName(args[2]);
                                            if(!waterDBAreas.isEmpty()){
                                                PlayerDeleteWaterAreaForm form = new PlayerDeleteWaterAreaForm(waterDBAreas,player);
                                                mainClass.formManager.addForm(player, form);
                                            }else{
                                                FishBasketMainClass.sendMessageToObject("&c没有任何名称为 "+args[2]+" 的水域",player);
                                            }
                                        }else{
                                            FishBasketMainClass.sendMessageToObject("&c/fsh help 查看帮助",player);
                                        }

                                        break;
                                    case "see":
                                        List<WaterDBArea> waterDBAreas = mainClass.waterAreaManager.getWaterAreas();
                                        for (WaterDBArea waterDBArea1 : waterDBAreas){
                                            FishBasketMainClass.sendMessageToObject("&a水域名称: &7"+waterDBArea1.name+" *&e位置: &r"+waterDBArea1.start_x+":"+waterDBArea1.start_z ,player);
                                        }
                                        break;
                                }

                            }else{
                                FishBasketMainClass.sendMessageToObject("&c/fsh help 查看帮助",player);
                            }
                            break;
                        default:
                            break;
                    }
                }else{
                    FishBasketMainClass.sendMessageToObject("&c/fsh help 查看帮助",player);
                }
            }
        }

        return true;
    }
}
