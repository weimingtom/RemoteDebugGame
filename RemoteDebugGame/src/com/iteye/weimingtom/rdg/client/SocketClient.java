package com.iteye.weimingtom.rdg.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.iteye.weimingtom.rdg.server.SocketServerThread;

import android.util.Log;

public class SocketClient {
	private static final boolean D = false;
	private static final String TAG = "DebugClient";
	
	private Socket sock = null;
	private OutputStream ostr = null;
	private DataOutputStream dout = null;
	private InputStream istr = null;
	private DataInputStream din = null;
	
	public SocketClient() throws UnknownHostException, IOException {
		sock = new Socket("127.0.0.1", SocketServerThread.PORT_DEVICE);
		ostr = sock.getOutputStream();
		dout = new DataOutputStream(ostr);
		istr = sock.getInputStream();
		din = new DataInputStream(istr);
	}
	
	public void send(String str) {
		if (sock != null && sock.isConnected() && ostr != null && dout != null) {
			try {
				if (D) {
					Log.d(TAG, "send: " + str);
				}
				dout.writeUTF(str);
				dout.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String recv() {
		String str = null;
		try {
			if (sock != null && sock.isConnected() && istr != null && din != null && din.available() > 0) {
				try {
					str = din.readUTF();
					if (D) {
						Log.d(TAG, "recv: " + str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			//do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public void close() {
		if (dout != null) {
			try {
				dout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (din != null) {
			try {
				din.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ostr != null) {
			try {
				ostr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (istr != null) {
			try {
				istr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
