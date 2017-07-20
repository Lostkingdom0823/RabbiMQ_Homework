package com.biz_united.rabbitmqMaven;

import java.io.IOException;
import com.rabbitmq.client.*;

public class ChattingRoomSetup {

	private Connection connection = null;
	private Channel channel = null;
	private Consumer consumer = null;
	private String userName = null;
	private String msg = "";

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUserName() {
		return userName;
	}

	public Connection getConnection() {
		return connection;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public Channel getChannel() {
		return channel;
	}

	/**
	 * rabbitmq host:localhost rabbitmq user:guest rabbitmq password:guest
	 * rabbitmq port:5672 rabbitmq virtual host: /
	 */
	public ChattingRoomSetup(String userName) {

		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5672);
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			this.userName = userName;
			// 设置exchange
			channel.exchangeDeclare("chattingRoomExchange", "fanout", false, true, null);
			// 设置用户队列
			String queueSet = userName + "MessageQueue";
			channel.queueDeclare(queueSet, false, false, true, null);
			channel.queueBind(queueSet, "chattingRoomExchange", "");
			String message = userName + " has joined the chattingroom";

			channel.basicPublish("chattingRoomExchange", userName, null, message.getBytes());
			// 设置客户端consumer
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					if (!message.equals("") || !message.equals(null)) {
						msg = message;
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送信息
	public void sendMessage(Channel channel, String message) throws IOException {
		channel.basicPublish("chattingRoomExchange", "", null, message.getBytes());
	}

	// 接受信息
	public void receiveMessage(ChattingRoomSetup chattingRoomSetup) throws IOException {
		channel.basicConsume(chattingRoomSetup.getUserName() + "MessageQueue", true, chattingRoomSetup.getConsumer());
	}
}
