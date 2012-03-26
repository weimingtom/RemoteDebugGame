package com.iteye.weimingtom.rdg;

import com.iteye.weimingtom.rdg.client.HostSocketClient;
import com.iteye.weimingtom.rdg.model.MessageItem;

public class HostGameThread extends Thread {
	private static final boolean ENABLE_GAME_LOOP = true;
	
	private HostSocketClient debugClient;
	private boolean isRunning = false;
	private int echoTimes;
	
	public HostGameThread(HostSocketClient debugClient) {
		this.debugClient = debugClient;
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
				HostClientUI.log(e.toString());
			}
		}
	}
	
	private void ping(String str) {
		MessageItem sendData = new MessageItem(MessageItem.ECHO_PING, str);
		debugClient.send(sendData.toString());
	}

	private void echoText(String str) {
		MessageItem sendData = new MessageItem(MessageItem.ECHO_TEXT, str);
		debugClient.send(sendData.toString());
	}

	private void moveTo(double x, double y) {
		MessageItem sendData = new MessageItem(MessageItem.MOVE_TO, x, y);
		HostClientUI.log("moveTo : " + sendData.toString());
		debugClient.send(sendData.toString());
	}
	
	private MessageItem recv() {
		String str;
		str = debugClient.recv();
		if (str != null) {
			MessageItem recvData = new MessageItem(str);
			if (recvData != null && recvData.getType() > 0) {
				HostClientUI.log("recvData : " + recvData);
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
