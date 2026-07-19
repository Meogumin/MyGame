package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import common.Message;
import common.MessageType;
import common.PlayerInfo;
import common.RoomMode;
import server.NetworkManager;

public class RoomFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private NetworkManager network;
	private JTextField codeField;
	private JButton startButton, redButton, blueButton, greenButton, yellowButton;
	private String playerName;
	private JPanel playersPanel;
	private JLabel[] playerLabels;
	private String roomCode;
	private RoomMode mode;
	private String selectedColor;

	public RoomFrame(NetworkManager network, String roomCode, RoomMode mode, String playerName) {
		this.network = network;
		this.roomCode = roomCode;
		this.mode = mode;
		this.playerName = playerName;

		setTitle("Phòng chờ - " + roomCode);
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout(10, 10));
		// =====================================================
		// HEADER
		// =====================================================
		JPanel header = new JPanel(new BorderLayout());
		JLabel title = new JLabel("PHÒNG CHỜ", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		header.add(title, BorderLayout.NORTH);
		JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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

		// =====================================================
		// CENTER
		// =====================================================
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		// =====================================================
		// LEFT - PLAYERS
		// =====================================================
		playersPanel = new JPanel(new GridLayout(4, 1, 5, 5));
		playersPanel.setBorder(BorderFactory.createTitledBorder("NGƯỜI CHƠI"));
		playerLabels = new JLabel[4];

		for (int i = 0; i < 4; i++) {
			playerLabels[i] = new JLabel("  Player " + (i + 1) + ": Đang chờ...");
			playerLabels[i].setFont(new Font("Arial", Font.PLAIN, 16));
			playerLabels[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			playersPanel.add(playerLabels[i]);
		}
		centerPanel.add(playersPanel);

		// =====================================================
		// RIGHT - OPTIONS
		// =====================================================
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout(10, 10));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("TÙY CHỌN"));

		// -------------------------------
		// CHỌN MÀU
		// -------------------------------

		JPanel colorPanel = createColorPanel();
		optionsPanel.add(colorPanel, BorderLayout.NORTH);

		// -------------------------------
		// NHÂN VẬT - TẠM THỜI
		// -------------------------------
		JPanel characterPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		characterPanel.setBorder(BorderFactory.createTitledBorder("NHÂN VẬT"));

		JButton character1 = new JButton("?");
		JButton character2 = new JButton("?");
		JButton character3 = new JButton("?");

		character1.setEnabled(false);
		character2.setEnabled(false);
		character3.setEnabled(false);

		characterPanel.add(character1);
		characterPanel.add(character2);
		characterPanel.add(character3);

		optionsPanel.add(characterPanel, BorderLayout.CENTER);
		centerPanel.add(optionsPanel);
		add(centerPanel, BorderLayout.CENTER);

		// =====================================================
		// FOOTER
		// =====================================================
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JButton leaveButton = new JButton("RỜI PHÒNG");
		leaveButton.addActionListener(e -> {
			// Tạm thời đóng cửa sổ
			dispose();

			// Sau này có thể gửi:
			// PLAYER_LEAVE
		});
		footer.add(leaveButton);
		startButton = new JButton("BẮT ĐẦU GAME");
		if (mode == RoomMode.JOIN) {
			startButton.setEnabled(false);
		}
		startButton.addActionListener(e -> {
			startGame();
		});
		footer.add(startButton);
		add(footer, BorderLayout.SOUTH);
	}

	// =====================================================
	// CHỌN MÀU
	// =====================================================
	private JPanel createColorPanel() {
		JPanel colorPanel = new JPanel(new GridLayout(2, 2, 10, 10));
		colorPanel.setBorder(BorderFactory.createTitledBorder("CHỌN MÀU"));

		redButton = createColorButton("RED", new Color(255, 102, 102));
		blueButton = createColorButton("BLUE", new Color(102, 178, 255));
		greenButton = createColorButton("GREEN", new Color(100, 220, 153));
		yellowButton = createColorButton("YELLOW", new Color(255, 230, 100));

		redButton.addActionListener(e -> selectColor("red"));
		blueButton.addActionListener(e -> selectColor("blue"));
		greenButton.addActionListener(e -> selectColor("green"));
		yellowButton.addActionListener(e -> selectColor("yellow"));

		colorPanel.add(redButton);
		colorPanel.add(blueButton);
		colorPanel.add(greenButton);
		colorPanel.add(yellowButton);
		return colorPanel;
	}

	private JButton createColorButton(String colorName, Color color) {
		JButton button = new JButton("<html><center>" + colorName + "<br>" + "Trống" + "</center></html>");
		button.setBackground(color);
		button.setOpaque(true);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setFocusPainted(false);
		return button;
	}

	private void updateColorButton(JButton button, String colorName, String color, List<PlayerInfo> players) {
		String ownerName = null;
		for (PlayerInfo player : players) {
			if (color.equals(player.getColor())) {
				ownerName = player.getName();
				break;
			}
		}		
		// Màu chưa có ai chọn
		if (ownerName == null) {
			button.setText("<html><center>" + colorName + "<br>Trống" + "</center></html>");
			button.setEnabled(true);
			return;
		}
		// Màu đã có người chọn
		button.setText("<html><center>" + colorName + "<br>" + ownerName + "</center></html>");
		// Chỉ chủ sở hữu được bấm lại để bỏ chọn
		button.setEnabled(ownerName.equals(playerName));
	}

	public void updateColorSelection(List<PlayerInfo> players) {
		updateColorButton(redButton, "Đỏ", "red", players);
		updateColorButton(blueButton, "Xanh", "blue", players);
		updateColorButton(greenButton, "Lục", "green", players);
		updateColorButton(yellowButton, "Vàng", "yellow", players);
	}

	private void selectColor(String color) {
		// Nếu bấm lại màu đang chọn → bỏ chọn
		if (color.equals(selectedColor)) {
			selectedColor = null;
			network.send(new Message(MessageType.COLOR_SELECT, null));
			return;
		}
		// Chọn màu mới
		selectedColor = color;
		network.send(new Message(MessageType.COLOR_SELECT, color));
	}

	// =====================================================
	// START GAME
	// =====================================================
	private void startGame() {
		network.send(new Message(MessageType.START, null));
	}

	// =====================================================
	// UPDATE PLAYERS
	// =====================================================
	public void updatePlayers(List<PlayerInfo> players) {
		for (int i = 0; i < playerLabels.length; i++) {
			if (i < players.size()) {
				PlayerInfo player = players.get(i);
				String text = player.getName();
				if (player.getColor() != null) {
					text += " - " + player.getColor();
				}
				playerLabels[i].setText("  " + text);
			} else {
				playerLabels[i].setText("  Player " + (i + 1) + ": Đang chờ...");
			}
		}
	}

	// =====================================================
	// COPY ROOM CODE
	// =====================================================
	private void copyRoomCode() {
		StringSelection selection = new StringSelection(roomCode);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
}