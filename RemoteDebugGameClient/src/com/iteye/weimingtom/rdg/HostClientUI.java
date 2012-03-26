package com.iteye.weimingtom.rdg;

import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import com.iteye.weimingtom.rdg.client.HostSocketClient;

public class HostClientUI {
	private HostGameThread gameThread;
	private HostSocketClient socketClient;
	
	private static TextArea ta;
	
	public HostClientUI() {
		Frame frame = new Frame("HostClientUI");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				onCreate();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				onStop();
				System.exit(0);
			}
		});
		
		ta = new TextArea();
		ta.setEditable(false);
		frame.add(ta);
		
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	
	private void onCreate() {
		try {
			socketClient = new HostSocketClient();
			gameThread = new HostGameThread(socketClient);
			gameThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			log(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			log(e.toString());
		}
	}
	
	private void onStop() {
		if (gameThread != null) {
			try {
				gameThread.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (socketClient != null) {
			socketClient.close();
		}
	}
	
	public static void log(String str) {
		if (str != null) {
			ta.append(str + "\n");
		}
	}
	
	public static final void main(String[] args) {
		new HostClientUI();
	}
}
