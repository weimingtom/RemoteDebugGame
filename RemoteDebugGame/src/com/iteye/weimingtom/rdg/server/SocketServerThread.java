package com.iteye.weimingtom.rdg.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


import android.util.Log;

public class SocketServerThread extends Thread {
	private static final boolean D = false;
	private static final String TAG = "DebugServerThread";
	
	public static final int PORT_DEVICE = 7000;
	//$ adb forward tcp:6800 tcp:7000
	public static final int PORT_ADB = 6800;
	
    private ServerSocket serverSock;
    private boolean isRunning = true;
    private GameHandler handler;
    private CommonDataQueue queue;
    
    public SocketServerThread(GameHandler handler, CommonDataQueue queue) throws IOException {
    	this.handler = handler;
		this.queue = queue;
    	serverSock = new ServerSocket(PORT_DEVICE);
    }
    
    @Override
	public void run() {
		Socket socket = null;
		InputStream instr = null;
		OutputStream outstr = null;
		DataInputStream din = null;
		DataOutputStream dout = null;
		String s;
		if (serverSock != null) {
			isRunning = true;
			while (isRunning && serverSock != null && handler != null) {
				try {
					if (D) {
						Log.d(TAG, "Echo server is listening on " + serverSock.getLocalPort());
					}
					socket = serverSock.accept();
					instr = socket.getInputStream();
					outstr = socket.getOutputStream();
					din = new DataInputStream(instr);
					dout = new DataOutputStream(outstr);
					while (isRunning && serverSock != null) {
						if (D) {
							Log.d(TAG, "please wait client's message...");
						}
						s = din.readUTF();
						if (s == null) {
						    break;
						}
						if (D) {
							Log.d(TAG, "client said:" + s);
						}
						handler.sendMessage(handler.obtainMessage(IGameUI.MSG_RECV, IGameUI.TYPE_SOCK, 0, s));
						String[] jsons = queue.getJSON();
						if (D && jsons.length > 0) {
							Log.e(TAG, "getJSON : " + jsons.length);
						}
						for (int i = 0; i < jsons.length; i++) {
							dout.writeUTF(jsons[i]);
						}
						dout.flush();
					}
				} catch (EOFException e) {
					//do nothing
				} catch (SocketException e) {
					//do nothing
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (din != null) {
						try {
							din.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (dout != null) {
						try {
							dout.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (instr != null) {
						try {
						    instr.close();
						} catch (IOException e) {
						    e.printStackTrace();
						}
					}
					if (outstr != null) {
						try {
						    outstr.close();
						} catch (IOException e) {
						    e.printStackTrace();
						}
					}
					if (socket != null) {
						try {
						    socket.close();
						} catch (IOException e) {
						    e.printStackTrace();
						}
					}
				}
			}
		}
	}    
	
	public void setStop() {
		if (isRunning) {
		    isRunning = false;
		}
		if (!serverSock.isClosed()) {
		    try {
		    	serverSock.close();
		    } catch (Exception ex) {
		    	ex.printStackTrace();
		    }
		}
	}
}
