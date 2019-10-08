/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ubtrobot.alog;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @version V1.0
 *  CHExternalOverFroyoUtils
 *  com.changhong.util.cache
 *  缓存的工具类, Android 2.2以上版本使用
 */
@TargetApi(9)
public class ExternalOverFroyoUtils {
    public enum Dir {
        Model("model"), Cache("cache"), User("user"), Image("image");
        private String name;

        Dir(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 判断是否存在外部存储设备
     *
     * @return 如果不存在返回false
     */
    public static boolean hasExternalStorage() {
        Boolean externalStorage = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        return externalStorage;
    }

    /**
     * 获取目录使用的空间大小
     *
     * @param path 检查的路径路径
     * @return 在字节的可用空间
     */
    @SuppressWarnings("deprecation")
    public static long getUsableSpace(File path) {
        if (AndroidVersionCheckUtils.hasGingerbread()) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * 检查如果外部存储器是内置的或是可移动的。
     *
     * @return 如果外部存储是可移动的(就像一个SD卡)返回为 true,否则false。
     */
    public static boolean isExternalStorageRemovable() {
        if (AndroidVersionCheckUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * 一个散列方法,改变一个字符串(如URL)到一个散列适合使用作为一个磁盘文件名。
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
     *
     * @param context    上下文信息
     * @param uniqueName 目录名字
     * @return 返回目录名字：/Android/data/包名/cache/uniqueName
     */
    @NonNull public static File getDiskCacheDir(Context context, String uniqueName) {
        // 检查是否安装或存储媒体是内置的,如果是这样,试着使用
        // 外部缓存 目录
        // 否则使用内部缓存目录
        String cachePath = context.getCacheDir().getPath();
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState()) || !isExternalStorageRemovable()) {
                cachePath = getExternalCacheDir(context).getPath();
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("DiskCacheDir", e.getMessage() + "");
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
     *
     * @param context 上下文信息
     *                目录名字
     * @return 返回目录名字
     */
    public static File getSystemDiskCacheDir(Context context) {
        // 检查是否安装或存储媒体是内置的,如果是这样,试着使用
        // 外部缓存 目录
        // 否则使用内部缓存 目录

        String cachePath = context.getCacheDir().getPath();
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState()) || !isExternalStorageRemovable()) {
                cachePath = getExternalCacheDir(context).getPath();
            }
        } catch (Exception e) {
            Log.e("DiskCacheDir", e.getMessage());
        }
        return new File(cachePath);
    }

    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }


    /**
     * 获得外部应用程序缓存目录 :/Android/data/com.package.nam/cache/
     *
     * @param context 上下文信息
     * @return 外部缓存目录
     */
    public static File getExternalCacheDir(Context context) {
        if (AndroidVersionCheckUtils.hasFroyo()) {
            return context.getExternalCacheDir();
        }
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }


    /**
     *  下载目录
     * @param context
     * @return
     */
    public static  String getApkDir(Context context){
        String apk = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Android"
                + File.separator + "data"
                + File.separator + context.getPackageName()
                + File.separator + "apk";
        File file  = new File(apk);
        if (!file.exists()) file.mkdirs();
        return apk;
    }
    @NonNull
    public static String getPackageRoot(Context context) {
        return Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Android"
                + File.separator + "data"
                + File.separator + context.getPackageName();
    }

    /**
     * 根据enum 获取不同的package root dir
     *
     * @param context
     * @param name
     * @return
     */
    public static File getCacheDir(Context context, String name) {
        return new File(getPackageRoot(context), name);
    }


}
