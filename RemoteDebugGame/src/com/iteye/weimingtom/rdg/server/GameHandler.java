package com.iteye.weimingtom.rdg.server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GameHandler extends Handler {
	private static final boolean D = false;
	private static final String TAG = "GameHandler";
	
	private IGameUI ui;
	
	public GameHandler(IGameUI ui) {
		this.ui = ui;
	}
	
	@Override
    public void handleMessage(Message msg) {
		String text;
		switch (msg.what) {
    	case IGameUI.MSG_RECV:
    		text = (String) msg.obj;
    		if (D) {
    			Log.d(TAG, "handleMessage:" + text + ", what = " + msg.what + ", arg1 = " + msg.arg1);
    		}
    		if (text != null) {
    			ui.onRecvGameMessage(msg.arg1, text);
    		}
    		break;
    	}
    }
}
