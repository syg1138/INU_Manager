package inucseapp.inumanager.damain;

/**
 * Created by syg11 on 2017-11-15.
 */

public class Classroom {
    String roomName;
    String roomType;

    boolean roomCheck;

    public Classroom(String roomName, String roomType) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomCheck = false;
    }

    public boolean isRoomCheck() {
        return roomCheck;
    }

    public void setRoomCheck(boolean roomCheck) {
        this.roomCheck = roomCheck;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
