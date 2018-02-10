package com.example.guohouxiao.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/12.
 * 模型及视图对象交互的控制器，用于显示特定crime的明细信息，并在用户修改这些信息后立刻进行更新
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ViewTreeObserver mTreeObserver;
    private Callbacks mCallbacks;

    private TextView mTitleTextView;
    private TextView mDetailTextView;
    private boolean mCrimeIsDeleted;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    //附加argument bundle给fragment，必须在fragment创建后、添加给activity前完成，因此添加newInstance静态方法
    public static CrimeFragment newInstance(UUID crimeId){
        //每个fragment实例可附带一个Bundle对象
        Bundle args = new Bundle();
        //向bundle中添加argument，一个键值对即一个argument
        args.putSerializable(ARG_CRIME_ID,crimeId);

        //创建fragment
        CrimeFragment fragment = new CrimeFragment();
        //调用Fragment.setArguments(Bundle)方法，附加argument bundle给fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    //涉及生命周期的方法是公共方法，不同于activity的保护方法，因为托管fragment的activiy要调用它们
    //同时不同于activity，onCreate()方法配置实例，但没有创建和配置视图
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        //getActivity().getIntent()方法返回用来启动CrimeActivity的Intent
        //Intent的getSerializableExtra(String)方法用来获取UUID并存入变量中
        UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);*/
        //先调用getArguments()方法，再调用Bundle的限定类型"get"方法来获取argument
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //从CrimeLab单例中调取Crime对象
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        //获取图片文件位置
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        //表示fragment愿意添加item到选项菜单（否则，fragment将接收不到对onCreateOptionsMenu()的调用）
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Crime数据刷新
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    //该方法用来实例化布局，同时将实例化的View返回给托管的activity
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //用LayoutInflater的inflate方法生成视图
        //第一个参数传入布局的资源ID，第二个参数传入父视图，第三个参数为布尔类型，设置是否将生成视图添加到父视图
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleTextView = (TextView) v.findViewById(R.id.title_text_view);
        mDetailTextView = (TextView) v.findViewById(R.id.detail_text_view);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        //创建TextWatcher监听器，监听文本变化
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            //文本改变之前调用
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            //文本改变时调用
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());//调用CharSequence（代表用户输入）的toString()方法，设置Crime标题的字符串
                updateCrime();
            }

            @Override
            //在TextView调用完所有已注册的TextWatcher的onTextChanged方法之后回调，此时新文本已经修改完毕
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_data);
        updateDate();
/*        mDateButton.setEnabled(false);//将按钮设置为禁用状态，确保不响应用户的点击事件*/
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());//new一个DatePickerFragment改为获得一个带有argument的DatePickerFragment
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);//将CrimeFragment设置成目标fragment，用请求代码确认是哪个fragment
                dialog.show(manager,DIALOG_DATE);//将DialogFragment添加给FragmentManager管理并放置到屏幕上，选择传入FragmentManager参数，系统会自动创建并提交事务
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());//将CrimeFragment的数据传递给TimePickerFragment
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(manager,DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);//监听器用于更新Crime的mSolved变量值
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent i = new Intent(Intent.ACTION_SEND);//定义隐式intent的操作是ACTION_SEND
                i.setType("text/plain");//指定数据类型
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());//消息内容
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));//主题
                i = Intent.createChooser(i, getString(R.string.send_report));//使用选择器
                startActivity(i);*/
                ShareCompat.IntentBuilder sc = ShareCompat.IntentBuilder.from(getActivity());
                sc.setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .createChooserIntent();
                sc.startChooser();
            }
        });

        mCallButton = (Button) v.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                //用ContentResolver()查询联系人数据
                Cursor cursor = getActivity().getContentResolver()
                        .query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        //获取联系人ID
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        if (mCrime.getId().equals(contactId)) {
                            Cursor phone = getActivity().getContentResolver()
                                    //根据联系人ID查询CommonDataKinds.Phone表
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                                            null,null);
                            //获得电话号码，用电话URI创建隐式intent
                            if (phone.moveToNext()) {
                                String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Uri number = Uri.parse(phoneNumber);
                                Intent i = new Intent(Intent.ACTION_DIAL,number);
                                startActivity(i);
                            }
                        }
                    }
                }*/
                Uri number = Uri.parse("tel:5551234");
                Intent i = new Intent(Intent.ACTION_DIAL, number);
                startActivity(i);
            }
        });

        //第一个参数为要执行的操作，第二个参数为要访问数据的位置
        //ContactsContract.Contacts.CONTENT_URI为联系人数据获取位置
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);//过滤器验证代码，不让任何联系人应用和你的intent匹配
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //用PackageManager类进行自检，检查有没有可响应任务的activity
        PackageManager packageManager = getActivity().getPackageManager();
        //调用resolveActivity()方法可以找到匹配给定Intent任务的activity
        //flag标志MATCH_DEFAULT_ONLY限定只搜索带CATEGORY_DEFAULT标志的activity
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);//找不到合适的activity时，必须禁用按钮
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&//是否存在图片文件
                captureImage.resolveActivity(packageManager) != null;//是否有响应相机隐式intent的activity
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            //要想获得全尺寸照片，就要使用文件系统存储照片
            //通过传入保存在MediaStore.EXTRA_OUTPUT中的指向存储路径的Uri来完成
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        //缩略图
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                PhotoFragment fragment = PhotoFragment.newInstance(mPhotoFile.getPath());
                fragment.show(manager,DIALOG_PHOTO);
            }
        });

        //优化缩略图加载
        mTreeObserver = mPhotoView.getViewTreeObserver();
        mTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    /**
     * 判断当前设备是手机还是平板，代码来自Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                if (isPad(getActivity())) {
                    CrimeLab.get(getActivity()).deleteCrime(mCrime);
                    mCrimeIsDeleted = true;
                    updateCrime();
                } else {
                    CrimeLab.get(getActivity()).deleteCrime(mCrime);
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    //接受回传数据，响应对话框
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            //用DatePickerFragment的extra获取回传的数据，并做类型转换
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateCrime();
            updateTime();
        } else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            //Specify which fields you want your query to return
            //values for.
            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
            //ContentProvider类用于处理联系人信息，通过一个ContentResolver访问ContentProvider
            //Perform your query - the contactUri is like a "where"
            //clause here
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);

            try {
                //Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                //Pull out the first column of the first row of data -
                //that is your suspect's name.
                //已知Cursor只包含一条记录（一个嫌疑人），所以移动到第一条记录并获取它的字符串形式即可
                c.moveToFirst();
                String suspect = c.getString(0);
                updateCrime();
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateCrime();
            updatePhotoView();
        }
    }

    private void updateCrime() {
        if (mCrimeIsDeleted) {
            mTitleField.setVisibility(View.GONE);
            mDateButton.setVisibility(View.GONE);
            mTimeButton.setVisibility(View.GONE);
            mSolvedCheckBox.setVisibility(View.GONE);
            mReportButton.setVisibility(View.GONE);
            mCallButton.setVisibility(View.GONE);
            mSuspectButton.setVisibility(View.GONE);
            mPhotoView.setVisibility(View.GONE);
            mPhotoButton.setVisibility(View.GONE);
            mTitleTextView.setVisibility(View.GONE);
            mDetailTextView.setVisibility(View.GONE);
        }
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        //用SimpleDateFormat类设置日期格式
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy", Locale.CHINA);
        String date = format.format(mCrime.getDate());
        mDateButton.setText(date);//将按钮显示内容设置成日期
    }

    private void updateTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String date = format.format(mCrime.getDate());
        mTimeButton.setText(date);
    }

    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
