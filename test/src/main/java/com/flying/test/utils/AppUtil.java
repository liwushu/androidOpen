package com.flying.test.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.WindowManager;

import com.flying.test.TestApplication;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtil {
    
	/**
     * 获取屏幕分辨
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = windowManager.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = windowManager.getDefaultDisplay().getHeight();
		int result[] = { width, height };
		return result;
	}
	public static String getAppVersion(){
		try {
			PackageManager manager = TestApplication.getContext().getPackageManager();
			PackageInfo info = manager.getPackageInfo(TestApplication.getContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return String.valueOf("V "+info.versionName);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
    public static String getPackageName(Context context){
		return context.getPackageName();
	}

	public static String getMD5Values(String origin,String encode){
		String hexStr=MD5Util.MD5Encode(origin,encode);
		return hexStr2str(hexStr);
	}

	private static String hexStr2str(String str){
		String temp="";
		for(int i=0;i<str.length()/2;i++){
			temp=temp+(char)Integer.valueOf(str.substring(i*2,i*2+2),16).byteValue();
		}
		return temp;
	}

	public static int getOSVersion(){
		return Build.VERSION.SDK_INT;
	}

	public static String getDeviceId(){
		return Build.MODEL;
	}

	public static String getCountryCode(){
		try{
			TelephonyManager manager = (TelephonyManager) TestApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getSimCountryIso();
		}catch (Exception e){
			return "";
		}
	}

    public static String getIMEI() {
		try{
			TelephonyManager manager = (TelephonyManager) TestApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getDeviceId();
		}catch (Exception e){
			return "";
		}
	}
	public static String getSystemTime(){
		SimpleDateFormat format=null;
		String timeFormat= Settings.System.getString(TestApplication.getContext().getContentResolver(), Settings.System.TIME_12_24);
		if("12".equals(timeFormat)) {
			format = new SimpleDateFormat("hh:mm");
		}else{
			format=new SimpleDateFormat("HH:mm");
		}
		Date date=new Date(System.currentTimeMillis());
		return format.format(date);
	}
	public static String getSystemDate(){
		Date date=new Date(System.currentTimeMillis());
		String strDate=DateFormat.getMediumDateFormat(TestApplication.getContext()).format(date);
		String strWeek= DateFormat.format("E",date).toString();
		return strWeek+" "+strDate;
	}

    public static String getANDROIDID() {
        String androidId =
                Settings.Secure
                        .getString(TestApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) {
            androidId = "";
        }
        return androidId;
    }

    public static String getUUID() {
        return "";
    }

    public static int getVersionCode() {
        try {
            PackageManager manager = TestApplication.getContext().getPackageManager();
            PackageInfo info =
                    manager.getPackageInfo(TestApplication.getContext().getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial == null ? "" : macSerial;
	}

}