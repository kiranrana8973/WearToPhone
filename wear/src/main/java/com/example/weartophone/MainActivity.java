package com.example.weartophone;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        String message = getIntent().getStringExtra("message");
        if(message==null)
        {
            mTextView.setText("No message");
        }
        mTextView.setText(message);


        // Enables Always-on
        setAmbientEnabled();
    }


}
