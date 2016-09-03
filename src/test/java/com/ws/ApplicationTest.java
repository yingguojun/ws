package com.ws;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.ws.client.OrderBean;
import com.ws.client.OrderProcessService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApplicationTest {
	@Test
	public void testClient() {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://localhost:8080/services/orderprocess?wsdl");
		soapFactoryBean.setServiceClass(OrderProcessService.class);
		OrderProcessService service = (OrderProcessService) soapFactoryBean.create();
		OrderBean order = new OrderBean();
		order.setData("客户端发送数据");
		com.ws.client.Response rs = service.processOrder(order);
		System.out.println(rs.getData());
		System.out.println("调用完成");
	
	}
}
