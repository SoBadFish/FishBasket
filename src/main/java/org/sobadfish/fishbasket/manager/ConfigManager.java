package org.sobadfish.fishbasket.manager;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import org.sobadfish.fishbasket.FishBasketMainClass;

import java.io.IOException;
import java.util.*;

public class ConfigManager {

    public FishBasketMainClass mainClass;

    public List<String> roundItems = new ArrayList<>();

    public int needFood;

    public LinkedHashMap<String,TagItemData> tagItems = new LinkedHashMap<>();


    public ConfigManager(FishBasketMainClass mainClass){
        this.mainClass = mainClass;
    }

    public void init(){
        List<String> strings = mainClass.getConfig().getStringList("fish-list");
        for(String string : strings) {
            String[] split = string.split("~");
            int r = 1;
            if(split.length > 1){
                r = Integer.parseInt(split[1]);
            }
            for (int i = 0; i < r; i++) {
                roundItems.add(split[0]);
            }
        }
        mainClass.saveResource("nbt_item.yml",false);
        //加载
        Config config = new Config(mainClass.getDataFolder()+"/nbt_item.yml",Config.YAML);
        Map<?,?> tagItems = (Map<?, ?>) config.get("tagItem");
        if(tagItems != null){
            for(Object key : tagItems.keySet()){
                this.tagItems.put(key.toString(),new TagItemData(key.toString(),tagItems.get(key).toString()));
            }
        }
        needFood = getNeedFishFood();
    }

    public int getNeedFishFood(){
        return mainClass.getConfig().getInt("working-food",1);
    }

    public int[] getRoundTime(){
        String time = mainClass.getConfig().getString("fish-get-time","1~10");
        String[] timeArray = time.split("~");
        int[] roundTime = new int[2];
        try {
            roundTime[0] = Integer.parseInt(timeArray[0].trim());
            roundTime[1] = Integer.parseInt(timeArray[1].trim());
        }catch (Exception e){
            FishBasketMainClass.sendMessageToConsole("&c配置文件错误，请检查配置文件: "+e.getMessage());
            return new int[]{10,20};
        }

        return roundTime;
    }




    public void saveItemToTagFile(String tag,Item item){
        try {
            String nbt = Base64.getEncoder().encodeToString(NBTIO.write(NBTIO.putItemHelper(item)));
            tagItems.put(tag,new TagItemData(tag,nbt));
            saveFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFile() {
        Config config = new Config(mainClass.getDataFolder()+"/nbt_item.yml",Config.YAML);
        LinkedHashMap<String,String> configMap = new LinkedHashMap<>();
        for(TagItemData tagItemData : tagItems.values()){
            configMap.put(tagItemData.name,tagItemData.tagItem);
        }
        config.set("tagItem",configMap);
        config.save();

    }

    public static class TagItemData{
        public String name;

        public String tagItem;

        public TagItemData(String name, String tagItem) {
            this.name = name;
            this.tagItem = tagItem;
        }


    }
}
