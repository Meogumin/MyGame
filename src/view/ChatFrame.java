package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatFrame extends JFrame {

	private JTextArea chatArea;
	private JTextField input;
	private JButton sendButton;

	public ChatFrame() {

		setTitle("Chat Client");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		chatArea = new JTextArea();
		chatArea.setEditable(false);

		input = new JTextField();

		sendButton = new JButton("Send");

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(input, BorderLayout.CENTER);
		bottom.add(sendButton, BorderLayout.EAST);

		add(new JScrollPane(chatArea), BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		setVisible(true);
	}

	public void setSendAction(ActionListener listener) {
		sendButton.addActionListener(listener);
	}

	public JTextField getInput() {
		return input;
	}

	public JButton getSendButton() {
		return sendButton;
	}

	public void append(String text) {
		chatArea.append(text + "\n");
	}
}