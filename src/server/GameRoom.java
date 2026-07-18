package server;

import java.util.ArrayList;
import java.util.List;

import common.Message;
import common.MessageType;

public class GameRoom {
	private static final int MAX_PLAYERS = 4;
	private List<ClientHandler> players = new ArrayList<>();
	private ClientHandler currentPlayer;

	public GameRoom() {

	}

	public boolean addPlayer(ClientHandler player) {
		if (players.size() >= MAX_PLAYERS) {
			return false;
		}
		players.add(player);
		if (currentPlayer == null) {
			currentPlayer = player;
		}
		player.setRoom(this);
		return true;
	}

	public void broadcastPlayers() {
		for (ClientHandler player : players) {
			Message message = new Message(MessageType.PLAYER_JOINED, getPlayerNames());
			player.send(message);
		}
	}

	private List<String> getPlayerNames() {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < players.size(); i++) {
			names.add("Player " + (i + 1));
		}
		return names;
	}

	public void receive(ClientHandler sender, Message message) {
		switch (message.getType()) {
		case CHAT:
			handleChat(sender, message);
			break;
		case START:
			handleStart(sender);
			break;
		default:
			break;
		}
	}

	private void handleChat(ClientHandler sender, Message message) {
		Message forward = new Message(MessageType.CHAT,
				"Player " + (players.indexOf(sender) + 1) + ": " + message.getData());
		for (ClientHandler player : players) {
			player.send(forward);
		}
	}

	private void handleStart(ClientHandler sender) {
		if (players.size() < 2) {
			sender.send(new Message(MessageType.CHAT, "Cần ít nhất 2 người chơi."));
			return;
		}

		// Chỉ Player 1 được bắt đầu
		if (players.get(0) != sender) {
			return;
		}

		Message startMessage = new Message(MessageType.START, null);
		for (ClientHandler player : players) {
			player.send(startMessage);
		}
	}
}