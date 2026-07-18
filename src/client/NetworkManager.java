package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import common.Message;
import common.MessageType;
import view.ChatFrame;
import view.RoomFrame;

public class NetworkManager {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ChatFrame frame;
	private RoomFrame roomFrame;

	public void connect(String ip) {
		try {
			socket = new Socket(ip, 5000);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("Connected to: " + ip);

			// Bắt đầu lắng nghe Server
			new Thread(() -> {
				try {
					while (true) {
						Message message = (Message) in.readObject();
						if (message.getType() == MessageType.PLAYER_JOINED) {
							if (roomFrame != null) {
								@SuppressWarnings("unchecked")
								List<String> players = (List<String>) message.getData();
								roomFrame.updatePlayers(players);
							}
						} else if (message.getType() == MessageType.CHAT) {
							if (frame != null) {
								frame.append(message.getData().toString());
							}
						} else if (message.getType() == MessageType.START) {
							System.out.println("GAME STARTED");
							openChatFrame();
						}
					}
				} catch (Exception e) {
					System.out.println("Disconnected.");
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openChatFrame() {
		ChatFrame chatFrame = new ChatFrame();
		setFrame(chatFrame);
		chatFrame.setSendAction(e -> {
			String text = chatFrame.getInput().getText();
			if (text == null || text.isBlank()) {
				return;
			}
			send(new Message(MessageType.CHAT, text));
			chatFrame.getInput().setText("");
		});
		if (roomFrame != null) {
			roomFrame.dispose();
		}
	}

	public void send(Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setRoomFrame(RoomFrame roomFrame) {
		this.roomFrame = roomFrame;
	}

	public void setFrame(ChatFrame frame) {
		this.frame = frame;
	}
}