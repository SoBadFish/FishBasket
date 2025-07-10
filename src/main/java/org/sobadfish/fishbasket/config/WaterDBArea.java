package org.sobadfish.fishbasket.config;

/**
 * 数据库存储的区域 由于可能会很多 所以采用数据库查询的形式实现 减少不必要的开支
 * */
public class WaterDBArea {

    public static final String TABLE_NAME = "water_info";
    /**
     * 名字可以重复 但是要有UUID唯一值划定
     * */
    public String id;


    public String name = "";

    /**
     * 显示名称
     * */
    public String display_name = "";

    /**
     * 起点
     * */
    public double start_x = 0.0;


    public double start_z = 0.0;

    /**
     * 终点 
     * */
    public double end_x = 0.0;

    public double end_z = 0.0;

    /**
     * 所在地图
     * */
    public String level_name;




}
