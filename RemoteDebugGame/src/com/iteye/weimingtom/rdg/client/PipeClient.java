package com.iteye.weimingtom.rdg.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import android.util.Log;

public class PipeClient {
	private static final boolean D = false;
	private static final String TAG = "PipeClient";
	
	private PipedOutputStream pout;
	private DataOutputStream dout;
	private PipedInputStream pin;
	private DataInputStream din;
	
	public PipeClient(PipedOutputStream pout, PipedInputStream pin) {
		this.pout = pout;
		this.dout = new DataOutputStream(this.pout);
		this.pin = pin;
		this.din = new DataInputStream(this.pin);
	}
	
	public void send(String str) {
		try {
			if (pout != null && dout != null) {
				if (D) {
					Log.d(TAG, "send: " + str);
				}
				dout.writeUTF(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String recv() {
		String str = null;
		try {
			if (pin != null && din != null && din.available() > 0) {
				try {
					str = din.readUTF();
					if (D) {
						Log.d(TAG, "recv: " + str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	//FIXME:
	public void close() {
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
	}
}
