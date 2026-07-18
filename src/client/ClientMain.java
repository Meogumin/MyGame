package client;

import view.MainFrame;

public class ClientMain {

	public static void main(String[] args) {
		NetworkManager network = new NetworkManager();
		MainFrame mainFrame = new MainFrame(network);
		mainFrame.setVisible(true);
	}
}