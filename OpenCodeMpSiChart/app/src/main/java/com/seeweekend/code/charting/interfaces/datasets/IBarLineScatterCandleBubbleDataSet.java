package com.seeweekend.code.charting.interfaces.datasets;

import com.seeweekend.code.charting.data.Entry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    /**
     * Returns the color that is used for drawing the highlight indicators.
     */
    int getHighLightColor();
}
