package com.seeweekend.code;
/**
 * author Keinta Powered by (SK开源) seeweekend.com/code on 2017/6/29.
 * email: hronyt@foxmail.com
 */
public class Sleepdate {
	
	 public Sleepdate(float temperature, int hour, int minute, String daysec, String timestamp) {
	        this.temperature = temperature;
	        this.hour = hour;
	        this.minute = minute;
	        this.daysec = daysec;
	        this.timestamp = timestamp;
	    }

	    public int getHour() {
	        return hour;
	    }

	    public void setHour(int hour) {
	        this.hour = hour;
	    }

	    public int getMinute() {
	        return minute;
	    }

	    public void setMinute(int minute) {
	        this.minute = minute;
	    }

	    public String getTimestamp() {
	        return timestamp;
	    }

	    public void setTimestamp(String timestamp) {
	        this.timestamp = timestamp;
	    }

	    private String timestamp;

	    private int hour;
	    private int minute;

	    public String getDaysec() {
	        return daysec;
	    }

	    public void setDaysec(String daysec) {
	        this.daysec = daysec;
	    }

	    private String daysec = "";

	    public float getTemperature() {
	        return temperature;
	    }


	    public void setTemperature(float temperature) {
	        this.temperature = temperature;
	    }

	    private float temperature;

}
