package com.joki.studyapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tView;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        tView = (TextView) findViewById(R.id.textViewtest);
        btn.setText("0");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do somethinguj8
                btn.setText(Integer.parseInt((String) btn.getText()) + 1 + "");
            }
        });
    }
}
