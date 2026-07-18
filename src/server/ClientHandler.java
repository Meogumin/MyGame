package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;

public class ClientHandler extends Thread {
	private Socket socket;
	private int playerId;
	private GameRoom room;

	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ClientHandler(Socket socket, int playerId) throws IOException {
		this.socket = socket;
		this.playerId = playerId;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		try {
			while (true) {
				Message message = (Message) in.readObject();
				room.receive(this, message);
			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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

	public int getIdPlayer() {
		return playerId;
	}

	public void setRoom(GameRoom room) {
		this.room = room;
	}
}