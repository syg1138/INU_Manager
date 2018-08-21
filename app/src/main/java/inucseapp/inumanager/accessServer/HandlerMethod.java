package inucseapp.inumanager.accessServer;

import android.os.Message;

/**
 * Created by syg11 on 2018-04-30.
 */

public interface HandlerMethod {
    public void beforeAccess();
    public void afterAccess(Message msg);
    public void accessError();
}
