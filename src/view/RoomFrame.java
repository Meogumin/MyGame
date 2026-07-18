package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.NetworkManager;
import common.Message;
import common.MessageType;
import common.RoomMode;

public class RoomFrame extends JFrame {
	private NetworkManager network;
	private JTextField codeField;
	private JButton startButton;
	private JPanel playersPanel;
	private JLabel[] playerLabels;
	private String roomCode;
	private RoomMode mode;

	public RoomFrame(NetworkManager network, String roomCode, RoomMode mode) {
		this.network = network;
		this.roomCode = roomCode;
		this.mode = mode;
		setTitle("Phòng chờ - " + roomCode);
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout(10, 10));

		// =========================
		// HEADER
		// =========================
		JPanel header = new JPanel(new BorderLayout());
		JLabel title = new JLabel("PHÒNG CHỜ", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		header.add(title, BorderLayout.NORTH);
		JPanel codePanel = new JPanel();
		JLabel codeLabel = new JLabel("Mã phòng:");
		codeField = new JTextField(roomCode);
		codeField.setEditable(false);
		codeField.setHorizontalAlignment(JTextField.CENTER);
		codeField.setPreferredSize(new Dimension(150, 35));
		JButton copyButton = new JButton("COPY");
		copyButton.addActionListener(e -> {
			copyRoomCode();
		});
		codePanel.add(codeLabel);
		codePanel.add(codeField);
		codePanel.add(copyButton);
		header.add(codePanel, BorderLayout.CENTER);
		add(header, BorderLayout.NORTH);

		// =========================
		// PLAYERS
		// =========================
		playerLabels = new JLabel[4];
		playersPanel = new JPanel(new GridLayout(2, 2, 10, 10));
		playersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		for (int i = 0; i < 4; i++) {
			playersPanel.add(createPlayerSlot(i));
		}
		add(playersPanel, BorderLayout.CENTER);

		//
		startButton = new JButton("BẮT ĐẦU GAME");
		if (mode == RoomMode.JOIN) {
			startButton.setEnabled(false);
		}

		startButton.addActionListener(e -> {
			startGame();
		});
		add(startButton, BorderLayout.SOUTH);
	}

	private void startGame() {
		network.send(new Message(MessageType.START, null));
	}

	private JPanel createPlayerSlot(int index) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Player " + (index + 1)));
		JLabel status = new JLabel("Đang chờ...", JLabel.CENTER);
		playerLabels[index] = status;
		panel.add(status, BorderLayout.CENTER);
		return panel;
	}

	public void updatePlayers(List<String> players) {
		for (int i = 0; i < playerLabels.length; i++) {
			if (i < players.size()) {
				playerLabels[i].setText(players.get(i));
			} else {
				playerLabels[i].setText("Đang chờ...");
			}
		}
	}

	private void copyRoomCode() {
		StringSelection selection = new StringSelection(roomCode);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
}