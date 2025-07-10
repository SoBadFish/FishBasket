package org.sobadfish.fishbasket;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.fishbasket.commands.FishBasketCommand;
import org.sobadfish.fishbasket.entitys.FishBasketEntity;
import org.sobadfish.fishbasket.form.ICustomForm;
import org.sobadfish.fishbasket.items.FishBasketItem;
import org.sobadfish.fishbasket.items.FishFoodItem;
import org.sobadfish.fishbasket.manager.ConfigManager;
import org.sobadfish.fishbasket.manager.FormManager;
import org.sobadfish.fishbasket.manager.WaterAreaManager;
import org.sobadfish.fishbasket.panel.DisplayPlayerPanel;
import org.sobadfish.fishbasket.panel.lib.AbstractFakeInventory;

public class FishBasketMainClass extends PluginBase implements Listener {

    private static final String PLUGIN_NAME = "&e[捕鱼篓]";

    public static FishBasketMainClass INSTANCE;

    public ConfigManager configManager;

    public WaterAreaManager waterAreaManager;

    public FormManager formManager;

    @Override
    public void onLoad() {
        EntityManager.get().registerDefinition(FishBasketEntity.DEF_FISH_BASKET);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        Item.registerCustomItem(FishBasketItem.class,true);
        Item.registerCustomItem(FishFoodItem.class,true);
        saveDefaultConfig();
        reloadConfig();
        checkServer();
        formManager = new FormManager();
        configManager = new ConfigManager(this);
        configManager.init();
        waterAreaManager = new WaterAreaManager(this);
        waterAreaManager.init();
        this.getServer().getCommandMap().register("fishbasket", new FishBasketCommand(this));
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    public static FishBasketMainClass getINSTANCE() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof FishBasketEntity){

            if(event.getPlayer().isSneaking()){
                Item hand = event.getPlayer().getInventory().getItemInHand();
                if(hand.getId() == 0) {
                    //收起 要掉落鱼
                    entity.close();
                    event.getPlayer().getInventory().addItem(new FishBasketItem());
                }else if(hand instanceof FishFoodItem){
                    // 添加鱼饵
                    ((FishBasketEntity) entity).addFishFood(hand.getCount());
                    event.getPlayer().getInventory().removeItem(hand);
                }
            }else{
                DisplayPlayerPanel displayPlayerPanel = new DisplayPlayerPanel((FishBasketEntity) entity);
                displayPlayerPanel.displayPlayer(event.getPlayer(), "鱼篓");
            }
        }

    }

    public static String CORE_NAME = "";

    private void checkServer(){
        boolean ver = false;
        //双核心兼容
        CORE_NAME = "Nukkit";
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;
            CORE_NAME = "Nukkit PM1E";


        } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            CORE_NAME = c.getField("NUKKIT").get(c).toString();

            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }


        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            Server.getInstance().enableExperimentMode = true;
            Server.getInstance().forceResources = true;
        }
        sendMessageToConsole("&e当前核心为 "+CORE_NAME);
    }

    public static void sendMessageToObject(String msg, Object o){
        String message = TextFormat.colorize('&',PLUGIN_NAME+" &r"+msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                    return;
                }
            }
            if(o instanceof EntityHuman){
                message = ((EntityHuman) o).getName()+"->"+message;
            }
        }
        INSTANCE.getLogger().info(message);

    }

    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }

    @EventHandler
    public void onFormListener(PlayerFormRespondedEvent event){
        Player player = event.getPlayer();
        ICustomForm<? extends FormResponse> customForm = formManager.getFrom(player.getName());
        if(customForm == null) return;
        if (event.wasClosed()) {
            //防止误触其他的GUI
            if(customForm.isRunnable()){
                formManager.removeForm(player.getName());
                return;
            }
            return;
        }

        if(!player.isOnline()){
            return;
        }

        if(event.getFormID() == customForm.getFormId()){
            if(customForm.isRunnable()){
                return;
            }
            FormResponse response = event.getResponse();
            if(response != null) {
                customForm.setRunnable(true);
                try {
                    customForm.callbackData(response);
                } catch (ClassCastException e) {
                    System.err.println("表单响应类型不匹配: " + e.getMessage());
                }
                if(customForm.isCanRemove()){
                    formManager.removeForm(player.getName());
                }
            }
        }

    }

}
