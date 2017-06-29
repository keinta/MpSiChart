package com.seeweekend.code.charting.utils;

import android.content.Context;

/**
 * xyvals 这个类也是自定义的数据，处理XY标签的，MpYlabeView 父类 mpYlabeView 子类 处理自定义Y轴的视图
 * 这个类用于识别当前所用图型框架的处理方式，它可以把所需要设置的地方进行
 * 关联和解偶，减少查找类和方法的难度； 可在扩充圆型图区块图，柱状图的样式,把所有参数都写在
 * 并且这个类一定要写在VIEW 界面，这样才好把视图和很多杂乱的实现相互关联
 */
public class MpChartSetdate {
        public MpChartSetdate(Context context) {
        drawCubicBezieType = defaultType;
        drawLineType = defaultType;
        drawValeType = defaultType;
        drawPointType = defaultType;
        drawYLineType = defaultType;
        drawXlineType = defaultType;
        drawYTextType = defaultType;
        drawXTextType = defaultType;
    }
    public static final String tempYsetkey = "℃";
    public static final String HumYsetkey = "%";
    public static final String AirYsetkey = "ppb";
    public static  int XlineStype = 20170315;
    public  static int YlabTextSizeNull=0;


    public static int getYlabTextSizeUnit(Context context){
     return   15;
    }

    public static final int tempmax = 80;
    public static final int watermax = 100;
    public static final int airlevemax = 1000;

  //  mpLinechartActivity 下面三个参数主要与此类相关
    public static final int daytype = 2001;
    public static final int wenktype = 2005;
    public static final int montype = 2006;

    public static final int mpLinechartActivitykey = 2017323;

    public static final int editSleppTYPE=201620102;
    public static final int defaultType = -13;
    public  static final int YSLEEPTYPE =20170220;
    //如果是隐藏点的话 默认是 -1 ，最少不能少于 -10，不然不会认为是重新开始
    public  static final int MAXBottn =-10;
    public  static final int MAXBottnTwo =-11;
    //曲线样式
    public int drawCubicBezieType ;
    //直线样式： 1，舒适要区分 舒适为 2，其它先不管
    public int drawLineType ;
    //显示的数据样式
    public int drawValeType ;
    //显示的点样式
    public int drawPointType ;
    //Y轴 线的样式
    public int drawYLineType ;
    //X轴 线的样式
    public int drawXlineType ;
    //Y轴 文字样式
    public int drawYTextType;
    //X轴 文字样式
    public int drawXTextType ;
    //当前所选择的点
    public int mynowindex=-1;
}
