package com.ubtrobot.alog;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.orhanobut.logger.Printer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ALog {

    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数


    private static final String DEFAULT_TAG = "ALog";

    private FormatStrategy mFormatStrategy;
    private CsvFormatStrategy mFormatStrategyDisk;

    public static void init(Context context) {

        FormatStrategy mFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(new LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(DEFAULT_TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(mFormatStrategy));

        //保存日志到本地文件
        File file = ExternalOverFroyoUtils.getDiskCacheDir(context, "log");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK);
        FormatStrategy mFormatStrategyDisk = CsvFormatStrategy.newBuilder()
                //.date(new Date())  // 设置保存的时间
                .dateFormat(dateFormat)  // 设置保存的格式化时间,默认 yyyy.MM.dd HH:mm:ss.SSS
                .folder(file.getAbsolutePath())
                .tag(DEFAULT_TAG)  // 自定义日志标记
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(mFormatStrategyDisk));

        //保存crash日志
        Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler(context));

    }

    public static Printer tag(String tag) {
        return Logger.t(tag);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        Logger.d(message, args);
    }

    public static void d(@Nullable Object object) {
        Logger.d(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args){
        Logger.e(message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args){
        Logger.e(throwable, message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args){
        Logger.w(message, args);
    }

    public static void i(@NonNull String message, @Nullable Object... args){
        Logger.i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args){
        Logger.v(message, args);
    }

    public static void wtf(@NonNull String message, @Nullable Object... args) {
        Logger.wtf(message, args);
    }


    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        Logger.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        Logger.xml(xml);
    }

    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(Context context) {
        if (uncaughtExceptionHandler == null) {
            uncaughtExceptionHandler = CrashExceptionHandler.getInstance(context);
        }
        return uncaughtExceptionHandler;
    }


    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
//        buffer.append("函数名称："+methodName);
        buffer.append("  ：");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }


    public static void E(String message) {
        if (!isDebuggable())
            return;

        // Throwable instance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        Log.e("TestLog", createLog(message));
    }

    public static boolean isDebuggable() {

        return BuildConfig.DEBUG;
//        return isDebuggable;
    }
}
