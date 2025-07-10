package org.sobadfish.fishbasket.manager;

import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import org.sobadfish.fishbasket.FishBasketMainClass;
import org.sobadfish.fishbasket.config.WaterDBArea;
import org.sobadfish.fishbasket.db.SqliteHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.*;

/**
 * 管理水域
 * */
public class WaterAreaManager {

    public FishBasketMainClass mainClass;


    public SqliteHelper sqliteHelper;


    /**
     * 加载水域对应的奖励
     * */
    public Map<String,List<String>> waterAreaItemList = new LinkedHashMap<>();


    public WaterAreaManager(FishBasketMainClass mainClass){
        this.mainClass = mainClass;
    }


    public void init(){
        initDB();
        loadWaterArea();
    }

    private void loadWaterArea() {
        mainClass.saveResource("water_area.yml",false);
        Config config = new Config(mainClass.getDataFolder()+"/water_area.yml", Config.YAML);
        Map<?,?> map = (Map<?, ?>) config.get("water_area");
        if(map != null){
            for(Object key : map.keySet()){
                String name = (String) key;
                Object list_ = map.get(key);
                List<String> items = new ArrayList<>();
                if(list_ instanceof List<?> list){
                    for(Object string : list) {
                        String[] split = string.toString().split("~");
                        int r = 1;
                        if(split.length > 1){
                            r = Integer.parseInt(split[1]);
                        }
                        for (int i = 0; i < r; i++) {
                            items.add(split[0]);
                        }
                    }
                }
                waterAreaItemList.put(name,items);
            }
        }
    }

    public WaterDBArea getWaterAreaOnLocation(Position pos){
        if(sqliteHelper != null){
            List<WaterDBArea> waterDBAreas = sqliteHelper.getDataByString(WaterDBArea.TABLE_NAME,"start_x >= ? and end_x <= ? and start_z >= ? and end_z <= ? and level_name = ?",
                    new String[]{
                            pos.getX()+"",
                            pos.getX()+"",
                            pos.getZ()+"",
                            pos.getZ()+"",
                            pos.getLevel().getFolderName(),
                    },WaterDBArea.class);
            if(!waterDBAreas.isEmpty()){
                return waterDBAreas.get(0);
            }
        }
        return null;
    }



    public Item getRoundItem(Position position){
        WaterDBArea waterDBArea = getWaterAreaOnLocation(position);
        List<String> roundItems = mainClass.configManager.roundItems;
        if(waterDBArea != null){
            if(waterAreaItemList.containsKey(waterDBArea.name)) {
                roundItems = waterAreaItemList.get(waterDBArea.name);
            }
        }
        //roundItems 列表随机打乱
        if(!roundItems.isEmpty()){
            Collections.shuffle(roundItems);
            String itemStr = roundItems.get(Utils.rand(0, roundItems.size() - 1));
            if(mainClass.configManager.tagItems.containsKey(itemStr)){
                String tag = mainClass.configManager.tagItems.get(itemStr).tagItem;
                try {
                    return NBTIO.getItemHelper(NBTIO.read(Base64.getDecoder().decode(tag)));
                } catch (IOException e) {
                    return Item.get(0);
                }

            }
            return Item.fromString(itemStr);
        }

        return Item.get(0);
    }


    private void initDB() {
        //初始化物品数据数据库
        try {
            sqliteHelper = new SqliteHelper(mainClass.getDataFolder()+"/data.db");
        } catch (ClassNotFoundException | SQLException e) {
            FishBasketMainClass.sendMessageToConsole(e.getLocalizedMessage());
        }

        if(sqliteHelper != null){
            if(!sqliteHelper.exists(WaterDBArea.TABLE_NAME)){
                sqliteHelper.addTable(WaterDBArea.TABLE_NAME, SqliteHelper.DBTable.asDbTable(WaterDBArea.class));
            }else{
                chunkDb();
            }
        }


    }

    private void chunkDb(){
        //检查DB
        if(sqliteHelper != null){
            List<String> columns = sqliteHelper.getColumns(WaterDBArea.TABLE_NAME);
            Field[] fd = WaterDBArea.class.getFields();
            for (Field field : fd){
                if(Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                if(!columns.contains(field.getName())){
                    //新增...
                    sqliteHelper.addColumns(WaterDBArea.TABLE_NAME,field.getName().toLowerCase(),field);
                    mainClass.getLogger().info("检测到新字段 "+field.getName()+" 正在写入数据库...");

                }
            }
        }
    }


    public void saveWaterDBArea(WaterDBArea wb) {
        if(sqliteHelper != null){
            sqliteHelper.add(WaterDBArea.TABLE_NAME,wb);
        }
    }

    /**
     * 获取名称一样的水域
     * */
    public List<WaterDBArea> getWaterAreasByName(String name) {
        List<WaterDBArea> waterDBAreas = new ArrayList<>();
        if(sqliteHelper != null){
            waterDBAreas = sqliteHelper.getDataByString(
                    WaterDBArea.TABLE_NAME,
                    "name = ?",
                    new String[]{name},
                    WaterDBArea.class);
        }
        return waterDBAreas;
    }

    public void deleteWaterArea(WaterDBArea waterDBArea) {
        if(sqliteHelper != null){
            sqliteHelper.remove(WaterDBArea.TABLE_NAME,waterDBArea.id);
        }
    }

    public List<WaterDBArea> getWaterAreas() {
        List<WaterDBArea> waterDBAreas = new ArrayList<>();
        if(sqliteHelper != null){
            waterDBAreas = sqliteHelper.getAll(WaterDBArea.TABLE_NAME,WaterDBArea.class);
        }
        return waterDBAreas;
    }
}
