package com.iteye.weimingtom.rdg.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.iteye.weimingtom.rdg.model.MessageItem;

public class CommonDataQueue {
	private Object queueLock = new Object();
	private Queue<MessageItem> queue = new LinkedList<MessageItem>(); 
	
	public void touchDown(double x, double y) {
		synchronized (queueLock) {
			queue.offer(new MessageItem(MessageItem.TOUCH_DOWN, x, y));
		}
	}

	public void touchUp(double x, double y) {
		synchronized (queueLock) {
			queue.offer(new MessageItem(MessageItem.TOUCH_UP, x, y));
		}
	}

	public void touchMove(double x, double y) {
		synchronized (queueLock) {
			queue.offer(new MessageItem(MessageItem.TOUCH_MOVE, x, y));
		}
	}
	
	public String[] getJSON() {
		ArrayList<String> list = new ArrayList<String>();
		MessageItem data;
		synchronized (queueLock) {
			while ((data = queue.poll()) != null) {  
	            list.add(data.toString()); 
	        }
		}
		return (String[])list.toArray(new String[list.size()]);
	}
}
