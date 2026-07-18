package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameFrame extends JFrame {
	private JLabel lblOpponent, lblMine, lblResult;
	private JButton btnRock, btnPaper, btnScissors;

	public GameFrame() {
		setTitle("Rock Paper Scissors");
		setSize(500, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// ---------------------- TOP ----------------------
		JPanel top = new JPanel(new GridLayout(2, 1));
		lblOpponent = createLabel("Opponent : Waiting...");
		lblMine = createLabel("You : Nothing");
		top.add(lblOpponent);
		top.add(lblMine);

		// ---------------------- CENTER ----------------------
		JPanel center = new JPanel(new GridLayout(3, 1, 15, 15));
		btnRock = new JButton("ROCK");
		btnPaper = new JButton("PAPER");
		btnScissors = new JButton("SCISSORS");
		btnRock.setFont(new Font("Arial", Font.BOLD, 20));
		btnPaper.setFont(new Font("Arial", Font.BOLD, 20));
		btnScissors.setFont(new Font("Arial", Font.BOLD, 20));
		center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		center.add(btnRock);
		center.add(btnPaper);
		center.add(btnScissors);

		// ---------------------- BOTTOM ----------------------
		JPanel bottom = new JPanel(new BorderLayout());
		lblResult = createLabel("Waiting for game...");
		lblResult.setPreferredSize(new Dimension(100, 70));
		bottom.add(lblResult);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		addEvents();
		setVisible(true);
	}

	private JLabel createLabel(String text) {
		JLabel lb = new JLabel(text);
		lb.setHorizontalAlignment(SwingConstants.CENTER);
		lb.setFont(new Font("Arial", Font.BOLD, 22));
		lb.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		return lb;
	}

	private void addEvents() {
		btnRock.addActionListener(e -> {
			setMyMove("ROCK");
		});
		btnPaper.addActionListener(e -> {
			setMyMove("PAPER");
		});
		btnScissors.addActionListener(e -> {
			setMyMove("SCISSORS");
		});
	}

	// =====================================================
	public void setOpponentMove(String move) {
		lblOpponent.setText("Opponent : " + move);
	}

	public void setMyMove(String move) {
		lblMine.setText("You : " + move);
	}

	public void setResult(String result) {
		lblResult.setText(result);
	}

}