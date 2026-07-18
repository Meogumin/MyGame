package view;

import java.awt.GridLayout;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.NetworkManager;
import common.RoomMode;
import server.GameServer;

public class MainFrame extends JFrame {
	private NetworkManager network;
	private JButton createRoomButton, joinRoomButton;

	public MainFrame(NetworkManager network) {
		this.network = network;
		setTitle("Online Game");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createUI();
	}

	private void createUI() {
		createRoomButton = new JButton("TẠO PHÒNG");
		joinRoomButton = new JButton("NHẬP MÃ PHÒNG");
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		panel.add(createRoomButton);
		panel.add(joinRoomButton);
		add(panel);
		createRoomButton.addActionListener(e -> {
			createRoom();
		});

		joinRoomButton.addActionListener(e -> {
			joinRoom();
		});
	}

	private void createRoom() {
		// 1. Khởi động server
		GameServer server = new GameServer();
		server.startInBackground();

		// 2. Đợi server mở port
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 3. Lấy IP
		String ip = getLocalIP();

		// 4. Tạo giao diện phòng
		RoomFrame roomFrame = new RoomFrame(network, ip, RoomMode.CREATE);
		network.setRoomFrame(roomFrame);

		// 5. Kết nối client vào server
		network.connect(ip);
		roomFrame.setVisible(true);
		dispose();
	}

	private void joinRoom() {
		String ip = JOptionPane.showInputDialog(this, "Nhập mã phòng:");
		if (ip == null || ip.isBlank()) {
			return;
		}
		network.connect(ip);
		RoomFrame roomFrame = new RoomFrame(network, ip, RoomMode.JOIN);
		network.setRoomFrame(roomFrame);
		roomFrame.setVisible(true);
		dispose();
	}

	private String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
			return "127.0.0.1";
		}
	}
}