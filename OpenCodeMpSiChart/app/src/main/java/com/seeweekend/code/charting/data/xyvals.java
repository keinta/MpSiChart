package com.seeweekend.code.charting.data;

/**
 * 这里面保存了xy 要显地示的值  Indexsec; 这个表示当前轴的描述语句,marksec 标签语句
 */
public class xyvals {
    /**
     *   第一个为标识码,第二个为负数坐标时指定起始高度，第三个为数字后面要显示的单位
     */
    public xyvals(int index, String indexsec, String marksec) {
        this.Index = index;
        this.Indexsec = indexsec;
        this.marksec = marksec;
    }

    public int getStype() {
        return stype;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    private int stype=-1;
    private int Index;

    public String getMarksec() {
        return marksec;
    }

    public void setMarksec(String marksec) {
        this.marksec = marksec;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getIndexsec() {
        return Indexsec;
    }

    public void setIndexsec(String indexsec) {
        Indexsec = indexsec;
    }

    private String marksec;
    private String Indexsec; //这个表示当前轴的描述语句
}
