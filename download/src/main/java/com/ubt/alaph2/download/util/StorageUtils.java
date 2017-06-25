package com.ubt.alaph2.download.util;

import android.os.Environment;
import android.os.StatFs;

/**
 * @author: liwushu
 * @description:
 * @created: 2017/6/21
 * @version: 1.0
 * @modify: liwushu
*/

public class StorageUtils {

    private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;

    // public static boolean isSdCardWrittenable() {
    // if
    // (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
    // {
    // return true;
    // }
    // return false;
    // }

    public static long getAvailableStorage() {
        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();
        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    // public static boolean checkAvailableStorage() {
    //
    // if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
    // return false;
    // }
    //
    // return true;
    // }

    // public static boolean isSDCardPresent() {
    //
    // return
    // Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    // }

    // public static Bitmap getLoacalBitmap(String url) {
    //
    // try {
    // FileInputStream fis = new FileInputStream(url);
    // return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
    //
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // return null;
    // }
    // }

    // public static String size(long size) {
    //
    // if (size / (1024 * 1024) > 0) {
    // float tmpSize = (float) (size) / (float) (1024 * 1024);
    // DecimalFormat df = new DecimalFormat("#.##");
    // return "" + df.format(tmpSize) + "MB";
    // } else if (size / 1024 > 0) {
    // return "" + (size / (1024)) + "KB";
    // } else
    // return "" + size + "B";
    // }

    // public static void installAPK(Context context, final String url) {
    //
    // Intent intent = new Intent(Intent.ACTION_VIEW);
    // String fileName = FILE_ROOT + NetworkUtils.getFileNameFromUrl(url);
    // intent.setDataAndType(Uri.fromFile(new File(fileName)),
    // "application/vnd.android.package-archive");
    // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    // intent.setClassName("com.android.packageinstaller",
    // "com.android.packageinstaller.PackageInstallerActivity");
    // context.startActivity(intent);
    // }

    // public static boolean delete(File path) {
    //
    // boolean result = true;
    // if (path.exists()) {
    // if (path.isDirectory()) {
    // for (File child : path.listFiles()) {
    // result &= delete(child);
    // }
    // result &= path.delete(); // Delete empty directory.
    // }
    // if (path.isFile()) {
    // result &= path.delete();
    // }
    // if (!result) {
    // Log.e(null, "Delete failed;");
    // }
    // return result;
    // } else {
    // Log.e(null, "File does not exist.");
    // return false;
    // }
    // }
}
