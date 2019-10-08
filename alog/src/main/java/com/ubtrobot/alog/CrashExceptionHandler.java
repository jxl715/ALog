package com.ubtrobot.alog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashExceptionHandler implements UncaughtExceptionHandler{

	public static final String TAG = "CrashExceptionHandler";
	private static CrashExceptionHandler instance;
	private Context context;
	private UncaughtExceptionHandler defaultHandler;
	private Map<String, String> infos = new HashMap<String, String>();   

	private CrashExceptionHandler(Context context){
		init(context);
	}

	public static CrashExceptionHandler getInstance(Context context) {
		if (instance == null) {
			instance = new CrashExceptionHandler(context);
		}
		return instance;
	}

	/**
	 * 
	 * @Title: init  
	 * @Description: 实例化一个默认的UncaughtExceptionHandler，
	 *               如果本类没有处理好UncaughtException，那么由
	 *               默认的UncaughtExceptionHandler来处理
	 * @param @param context      
	 * @return void  
	 * @throws
	 */
	private void init(Context context) {
		this.context = context;
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (handleException(ex, thread) && defaultHandler != null) {
			defaultHandler.uncaughtException(thread, ex); // 使用系统的弹出框
			android.os.Process.killProcess(android.os.Process.myPid()); // 杀死进程
			System.exit(10); // 退出进程
		}else{
			defaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 
	 * @Title: handleException  
	 * @Description: 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *               开发者可以根据自己的情况来自定义异常处理逻辑
	 * @param @param ex
	 * @param @return      
	 * @return boolean 
	 * @throws
	 */
	private boolean handleException(final Throwable ex, final Thread thread) {
		//ALog.writeLog();//日志模型写入
		if (ex == null) {
			return true;
		}
        collectDeviceInfo(context);
        writeCrashInfoIntoFile(ex);
		return true;
	}

	/**
	 * 
	 * @Title: writeCrashInfoIntoFile  
	 * @Description: 将导致程序奔溃的错误保存到手机文件 
	 * @param @param ex      
	 * @return void  
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	private void writeCrashInfoIntoFile(Throwable ex){
		if(ex == null){
			return;
		}
		// 设备信息
		StringBuilder sb = new StringBuilder();
		String value = "";
		for(String key : infos.keySet()){
			value = infos.get(key);
			sb.append(key).append("=").append(value).append("\n");
		}
		// 错误信息
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);  
		ex.printStackTrace(printWriter);  
		Throwable cause = ex.getCause();  
		while (cause != null) {  
			cause.printStackTrace(printWriter);  
			cause = cause.getCause();  
		}  
		printWriter.close();  
		String result = writer.toString();  
		sb.append(result);
		// 保存到文件
		FileOutputStream fos = null; 
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		long timestamp = System.currentTimeMillis();  
		String time = formatter.format(new Date());  
		String fileName = time + "_" + timestamp + ".txt";
		try {   
			File file = ExternalOverFroyoUtils.getDiskCacheDir(context, "crash");
			if (!file.exists()) {  
				file.mkdirs();  
			}  
			File newFile = new File(file.getAbsolutePath() + File.separator + fileName);
			fos = new FileOutputStream(newFile);
			fos.write(sb.toString().getBytes());
		}catch( FileNotFoundException fne){
			Log.e(TAG, "保存crash文件时出错：" + fne.getMessage());
		}catch (Exception e) { 
			Log.e(TAG, "保存crash文件时出错：" + e.getMessage());  
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e){ 
					Log.e(TAG,"关闭Crash文件流时出错:" + e.getMessage());
				}
			}
		}
	}

	/**
	 * 
	 * @Title: collectDeviceInfo  
	 * @Description: 收集设备信息 
	 * @param @param context      
	 * @return void  
	 * @throws
	 */
	private void collectDeviceInfo(Context context) {  
		try {  
			PackageManager pm = context.getPackageManager();  
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);  
			if (pi != null) {  
				String versionName = pi.versionName == null ? "null" : pi.versionName;  
				String versionCode = pi.versionCode + "";  
				infos.put("versionName", versionName);  
				infos.put("versionCode", versionCode);  
			}  
		} catch (NameNotFoundException e) {  
			Log.e(TAG, "收集包信息时出错。");  
		}  
		Field[] fields = Build.class.getDeclaredFields();  
		try {  
			for (Field field : fields) {  
				field.setAccessible(true);  
				infos.put(field.getName(), field.get(null).toString());  
			} 
		} catch (Exception e) {  
			Log.e(TAG, "收集crash信息时出错。");  
		} 
	}  

}