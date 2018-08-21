package inucseapp.inumanager.damain;

/**
 * Created by syg11 on 2017-12-14.
 */

public class History {
    String roomnumber;
    String userId;
    String time;
    String check_list_history_id;

    public History(String roomnumber, String userId, String time, String check_list_history_id) {
        this.roomnumber = roomnumber;
        this.userId = userId;
        this.time = time;
        this.check_list_history_id = check_list_history_id;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCheck_list_history_id() {
        return check_list_history_id;
    }

    public void setCheck_list_history_id(String check_list_history_id) {
        this.check_list_history_id = check_list_history_id;
    }
}
