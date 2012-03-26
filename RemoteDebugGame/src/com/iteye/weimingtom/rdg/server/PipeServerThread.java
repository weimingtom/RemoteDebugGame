package com.iteye.weimingtom.rdg.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import android.os.Handler;
import android.util.Log;

public class PipeServerThread extends Thread {
	private static final boolean D = false;
	private static final String TAG = "PipeServerThread";
	
	private PipedInputStream pin;
	private DataInputStream din;
	private PipedOutputStream pout;
	private DataOutputStream dout;
	private Handler handler;
	private CommonDataQueue queue;
	private boolean isLisening = false;
	
	public PipeServerThread(PipedInputStream pin, PipedOutputStream pout, Handler handler, CommonDataQueue queue) {
		this.pin = pin;
		this.din = new DataInputStream(pin);
		this.pout = pout;
		this.dout = new DataOutputStream(pout);
		this.handler = handler;
		this.queue = queue;
	}
	
	@Override
	public void run() {
		if (din != null && handler != null) {
			isLisening = true;
			while (isLisening) {
				try {
					String s = din.readUTF();
					handler.sendMessage(handler.obtainMessage(IGameUI.MSG_RECV, IGameUI.TYPE_PIPE, 0, s));
					String[] jsons = queue.getJSON();
					if (D && jsons.length > 0) {
						Log.e(TAG, "getJSON : " + jsons.length);
					}
					for (int i = 0; i < jsons.length; i++) {
						dout.writeUTF(jsons[i]);
					}
					dout.flush();
				} catch (EOFException e) {
					isLisening = false;
				} catch (IOException e) {
					//e.printStackTrace();
					isLisening = false;
				}
			}
		}
	}
	
	public void setStop() {
		isLisening = false;
		if (din != null) {
			try {
				din.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (pin != null) {
			try {
				pin.close();
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
		if (pout != null) {
			try {
				pout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
