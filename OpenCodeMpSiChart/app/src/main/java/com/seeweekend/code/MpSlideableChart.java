package com.seeweekend.code;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.seeweekend.code.charting.charts.LineChart;
import com.seeweekend.code.charting.components.LimitLine;
import com.seeweekend.code.charting.components.YAxis;
import com.seeweekend.code.charting.data.Entry;
import com.seeweekend.code.charting.data.LineData;
import com.seeweekend.code.charting.data.LineDataSet;
import com.seeweekend.code.charting.data.xyvals;
import com.seeweekend.code.charting.highlight.Highlight;
import com.seeweekend.code.charting.interfaces.datasets.ILineDataSet;
import com.seeweekend.code.charting.listener.ChartTouchListener;
import com.seeweekend.code.charting.listener.OnChartGestureListener;
import com.seeweekend.code.charting.listener.OnChartValueSelectedListener;
import com.seeweekend.code.charting.utils.MpChartSetdate;
import com.seeweekend.code.charting.utils.Transformer;

/**
 * author Keinta Powered by (SK开源) seeweekend.com/code on 2017/6/29.
 * email: hronyt@foxmail.com
 */
public class MpSlideableChart implements OnChartGestureListener, OnChartValueSelectedListener {
    private ArrayList<Sleepdate> sleep1_linedate;//最后将这个添加到原数组里面
    private static final int controltag = 31;
    private static final int hourUtil = 2;//4
    private final static int MunitUtils = 30;//15
    private Transformer mytrans1;
    private LineChart mChart1;
    private int screenWidth, maxwidth;
  
    private Activity context;
    private View rootView;
    private final int skipleng = 1;
    private final float PointCR = 5f;
    private int maxpoint = 70;
    public MpSlideableChart(View v, Activity context) {
        this.rootView = v;
        this.context = context;
    }
    public void myonCreate() {
        sleep1_linedate = new ArrayList<>();
        mChart1 = (LineChart) rootView.findViewById(R.id.mytempchart);
      //  mChart1.setNoDataText(context.getResources().getString(R.string.isnulldate) + "");
        mChart1.getXAxis().setAxisLineWidth(0.3f);
        mChart1.getXAxis().setAxisLineColor(Color.TRANSPARENT);
        mChart1.getAxisLeft().setAxisLineWidth(0.2f);//少于 0.5F就画不出来了
        screenWidth = widthPixels(context);
        maxwidth = screenWidth * 3;
        showChartView(mChart1,getTestData());
    }
    public static int widthPixels(Activity activity) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int widthPixels = mDisplayMetrics.widthPixels;
		return widthPixels;
	}
    private ArrayList<Sleepdate> getTestData(){
        ArrayList<Sleepdate> sleepdateList=new ArrayList<>();
        float[] tmeplist=new float[]{28,20,25,23,27,29,18,25,28,20,19,29,20,24,23,28};
        for (int i = 0; i < tmeplist.length; i++) {
            Sleepdate sleepdate=new Sleepdate(tmeplist[i], 7, 30, "","2017-6-29 10:30:33");
            sleepdateList.add(sleepdate);
        }
        return sleepdateList;
    }
    //如果初始化进定来的是添加，就起点为00，终点为24.00 在进来时就把数据给写好,这里是画点的数据
    private synchronized void showChartView(LineChart mChart, final ArrayList<Sleepdate> sleepdate) {
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setLogEnabled(false);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHorizontalScrollBarEnabled(true);
        mChart.setHorizontalFadingEdgeEnabled(true);
        mChart.setScrollContainer(true);
        if (sleepdate != null) {
            setLinedate(mChart, sleepdate);
        }
    }
    //这里把画Y轴的值改为画横线，Y值需要自定义一个布局在左边去实现它固定好就行了
    private  final int LeftYlabWith=-5;
    private synchronized void setLinedate(LineChart mChart, final List<Sleepdate> listdate) {
        mChart.setHighlightPerDragEnabled(false);
        float max = 100;
        float min = 0;
        int getcolor =Color.GREEN;
        int tolsize = listdate.size();
        if (listdate == null || listdate.size() == 0) {
            return;
        }
        float[] getyvels = new float[tolsize];
        String[] times = new String[tolsize];
        for (int i = 0; i < listdate.size(); i++) {
            getyvels[i] =listdate.get (i).getTemperature();//float) controltag;
            times[i] =listdate.get(i).getHour()+":"+listdate.get(i).getMinute();// "time";
        }
        int resize = listdate.size() / skipleng;
        float maxwith = getmaxwith(resize);
        LinearLayout.LayoutParams layoutParamsfram = new LinearLayout.LayoutParams((int) maxwith,
                LinearLayout.LayoutParams.MATCH_PARENT);
        maxwidth=(int)maxwith;
        mChart1.setLayoutParams(layoutParamsfram);
        mChart.getXAxis().setLabelsToSkip(0);//skipleng
        mChart.getXAxis().setTextSize(9);
        // 自定义一个Y 坐杯的显示方式
        LimitLine lmin = new LimitLine(min, "");
        lmin.setLineWidth(1f);
        lmin.enableDashedLine(10f, 10f, 0f);
        lmin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lmin.setLineColor(Color.GRAY);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(lmin);
        leftAxis.setAxisMaxValue(max);
        leftAxis.setAxisMinValue(min);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        xyvals xyval = new xyvals(MpChartSetdate.YSLEEPTYPE, "0", LeftYlabWith + "");
        List<xyvals> myyvals = new ArrayList<>();
        myyvals.add(xyval);
        leftAxis.setMyYvals(myyvals);
        mChart.getAxisLeft().setTextSize(MpChartSetdate.YlabTextSizeNull);
        mChart.getAxisRight().setEnabled(false);
        setTData(mChart, getcolor, getyvels, times);
    }

    private void setTData(LineChart mChart, final int colors, float[] getyvels,  final String[] times) {
        ArrayList<String> xVals = new ArrayList<String>();
       // ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Entry> yValsline = new ArrayList<Entry>();
        for (int i = 0; i < getyvels.length; i++) {
            String Xveltv = times[i].trim();
            if (Xveltv.endsWith("30")) {
                Xveltv = "";
            }
            xVals.add(Xveltv);//这是X轴的内容
            yValsline.add(new Entry(chicklistdate(i, getyvels), i, new String[]{times[i], (int) getyvels[i] + "", null, "null"}));
        }
        LineDataSet setline;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            setline = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            setline.setYVals(yValsline);
            setline.setColor(colors);
            setline.setDrawFilled(true);
            setline.setHighlightEnabled(false);
            setline.setHighLightColor(Color.TRANSPARENT);
            if (setline.mpChartSetdate == null) {
                setline.mpChartSetdate = new MpChartSetdate(context);
                setline.mpChartSetdate.drawLineType = 2;
            }
            setline.setLineWidth(1f);
            setline.setCircleRadius(0f);
            setline.setValueTextSize(7f);
            setline.setDrawValues(false);
            setline.setDrawCircles(false);
            setline.setMode(LineDataSet.Mode.CUBIC_BEZIER);//LineDataSet.Mode.CUBIC_BEZIER
            mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            setline = new LineDataSet(yValsline, "temperature line");
            setline.setColor(colors);
            setline.setLineWidth(1f);
            setline.setCircleRadius(0f);
            setline.setValueTextSize(7f);
            setline.setDrawFilled(true);
            setline.setDrawValues(false);
            setline.setDrawCircles(false);
            setline.setHighlightEnabled(false);
            setline.setHighLightColor(Color.TRANSPARENT);
            setline.setMode(LineDataSet.Mode.CUBIC_BEZIER);//LineDataSet.Mode.CUBIC_BEZIER STEPPED
            if (setline.mpChartSetdate == null) {
                setline.mpChartSetdate = new MpChartSetdate(context);
                setline.mpChartSetdate.drawLineType = 2;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.lchartbk_stye);
                setline.setFillDrawable(drawable);
            } else {
                setline.setFillColor(colors);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setline);
            LineData data = new LineData(xVals, dataSets);
            mChart.setData(data);
            mytrans1 = mChart.getTransformer(mChart.getLineData().getDataSets().get(0).getAxisDependency());
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        mChart.invalidate();
    }
    //传 数据列表 长度的 五分之一
    private float getmaxwith(int resize) {
        float remax = (screenWidth / 10f) * resize;
        if (remax > screenWidth * 3.5f) {
            remax = screenWidth * 3.5f;
        }
        if (remax < screenWidth) {
            remax = screenWidth;
        }
        return remax; // 最多七个屏幕的数据，华为最多画五个 屏幕 超过就画不出来，一个屏幕显示七个小时  screenWidth * 7
    }

    /**
     * @param index 当前值的位置
     * @param ylist 原数组
     * @return 合格值
     */
    private static float chicklistdate(int index, float[] ylist) {
        if (ylist[index] >= 0) return ylist[index];
        for (int length = index; length > 0; length--) {
            if (ylist[length] >= 0) {
                return ylist[length];
            }
        }
        for (int i = index; i < ylist.length; i++) {
            if (ylist[i] >= 0) {
                return ylist[i];
            }
        }
        return -10;
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (getTrandt() == null) {
            return;
        }
    }

    private float scrollx = -1555;
    @Override
    public void onChartGestureMove(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (scrollx != -1555) {
            float mov = me.getX() - scrollx;
            if (mov > 1 || mov < -1) {
                float movleng = mChart1.getScrollX() - mov;
                if (movleng < 0) {
                    movleng = 0;
                }
                if (movleng >= 0 && movleng <= (maxwidth-screenWidth+50)  && movleng != mChart1.getScaleX()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        mChart1.setScrollX((int) (movleng));
                    }
                }
            }
        }
        scrollx = me.getX();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        scrollx = -1555;
        if (getTrandt() != null) {
            setLinedate(getmart(), getSleep());
        }
    }

    private ArrayList<Sleepdate> getSleep() {
        return   sleep1_linedate;
    }
    private synchronized LineChart getmart() {
        return mChart1;
    }
    private Transformer getTrandt() {
        return mytrans1;
    }
    @Override
    public void onChartLongPressed(MotionEvent me) {
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
    }
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
    }
    @Override
    public void onNothingSelected() {
    }
}
