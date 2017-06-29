package com.seeweekend.code.charting.interfaces.dataprovider;

import com.seeweekend.code.charting.components.YAxis.AxisDependency;
import com.seeweekend.code.charting.data.BarLineScatterCandleBubbleData;
import com.seeweekend.code.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    int getMaxVisibleCount();
    boolean isInverted(AxisDependency axis);
    
    int getLowestVisibleXIndex();
    int getHighestVisibleXIndex();

    BarLineScatterCandleBubbleData getData();
}
