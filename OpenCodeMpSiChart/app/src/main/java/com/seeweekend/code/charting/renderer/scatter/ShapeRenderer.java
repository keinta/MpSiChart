package com.seeweekend.code.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.seeweekend.code.charting.buffer.ScatterBuffer;
import com.seeweekend.code.charting.charts.ScatterChart;
import com.seeweekend.code.charting.interfaces.datasets.IScatterDataSet;
import com.seeweekend.code.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:07
 */
public interface ShapeRenderer {

    /**
     * Renders the provided ScatterDataSet with a shape.
     *
     * @param c
     * @param dataSet
     * @param viewPortHandler
     * @param buffer
     * @param renderPaint
     * @param shapeSize
     */
    void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, ScatterBuffer buffer, Paint
            renderPaint, final float shapeSize);
}
