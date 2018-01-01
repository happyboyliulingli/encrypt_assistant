package com.example.liulingli.encrypt_assistant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

import com.example.liulingli.encrypt_assistant.utils.FileHelper;
import com.example.liulingli.encrypt_assistant.utils.MyUtil;
import android.widget.Toast;

public class ThreadReadWriterIOSocket implements Runnable {
	private Socket client;
	public ThreadReadWriterIOSocket(Context context, Socket client) {
		this.client = client;	
	}

	@Override
	public void run() {
		Log.d(MainActivity.TAG, "A client has connected!");
//		Toast.makeText(UsbConnect.getApplicationContext(), "A client has connected", Toast.LENGTH_SHORT).show();
		BufferedOutputStream out;
		BufferedInputStream in;
		try {
			/* PC端发来的数据msg */
			String currCMD = "";
			out = new BufferedOutputStream(client.getOutputStream());
			in = new BufferedInputStream(client.getInputStream());
			MainActivity.ioThreadFlag = true;
			while (MainActivity.ioThreadFlag) {
				try {
					if (!client.isConnected()) {
						break;
					}
					/* 接收PC发来的命令 */
					Log.e(MainActivity.TAG, Thread.currentThread().getName()
							+ "---->" + "will read......");
					/* 读操作命令 */
					currCMD = readCMDFromSocket(in);
					Log.e(MainActivity.TAG, Thread.currentThread().getName()
							+ "---->" + "**currCMD ==== " + currCMD);
					/* 根据命令分别处理数据 */
					if (currCMD.equals("0")) {
						Log.e(MainActivity.TAG, "start handle client event");
						byte[] filebytes = FileHelper.readFile("test.png");//文件大小
						System.out.println("fileszie = "+filebytes.length);
						Log.e(MainActivity.TAG, "fileszie = "+filebytes.length);
						/* 将整数转成4字节byte数组 */
						byte[] filelength = new byte[4];//文件 长度
						filelength = MyUtil.intToByte(filebytes.length);

						byte[] fileformat = null;  //文件格式
						fileformat = ".png".getBytes(); //得到一个操作系统默认的编码格式的字节数组
						System.out.println("fileformat length=" + fileformat.length);
						Log.e(MainActivity.TAG, "fileformat length=" + fileformat.length);
						/* 字节流中前4字节为文件长度，4字节文件格式，以后是文件流 */
						/* 注意如果write里的byte[]超过socket的缓存，系统自动分包写过去，所以对方要循环写完 */
						System.out.println("write filelength to PC");
						Log.e(MainActivity.TAG, "write filelength to PC");
						out.write(filelength);//输出文件长度
						out.flush();

						System.out.println("write file to PC");
						Log.e(MainActivity.TAG, "write to PC");
						out.write(filebytes);//输出文件
						out.flush();

						/* 客户端反馈：接收成功 */
						String strread = readFromSocket(in);
						System.out.println(" send data success!" + strread);
						Log.e(MainActivity.TAG, "send data success");
						System.out.println("=============================================");
					} else if (currCMD.equalsIgnoreCase("-1")){
						out.write("exit ok".getBytes());
						out.flush();
					}
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* 从InputStream流读取命令 */
	public String readCMDFromSocket(InputStream in) {
		int MAX_BUFFER_BYTES = 2048;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try {
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
			tempbuffer = null;
		} catch (Exception e) {
			Log.v(MainActivity.TAG, Thread.currentThread().getName()
					+ "---->" + "readFromSocket error");
			e.printStackTrace();
		}
		return msg;
	}
	/* 从InputStream流中读数据 */
	public static String readFromSocket(InputStream in) {
		int MAX_BUFFER_BYTES = 4096;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try {
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
}
