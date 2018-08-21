package inucseapp.inumanager.damain;

/**
 * Created by syg11 on 2017-12-06.
 */

public class CheckListHistory {
    String time;
    String checkUser;
    String checkHistoryId;

    public CheckListHistory(){}

    public CheckListHistory(String time, String checkUser,String checkHistoryId){
        this.time = time;
        this.checkUser = checkUser;
        this.checkHistoryId = checkHistoryId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public String getCheckHistoryId() {
        return checkHistoryId;
    }

    public void setCheckHistoryId(String checkHistoryId) {
        this.checkHistoryId = checkHistoryId;
    }
}
