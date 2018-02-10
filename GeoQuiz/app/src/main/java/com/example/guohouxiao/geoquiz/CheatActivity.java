package com.example.guohouxiao.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    //为activity获取和使用的extra定义键，使用包名修饰extra数据信息，避免来自不同应用的extra间发生命名冲突
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.guohouxiao.geoquiz.answer_is_true";
    //为extra增加一个常量，用来创建一个新的intent作为setResult()的参数
    private static final String EXTRA_ANSWER_SHOWN = "com.example.guohouxiao.geoquiz.answer_shown";

    private static final String KEY_WAS_CHEATER = "wascheater";
    private boolean mWasCheater;

    private boolean mAnswerIsTrue;

    private TextView mAnswerTextView;
    private Button mShowAnswer;
    private TextView mApiTextView;

    //将CheatActivity处理extra信息的实现细节，用newIntent()方法进行封装
    public static Intent newIntent(Context packageContext,boolean answerIsTrue){
        Intent intent = new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }

    //用来协助解析出QuizActivity能用的信息
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //指定extra为EXTRA_ANSWER,IS_TRUE,,从EXTRA_ANSWER_IS_TRUE中获取一个布尔类型的数据
        // 如果获取不到数据，则使用默认参数，默认参数为true
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,true);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mApiTextView = (TextView) findViewById(R.id.api_text_view);
        mApiTextView.setText("API level " + Build.VERSION.SDK_INT);

        mShowAnswer = (Button) findViewById(R.id.show_answer_button);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWasCheater = true;
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);

                //加一个版本判断，使动画特效代码只有在API 21级或更高版本的设备上才被调用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    int cx = mShowAnswer.getWidth() / 2;
                    int cy = mShowAnswer.getHeight() / 2;
                    float radius = mShowAnswer.getWidth();
                    Animator anim = ViewAnimationUtils.
                            //创建一个Animator，分别传入View，动画的中心位置，起始半径和结束半径
                                    createCircularReveal(mShowAnswer,cx,cy,radius,0);
                    //动画启动前，设置一个监听器，确定动画何时展示完毕
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //动画展示完毕时，显示答案
                            mShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswer.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (savedInstanceState != null){
            mWasCheater = savedInstanceState.getBoolean(KEY_WAS_CHEATER);
            //保证获得的答案在屏幕旋转后也不会消失
            if (mWasCheater) {
                mShowAnswer.setVisibility(View.INVISIBLE);
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        }

    }

    //用户点击SHOW ANSWER按钮时，子Activity调用setResult()将结果代码和intent打包返回父Activity
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        //调用setResult()方法的情况下，用户点击后退按钮时，父Activity收到RESULT_OK的结果代码
        //证明调用了setResult()方法有数据返回
        setResult(RESULT_OK,data);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_WAS_CHEATER,mWasCheater);
    }
}
