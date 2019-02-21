package witparking.inspection.inspectionmqtt;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by LYC on 2017/8/28.
 */

public class FileSaveUtil {

  // 向SD卡写入数据
  public static  void writeSDcard(Context mComtext,String str, String path) {

    try {
      // 判断是否存在SD卡
      if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
        String pkName = mComtext.getPackageName();
        File file2 = new File(Environment.getExternalStorageDirectory() + "/"+pkName+path);

        if(!file2.exists()){
          file2.mkdirs();
        }
        // 获取SD卡的目录
        File sdDire = Environment.getExternalStorageDirectory();
        FileOutputStream outFileStream = new FileOutputStream(
          sdDire.getCanonicalPath() + "/"+pkName+path+"/cache.txt");
        outFileStream.write(str.getBytes());
        outFileStream.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // 从SD卡中读取数据
  public static String readSDcard(Context mComtext, String path) {
    StringBuffer strsBuffer = new StringBuffer();
    try {
      // 判断是否存在SD
      if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
        String pkName = mComtext.getPackageName();
        File file2 = new File(Environment.getExternalStorageDirectory() + "/"+pkName+path);
        if(!file2.exists()){
          file2.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory()
          .getCanonicalPath() + "/"+pkName+path+"/cache.txt");
        // 判断是否存在该文件
        if (file.exists()) {
          // 打开文件输入流
          FileInputStream fileR = new FileInputStream(file);
          BufferedReader reads = new BufferedReader(
            new InputStreamReader(fileR));
          String st = null;
          while ((st = reads.readLine()) != null) {
            strsBuffer.append(st);
          }
          fileR.close();

        } else {
          //该目录下文件不存在

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return strsBuffer.toString();
  }
}
