package inucseapp.inumanager.damain;

/**
 * Created by syg11 on 2017-12-10.
 */

public class ObjectName {

    String objectName;
    String objectNmaeKr;

    public ObjectName(String objectName,String objectNmaeKr){
        this.objectName = objectName;
        this.objectNmaeKr = objectNmaeKr;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectNmaeKr() {
        return objectNmaeKr;
    }

    public void setObjectNmaeKr(String objectNmaeKr) {
        this.objectNmaeKr = objectNmaeKr;
    }
}
