package view;

import javax.swing.JPanel;

import GameSetup.BoardCell;
import GameSetup.Game;
import GameSetup.Piece;

import java.awt.Graphics;
import java.util.List;

public class BoardPanel extends JPanel {
	private Game game;

	public BoardPanel(Game game) {
		this.game = game;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (game == null)
			return;
		drawPieces(g);
	}

	private void drawPieces(Graphics g) {
		List<BoardCell> cells = game.getBoard().getGridNormal();
		for (BoardCell cell : cells) {
			Piece piece = cell.getPiece();
			if (piece == null)
				continue;
			int x = cell.coordinate.getX();
			int y = cell.coordinate.getY();
			drawPiece(g, piece, x, y);
		}
	}

	private void drawPiece(Graphics g, Piece piece, int x, int y) {
		// Tạm thời vẽ hình tròn
		g.fillOval(x * 50, y * 50, 40, 40);
	}
}