package view;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class PieceView extends JLabel {

	private static final long serialVersionUID = 1L;

	private String color;
	private int pieceId;

	public PieceView(String color, int pieceId, ImageIcon image) {

		this.color = color;
		this.pieceId = pieceId;

		setIcon(image);

		setSize(image.getIconWidth(), image.getIconHeight());

		setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
	}

	public String getColor() {
		return color;
	}

	public int getPieceId() {
		return pieceId;
	}
}