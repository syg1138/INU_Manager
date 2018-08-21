package inucseapp.inumanager.damain;

/**
 * Created by syg11 on 2017-12-04.
 */

public class CheckList {
    String checkID;
    String objectName;
    String objectName_kr;
    String checkList;
    String check = "0"; // 0 or 1 // 0 = 정상, 1 = 이상
    String reason = "";


    public CheckList(String checkID,String objectName,String objectName_kr,String checkList){
        this.checkID = checkID;
        this.objectName = objectName;
        this.objectName_kr = objectName_kr;
        this.checkList = checkList;
        this.check = "0";
    }

    public CheckList(String checkID,String objectName,String  objectName_kr,String checkList,String check,String reason){
        this.checkID = checkID;
        this.objectName = objectName;
        this.objectName_kr = objectName_kr;
        this.checkList = checkList;
        this.check = check;
        this.reason = reason;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getCheckID() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID = checkID;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName_kr() {
        return objectName_kr;
    }

    public void setObjectName_kr(String objectName_kr) {
        this.objectName_kr = objectName_kr;
    }

    public String getCheckList() {
        return checkList;
    }

    public void setCheckList(String checkList) {
        this.checkList = checkList;
    }
}
