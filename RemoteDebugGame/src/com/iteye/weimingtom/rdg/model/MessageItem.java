package com.iteye.weimingtom.rdg.model;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageItem {
	public static final int TOUCH_DOWN = 1;
	public static final int TOUCH_UP = 2;
	public static final int TOUCH_MOVE = 3;
	
	public static final int ECHO_PING = 11;
	public static final int ECHO_TEXT = 12;
	public static final int MOVE_TO = 13;
	
	private int type;
	private double x;
	private double y;
	private String text;
	
	public int getType() {
		return type;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public String getText() {
		return text;
	}

	public MessageItem(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);
			type = jsonObject.getInt("type");
			switch (type) {
			case TOUCH_DOWN:
			case TOUCH_UP:
			case TOUCH_MOVE:
			case MOVE_TO:
				x = jsonObject.getDouble("x");
				y = jsonObject.getDouble("y");
				break;
			
			case ECHO_PING:
			case ECHO_TEXT:
				text = jsonObject.getString("text");
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public MessageItem(int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public MessageItem(int type, String text) {
		this.type = type;
		this.text = text;
	}
	
	@Override
	public String toString() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			switch (type) {
			case TOUCH_DOWN:
			case TOUCH_UP:
			case TOUCH_MOVE:
			case MOVE_TO:
				jsonObject.put("x", x);
				jsonObject.put("y", y);
				break;
				
			case ECHO_PING:
			case ECHO_TEXT:
				jsonObject.put("text", text);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
