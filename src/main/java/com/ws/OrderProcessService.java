package com.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService(serviceName="OrderProcess", portName="OrderProcessPort",
		targetNamespace="http://www.ben.com/orderprocess")
@SOAPBinding(style=Style.RPC,use=Use.LITERAL,
		parameterStyle=ParameterStyle.BARE)
public class OrderProcessService {
	@WebMethod
	public Response processOrder(OrderBean orderBean) {
		System.out.println("传过来数据:"+orderBean.toString());
		Response rs = new Response();
		rs.setData("返回数据");
		rs.setOrderId("xxxsdfsdfsd");
		return rs;
	}
}
