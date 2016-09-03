package com.ws;

public class Response {
	private String orderId;
	private String data;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Response [orderId=" + orderId + ", data=" + data + "]";
	}
	
	
}
