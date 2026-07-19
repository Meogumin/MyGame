package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingUtilities;

import common.Message;
import common.MessageType;
import common.PlayerInfo;
import view.ChatFrame;
import view.GameFrame;
import view.RoomFrame;

public class NetworkManager {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	private ChatFrame frame;
	private RoomFrame roomFrame;

	private String playerName;

	public void connect(String ip) {
		try {
			socket = new Socket(ip, 5000);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("Connected to: " + ip);

			// =========================
			// BẮT ĐẦU LẮNG NGHE SERVER
			// =========================
			new Thread(() -> {
				try {
					while (true) {
						Message message = (Message) in.readObject();
						handleMessage(message);
					}
				} catch (Exception e) {
					System.out.println("Disconnected.");
				}
			}).start();

			// =========================
			// SAU ĐÓ MỚI GỬI TÊN
			// =========================
			send(new Message(MessageType.JOIN_ROOM, playerName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(Message message) {
		switch (message.getType()) {
		case PLAYER_JOINED:
			handlePlayerJoined(message);
			break;
		case COLOR_SELECT:
			handleColorSelect(message);
			break;
		case CHAT:
			handleChat(message);
			break;
		case START:
			handleStart();
			break;
		case ROLL:
			handleRoll(message);
			break;
		case MOVE:
			handleMove(message);
			break;
		case DEPLOY:
			handleDeploy(message);
			break;
		case END_TURN:
			handleEndTurn(message);
			break;
		case SKIP:
			handleSkip(message);
			break;

		default:
			break;
		}
	}

	private void handlePlayerJoined(Message message) {
		if (roomFrame == null)
			return;
		@SuppressWarnings("unchecked")
		List<PlayerInfo> players = (List<PlayerInfo>) message.getData();
		SwingUtilities.invokeLater(() -> {
			roomFrame.updatePlayers(players);
		});
	}

	private void handleColorSelect(Message message) {
		if (roomFrame == null)
			return;
		@SuppressWarnings("unchecked")
		List<PlayerInfo> players = (List<PlayerInfo>) message.getData();
		SwingUtilities.invokeLater(() -> {
			roomFrame.updateColorSelection(players);
		});
	}

	private void handleChat(Message message) {
		if (frame == null)
			return;
		String text = message.getData().toString();
		SwingUtilities.invokeLater(() -> {
			frame.append(text);
		});
	}

	private void handleStart() {
		SwingUtilities.invokeLater(() -> {
			GameFrame gameFrame = new GameFrame(this);
			gameFrame.setVisible(true);
			if (roomFrame != null) {
				roomFrame.dispose();
			}
		});
	}

	private void handleRoll(Message message) {
		System.out.println("Received ROLL message from server.");
		/*
		 * Sau này:
		 *
		 * int diceResult = (Integer) message.getData();
		 *
		 * gameFrame.setDice(diceResult);
		 *
		 * gameFrame.repaint();
		 */
	}

	private void handleMove(Message message) {
		System.out.println("Received MOVE message from server.");
		/*
		 * Sau này:
		 *
		 * MoveData data = (MoveData) message.getData();
		 *
		 * gameFrame.updatePiece(data);
		 *
		 * gameFrame.repaint();
		 */
	}

	private void handleDeploy(Message message) {
		System.out.println("Received DEPLOY message from server.");
		/*
		 * Sau này:
		 *
		 * DeployData data = (DeployData) message.getData();
		 *
		 * gameFrame.updatePiece(data);
		 *
		 * gameFrame.repaint();
		 */
	}

	private void handleEndTurn(Message message) {
		System.out.println("Received END_TURN message from server.");
		/*
		 * Sau này:
		 *
		 * PlayerInfo player = (PlayerInfo) message.getData();
		 *
		 * gameFrame.setTurn( player.getName() );
		 */
	}

	private void handleSkip(Message message) {
		System.out.println("Received SKIP message from server.");
		/*
		 * Sau này:
		 *
		 * gameFrame.setTurn(...); gameFrame.repaint();
		 */
	}

	public void send(Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			System.out.println("Send failed");
			e.printStackTrace();
		}
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setRoomFrame(RoomFrame roomFrame) {
		this.roomFrame = roomFrame;
	}

	public void setFrame(ChatFrame frame) {
		this.frame = frame;
	}
}