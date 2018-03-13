package com.ysjr.mmjf.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-11-15.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
  private static final String TAG = CrashHandler.class.getSimpleName();
  private static CrashHandler mInstance;
  private Context mContext;
  private Thread.UncaughtExceptionHandler mDefaultHandler;
  private Map<String, String> mInfo = new HashMap<>();
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private CrashHandler() {
  }

  public static CrashHandler getInstance() {
    if (mInstance == null) {
      synchronized (CrashHandler.class) {
        if (mInstance == null) {
          mInstance = new CrashHandler();
        }
      }
    }
    return mInstance;
  }

  public void init(Context context) {
    mContext = context;
    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  @Override public void uncaughtException(Thread t, Throwable e) {
    if (!handleException(e)) {
      if (mDefaultHandler != null) {
        mDefaultHandler.uncaughtException(t, e);
      }
    } else {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      Process.killProcess(Process.myPid());
      System.exit(1);
    }
  }

  private boolean handleException(Throwable e) {
    if (e == null) {
      return false;
    }
    new Thread(new Runnable() {
      @Override public void run() {
        Looper.prepare();
        Toast.makeText(mContext, "程序异常退出", Toast.LENGTH_LONG).show();
        Looper.loop();
      }
    }).start();
    //        收集错误信息
    collectErrorInfo();
    //        保存错误信息
    saveErrorInfo(e);
    return true;
  }

  private void saveErrorInfo(Throwable e) {
    StringBuffer stringBuffer = new StringBuffer();
    for (Map.Entry<String, String> entry : mInfo.entrySet()) {
      String keyName = entry.getKey();
      String value = entry.getValue();
      stringBuffer.append(keyName + "=" + value + "\n");
      Log.e("Info", keyName + "=" + value + "\n");
    }

    Writer writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    e.printStackTrace(printWriter);
    Throwable cause = e.getCause();
    Log.e("Cause", cause + "");
    if (cause != null) {
      cause.printStackTrace(printWriter);
      cause = e.getCause();
      Log.e("CauseThrowable", cause.toString());
    }
    printWriter.close();
    String result = writer.toString();
    Log.e(TAG, "writer =" + result);
    stringBuffer.append(result);
    long curTime = System.currentTimeMillis();
    String time = dateFormat.format(new Date());
    String fileName = "crash_" + time + "-" + curTime + ".log";
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      String path = Environment.getExternalStorageDirectory() + "/crash/";
      Log.d(TAG, "saveErrorInfo: path=" + path);
      File file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
      }
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(path + fileName);
        fos.write(stringBuffer.toString().getBytes());
      } catch (IOException e1) {
        e1.printStackTrace();
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }

  private void collectErrorInfo() {
    PackageManager pm = mContext.getPackageManager();
    try {
      PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
      if (pi != null) {
        String versionName = TextUtils.isEmpty(pi.versionName) ? "未设置" : pi.versionName;
        String versionCode = pi.versionCode + "";
        mInfo.put("versionName", versionName);
        mInfo.put("versionCode", versionCode);
      }
      Field[] fields = Build.class.getFields();
      for (Field field : fields) {
        field.setAccessible(true);
        mInfo.put(field.getName(), field.get(null).toString());
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
