package com.netty.test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.netty.test.client.NettyClient;
import com.netty.test.event.EventType;
import com.netty.test.event.TestEvent;
import com.netty.test.eventbus.EventBusFactory;
import com.netty.test.eventbus.IEventListener;

/**
 * 客户端启动入口
 * 
 * @author lucher
 *
 */
public class RunClient {

	// Server端IP地址，根据实际情况进行修改
	static final String HOST = System.getProperty("host", "127.0.0.1");
	// Netty服务端监听端口号
	static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));
	// Netty客户端
	private static NettyClient mClient;
	// 消息显示区域
	private static JTextArea taArea;

	public static void main(String[] args) throws Exception {
		// 注册EventBus监听
		EventBusFactory.build().register(EventListener.class);
		// 显示窗口
		showWindow();
		// 连接服务端
		mClient = new NettyClient(HOST, PORT);
		mClient.start();
	}

	/**
	 * 显示窗口
	 */
	private static void showWindow() {
		// 窗口初始化，位置大小等
		JFrame frame = new JFrame("Netty Java客户端");
		Container container = new Container();
		double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setSize(850, 350);// 设定窗口大小
		frame.setLocation(new Point((int) (lx - frame.getWidth()) / 2, (int) (ly - frame.getHeight()) / 2));// 设定窗口出现位置
		frame.setResizable(false);
		frame.setContentPane(container);// 设置布局

		// 消息编辑框
		TextField tfMsg = new TextField();
		tfMsg.setBounds(25, 10, 800, 25);
		tfMsg.setText("Java客户端本来不准备加界面，强迫症不让我这么干，还是搞个界面比较美观哈哈");
		container.add(tfMsg);

		// 发送按钮
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(25, 45, 800, 25);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 发送测试消息
				mClient.sendMessage(new TestEvent(EventType.TEST_EVENT, "Java", tfMsg.getText()));
			}
		}); // 添加事件处理
		container.add(btnSend);

		// 消息显示
		taArea = new JTextArea();
		taArea.setBackground(Color.LIGHT_GRAY);
		taArea.setMargin(new Insets(5, 5, 5, 5));
		taArea.setBounds(25, 80, 800, 230);
		taArea.setEditable(false);
		container.add(taArea);

		frame.setVisible(true);// 窗口可见
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序

		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				int result = JOptionPane.showConfirmDialog(null, "是否要关闭？", "提示", 2);
				if (result == 0) {// 点击确定按钮
					EventBusFactory.build().unregister(EventListener.class);// 这个取消注册过后，貌似还能收到消息，需要再研究下
				}
			}
		});
	}

	/**
	 * EventBus事件监听
	 * 
	 * @author lucher
	 *
	 */
	public static class EventListener implements IEventListener {

		@Override
		@Subscribe
		@AllowConcurrentEvents
		public void onEvent(String message) {
			System.out.println("Message:" + message);
			taArea.insert(message + "\n",0);
		}
	}
}
