package com.seeweekend.code.charting.interfaces.dataprovider;

import com.seeweekend.code.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
