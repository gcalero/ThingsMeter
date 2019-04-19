package com.course.android.thingmeterview;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ThingMeterView mThingMeter;
    private Handler handler;
    private int direction = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThingMeter = findViewById(R.id.thingMeterView);
        mThingMeter.setValue(mThingMeter.getMinValue());
        direction = 1;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float value = mThingMeter.getValue();
                value += direction;
                if (value == mThingMeter.getMaxValue() || value == mThingMeter.getMinValue()) {
                    direction *= -1;
                }
                mThingMeter.setValue(value);
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

}
