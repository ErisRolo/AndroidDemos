package com.example.guohouxiao.simplecamera;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View layout;

    //这里导包选择的是hardware而不是graphics，因为前者侧重照相本身的功能，后者偏向3D图片转换之类
    private Camera camera;

    Bundle bundle = null;//声明一个Bundle变量用来存储数据

    /*    OrientationEventListener mScreenOrientationEventListener;//设置一个方向事件监听器
    public static int orientations;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //去掉标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉信息栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        layout = this.findViewById(R.id.buttonLayout);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setKeepScreenOn(true);//保持屏幕常亮状态，不被锁定
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        /*        mScreenOrientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                orientations=orientation;
            }
        };*/
    }


    /*    @Override
    public void onResume() {
        super.onResume();
        if(mScreenOrientationEventListener != null) {
            mScreenOrientationEventListener.enable();
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScreenOrientationEventListener != null) {
            mScreenOrientationEventListener.disable();
            }
    }*/


    /**
     * 实现拍照时的各项参数类型，处理相关回调方法
     */
    private final class SurfaceCallback implements SurfaceHolder.Callback {


        /**
         *开始拍照时调用的方法
         *
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();//打开摄像头
                camera.setPreviewDisplay(holder);//设置用于显示拍照影像的surfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));//设置预览角度
                camera.startPreview();//开始预览
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * 拍照状态变化时调用的方法
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters;
            parameters = camera.getParameters();//获取各项参数

            //根据手机参数设置不同参数，不设置则为默认
            camera.setParameters(parameters);//设置照相机参数
            parameters.setJpegQuality(80);//设置图片质量
            parameters.setPictureSize(width, height);//设置保存图片尺寸
            parameters.setPreviewSize(width, height);//设置预览图片尺寸
            parameters.setPreviewFrameRate(15);//设置每秒显示的帧数，此处为14
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        /**
         * 停止拍照时调用的方法
         *
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();//释放照相机
                camera = null;
            }
        }
    }

    /**
     * 创建一个静态方法，用来根据手机方向获得画面旋转的角度
     *
     * @param activity
     * @return
     */
    private int getPreviewDegree(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 90;
                break;
            case Surface.ROTATION_90:
                degrees = 180;
                break;
            case Surface.ROTATION_180:
                degrees = 270;
                break;
            case Surface.ROTATION_270:
                degrees = 360;
                break;
        }
        return degrees;
    }


    /**
     * 拍照
     *
     * @param view
     */
    public void photograph(View view){
        if (camera != null) {
            switch (view.getId()) {
                case R.id.photograph:
                    camera.takePicture(null, null, new MyPictureCallback());
                    break;
            }
        }
    }


    /**
     * 重写MyPictureCallback()方法进行拍照和保存
     *
     */
    private final class MyPictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
/*                Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                int width=bitmap.getWidth();
                int height=bitmap.getHeight();
                Matrix matrix=new Matrix();
                mScreenOrientationEventListener.enable();
                System.out.println("orientations的值为"+orientations);
                if(orientations > 325 || orientations <= 45){
                    matrix.setRotate(90);
                }else if(orientations > 45 && orientations <= 135){
                    matrix.setRotate(180);
                }else if(orientations > 135 && orientations < 225){
                    matrix.setRotate(270);
                }else {
                    matrix.setRotate(0);
                }
                mScreenOrientationEventListener.disable();
                Bitmap resizeBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                resizeBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                byte[] array=outputStream.toByteArray();
                bundle.putByteArray("bytes",array);
                saveToSDCard(array);*/
                bundle = new Bundle();
                bundle.putByteArray("bytes", data);
                saveToSDCard(data);
                Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 保存图片到SD卡
     * @param data
     * @return
     */
    private int saveToSDCard(byte[] data) throws IOException {
        Date date = new Date();
        //注意这里使用SimpleDateFormat来格式化时间，有两种选择，android中的包或java中的包
        //记得选择java中的,否则容易造成API版本冲突
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//存储时间格式
        String filename = format.format(date) + ".jpg";//存储的文件名及文件类型
        //建立一个文件夹用来存储文件
        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/photograph/");
        if (!fileFolder.exists()) {
            fileFolder.mkdir();//如果不存在则新建一个
        }
        File jpgFile = new File(fileFolder, filename);//存储时新建一个jpg文件
        FileOutputStream outputStream = new FileOutputStream(jpgFile);//文件输出流
        outputStream.write(data);//写入SD卡中
        setSavePictureDegree(Environment.getExternalStorageDirectory() + "/photograph/" + filename);
        outputStream.close();//关闭输出流
        return 0;
    }

    /**
     * 设置保存的图片的旋转角度
     *
     * @param path
     */
    private void setSavePictureDegree(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
            // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                }
            } catch (IOException e) {
            e.printStackTrace();
            }
        return degree;
    }*/


    /**
     * 定点对焦
     *
     * @param event
     */
    public void pointFocus(MotionEvent event){
        camera.cancelAutoFocus();
        Camera.Parameters parameters = camera.getParameters();
        //如果SDK版本在4.0以上
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            focusOnTouch(event);
        }
        camera.setParameters(parameters);
        autofocus();
    }

    /**
     * 自动对焦
     *
     */
    public void autofocus() {
        new Thread(){
            public void run(){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(camera==null){
                    return;
                }
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success){
                            camera.setOneShotPreviewCallback(null);
                        }
                    }
                });
            }
        };
    }

    /**
     * 触摸聚焦
     *
     * @param event
     */
    public void focusOnTouch(MotionEvent event) {
        if(camera!=null){
            Rect focusRect = calculateTapArea(event.getRawX(),event.getRawY(),1f);//计算对焦区域
            Rect meteringRect = calculateTapArea(event.getRawX(),event.getRawY(),1.5f);//计算测光区域
            Camera.Parameters parameters = camera.getParameters();//创建新的Camera.parameters
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//设置对焦模式

            //如果支持对焦区域，设置计算好的参数
            if(parameters.getMaxNumFocusAreas()>0){
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(focusRect,1000));
                parameters.setFocusAreas(focusAreas);
            }
            //如果支持测光区域，设置计算好的参数
            if(parameters.getMaxNumMeteringAreas()>0){
                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(meteringRect,1000));
                parameters.setMeteringAreas(meteringAreas);
            }
            try {
                camera.setParameters(parameters);//设置参数
                camera.autoFocus(null);//自动对焦
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 计算对焦区域，完成从屏幕坐标系到对焦坐标系的转换
     * @param pointWidth
     * @param pointHeight
     * @param v
     * @return
     */
    private Rect calculateTapArea(float pointWidth, float pointHeight, float v) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * v).intValue();
        int centerX = (int) (pointWidth/getResolution().width - 1000);
        int centerY = (int) (pointHeight/getResolution().height - 1000);
        int left = clamp(centerX - areaSize/2,-1000,1000);
        int top = clamp(centerY - areaSize/2,-1000,1000);
        RectF rectF = new RectF(left,top,left + areaSize,top + areaSize);
        return new Rect(Math.round(rectF.left),Math.round(rectF.top),Math.round(rectF.right),Math.round(rectF.bottom));
    }

    /**
     * 将边界值限制在范围内
     * @param x
     * @param min
     * @param max
     * @return
     */
    private int clamp(int x,int min,int max) {
        if(x>max){
            return max;
        }
        if(x<min){
            return min;
        }
        return x;
    }

    /**
     * 获取分辨率
     * @return
     */
    private Camera.Size getResolution() {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size=parameters.getPreviewSize();
        return size;
    }
}