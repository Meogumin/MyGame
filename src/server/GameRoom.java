package server;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import GameSetup.Game;
import GameSetup.Player;
import P_AI.AIEasy;
import P_AI.AIHard;
import P_AI.AINormal;
import P_Human.HumanBlue;
import P_Human.HumanGreen;
import P_Human.HumanRed;
import P_Human.HumanYellow;
import common.Message;
import common.MessageType;
import common.PlayerInfo;

public class GameRoom {
	private static final int MAX_PLAYERS = 4;
	private List<ClientHandler> players = new ArrayList<>();
	private ClientHandler currentPlayer;
	private Game game;
	private boolean gameStarted = false;

	public GameRoom() {

	}

	private void startGame() {
		int totalPlayers = 4;
		List<String> humanColors = new ArrayList<>(Arrays.asList("red", "blue"));
		String difficulty = "Normal";
		if (gameStarted) {
			return;
		}
		gameStarted = true;
		System.out.println("===== SETTING UP GAME =====");
		int humanCount = humanColors.size();
		int aiCount = totalPlayers - humanCount;
		List<String> allColors = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));
		List<String> availableColors = new ArrayList<>(allColors);

		// Xóa màu đã được người chơi chọn
		availableColors.removeAll(humanColors);

		// Tạo Game
		game = new Game();

		// =========================
		// ADD HUMAN PLAYERS
		// =========================
		for (String color : humanColors) {
			Player player = createHumanPlayer(color);
			game.addPlayer(player);
		}

		// =========================
		// ADD AI PLAYERS
		// =========================
		Collections.shuffle(availableColors);
		for (int i = 0; i < aiCount; i++) {
			String color = availableColors.get(i);
			Player ai = createAIPlayer(color, difficulty);
			game.addPlayer(ai);
		}
		System.out.println("Human players: " + humanCount);
		System.out.println("AI players: " + aiCount);

		// =========================
		// START GAME
		// =========================
		game.start();
	}

	private Player createHumanPlayer(String color) {
		switch (color) {
		case "red":
			return new HumanRed("(Player RED)", getPieceImage(color));
		case "blue":
			return new HumanBlue("(Player BLUE)", getPieceImage(color));
		case "green":
			return new HumanGreen("(Player GREEN)", getPieceImage(color));
		case "yellow":
			return new HumanYellow("(Player YELLOW)", getPieceImage(color));
		default:
			throw new IllegalArgumentException("Unknown color: " + color);
		}
	}

	private Player createAIPlayer(String color, String difficulty) {
		String aiName = "(AI " + color.toUpperCase() + " | " + difficulty + ")";
		switch (difficulty) {
		case "Easy":
			return new AIEasy(aiName, color, getPieceImage(color));
		case "Normal":
			return new AINormal(aiName, color, getPieceImage(color));
		case "Hard":
			return new AIHard(aiName, color, getPieceImage(color));
		default:
			throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
		}
	}

	public static Image getPieceImage(String color) {
		switch (color.toLowerCase()) {
		case "red":
			return new ImageIcon("img/pieceRed.png").getImage();
		case "blue":
			return new ImageIcon("img/pieceBlue.png").getImage();
		case "yellow":
			return new ImageIcon("img/pieceYellow.png").getImage();
		case "green":
			return new ImageIcon("img/pieceGreen.png").getImage();
		default:
			throw new IllegalArgumentException("Màu không hợp lệ: " + color);
		}
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
			Message message = new Message(MessageType.PLAYER_JOINED, getPlayerInfos());
			player.send(message);
		}
	}

	private List<PlayerInfo> getPlayerInfos() {
		List<PlayerInfo> infos = new ArrayList<>();
		for (ClientHandler player : players) {
			PlayerInfo info = player.getPlayerInfo();
			if (info != null)
				infos.add(info);
		}
		return infos;
	}

	public void receive(ClientHandler sender, Message message) {
		switch (message.getType()) {
		case CHAT:
			handleChat(sender, message);
			break;
		case COLOR_SELECT:
			handleColorSelect(sender, message);
			break;
		case JOIN_ROOM:
			handleJoin(sender, message);
			break;
		case START:
			handleStart(sender);
			break;
		default:
			break;
		}
	}

	private void handleColorSelect(ClientHandler sender, Message message) {
		if (sender.getPlayerInfo() == null) {
			return;
		}
		String color = (String) message.getData();
		if (color == null) {
			sender.getPlayerInfo().setColor(null);
			broadcastColors();
			return;
		}
		for (ClientHandler player : players) {
			if (player == sender) {
				continue;
			}
			PlayerInfo info = player.getPlayerInfo();
			if (info != null && color.equals(info.getColor())) {
				System.out.println("Color already used");
				return;
			}
		}
		sender.getPlayerInfo().setColor(color);
		broadcastColors();
	}

	private void broadcastColors() {
		for (ClientHandler player : players) {
			Message message = new Message(MessageType.COLOR_SELECT, getPlayerInfos());
			player.send(message);
		}
	}

	private void handleJoin(ClientHandler sender, Message message) {
		String playerName = (String) message.getData();
		if (playerName == null || playerName.isBlank()) {
			return;
		}
		sender.setPlayerInfo(new PlayerInfo(playerName.trim()));
		broadcastPlayers();
	}

	private void handleChat(ClientHandler sender, Message message) {
		String playerName = sender.getPlayerInfo().getName();
		Message forward = new Message(MessageType.CHAT, playerName + ": " + message.getData());
		for (ClientHandler player : players) {
			player.send(forward);
		}
	}

	private void handleStart(ClientHandler sender) {
		if (players.size() < 2) {
			sender.send(new Message(MessageType.CHAT, "Cần ít nhất 2 người chơi."));
			return;
		}

		// Chỉ chủ phòng được bắt đầu
		if (players.get(0) != sender) {
			return;
		}

		// Tạo người chơi và bắt đầu Game
		startGame();

		// Thông báo cho Client mở GameFrame
		Message startMessage = new Message(MessageType.START, players.size());

		for (ClientHandler player : players) {
			player.send(startMessage);
		}
	}
}