package com.example.liulingli.encrypt_assistant.utils;

import android.os.Environment;
import android.util.Log;

import com.example.liulingli.encrypt_assistant.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
	private static String FILEPATH = "/storage/emulated/0/aaaa/";
	 
	 public static File newFile(String filename){
		 File file = null;
		 
		 try {
			 file=new File(FILEPATH,filename);
			 file.delete();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	 }
	 
	 public static void writeFile(File file,byte[] data,int offset,int count)throws IOException{
		 FileOutputStream fos = new FileOutputStream(file,true);
		 fos.write(data,offset,count);
		 fos.flush();
		 fos.close();
	 }
	 
	 public static byte[] readFile(String filename) throws IOException{
		 try{
			 String sdInnserPath = Environment.getExternalStorageDirectory().getPath();
			 String filePath = sdInnserPath + "/aaaa";
			 File file = new File(filePath, "a.txt");
			 FileInputStream fin = new FileInputStream(file);
			 int length = fin.available();
			 byte [] buffer = new byte[length];
			 fin.read(buffer);
			 fin.close();
			 return buffer;
		 }catch(Exception e){
			 Log.e(MainActivity.TAG, "error:"+ e.getMessage());
			 e.printStackTrace();
		 }
		 return null;
	 }
}
