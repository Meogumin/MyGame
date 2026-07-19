package common;

import java.io.Serializable;

public class PlayerInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String color;
	private boolean ready;

	public PlayerInfo(String name) {
		this.name = name;
		this.color = null;
		this.ready = false;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public boolean isReady() {
		return ready;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}