package zMain;

import server.NetworkManager;
import view.MainFrame;

public class Main {

	public static void main(String[] args) {
		NetworkManager network = new NetworkManager();
		MainFrame mainFrame = new MainFrame(network);
		mainFrame.setVisible(true);
	}
}