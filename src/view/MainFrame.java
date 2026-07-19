package view;

import java.awt.GridLayout;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.RoomMode;
import server.GameServer;
import server.NetworkManager;

public class MainFrame extends JFrame {
	private NetworkManager network;
	private JButton createRoomButton, joinRoomButton;
	private JTextField nameField;

	public MainFrame(NetworkManager network) {
		this.network = network;
		setTitle("Online Game");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createUI();
	}

	private void createUI() {
		JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
		JLabel nameLabel = new JLabel("Tên người chơi:");
		nameField = new JTextField();
		createRoomButton = new JButton("TẠO PHÒNG");
		joinRoomButton = new JButton("NHẬP MÃ PHÒNG");

		panel.add(nameLabel);
		panel.add(nameField);
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
		String playerName = nameField.getText().trim();
		if (playerName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
			return;
		}
		network.setPlayerName(playerName);
		// Khởi động server
		GameServer server = new GameServer();
		server.startInBackground();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String serverIp = getTailscaleIP();
		RoomFrame roomFrame = new RoomFrame(network, serverIp, RoomMode.CREATE, playerName);
		network.setRoomFrame(roomFrame);
		network.connect(serverIp);
		roomFrame.setVisible(true);
		dispose();
	}

	private void joinRoom() {
		String playerName = nameField.getText().trim();
		if (playerName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
			return;
		}
		String ip = JOptionPane.showInputDialog(this, "Nhập mã phòng:");
		if (ip == null || ip.isBlank()) {
			return;
		}
		network.setPlayerName(playerName);
		RoomFrame roomFrame = new RoomFrame(network, ip, RoomMode.JOIN, playerName);
		network.setRoomFrame(roomFrame);
		network.connect(ip);
		roomFrame.setVisible(true);
		dispose();
	}

	private String getPlayerName() {
		String name = nameField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người chơi!");
			return null;
		}
		return name;
	}

	private String getTailscaleIP() {
		try {
			for (InetAddress address : InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())) {
				String ip = address.getHostAddress();
				if (ip.startsWith("100.")) {
					return ip;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "127.0.0.1";
	}
}