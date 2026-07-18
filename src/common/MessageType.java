package common;

import java.io.Serializable;

public enum MessageType implements Serializable {
    CONNECT,
    CHAT,
    MOVE,
    RESULT,
    TURN,
    START,
    PLAYER_JOINED
}