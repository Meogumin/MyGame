package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
	private ServerSocket serverSocket;
	private int playerCount = 0;
	private final GameRoom room = new GameRoom();

	public void startInBackground() {
		Thread serverThread = new Thread(() -> {
			start();
		});
		serverThread.start();
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(5000);
			while (true) {
				Socket socket = serverSocket.accept();
				playerCount++;
				ClientHandler client = new ClientHandler(socket, playerCount);
				if (room.addPlayer(client)) {
					client.start();
					System.out.println("Player " + playerCount + " joined.");
				} else {
					socket.close();
					System.out.println("Room is full.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}