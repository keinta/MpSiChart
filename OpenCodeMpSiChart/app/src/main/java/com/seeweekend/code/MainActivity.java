package com.seeweekend.code;
/**
 * author Keinta Powered by (SK开源) seeweekend.com/code on 2017/6/29.
 * email: hronyt@foxmail.com
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = View.inflate(this, R.layout.activity_main, null);
		setContentView(rootView);
		MpSlideableChart mpSlideableChart = new MpSlideableChart(rootView, this);
		mpSlideableChart.myonCreate();
	}
}
