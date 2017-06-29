package com.seeweekend.code.charting.interfaces.dataprovider;

import com.seeweekend.code.charting.components.YAxis;
import com.seeweekend.code.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
