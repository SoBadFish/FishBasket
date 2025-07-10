package org.sobadfish.fishbasket.entitys;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityClimateVariant;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.EntityDefinition;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import org.sobadfish.fishbasket.FishBasketMainClass;
import org.sobadfish.fishbasket.items.FishFoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishBasketEntity extends Entity implements CustomEntity,EntityClimateVariant{

//
    public int fishModel = 0;

    public long lastTime = 0;
    /**
     * 鱼饵数量
     * */
    public int fishFood;

    public String master = "";

    public ChestInventory inventory = new ChestInventory(null);


    public static final EntityDefinition DEF_FISH_BASKET =
            EntityDefinition
                    .builder()
                    .identifier("fishbasket:basket")
                    //.summonable(true)
                    .spawnEgg(true)
                    .implementation(FishBasketEntity.class)
                    .build();




    public FishBasketEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return DEF_FISH_BASKET;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("variant")) {
            this.setVariant(Variant.get(this.namedTag.getString("variant")));
        } else {
            this.setVariant(this.getBiomeVariant(this.getLevel().getBiomeId(this.getFloorX(), this.getFloorZ())));
        }

        if (this.namedTag.contains("fishModel")) {
            this.setFishModel(this.namedTag.getInt("fishModel"));
        }
        if (this.namedTag.contains("fishFood")) {
            this.fishFood = this.namedTag.getInt("fishFood");
        }
        if(this.namedTag.contains("fishItems")){
            if(inventory == null){
                inventory = new ChestInventory(null);
            }
            for(CompoundTag tag: this.namedTag.getList("fishItems",CompoundTag.class).getAll()){
                inventory.addItem(NBTIO.getItemHelper(tag));
            }
        }
        if(this.namedTag.contains("master")){
            this.master = this.namedTag.getString("master");
        }

        if(this.namedTag.contains("lastTime")){
            this.lastTime = this.namedTag.getLong("lastTime");
        }


        setFishModel(0);

        this.setNameTagAlwaysVisible();
        this.setNameTagVisible();
    }


    @Override
    public boolean onUpdate(int currentTick) {
        boolean b = super.onUpdate(currentTick);
        boolean isWorkingWater = isWorkingWater();
        boolean fish = false;
        if(isWorkingWater && fishFood > 0 && fishFood >= FishBasketMainClass.INSTANCE.configManager.needFood) {
            //判断周围3格都是水域
            if(lastTime == 0){
                lastTime = System.currentTimeMillis();
            }
            int[] randomTime = FishBasketMainClass.INSTANCE.configManager.getRoundTime();
            if (System.currentTimeMillis() - lastTime > (Utils.rand(randomTime[0],randomTime[1])) * 1000L) {
                if (fishFood > 0 && fishFood >= FishBasketMainClass.INSTANCE.configManager.needFood) {
                    fishFood -= FishBasketMainClass.INSTANCE.configManager.needFood;
                    lastTime = System.currentTimeMillis();
                    fish = true;
                }
            }
        }
        if (fish) {
            //不能影响主线程 扔子线程
            Position location = this.getPosition();
            Item random = FishBasketMainClass.getINSTANCE().waterAreaManager.getRoundItem(location);
            addFishToInventory(random);
        }
        if(inventory != null) {
            setFishModel(Math.min(28, inventory.slots.size()));
        }else{
            setFishModel(0);
        }
        //隐藏功能 如果附近有掉落物 则直接吸附进去
        if(!close) {
            for (Entity dropEntity : level.getEntities()) {
                if (dropEntity instanceof EntityItem drop) {
                    if (dropEntity.distance(this) < 1.5) {
                        if (addFishToInventory(drop.getItem())) {
                            dropEntity.close();
                        }
                    }
                }
            }
        }

        //更新名称
        String name = "";
        if(isWorkingWater){
            if(fishFood > 0 && fishFood >= FishBasketMainClass.INSTANCE.configManager.needFood){
                name += " &a捕获中..&r\n&6鱼饵 &e* &2"+fishFood;
            }else{
                name += " &c鱼饵不足！\n&6鱼饵 &e* &c" + fishFood;
            }
        }else{
            name += " &c鱼篓必须有3*3水域";
        }

        setNameTag(TextFormat.colorize('&',name));
        return b;
    }

    public void addFishFood(int num){
        this.fishFood += num;
    }

    public boolean isWorkingWater(){
        if(isInsideOfWater()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (!this.getLevel().getBlock((int) this.x + i, (int) this.y, (int) this.z + j).isWater()) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }



    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    public boolean addFishToInventory(Item item){
        if(item.getId() != 0){
            return inventory.addItem(item).length == 0;
        }
        return false;
    }



    public List<Item> getFishItems(){
        return new ArrayList<>(inventory.slots.values());
    }
    boolean close;

    @Override
    public void close() {
        close = true;
        for(Item item: this.getFishItems()){
            level.dropItem(this,item);
        }
        this.inventory.clearAll();
        //掉落鱼饵
        if(this.fishFood > 0){
            FishFoodItem fishFoodItem = new FishFoodItem();
            fishFoodItem.setCount(fishFood);
            fishFood = 0;
            level.dropItem(this,fishFoodItem);

        }
        super.close();

    }

    @Override
    public float getWidth() {
        return 1;
    }


    @Override
    public float getHeight() {
        return 1;
    }

    public void setFishModel(int hasFish){
        this.fishModel = hasFish;
//        this.setDataFlag(0, 9, hasFish);

        this.setDataProperty(new IntEntityData(2,hasFish));
    }

    @Override
    public int getNetworkId() {
        return getEntityDefinition().getRuntimeId();
    }

    @Override
    public void saveNBT() {
        this.namedTag.putInt("fishModel", fishModel);
        this.namedTag.putInt("fishFood", this.fishFood);
        ListTag<CompoundTag> tag = new ListTag<>();
        for(Map.Entry<Integer,Item> entry: this.inventory.slots.entrySet()){
            tag.add(NBTIO.putItemHelper(entry.getValue(),entry.getKey()));
        }
        this.namedTag.putList("fishItems",tag);
        this.namedTag.putLong("lastTime", this.lastTime);
        this.namedTag.putString("master", this.master);
        super.saveNBT();

    }
}
