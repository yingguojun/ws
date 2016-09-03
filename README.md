# CXF+SpringBoot webservice
[TOC]
## 简介

本文源码：[https://github.com/zhangchuanben/ws.git](https://github.com/zhangchuanben/ws.git "git源码")
[引用地址](http://www.ibm.com/developerworks/webservices/tutorials/ws-jax/ws-jax.html "原文引用")，这篇文章介绍了一些webservice的基础，以及我们应如何去编写一个webservice。本人也是初学者，如果有不合适的地方还望指正。
写一个webservice有两种方式

> 引用块内容 wsdl优先，从wsdl文件开始编写，然后生成服务端代码

> 代码优先，从java代码开始编写，然后生成相应的wsdl文件

wsdl优先比较困难，因为我们必须wsdl的定义以及xsd文件都比较熟悉，所以本文基于**代码优先**

## 服务端编写
``` java 
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
		parameterStyle=ParameterStyle.WRAPPED)
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
```
这段代码采用JAX-WS声明了一个webservice服务端。但是要注意@SOAPBinding这个注解，它的Style属性有效值为Style.RPC和Style.DOCUMENT，当我选择style=Style.DOCUMENT时，生成客户端是有问题的，生成的客户端方法没有返回值，这显然与我服务端的定义是不符的，但是用[SoapUi](https://www.soapui.org/ "SoapUi")测试却是能通过的。
还有一点需要注意，@SOAPBinding的parameterStyle属性，它的有效值为ParameterStyle.BARE和ParameterStyle.WRAPPED，当我设置parameterStyle=ParameterStyle.WRAPPED生成客户端时会将入参和返回值进行包装。
### 使用jdk自带的工具wsgen生成wsdl和xsd文件（不是必要，可跳过）
打开控制台输入：

> wsgen

如果正确配置了java的jdk环境应该可以看到类似信息
![wsgen使用说明](http://img.blog.csdn.net/20160903110822097)
在编译路径（\target\classes 目录）初打开cmd，输入：

> wsgen -cp . com.ws.OrderProcessService -keep -wsdl

![这里写图片描述](http://img.blog.csdn.net/20160903111354441)
ok，你可以在当前目录下看到三个文件：
![这里写图片描述](http://img.blog.csdn.net/20160903112258345)
如果想使用可以将生成的文件复制到资源目录（maven项目下的src/main/resources）
## 发布服务端
服务端的发布有很多种方法，例如JAX-WS的

``` java 
public class OrderWebServicePublisher {

	public static void main(String[] args) {

		Endpoint.publish("http://localhost:8080/OrderProcessWeb/orderprocess",
				new OrderProcessService());

	}

}
```
而我采用的是springboot+cxf，具体发布代码为：
``` java 
package com.ws;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebserviceConfig {
	@Autowired
    private Bus bus;

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, new OrderProcessService());
        endpoint.publish("/orderprocess");
        return endpoint;
    }
}

```
因为cxf   starter 默认地址为/services/发布的服务名称，所以访问地址为：

> http://localhost:8080/services/orderprocess?wsdl

如果想了解更多cxf starter的用法建议参照：
[cxf+springboot](http://cxf.apache.org/docs/springboot.html "cxf-springboot")
如果浏览器显示如下信息，恭喜服务端已经搭建好了。你可以用[SoapUi](https://www.soapui.org/ "SoapUi")进行测试
![这里写图片描述](http://img.blog.csdn.net/20160903113848032)
## 使用jdk wsimport工具生成客户端
打开控制台输入：

> wsimport

如果正确配置了java的jdk环境应该可以看到类似信息
![wsimport说明](http://img.blog.csdn.net/20160903114120493)
在编译路径（\target\classes 目录）初打开cmd，输入：

> wsimport -d . -p com.ws.client http://localhost:8080/services/orderprocess?wsdl -keep -encoding utf-8

如果在./com/ws/client/目录下看到![这里写图片描述](http://img.blog.csdn.net/20160903114502966)
则说明生成客户端成功了，将客户端的源文件保存到自己需要的位置
## 调用客户端测试 ##

```
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

```
有一点需要注意如果生成的客户端OrderProcessService.java并不是类似于

```

package com.ws.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "OrderProcessService", targetNamespace = "http://www.ben.com/orderprocess")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface OrderProcessService {


    /**
     * 
     * @param arg0
     * @return
     *     returns com.ws.client.Response
     */
    @WebMethod
    @WebResult(partName = "return")
    public Response processOrder(
        @WebParam(name = "arg0", partName = "arg0")
        OrderBean arg0);

}

```
请检查服务端的配置