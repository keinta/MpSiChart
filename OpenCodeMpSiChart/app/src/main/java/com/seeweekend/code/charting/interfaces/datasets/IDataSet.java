package com.seeweekend.code.charting.interfaces.datasets;

import android.graphics.Typeface;

import com.seeweekend.code.charting.components.YAxis;
import com.seeweekend.code.charting.data.DataSet;
import com.seeweekend.code.charting.data.Entry;
import com.seeweekend.code.charting.formatter.ValueFormatter;
import com.seeweekend.code.charting.utils.MpChartSetdate;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface IDataSet<T extends Entry> {
     MpChartSetdate mpChartSetdate();

    float getYMin();
    /**
     * returns the maximum y-value this DataSet holds
     */
    float getYMax();

    /**
     * Returns the number of y-values this DataSet represents -> the size of the y-values array
     * -> yvals.size()
     */
    int getEntryCount();

    /**
     * Calculates the minimum and maximum y value (mYMin, mYMax). From the specified starting to ending index.
     * @param start starting index in your data list
     * @param end   ending index in your data list
     */
    void calcMinMax(int start, int end);

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. If the no Entry at the specified x-index is found, this method
     * returns the index at the closest x-index. Returns null if no Entry object
     * at that index. INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     */
    T getEntryForXIndex(int xIndex, DataSet.Rounding rounding);

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. If the no Entry at the specified x-index is found, this method
     * returns the index at the closest x-index. Returns null if no Entry object
     * at that index. INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     */
    T getEntryForXIndex(int xIndex);

    /**
     * Returns all Entry objects found at the given xIndex with binary
     * search. An empty array if no Entry object at that index.
     * INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     */
    List<T> getEntriesForXIndex(int xIndex);

    /**
     * Returns the Entry object found at the given index (NOT xIndex) in the values array.
     */
    T getEntryForIndex(int index);

    /**
     * Returns the first Entry index found at the given xIndex with binary
     * search. If the no Entry at the specified x-index is found, this method
     * returns the index at the closest x-index. Returns -1 if no Entry object
     * at that index. INFORMATION: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     */
    int getEntryIndex(int xIndex, DataSet.Rounding rounding);

    /**
     * Returns the position of the provided entry in the DataSets Entry array.
     * Returns -1 if doesn't exist.
     */
    int getEntryIndex(T e);

    float getYValForXIndex(int xIndex);

    float[] getYValsForXIndex(int xIndex);


    int getIndexInEntries(int xIndex);

    boolean addEntry(T e);


    boolean removeEntry(T e);


    void addEntryOrdered(T e);

    boolean removeFirst();


    boolean removeLast();


    boolean removeEntry(int xIndex);


    boolean contains(T entry);

    /**
     * Removes all values from this DataSet and does all necessary recalculations.
     */
    void clear();

    String getLabel();

    /**
     * Sets the label string that describes the DataSet.
     *
     * @param label
     */
    void setLabel(String label);

    YAxis.AxisDependency getAxisDependency();


    void setAxisDependency(YAxis.AxisDependency dependency);


    List<Integer> getColors();


    int getColor();


    int getColor(int index);


    boolean isHighlightEnabled();


    void setHighlightEnabled(boolean enabled);


    void setValueFormatter(ValueFormatter f);

    ValueFormatter getValueFormatter();

    /**
     * Sets the color the value-labels of this DataSet should have.
     *
     * @param color
     */
    void setValueTextColor(int color);

    /**
     * Sets a list of colors to be used as the colors for the drawn values.
     *
     * @param colors
     */
    void setValueTextColors(List<Integer> colors);

    /**
     * Sets a Typeface for the value-labels of this DataSet.
     *
     * @param tf
     */
    void setValueTypeface(Typeface tf);

    /**
     * Sets the text-size of the value-labels of this DataSet in dp.
     *
     * @param size
     */
    void setValueTextSize(float size);

    /**
     * Returns only the first color of all colors that are set to be used for the values.
     *
     * @return
     */
    int getValueTextColor();

    /**
     * Returns the color at the specified index that is used for drawing the values inside the chart.
     * Uses modulus internally.
     *
     * @param index
     * @return
     */
    int getValueTextColor(int index);

    /**
     * Returns the typeface that is used for drawing the values inside the chart
     *
     * @return
     */
    Typeface getValueTypeface();

    /**
     * Returns the text size that is used for drawing the values inside the chart
     *
     * @return
     */
    float getValueTextSize();

    /**
     * set this to true to draw y-values on the chart NOTE (for bar and
     * linechart): if "maxvisiblecount" is reached, no values will be drawn even
     * if this is enabled
     *
     * @param enabled
     */
    void setDrawValues(boolean enabled);

    /**
     * Returns true if y-value drawing is enabled, false if not
     *
     * @return
     */
    boolean isDrawValuesEnabled();

    /**
     * Set the visibility of this DataSet. If not visible, the DataSet will not
     * be drawn to the chart upon refreshing it.
     *
     * @param visible
     */
    void setVisible(boolean visible);

    /**
     * Returns true if this DataSet is visible inside the chart, or false if it
     * is currently hidden.
     *
     * @return
     */
    boolean isVisible();
}
