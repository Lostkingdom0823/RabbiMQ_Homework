package com.biz_united.rabbitmqMaven;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChattingRoom extends javax.swing.JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//
	private JButton entryChattingRoom;
	private JTextArea inputUserName;
	private JLabel text;
	private ChattingRoomSetup chattingRoomSetup;
	private JTextArea inputTextArea;
	private JButton sendText;
	private int count = 1;
	private JPanel messageContainer = null;
	private JScrollPane messageWindow;

	// 初始化客户端窗口
	public ChattingRoom() {
		super();
		this.setLayout(null);
		this.setSize(300, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		this.add(getInputUserName(), null);
		this.add(getText(), null);
		this.add(getLoginButton(), null);
		entryChattingRoom.addActionListener(loginAction);
		this.setTitle("Login");
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					chattingRoomSetup.sendMessage(chattingRoomSetup.getChannel(),
							chattingRoomSetup.getUserName() + " has quit the chattingroom");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 设置控件
	private JButton getLoginButton() {
		if (entryChattingRoom == null) {
			entryChattingRoom = new JButton();
			entryChattingRoom.setBounds(100, 110, 80, 25);
			entryChattingRoom.setText("Login");
		}
		return entryChattingRoom;
	}

	private JTextArea getInputUserName() {
		if (inputUserName == null) {
			inputUserName = new JTextArea();
			inputUserName.setBounds(130, 60, 110, 18);
		}
		return inputUserName;
	}

	private JLabel getText() {
		if (text == null) {
			text = new JLabel();
			text.setBounds(45, 60, 80, 18);
			text.setText("User Name:");
		}
		return text;
	}

	// JSscrollPane只能容纳一个组件
	private JScrollPane getMessageWindow() {
		if (messageWindow == null) {
			messageWindow = new JScrollPane();
			messageWindow.setBounds(5, 5, 578, 400);
		}
		return messageWindow;
	}

	private JTextArea getTextInputArea() {
		if (inputTextArea == null) {
			inputTextArea = new JTextArea();
			inputTextArea.setBounds(5, 410, 500, 145);
			inputTextArea.setFont(new Font("宋体", Font.BOLD, 16));
		}
		return inputTextArea;
	}

	private JButton getSendButton() {
		if (sendText == null) {
			sendText = new JButton();
			sendText.setText("Send");
			sendText.setBounds(510, 460, 70, 40);
		}
		return sendText;
	}

	private JPanel getMessageContainer() {
		if (messageContainer == null) {
			messageContainer = new JPanel();
			messageContainer.setLayout(null);
			messageContainer.setBackground(Color.WHITE);
		}
		return messageContainer;
	}

	// 设置登录事件
	private ActionListener loginAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (!inputUserName.getText().equals("")) {
				setTitle(inputUserName.getText());
				chattingRoomSetup = new ChattingRoomSetup(inputUserName.getText());
				entryChattingRoom();
				// 接收信息
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						try {
							chattingRoomSetup.receiveMessage(chattingRoomSetup);
							if (!chattingRoomSetup.getMsg().equals("")) {
								showMessage(chattingRoomSetup.getMsg());
								chattingRoomSetup.setMsg("");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, 200, 200);
			}
		}
	};

	// 设置消息发送事件
	private ActionListener sendMessage = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (!inputTextArea.getText().equals("")) {
				try {
					chattingRoomSetup.sendMessage(chattingRoomSetup.getChannel(),
							chattingRoomSetup.getUserName() + " say:" + inputTextArea.getText());
					inputTextArea.setText(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	};

	private void entryChattingRoom() {
		JPanel jPanel = (JPanel) getContentPane();
		setSize(600, 600);
		jPanel.add(getMessageWindow(), null);
		messageWindow.setVisible(true);
		jPanel.remove(text);
		jPanel.remove(inputUserName);
		jPanel.remove(entryChattingRoom);
		jPanel.add(getTextInputArea(), null);
		jPanel.add(getSendButton(), null);
		sendText.addActionListener(sendMessage);
		repaint();
	}

	private void showMessage(String msg) {
		System.out.println(msg);
		JScrollPane panel = getMessageWindow();
		JPanel messageContainer = getMessageContainer();
		JLabel showMessage = new JLabel();
		showMessage.setFont(new Font("宋体", Font.BOLD, 16));
		showMessage.setBounds(10, count * 30, 500, 30);
		showMessage.setText(msg);
		showMessage.setVisible(true);
		messageContainer.add(showMessage);
		panel.setViewportView(messageContainer);
		panel.repaint();
		count++;
	}

	public void actionPerformed(ActionEvent e) {

	}

	public static void main(String[] args) {
		new ChattingRoom();
	}

}
