
package com.seeweekend.code.charting.charts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.seeweekend.code.charting.components.XAxis.XAxisPosition;
import com.seeweekend.code.charting.components.YAxis;
import com.seeweekend.code.charting.components.YAxis.AxisDependency;
import com.seeweekend.code.charting.data.BarData;
import com.seeweekend.code.charting.data.BarEntry;
import com.seeweekend.code.charting.data.BarLineScatterCandleBubbleData;
import com.seeweekend.code.charting.data.Entry;
import com.seeweekend.code.charting.highlight.ChartHighlighter;
import com.seeweekend.code.charting.highlight.Highlight;
import com.seeweekend.code.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.seeweekend.code.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.seeweekend.code.charting.jobs.AnimatedMoveViewJob;
import com.seeweekend.code.charting.jobs.AnimatedZoomJob;
import com.seeweekend.code.charting.jobs.MoveViewJob;
import com.seeweekend.code.charting.jobs.ZoomJob;
import com.seeweekend.code.charting.listener.BarLineChartTouchListener;
import com.seeweekend.code.charting.listener.OnDrawListener;
import com.seeweekend.code.charting.renderer.XAxisRenderer;
import com.seeweekend.code.charting.renderer.YAxisRenderer;
import com.seeweekend.code.charting.utils.PointD;
import com.seeweekend.code.charting.utils.Transformer;
import com.seeweekend.code.charting.utils.Utils;


/**
 * Base-class of LineChart, BarChart, ScatterChart and CandleStickChart.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("RtlHardcoded")
public abstract class BarLineChartBase<T extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>>
        extends Chart<T> implements BarLineScatterCandleBubbleDataProvider {
    private static int mBorderColor= Color.GRAY;
    private float mBorderWidth=1f;
    /**
     * the maximum number of entries to which values will be drawn
     * (entry numbers greater than this value will cause value-labels to disappear)
     */
    protected int mMaxVisibleCount = 100;

    /**
     * flag that indicates if auto scaling on the y axis is enabled
     */
    private boolean mAutoScaleMinMaxEnabled = false;
    private Integer mAutoScaleLastLowestVisibleXIndex = null;
    private Integer mAutoScaleLastHighestVisibleXIndex = null;

    /**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    protected boolean mPinchZoomEnabled = false;

    /**
     * flag that indicates if double tap zoom is enabled or not
     */
    protected boolean mDoubleTapToZoomEnabled = true;

    /**
     * flag that indicates if highlighting per dragging over a fully zoomed out
     * chart is enabled
     */
    protected boolean mHighlightPerDragEnabled = true;

    /**
     * flag that indicates whether the highlight should be full-bar oriented, or single-value?
     */
    protected boolean mHighlightFullBarEnabled = false;

    /**
     * if true, dragging is enabled for the chart
     */
    private boolean mDragEnabled = true;

    private boolean mScaleXEnabled = true;
    private boolean mScaleYEnabled = true;

    /**
     * paint object for the (by default) lightgrey background of the grid
     */
    protected Paint mGridBackgroundPaint;

    protected Paint mBorderPaint;

    /**
     * flag indicating if the grid background should be drawn or not
     */
    protected boolean mDrawGridBackground = false;

    protected boolean mDrawBorders = false;

    /**
     * Sets the minimum offset (padding) around the chart, defaults to 15
     */
    protected float mMinOffset = 15.f;

    /**
     * flag indicating if the chart should stay at the same position after a rotation. Default is false.
     */
    protected boolean mKeepPositionOnRotation = false;

    /**
     * the listener for user drawing on the chart
     */
    protected OnDrawListener mDrawListener;

    /**
     * the object representing the labels on the left y-axis
     */
    protected YAxis mAxisLeft;

    /**
     * the object representing the labels on the right y-axis
     */
    protected YAxis mAxisRight;

    protected YAxisRenderer mAxisRendererLeft;
    protected YAxisRenderer mAxisRendererRight;

    protected Transformer mLeftAxisTransformer;
    protected Transformer mRightAxisTransformer;

    protected XAxisRenderer mXAxisRenderer;

    // /** the approximator object used for data filtering */
    // private Approximator mApproximator;

    public BarLineChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BarLineChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarLineChartBase(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();

        mAxisLeft = new YAxis(AxisDependency.LEFT);
        mAxisRight = new YAxis(AxisDependency.RIGHT);

        mLeftAxisTransformer = new Transformer(mViewPortHandler);
        mRightAxisTransformer = new Transformer(mViewPortHandler);

        mAxisRendererLeft = new YAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mAxisRendererRight = new YAxisRenderer(mViewPortHandler, mAxisRight, mRightAxisTransformer);

        mXAxisRenderer = new XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);

        setHighlighter(new ChartHighlighter(this));

        mChartTouchListener = new BarLineChartTouchListener(this, mViewPortHandler.getMatrixTouch());

        mGridBackgroundPaint = new Paint();
        mGridBackgroundPaint.setStyle(Style.FILL);
        // mGridBackgroundPaint.setColor(Color.WHITE);
        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
        // grey
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(mBorderWidth));
    }

    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;
    public static int LineChatHeight = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null)
            return;
        LineChatHeight = getHeight();
        calcModulus();
        mXAxisRenderer.calcXBounds(this, mXAxis.mAxisLabelModulus);
        mRenderer.calcXBounds(this, mXAxis.mAxisLabelModulus);
        // execute all drawing commands
        drawGridBackground(canvas);

        if (mAxisLeft.isEnabled())
            mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        if (mAxisRight.isEnabled())
            mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        mXAxisRenderer.renderAxisLine(canvas);
        mAxisRendererLeft.renderAxisLine(canvas);
        mAxisRendererRight.renderAxisLine(canvas);
        if (mAutoScaleMinMaxEnabled) {
            final int lowestVisibleXIndex = getLowestVisibleXIndex();
            final int highestVisibleXIndex = getHighestVisibleXIndex();
            if (mAutoScaleLastLowestVisibleXIndex == null ||
                    mAutoScaleLastLowestVisibleXIndex != lowestVisibleXIndex ||
                    mAutoScaleLastHighestVisibleXIndex == null ||
                    mAutoScaleLastHighestVisibleXIndex != highestVisibleXIndex) {
                calcMinMax();
                calculateOffsets();
                mAutoScaleLastLowestVisibleXIndex = lowestVisibleXIndex;
                mAutoScaleLastHighestVisibleXIndex = highestVisibleXIndex;
            }
        }
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mViewPortHandler.getContentRect());
        mXAxisRenderer.renderGridLines(canvas);
        mAxisRendererLeft.renderGridLines(canvas);
        mAxisRendererRight.renderGridLines(canvas);

        if (mXAxis.isDrawLimitLinesBehindDataEnabled())
            mXAxisRenderer.renderLimitLines(canvas);

        if (mAxisLeft.isDrawLimitLinesBehindDataEnabled())
            mAxisRendererLeft.renderLimitLines(canvas);

        if (mAxisRight.isDrawLimitLinesBehindDataEnabled())
            mAxisRendererRight.renderLimitLines(canvas);

        mRenderer.drawData(canvas);

        // if highlighting is enabled
        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        mRenderer.drawExtras(canvas);

        clipRestoreCount = canvas.save();
        canvas.clipRect(mViewPortHandler.getContentRect());

        if (!mXAxis.isDrawLimitLinesBehindDataEnabled())
            mXAxisRenderer.renderLimitLines(canvas);

        if (!mAxisLeft.isDrawLimitLinesBehindDataEnabled())
            mAxisRendererLeft.renderLimitLines(canvas);

        if (!mAxisRight.isDrawLimitLinesBehindDataEnabled())
            mAxisRendererRight.renderLimitLines(canvas);
        canvas.restoreToCount(clipRestoreCount);
        mXAxisRenderer.renderAxisLabels(canvas);
        mAxisRendererLeft.renderAxisLabels(canvas);
        mAxisRendererRight.renderAxisLabels(canvas);
        drawMarkers(canvas);

    }

    /**
     * RESET PERFORMANCE TRACKING FIELDS
     */
    public void resetTracking() {
        totalTime = 0;
        drawCycles = 0;
    }

    protected void prepareValuePxMatrix() {

        if (mLogEnabled)
            Log.i(LOG_TAG, "Preparing Value-Px Matrix, xmin: " + mXAxis.mAxisMinimum + ", xmax: "
                    + mXAxis.mAxisMaximum + ", xdelta: " + mXAxis.mAxisRange);

        mRightAxisTransformer.prepareMatrixValuePx(mXAxis.mAxisMinimum,
                mXAxis.mAxisRange,
                mAxisRight.mAxisRange,
                mAxisRight.mAxisMinimum);
        mLeftAxisTransformer.prepareMatrixValuePx(mXAxis.mAxisMinimum,
                mXAxis.mAxisRange,
                mAxisLeft.mAxisRange,
                mAxisLeft.mAxisMinimum);
    }

    protected void prepareOffsetMatrix() {

        mRightAxisTransformer.prepareMatrixOffset(mAxisRight.isInverted());
        mLeftAxisTransformer.prepareMatrixOffset(mAxisLeft.isInverted());
    }

    @Override
    public void notifyDataSetChanged() {

        if (mData == null) {
            if (mLogEnabled)
                Log.i(LOG_TAG, "Preparing... DATA NOT SET.");
            return;
        } else {
            if (mLogEnabled)
                Log.i(LOG_TAG, "Preparing...");
        }

        if (mRenderer != null)
            mRenderer.initBuffers();

        calcMinMax();

        mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        mXAxisRenderer.computeAxis(mData.getXValMaximumLength(), mData.getXVals());

        if (mLegend != null)
            mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    protected void calcMinMax() {

        if (mAutoScaleMinMaxEnabled)
            mData.calcMinMax(getLowestVisibleXIndex(), getHighestVisibleXIndex());

        // calculate / set x-axis range
        mXAxis.mAxisMaximum = mData.getXVals().size() - 1;
        mXAxis.mAxisRange = Math.abs(mXAxis.mAxisMaximum - mXAxis.mAxisMinimum);

        // calculate axis range (min / max) according to provided data
        mAxisLeft.calculate(mData.getYMin(AxisDependency.LEFT), mData.getYMax(AxisDependency.LEFT));
        mAxisRight.calculate(mData.getYMin(AxisDependency.RIGHT), mData.getYMax(AxisDependency
                .RIGHT));
    }

    protected void calculateLegendOffsets(RectF offsets) {
        offsets.left = 0.f;
        offsets.right = 0.f;
        offsets.top = 0.f;
        offsets.bottom = 0.f;

        // setup offsets for legend
        if (mLegend != null && mLegend.isEnabled() && !mLegend.isDrawInsideEnabled()) {
            switch (mLegend.getOrientation()) {
                case VERTICAL:

                    switch (mLegend.getHorizontalAlignment()) {
                        case LEFT:
                            offsets.left += Math.min(mLegend.mNeededWidth,
                                    mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
                                    + mLegend.getXOffset();
                            break;

                        case RIGHT:
                            offsets.right += Math.min(mLegend.mNeededWidth,
                                    mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
                                    + mLegend.getXOffset();
                            break;

                        case CENTER:

                            switch (mLegend.getVerticalAlignment()) {
                                case TOP:
                                    offsets.top += Math.min(mLegend.mNeededHeight,
                                            mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                            + mLegend.getYOffset();

                                    if (getXAxis().isEnabled() && getXAxis().isDrawLabelsEnabled())
                                        offsets.top += getXAxis().mLabelRotatedHeight;
                                    break;

                                case BOTTOM:
                                    offsets.bottom += Math.min(mLegend.mNeededHeight,
                                            mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                            + mLegend.getYOffset();

                                    if (getXAxis().isEnabled() && getXAxis().isDrawLabelsEnabled())
                                        offsets.bottom += getXAxis().mLabelRotatedHeight;
                                    break;

                                default:
                                    break;
                            }
                    }

                    break;

                case HORIZONTAL:

                    switch (mLegend.getVerticalAlignment()) {
                        case TOP:
                            offsets.top += Math.min(mLegend.mNeededHeight,
                                    mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                    + mLegend.getYOffset();

                            if (getXAxis().isEnabled() && getXAxis().isDrawLabelsEnabled())
                                offsets.top += getXAxis().mLabelRotatedHeight;
                            break;

                        case BOTTOM:
                            offsets.bottom += Math.min(mLegend.mNeededHeight,
                                    mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                    + mLegend.getYOffset();

                            if (getXAxis().isEnabled() && getXAxis().isDrawLabelsEnabled())
                                offsets.bottom += getXAxis().mLabelRotatedHeight;
                            break;

                        default:
                            break;
                    }
                    break;
            }
        }
    }

    private RectF mOffsetsBuffer = new RectF();

    @Override
    public void calculateOffsets() {

        if (!mCustomViewPortEnabled) {

            float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

            calculateLegendOffsets(mOffsetsBuffer);

            offsetLeft += mOffsetsBuffer.left;
            offsetTop += mOffsetsBuffer.top;
            offsetRight += mOffsetsBuffer.right;
            offsetBottom += mOffsetsBuffer.bottom;

            // offsets for y-labels
            if (mAxisLeft.needsOffset()) {
                offsetLeft += mAxisLeft.getRequiredWidthSpace(mAxisRendererLeft
                        .getPaintAxisLabels());
            }

            if (mAxisRight.needsOffset()) {
                offsetRight += mAxisRight.getRequiredWidthSpace(mAxisRendererRight
                        .getPaintAxisLabels());
            }

            if (mXAxis.isEnabled() && mXAxis.isDrawLabelsEnabled()) {

                float xlabelheight = mXAxis.mLabelRotatedHeight + mXAxis.getYOffset();

                // offsets for x-labels
                if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

                    offsetBottom += xlabelheight;

                } else if (mXAxis.getPosition() == XAxisPosition.TOP) {

                    offsetTop += xlabelheight;

                } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

                    offsetBottom += xlabelheight;
                    offsetTop += xlabelheight;
                }
            }

            offsetTop += getExtraTopOffset();
            offsetRight += getExtraRightOffset();
            offsetBottom += getExtraBottomOffset();
            offsetLeft += getExtraLeftOffset();

            float minOffset = Utils.convertDpToPixel(mMinOffset);

            mViewPortHandler.restrainViewPort(
                    Math.max(minOffset, offsetLeft),
                    Math.max(minOffset, offsetTop),
                    Math.max(minOffset, offsetRight),
                    Math.max(minOffset, offsetBottom));

        
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    /**
     * calculates the modulus for x-labels and grid
     */
    protected void calcModulus() {

        if (mXAxis == null || !mXAxis.isEnabled())
            return;

        if (!mXAxis.isAxisModulusCustom()) {

            float[] values = new float[9];//10
            mViewPortHandler.getMatrixTouch().getValues(values);

            mXAxis.mAxisLabelModulus = (int) Math
                    .ceil((mData.getXValCount() * mXAxis.mLabelRotatedWidth)
                            / (mViewPortHandler.contentWidth() * values[Matrix.MSCALE_X]));

        }

        if (mLogEnabled)
      
        if (mXAxis.mAxisLabelModulus < 1)
            mXAxis.mAxisLabelModulus = 1;
    }

    @Override
    protected float[] getMarkerPosition(Entry e, Highlight highlight) {

        int dataSetIndex = highlight.getDataSetIndex();
        float xPos = e.getXIndex();
        float yPos = e.getVal();

        if (this instanceof BarChart) {

            BarData bd = (BarData) mData;
            float space = bd.getGroupSpace();
            int setCount = mData.getDataSetCount();
            int i = e.getXIndex();

            if (this instanceof HorizontalBarChart) {

                // calculate the x-position, depending on datasetcount
                float y = i + i * (setCount - 1) + dataSetIndex + space * i + space / 2f;

                yPos = y;

                BarEntry entry = (BarEntry) e;
                if (entry.getVals() != null) {
                    xPos = highlight.getRange().to;
                } else {
                    xPos = e.getVal();
                }

                xPos *= mAnimator.getPhaseY();
            } else {

                float x = i + i * (setCount - 1) + dataSetIndex + space * i + space / 2f;

                xPos = x;

                BarEntry entry = (BarEntry) e;
                if (entry.getVals() != null) {
                    yPos = highlight.getRange().to;
                } else {
                    yPos = e.getVal();
                }

                yPos *= mAnimator.getPhaseY();
            }
        } else {
            yPos *= mAnimator.getPhaseY();
        }

        // position of the marker depends on selected value index and value
        float[] pts = new float[]{
                xPos, yPos
        };

        getTransformer(mData.getDataSetByIndex(dataSetIndex).getAxisDependency())
                .pointValuesToPixel(pts);

        return pts;
    }

    /**
     * draws the grid background
     */
    protected void drawGridBackground(Canvas c) {

        if (mDrawGridBackground) {

            // draw the grid background
            c.drawRect(mViewPortHandler.getContentRect(), mGridBackgroundPaint);
        }

        if (mDrawBorders) {
            c.drawRect(mViewPortHandler.getContentRect(), mBorderPaint);
        }
    }

    /**
     * Returns the Transformer class that contains all matrices and is
     * responsible for transforming values into pixels on the screen and
     * backwards.
     *
     * @return
     */
    public Transformer getTransformer(AxisDependency which) {
        if (which == AxisDependency.LEFT)
            return mLeftAxisTransformer;
        else
            return mRightAxisTransformer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (mChartTouchListener == null || mData == null)
            return false;

        // check if touch gestures are enabled
        if (!mTouchEnabled)
            return false;
        else
            return mChartTouchListener.onTouch(this, event);
    }

    @Override
    public void computeScroll() {

        if (mChartTouchListener instanceof BarLineChartTouchListener)
            ((BarLineChartTouchListener) mChartTouchListener).computeScroll();
    }


    public void zoomIn() {

        PointF center = mViewPortHandler.getContentCenter();

        Matrix save = mViewPortHandler.zoomIn(center.x, -center.y);
        mViewPortHandler.refresh(save, this, false);

        calculateOffsets();
        postInvalidate();
    }

    /**
     * Zooms out by 0.7f, from the charts center. center.
     */
    public void zoomOut() {

        PointF center = mViewPortHandler.getContentCenter();

        Matrix save = mViewPortHandler.zoomOut(center.x, -center.y);
        mViewPortHandler.refresh(save, this, false);

        calculateOffsets();
        postInvalidate();
    }


    public void zoom(float scaleX, float scaleY, float x, float y) {
        Matrix save = mViewPortHandler.zoom(scaleX, scaleY, x, y);
        mViewPortHandler.refresh(save, this, false);

        calculateOffsets();
        postInvalidate();
    }

    public void zoom(float scaleX, float scaleY, float xValue, float yValue, AxisDependency axis) {

        Runnable job = new ZoomJob(mViewPortHandler, scaleX, scaleY, xValue, yValue, getTransformer(axis), axis, this);
        addViewportJob(job);
    }


    @TargetApi(11)
    public void zoomAndCenterAnimated(float scaleX, float scaleY, float xValue, float yValue, AxisDependency axis, long duration) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            PointD origin = getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), axis);

            Runnable job = new AnimatedZoomJob(mViewPortHandler, this, getTransformer(axis), getAxis(axis), mXAxis.getValues().size(), scaleX, scaleY, mViewPortHandler.getScaleX(), mViewPortHandler.getScaleY(), xValue, yValue, (float) origin.x, (float) origin.y, duration);
            addViewportJob(job);

        } else {
            Log.e(LOG_TAG, "Unable to execute zoomAndCenterAnimated(...) on API level < 11");
        }
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    public void fitScreen() {
        Matrix save = mViewPortHandler.fitScreen();
        mViewPortHandler.refresh(save, this, false);

        calculateOffsets();
        postInvalidate();
    }

    public void setScaleMinima(float scaleX, float scaleY) {
        mViewPortHandler.setMinimumScaleX(scaleX);
        mViewPortHandler.setMinimumScaleY(scaleY);
    }


    public void setVisibleXRangeMaximum(float maxXRange) {
        float xScale = mXAxis.mAxisRange / (maxXRange);
        mViewPortHandler.setMinimumScaleX(xScale);
    }


    public void setVisibleXRangeMinimum(float minXRange) {
        float xScale = mXAxis.mAxisRange / (minXRange);
        mViewPortHandler.setMaximumScaleX(xScale);
    }


    public void setVisibleXRange(float minXRange, float maxXRange) {
        float maxScale = mXAxis.mAxisRange / minXRange;
        float minScale = mXAxis.mAxisRange / maxXRange;
        mViewPortHandler.setMinMaxScaleX(minScale, maxScale);
    }


    public void setVisibleYRangeMaximum(float maxYRange, AxisDependency axis) {
        float yScale = getDeltaY(axis) / maxYRange;
        mViewPortHandler.setMinimumScaleY(yScale);
    }

    public void moveViewToX(float xIndex) {

        Runnable job = new MoveViewJob(mViewPortHandler, xIndex, 0f,
                getTransformer(AxisDependency.LEFT), this);

        addViewportJob(job);
    }


    public void moveViewToY(float yValue, AxisDependency axis) {

        float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();

        Runnable job = new MoveViewJob(mViewPortHandler, 0f, yValue + valsInView / 2f,
                getTransformer(axis), this);

        addViewportJob(job);
    }


    public void moveViewTo(float xIndex, float yValue, AxisDependency axis) {

        float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();

        Runnable job = new MoveViewJob(mViewPortHandler, xIndex, yValue + valsInView / 2f,
                getTransformer(axis), this);

        addViewportJob(job);
    }


    @TargetApi(11)
    public void moveViewToAnimated(float xIndex, float yValue, AxisDependency axis, long duration) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            PointD bounds = getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), axis);

            float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();

            Runnable job = new AnimatedMoveViewJob(mViewPortHandler, xIndex, yValue + valsInView / 2f,
                    getTransformer(axis), this, (float) bounds.x, (float) bounds.y, duration);

            addViewportJob(job);
        } else {
            Log.e(LOG_TAG, "Unable to execute moveViewToAnimated(...) on API level < 11");
        }
    }


    public void centerViewTo(float xIndex, float yValue, AxisDependency axis) {

        float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();
        float xsInView = getXAxis().getValues().size() / mViewPortHandler.getScaleX();

        Runnable job = new MoveViewJob(mViewPortHandler,
                xIndex - xsInView / 2f, yValue + valsInView / 2f,
                getTransformer(axis), this);

        addViewportJob(job);
    }


    @TargetApi(11)
    public void centerViewToAnimated(float xIndex, float yValue, AxisDependency axis, long duration) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            PointD bounds = getValuesByTouchPoint(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), axis);

            float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();
            float xsInView = getXAxis().getValues().size() / mViewPortHandler.getScaleX();

            Runnable job = new AnimatedMoveViewJob(mViewPortHandler,
                    xIndex - xsInView / 2f, yValue + valsInView / 2f,
                    getTransformer(axis), this, (float) bounds.x, (float) bounds.y, duration);

            addViewportJob(job);
        } else {
            Log.e(LOG_TAG, "Unable to execute centerViewToAnimated(...) on API level < 11");
        }
    }

    /**
     * flag that indicates if a custom viewport offset has been set
     */
    private boolean mCustomViewPortEnabled = false;

    public void setViewPortOffsets(final float left, final float top,
                                   final float right, final float bottom) {

        mCustomViewPortEnabled = true;
        post(new Runnable() {

            @Override
            public void run() {

                mViewPortHandler.restrainViewPort(left, top, right, bottom);
                prepareOffsetMatrix();
                prepareValuePxMatrix();
            }
        });
    }

    /**
     * Resets all custom offsets set via setViewPortOffsets(...) method. Allows
     * the chart to again calculate all offsets automatically.
     */
    public void resetViewPortOffsets() {
        mCustomViewPortEnabled = false;
        calculateOffsets();
    }


    public float getDeltaY(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mAxisLeft.mAxisRange;
        else
            return mAxisRight.mAxisRange;
    }

    /**
     * Sets the OnDrawListener
     *
     * @param drawListener
     */
    public void setOnDrawListener(OnDrawListener drawListener) {
        this.mDrawListener = drawListener;
    }

    /**
     * Gets the OnDrawListener. May be null.
     *
     * @return
     */
    public OnDrawListener getDrawListener() {
        return mDrawListener;
    }


    public PointF getPosition(Entry e, AxisDependency axis) {

        if (e == null)
            return null;

        float[] vals = new float[]{
                e.getXIndex(), e.getVal()
        };

        getTransformer(axis).pointValuesToPixel(vals);

        return new PointF(vals[0], vals[1]);
    }

    public void setMaxVisibleValueCount(int count) {
        this.mMaxVisibleCount = count;
    }

    public int getMaxVisibleCount() {
        return mMaxVisibleCount;
    }


    public void setHighlightPerDragEnabled(boolean enabled) {
        mHighlightPerDragEnabled = enabled;
    }

    public boolean isHighlightPerDragEnabled() {
        return mHighlightPerDragEnabled;
    }


    public void setHighlightFullBarEnabled(boolean enabled) {
        mHighlightFullBarEnabled = enabled;
    }

    /**
     * @return true the highlight is be full-bar oriented, false if single-value
     */
    public boolean isHighlightFullBarEnabled() {
        return mHighlightFullBarEnabled;
    }


    public void setGridBackgroundColor(int color) {
        mGridBackgroundPaint.setColor(color);
    }


    public void setDragEnabled(boolean enabled) {
        this.mDragEnabled = enabled;
    }

    /**
     * Returns true if dragging is enabled for the chart, false if not.
     *
     * @return
     */
    public boolean isDragEnabled() {
        return mDragEnabled;
    }


    public void setScaleEnabled(boolean enabled) {
        this.mScaleXEnabled = enabled;
        this.mScaleYEnabled = enabled;
    }

    public void setScaleXEnabled(boolean enabled) {
        mScaleXEnabled = enabled;
    }

    public void setScaleYEnabled(boolean enabled) {
        mScaleYEnabled = enabled;
    }

    public boolean isScaleXEnabled() {
        return mScaleXEnabled;
    }

    public boolean isScaleYEnabled() {
        return mScaleYEnabled;
    }

    /**
     * Set this to true to enable zooming in by double-tap on the chart.
     * Default: enabled
     *
     * @param enabled
     */
    public void setDoubleTapToZoomEnabled(boolean enabled) {
        mDoubleTapToZoomEnabled = enabled;
    }

    /**
     * Returns true if zooming via double-tap is enabled false if not.
     *
     * @return
     */
    public boolean isDoubleTapToZoomEnabled() {
        return mDoubleTapToZoomEnabled;
    }

    /**
     * set this to true to draw the grid background, false if not
     *
     * @param enabled
     */
    public void setDrawGridBackground(boolean enabled) {
        mDrawGridBackground = enabled;
    }

    /**
     * Sets drawing the borders rectangle to true. If this is enabled, there is
     * no point drawing the axis-lines of x- and y-axis.
     *
     * @param enabled
     */
    public void setDrawBorders(boolean enabled) {
        mDrawBorders = enabled;
    }

    /**
     * Sets the width of the border lines in dp.
     *
     * @param width
     */
    public void setBorderWidth(float width) {
        mBorderWidth=width;
        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(width));
    }

    /**
     * Sets the color of the chart border lines.
     *
     * @param color
     */
    public void setBorderColor(int color) {
        mBorderColor=color;
        mBorderPaint.setColor(color);
    }

    /**
     * Gets the minimum offset (padding) around the chart, defaults to 15.f
     */
    public float getMinOffset() {
        return mMinOffset;
    }

    /**
     * Sets the minimum offset (padding) around the chart, defaults to 15.f
     */
    public void setMinOffset(float minOffset) {
        mMinOffset = minOffset;
    }

    /**
     * Returns true if keeping the position on rotation is enabled and false if not.
     */
    public boolean isKeepPositionOnRotation() {
        return mKeepPositionOnRotation;
    }

    /**
     * Sets whether the chart should keep its position (zoom / scroll) after a rotation (orientation change)
     */
    public void setKeepPositionOnRotation(boolean keepPositionOnRotation) {
        mKeepPositionOnRotation = keepPositionOnRotation;
    }


    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        } else
            return getHighlighter().getHighlight(x, y);
    }


    public PointD getValuesByTouchPoint(float x, float y, AxisDependency axis) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        getTransformer(axis).pixelsToValue(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];

        return new PointD(xTouchVal, yTouchVal);
    }


    public PointD getPixelsForValues(float x, float y, AxisDependency axis) {

        float[] pts = new float[]{
                x, y
        };

        getTransformer(axis).pointValuesToPixel(pts);

        return new PointD(pts[0], pts[1]);
    }


    public float getYValueByTouchPoint(float x, float y, AxisDependency axis) {
        return (float) getValuesByTouchPoint(x, y, axis).y;
    }


    public Entry getEntryByTouchPoint(float x, float y) {
        Highlight h = getHighlightByTouchPoint(x, y);
        if (h != null) {
            return mData.getEntryForHighlight(h);
        }
        return null;
    }


    public IBarLineScatterCandleBubbleDataSet getDataSetByTouchPoint(float x, float y) {
        Highlight h = getHighlightByTouchPoint(x, y);
        if (h != null) {
            return mData.getDataSetByIndex(h.getDataSetIndex());
        }
        return null;
    }


    @Override
    public int getLowestVisibleXIndex() {
        float[] pts = new float[]{
                mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom()
        };
        getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
        return (pts[0] <= 0) ? 0 : (int) Math.ceil(pts[0]);
    }


    @Override
    public int getHighestVisibleXIndex() {
        float[] pts = new float[]{
                mViewPortHandler.contentRight(), mViewPortHandler.contentBottom()
        };
        getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
        return Math.min(mData.getXValCount() - 1, (int) Math.floor(pts[0]));
    }

    /**
     * returns the current x-scale factor
     */
    public float getScaleX() {
        if (mViewPortHandler == null)
            return 1f;
        else
            return mViewPortHandler.getScaleX();
    }

    /**
     * returns the current y-scale factor
     */
    public float getScaleY() {
        if (mViewPortHandler == null)
            return 1f;
        else
            return mViewPortHandler.getScaleY();
    }

    /**
     * if the chart is fully zoomed out, return true
     *
     * @return
     */
    public boolean isFullyZoomedOut() {
        return mViewPortHandler.isFullyZoomedOut();
    }

    /**
     * Returns the left y-axis object. In the horizontal bar-chart, this is the
     * top axis.
     *
     * @return
     */
    public YAxis getAxisLeft() {
        return mAxisLeft;
    }

    /**
     * Returns the right y-axis object. In the horizontal bar-chart, this is the
     * bottom axis.
     *
     * @return
     */
    public YAxis getAxisRight() {
        return mAxisRight;
    }


    public YAxis getAxis(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mAxisLeft;
        else
            return mAxisRight;
    }

    @Override
    public boolean isInverted(AxisDependency axis) {
        return getAxis(axis).isInverted();
    }

    /**
     * If set to true, both x and y axis can be scaled simultaneously with 2 fingers, if false,
     * x and y axis can be scaled separately. default: false
     *
     * @param enabled
     */
    public void setPinchZoom(boolean enabled) {
        mPinchZoomEnabled = enabled;
    }

    /**
     * returns true if pinch-zoom is enabled, false if not
     *
     * @return
     */
    public boolean isPinchZoomEnabled() {
        return mPinchZoomEnabled;
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the x-axis.
     *
     * @param offset
     */
    public void setDragOffsetX(float offset) {
        mViewPortHandler.setDragOffsetX(offset);
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the y-axis.
     *
     * @param offset
     */
    public void setDragOffsetY(float offset) {
        mViewPortHandler.setDragOffsetY(offset);
    }

    /**
     * Returns true if both drag offsets (x and y) are zero or smaller.
     *
     * @return
     */
    public boolean hasNoDragOffset() {
        return mViewPortHandler.hasNoDragOffset();
    }

    public XAxisRenderer getRendererXAxis() {
        return mXAxisRenderer;
    }

    /**
     * Sets a custom XAxisRenderer and overrides the existing (default) one.
     *
     * @param xAxisRenderer
     */
    public void setXAxisRenderer(XAxisRenderer xAxisRenderer) {
        mXAxisRenderer = xAxisRenderer;
    }

    public YAxisRenderer getRendererLeftYAxis() {
        return mAxisRendererLeft;
    }

    /**
     * Sets a custom axis renderer for the left axis and overwrites the existing one.
     *
     * @param rendererLeftYAxis
     */
    public void setRendererLeftYAxis(YAxisRenderer rendererLeftYAxis) {
        mAxisRendererLeft = rendererLeftYAxis;
    }

    public YAxisRenderer getRendererRightYAxis() {
        return mAxisRendererRight;
    }

    /**
     * Sets a custom axis renderer for the right acis and overwrites the existing one.
     *
     * @param rendererRightYAxis
     */
    public void setRendererRightYAxis(YAxisRenderer rendererRightYAxis) {
        mAxisRendererRight = rendererRightYAxis;
    }

    @Override
    public float getYChartMax() {
        return Math.max(mAxisLeft.mAxisMaximum, mAxisRight.mAxisMaximum);
    }

    @Override
    public float getYChartMin() {
        return Math.min(mAxisLeft.mAxisMinimum, mAxisRight.mAxisMinimum);
    }

    /**
     * Returns true if either the left or the right or both axes are inverted.
     *
     * @return
     */
    public boolean isAnyAxisInverted() {
        if (mAxisLeft.isInverted())
            return true;
        if (mAxisRight.isInverted())
            return true;
        return false;
    }


    public void setAutoScaleMinMaxEnabled(boolean enabled) {
        mAutoScaleMinMaxEnabled = enabled;
    }

    public boolean isAutoScaleMinMaxEnabled() {
        return mAutoScaleMinMaxEnabled;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_GRID_BACKGROUND:
                mGridBackgroundPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_GRID_BACKGROUND:
                return mGridBackgroundPaint;
        }

        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Saving current position of chart.
        float[] pts = new float[2];
        if (mKeepPositionOnRotation) {
            pts[0] = mViewPortHandler.contentLeft();
            pts[1] = mViewPortHandler.contentTop();
            getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
        }

        //Superclass transforms chart.
        super.onSizeChanged(w, h, oldw, oldh);

        if (mKeepPositionOnRotation) {

            //Restoring old position of chart.
            getTransformer(AxisDependency.LEFT).pointValuesToPixel(pts);
            mViewPortHandler.centerViewPort(pts, this);
        } else {
            mViewPortHandler.refresh(mViewPortHandler.getMatrixTouch(), this, true);
        }
    }
}