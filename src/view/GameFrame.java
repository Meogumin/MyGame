package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import common.Message;
import common.MessageType;
import server.NetworkManager;

public class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private NetworkManager network;
	private BoardPanel boardPanel;
	private JPanel gameArea, leftPanel, rightPlayerPanel;
	private JPanel centerPanel, playerPanel1, playerPanel2, playerPanel3, playerPanel4;
	private JButton rollButton, deployButton, skipButton;
	private JLabel diceLabel, turnLabel;
	private JTextArea historyArea;

	public GameFrame(NetworkManager network) {
		this.network = network;
		setTitle("Cờ cá ngựa");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout());

		// =====================================================
		// BÀN CỜ
		// =====================================================
		boardPanel = new BoardPanel();
		createGameArea();
		add(gameArea, BorderLayout.CENTER);

		// =====================================================
		// PANEL BÊN PHẢI
		// =====================================================
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setPreferredSize(new Dimension(300, 0));

		// =====================================================
		// DICE + TURN + BUTTONS
		// =====================================================
		JPanel topPanel = new JPanel(new BorderLayout());

		// -----------------------------
		// DICE
		// -----------------------------
		diceLabel = new JLabel("?", SwingConstants.CENTER);
		diceLabel.setFont(new Font("Arial", Font.BOLD, 80));
		diceLabel.setOpaque(true);
		diceLabel.setBackground(new Color(240, 240, 240));
		diceLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
		diceLabel.setPreferredSize(new Dimension(100, 120));
		topPanel.add(diceLabel, BorderLayout.NORTH);

		// -----------------------------
		// TURN
		// -----------------------------
		turnLabel = new JLabel("Đang chờ lượt...", SwingConstants.CENTER);
		turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
		turnLabel.setOpaque(true);
		turnLabel.setBackground(Color.LIGHT_GRAY);
		turnLabel.setPreferredSize(new Dimension(100, 45));
		topPanel.add(turnLabel, BorderLayout.CENTER);

		// -----------------------------
		// BUTTONS
		// -----------------------------
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
		rollButton = new JButton("ROLL DICE");
		deployButton = new JButton("DEPLOY");
		skipButton = new JButton("SKIP TURN");
		buttonPanel.add(rollButton);
		buttonPanel.add(deployButton);
		buttonPanel.add(skipButton);
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		actionPanel.add(buttonPanel, BorderLayout.CENTER);
		topPanel.add(actionPanel, BorderLayout.SOUTH);

		// =====================================================
		// LỊCH SỬ
		// =====================================================
		historyArea = new JTextArea();
		historyArea.setEditable(false);
		historyArea.setLineWrap(true);
		historyArea.setWrapStyleWord(true);
		historyArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(historyArea);
		JPanel historyPanel = new JPanel(new BorderLayout());
		historyPanel.add(scrollPane, BorderLayout.CENTER);

		// =====================================================
		// GỘP PANEL BÊN PHẢI
		// =====================================================
		rightPanel.add(topPanel, BorderLayout.NORTH);
		rightPanel.add(historyPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);

		// =====================================================
		// SỰ KIỆN NÚT
		// =====================================================
		rollButton.addActionListener(e -> {
			System.out.println("ROLL DICE");
			if (network != null) {
				network.send(new Message(MessageType.ROLL, null));
			}
		});

		deployButton.addActionListener(e -> {
			System.out.println("DEPLOY");
			if (network != null) {
				network.send(new Message(MessageType.DEPLOY, null));
			}
		});

		skipButton.addActionListener(e -> {
			System.out.println("SKIP TURN");
			if (network != null) {
				network.send(new Message(MessageType.SKIP, null));
			}
		});
	}

	private void createGameArea() {
		gameArea = new JPanel(new BorderLayout(5, 5));

		// =====================================================
		// BÊN TRÁI - PLAYER 1 + PLAYER 3
		// =====================================================
		leftPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		leftPanel.setPreferredSize(new Dimension(200, 0));
		playerPanel1 = createPlayerPanel("PLAYER 1");
		playerPanel3 = createPlayerPanel("PLAYER 3");
		leftPanel.add(playerPanel1);
		leftPanel.add(playerPanel3);

		// =====================================================
		// BÀN CỜ
		// =====================================================
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(boardPanel, BorderLayout.CENTER);

		// =====================================================
		// BÊN PHẢI - PLAYER 2 + PLAYER 4
		// =====================================================
		rightPlayerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		rightPlayerPanel.setPreferredSize(new Dimension(200, 0));
		playerPanel2 = createPlayerPanel("PLAYER 2");
		playerPanel4 = createPlayerPanel("PLAYER 4");
		rightPlayerPanel.add(playerPanel2);
		rightPlayerPanel.add(playerPanel4);

		// =====================================================
		// GỘP
		// =====================================================
		gameArea.add(leftPanel, BorderLayout.WEST);
		gameArea.add(centerPanel, BorderLayout.CENTER);
		gameArea.add(rightPlayerPanel, BorderLayout.EAST);
	}

	private JPanel createPlayerPanel(String playerName) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		panel.setBackground(Color.DARK_GRAY);

		// =====================================================
		// KHU VỰC NHÂN VẬT - KÍCH THƯỚC LỚN
		// =====================================================
		JLayeredPane characterArea = new JLayeredPane();
		characterArea.setPreferredSize(new Dimension(250, 300));

		// =====================================================
		// AVATAR
		// =====================================================
		JLabel avatarLabel = new JLabel();
		ImageIcon avatarIcon = new ImageIcon("img/avatar.jpg");
		Image avatarImage = avatarIcon.getImage().getScaledInstance(145, 145, Image.SCALE_SMOOTH);
		avatarLabel.setIcon(new ImageIcon(avatarImage));

		// Avatar nằm phía sau
		avatarLabel.setBounds(25, 25, 145, 145);

		// =====================================================
		// KHUNG LỚN
		// =====================================================
		JLabel frameLabel = new JLabel();
		ImageIcon frameIcon = new ImageIcon("img/character_frame.png");
		Image frameImage = frameIcon.getImage().getScaledInstance(205, 423, Image.SCALE_SMOOTH);
		frameLabel.setIcon(new ImageIcon(frameImage));

		// Khung phủ gần như toàn bộ khu vực nhân vật
		frameLabel.setBounds(-5, -5, 203, 423);

		// =====================================================
		// THỨ TỰ HIỂN THỊ
		// =====================================================
		// Avatar ở phía sau
		characterArea.add(avatarLabel, Integer.valueOf(100));
		// Khung lớn ở phía trước
		characterArea.add(frameLabel, Integer.valueOf(200));

		// =====================================================
		// TÊN NHÂN VẬT
		// =====================================================
		JLabel characterNameLabel = new JLabel(playerName, SwingConstants.CENTER);
		characterNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
		characterNameLabel.setOpaque(true);
		characterNameLabel.setBackground(new Color(0, 0, 0, 150));
		characterNameLabel.setForeground(Color.WHITE);

		// =====================================================
		// KHU VỰC TRÊN
		// =====================================================
		JPanel characterPanel = new JPanel(new BorderLayout());
		characterPanel.setOpaque(false);
		characterPanel.add(characterArea, BorderLayout.CENTER);
//		characterPanel.add(characterNameLabel, BorderLayout.SOUTH);

		// =====================================================
		// TÚI ĐỒ
		// =====================================================
		JPanel inventoryPanel = new JPanel(new GridLayout(1, 4, 5, 5));
		inventoryPanel.setOpaque(false);
		for (int i = 0; i < 4; i++) {
			JLabel itemSlot = new JLabel("ITEM", SwingConstants.CENTER);
			itemSlot.setOpaque(true);
			itemSlot.setBackground(new Color(0, 0, 0, 120));
			itemSlot.setForeground(Color.WHITE);
			itemSlot.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			inventoryPanel.add(itemSlot);
		}

		// =====================================================
		// GỘP
		// =====================================================
		panel.add(characterPanel, BorderLayout.CENTER);
//		panel.add(inventoryPanel, BorderLayout.SOUTH);
		return panel;
	}

	// =====================================================
	// CÁC METHOD CẬP NHẬT GIAO DIỆN
	// =====================================================
	public void setDice(int value) {
		diceLabel.setText(String.valueOf(value));
	}

	public void setTurn(String text) {
		turnLabel.setText(text);
	}

	public void addHistory(String text) {
		historyArea.append(text + "\n");
	}

	// =====================================================
	// MAIN TẠM THỜI
	// =====================================================
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			GameFrame frame = new GameFrame(null);
			frame.setVisible(true);
		});
	}
}