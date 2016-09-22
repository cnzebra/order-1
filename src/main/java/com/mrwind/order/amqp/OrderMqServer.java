package com.mrwind.order.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrwind.order.App;

@Service
public class OrderMqServer {
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	public void sendDataToCrQueue(Object obj) {
		amqpTemplate.convertAndSend(App.ORDER_MQ_3001, obj);
	}	

}
