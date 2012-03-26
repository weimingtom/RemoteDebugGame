package com.iteye.weimingtom.rdg;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.iteye.weimingtom.rdg.client.SocketClient;
import com.iteye.weimingtom.rdg.client.PipeClient;
import com.iteye.weimingtom.rdg.model.MessageItem;
import com.iteye.weimingtom.rdg.server.CommonDataQueue;
import com.iteye.weimingtom.rdg.server.SocketServerThread;
import com.iteye.weimingtom.rdg.server.GameHandler;
import com.iteye.weimingtom.rdg.server.GameSurfaceView;
import com.iteye.weimingtom.rdg.server.IGameUI;
import com.iteye.weimingtom.rdg.server.PipeServerThread;

public class RemoteDebugGameActivity extends Activity {
	private static final boolean D = false;
	private static final String TAG = "RemoteDebugGameActivity";

	private GameSurfaceView gameSurfaceView;
	private GameThread gameThread;
	
	private SocketServerThread socketServer;
	private SocketClient socketClient;
	private PipeServerThread pipeServer;
	private PipeClient pipeClient;
	
	// push server data to client, need polling.
	private CommonDataQueue queue = new CommonDataQueue();
	
	// push client data to server, don't need polling.
	private GameHandler handler = new GameHandler(new IGameUI() {
		@Override
		public void onRecvGameMessage(int type, String message) {
			if (D) {
				Log.e(TAG, "type = " + type + " message = " + message);
			}
			MessageItem item = new MessageItem(message);
			switch (item.getType()) {
			case MessageItem.ECHO_TEXT:
				if (gameSurfaceView != null) {
					gameSurfaceView.echoText(item.getText());
				}
				break;
				
			case MessageItem.MOVE_TO:
				if (gameSurfaceView != null) {
					gameSurfaceView.moveTo((int)item.getX(), (int)item.getY());
				}
				break;
			}
		}
    });

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        gameSurfaceView = new GameSurfaceView(this);
    	setContentView(gameSurfaceView);        	
    }

	@Override
	protected void onStop() {
		super.onStop();
		if (D) {
			Log.d(TAG, "onStop");
		}
		stopAll();
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		if (D) {
			Log.d(TAG, "onResume");
		}
		startAll();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (D) {
			Log.d(TAG, "onPause");
		}
		stopAll();
	}
	
	private void startAll() {
		if (gameSurfaceView != null) {
			gameSurfaceView.setCommonDataQueue(queue);
		}
		
		if (socketServer == null) {
			if (D) {
				Log.d(TAG, "new DebugServerThread");
			}
        	try {
				socketServer = new SocketServerThread(handler, queue);
				if (!GameThread.ENABLE_PIPE) {
					socketClient = new SocketClient();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socketServer.start();

		if (pipeServer == null) {
			if (D) {
				Log.d(TAG, "new PipeServerThread");
			}
			try {
				PipedOutputStream pout = new PipedOutputStream();
				PipedInputStream pin = new PipedInputStream(pout);
				PipedOutputStream pout2 = new PipedOutputStream();
				PipedInputStream pin2 = new PipedInputStream(pout2);
				pipeServer = new PipeServerThread(pin, pout2, handler, queue);
				if (GameThread.ENABLE_PIPE) {
					pipeClient = new PipeClient(pout, pin2);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pipeServer.start();

		if (gameThread == null) {
			gameThread = new GameThread(socketClient, pipeClient);
		}
		gameThread.start();
	}
	
	private void stopAll() {
		if (gameThread != null) {
			gameThread.setStop();
			try {
				gameThread.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (socketServer != null) {
			socketServer.setStop();
			try {
				socketServer.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			socketServer = null;
		}
		if (socketClient != null) {
			socketClient.close();
		}
		if (pipeServer != null) {
			pipeServer.setStop();
			try {
				pipeServer.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pipeServer = null;
		}
		if (pipeClient != null) {
			pipeClient.close();
		}
	}
}
