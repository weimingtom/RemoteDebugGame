package com.iteye.weimingtom.rdg;

import com.iteye.weimingtom.rdg.client.SocketClient;
import com.iteye.weimingtom.rdg.client.PipeClient;
import com.iteye.weimingtom.rdg.model.MessageItem;

import android.util.Log;

public class GameThread extends Thread {
	private static final boolean D = false;
	private static final String TAG = "GameThread";
	
	public static final boolean ENABLE_GAME_LOOP = false;
	public static final boolean ENABLE_PIPE = true;
	
	private SocketClient debugClient;
	private PipeClient pipeClient;
	private boolean isRunning = false;
	private int echoTimes;
	
	public GameThread(SocketClient debugClient, PipeClient pipeClient) {
		this.debugClient = debugClient;
		this.pipeClient = pipeClient;
	}
	
	public void setStop() {
		isRunning = false;
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			if (ENABLE_GAME_LOOP) {
				ping("Hello World! " + echoTimes + " !");
				echoTimes++;
				while (true) {
					if (recv() == null) {
						break;
					}
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void ping(String str) {
		MessageItem sendData = new MessageItem(MessageItem.ECHO_PING, str);
		if (ENABLE_PIPE) {
			pipeClient.send(sendData.toString());
		} else {
			debugClient.send(sendData.toString());
		}
	}

	private void echoText(String str) {
		MessageItem sendData = new MessageItem(MessageItem.ECHO_TEXT, str);
		if (ENABLE_PIPE) {
			pipeClient.send(sendData.toString());
		} else {
			debugClient.send(sendData.toString());
		}
	}

	private void moveTo(double x, double y) {
		MessageItem sendData = new MessageItem(MessageItem.MOVE_TO, x, y);
		if (ENABLE_PIPE) {
			pipeClient.send(sendData.toString());
		} else {
			debugClient.send(sendData.toString());
		}
	}
	
	private MessageItem recv() {
		String str;
		if (ENABLE_PIPE) {
			str = pipeClient.recv();
		} else {
			str = debugClient.recv();
		}
		if (str != null) {
			MessageItem recvData = new MessageItem(str);
			if (recvData != null && recvData.getType() > 0) {
				if (D) {
					Log.e(TAG, "recvData : " + recvData);
				}
				echoText("recvData : " + recvData);
				switch (recvData.getType()) {
				case MessageItem.TOUCH_DOWN:
				case MessageItem.TOUCH_MOVE:
				case MessageItem.TOUCH_UP:
					moveTo(recvData.getX(), recvData.getY());
					break;
				}
				return recvData;
			}
		}
		return null;
	}
}
