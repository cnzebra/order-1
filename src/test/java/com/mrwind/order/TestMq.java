package com.mrwind.order;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class TestMq {

	 private final static String QUEUE_NAME = "ftpAgent";
	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		// TODO Auto-generated method stub
		com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
		   factory.setHost("http://10.0.1.105");
		   factory.setPort(5672);
		   factory.setUsername("123feng");
		   factory.setPassword("123feng");
		   Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        Channel channel = connection.createChannel();
//	      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	  
	        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	          
	        QueueingConsumer consumer = new QueueingConsumer(channel);
	        channel.basicConsume(QUEUE_NAME, true, consumer);
	  
	        Date nowTime = new Date();
	          
	        while (true) {
	          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	          String message = new String(delivery.getBody());
	          System.out.println("RecieveTime: " + nowTime);
	          System.out.println(" [x] Received '" + message + "'");
	        }
	}

}
