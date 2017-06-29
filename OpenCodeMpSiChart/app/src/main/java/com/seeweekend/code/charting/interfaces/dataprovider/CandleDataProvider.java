package com.seeweekend.code.charting.interfaces.dataprovider;

import com.seeweekend.code.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
