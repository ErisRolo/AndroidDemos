package com.example.jpgtovideo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/****
 * 
 * @author yanjiaqi 2013.6.6
 *
 */
public class MainActivity extends Activity {
	public static String START = "开始";
	public static String END = "结束";
	public static String PAUSE = "暂停";
	public static String RESTART = "继续";
	EditText et_path;
	Button start,pause;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_path = (EditText)findViewById(R.id.editText1);
        start = (Button)findViewById(R.id.button1);
        pause = (Button)findViewById(R.id.button2);
        start.setText(START);
        pause.setText(PAUSE);
        
        start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = ((Button)v).getText().toString(); 
				if(START.equals(text)){
					VideoCapture.start(MainActivity.this, et_path.getText().toString());
					start.setText(END);
				}else
				if(END.equals(text)){
					VideoCapture.stop();
					start.setText(START);
				}				
			}
		});
        pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = ((Button)v).getText().toString(); 
				if(PAUSE.equals(text)){
					VideoCapture.pause();
					pause.setText(RESTART);
				}else
				if(RESTART.equals(text)){
					VideoCapture.restart();
					pause.setText(PAUSE);
				}
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
