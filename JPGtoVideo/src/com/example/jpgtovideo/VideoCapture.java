package com.example.jpgtovideo;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;
/**
 *	
 */
public class VideoCapture {
		private static int switcher = 0;//录像键
		private static boolean isPaused = false;//暂停键
		private static String filepath = "";
		private static String filename = null;
		private static Context context;
		public static int INDEX_MAX = 21;
		public static void start(Context context,String path){
			//init
			VideoCapture.context = context;
			if(path!=null){
				filepath = path;
			}
			
			switcher = 1;
			new Thread(){
				public void run(){
					
					OutputStream os = null;
					try {
					new FileUtils().creatSDDir(filepath);
					filename ="test.avi";
					Bitmap testBitmap = getImageFromAssetsFile("1.jpg");
					
					FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
							new FileUtils().getSDCardRoot()+filepath+File.separator+filename,
							testBitmap.getWidth(),
							testBitmap.getHeight());
					
					recorder.setFormat("avi");
					recorder.setFrameRate(2f);//录像帧率
					recorder.start();
					
					int index = 1;
					while(switcher!=0){
							if(!isPaused){				
								File file = null;
								if(!new FileUtils().isFileExist("video.jpg",filepath)){					
									file = new FileUtils().createFileInSDCard("video.jpg", filepath);
								}else{
									file = new FileUtils().getFile("video.jpg",filepath);
								}
								os = new FileOutputStream(file);
								Bitmap frame = getImageFromAssetsFile(index+".jpg");
								frame.compress(Bitmap.CompressFormat.JPEG, 100, os);
								os.flush();
								os.close();
								frame.recycle();
								frame = null;
								
								opencv_core.IplImage image = cvLoadImage(new FileUtils().getSDCardRoot()+filepath+File.separator+"video.jpg");
								recorder.record(image);

							}
							if(index+1>INDEX_MAX){
								index = 1;
							}else{
								index++;
							}
						}
						recorder.stop();
					}catch(FileUtils.NoSdcardException e){
						e.printStackTrace();
					}
					catch(FileNotFoundException e){
						e.printStackTrace();
					}
					catch(IOException e){
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
		public static void stop(){
			switcher = 0;
			isPaused = false;
		}
		public static void pause(){
			if(switcher==1){
				isPaused = true;
			}
		}
		public static void restart(){
			if(switcher==1){
				isPaused = false;
			}
		}
		public static boolean isStarted(){
			if(switcher==1){
				return true;
			}else{
				return false;
			}
		}
		public static boolean isPaused(){
			return isPaused;
		}
		private static Bitmap getImageFromAssetsFile(String filename){
			Bitmap image = null;
			AssetManager am = context.getResources().getAssets();
			try{			
				InputStream is = am.open(filename);
				image = BitmapFactory.decodeStream(is);
				is.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return image;
		}
}
