package com.seeweekend.code.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class MpYlabeView extends LinearLayout {
    private float[] yVels;
    private String[] ylabes;
    private Context context;
    private static int stye = 0;
    /**
     * @param i 默认为0，1 表示控制界面
     */
    public static void setStye(int i) {
        stye = i;
    }
    public int getTextsize() {
        return textsize;
    }

    public void setTextsize(int textsize) {
        this.textsize = textsize;
        if (mAxisLabelPaint != null) {
            mAxisLabelPaint.setTextSize(20);
        }
    }

    private int textsize = 10;
    public MpYlabeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mAxisLabelPaint = new Paint();
        mAxisLabelPaint.setTextSize(20);
        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setAntiAlias(true);
    }

    protected Paint mAxisLabelPaint;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (ylabes != null && ylabes.length > 0) {
            for (int i = 0; i < ylabes.length; i++) {
                if (stye == 1 && (Integer.valueOf(ylabes[i]) == 0 || Integer.valueOf(ylabes[i]) == 50 || Integer.valueOf(ylabes[i]) == 80 || Integer.valueOf(ylabes[i]) == 100)) {
                    canvas.drawText(ylabes[i], 1, yVels[i], mAxisLabelPaint);
                } else if (stye == 0) {
                    canvas.drawText(ylabes[i], 1, yVels[i], mAxisLabelPaint);
                }
            }
        }
    }

    public void DrawYlabeView(float[] yVels, String[] ylabes) {
        this.ylabes = ylabes;
        this.yVels = yVels;
        invalidate();
    }

}
