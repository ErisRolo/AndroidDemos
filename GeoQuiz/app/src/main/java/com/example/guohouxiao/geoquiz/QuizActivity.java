package com.example.guohouxiao.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
        new Question(R.string.question_oceans,true),
        new Question(R.string.question_mideast,false),
        new Question(R.string.question_africa,false),
        new Question(R.string.question_americas,true),
        new Question(R.string.question_asia,true),
    };

    private int mCurrentIndex = 0;

    //定义一个新键用来保存数组信息
    private static final String KEY_CHEATED_ARRAY = "cheated_array";
    //定义一个布尔类型数组用来保存用户是否在此题作弊的信息
    private boolean[] mCheatedArray = { false,false,false,false,false };

    private boolean mIsCheater;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        //如果作弊了或曾经在此题作弊
        if (mIsCheater || mCheatedArray[mCurrentIndex]){
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                //这里传入的布尔类型数据其实是传给了EXTRA_ANSWER_IS_TRUE作为键值来使用
                Intent intent = CheatActivity.newIntent(QuizActivity.this,answerIsTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER);
            mCheatedArray = savedInstanceState.getBooleanArray(KEY_CHEATED_ARRAY);
        }
        updateQuestion();

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;//切换到下一题时，标志是否作弊的静态常量初始为未作弊
                updateQuestion();
            }
        });
        updateQuestion();
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex == 0){
                    mCurrentIndex = mQuestionBank.length - 1;
                    mIsCheater = false;
                    updateQuestion();
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                }
            }
        });
        updateQuestion();
    }

    //重写onActivityResult方法对返回的数据进行处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果没获得RESULT_OK，证明没调用setResult，没有数据返回
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT){
            //如果返回数据为空，同样不做处理
            if (data == null){
                return;
            }
            //获取返回的布尔类型的数据，表示是否作弊
            mIsCheater = CheatActivity.wasAnswerShown(data);
            //表示是否曾经在此题作弊
            mCheatedArray[mCurrentIndex] = mIsCheater;
        }
    }

    /**
     * 设备旋转会销毁当前活动重建，因此需要在设备旋转前保存数据
     * 通过修改onCreate(Bundle)中的Bundle来实现
     * Bundle是存储字符串键与先动类型值之间映射关系（键值对）的一种结构
     * 通过重写onSaveInstanceState方法改变Bundle的值
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEATER,mIsCheater);
        savedInstanceState.putBooleanArray(KEY_CHEATED_ARRAY,mCheatedArray);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestory() called");
    }
}
