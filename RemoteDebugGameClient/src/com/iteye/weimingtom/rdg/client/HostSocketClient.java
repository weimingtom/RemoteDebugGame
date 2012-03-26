package com.iteye.weimingtom.rdg.client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.iteye.weimingtom.rdg.HostClientUI;
import com.iteye.weimingtom.rdg.server.SocketServerThread;

public class HostSocketClient {
	private Socket sock = null;
	private OutputStream ostr = null;
	private DataOutputStream dout = null;
	private InputStream istr = null;
	private DataInputStream din = null;
	
	public HostSocketClient() throws UnknownHostException, IOException {
		sock = new Socket("127.0.0.1", SocketServerThread.PORT_ADB);
		ostr = sock.getOutputStream();
		dout = new DataOutputStream(ostr);
		istr = sock.getInputStream();
		din = new DataInputStream(istr);
	}
	
	public void send(String str) {
		if (sock != null && sock.isConnected() && ostr != null && dout != null) {
			try {
				//HostClientUI.log("send : " + str);
				dout.writeUTF(str);
				dout.flush();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
	}
	
	public String recv() {
		String str = null;
		try {
			if (sock != null && sock.isConnected() && istr != null && din != null && din.available() > 0) {
				try {
					str = din.readUTF();
					//HostClientUI.log("recv : " + str);
				} catch (IOException e) {
					e.printStackTrace();
					HostClientUI.log(e.toString());
				}
			}
		} catch (SocketException e) {
			//do nothing
			HostClientUI.log(e.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			HostClientUI.log(e.toString());
		}
		return str;
	}
	
	public void close() {
		if (dout != null) {
			try {
				dout.close();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
		if (din != null) {
			try {
				din.close();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
		if (ostr != null) {
			try {
				ostr.close();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
		if (istr != null) {
			try {
				istr.close();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
				HostClientUI.log(e.toString());
			}
		}
	}
}
