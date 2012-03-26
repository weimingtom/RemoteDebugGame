package com.iteye.weimingtom.rdg.server;

public interface IGameUI {
	public final static int TYPE_SOCK = 0;
	public final static int TYPE_PIPE = 1;
	
	public final static int MSG_RECV = 0;
	/**
	 * 
	 * @param type : connection type, IGameUI.TYPE_SOCK or IGameUI.TYPE_PIPE 
	 * @param str message body
	 */
	public void onRecvGameMessage(int type, String message);
}
