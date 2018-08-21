package inucseapp.inumanager.accessServer;

import android.os.Message;

import java.util.logging.Handler;

import inucseapp.inumanager.MainActivity;

/**
 * Created by syg11 on 2018-04-30.
 */

public class AccessHandler extends android.os.Handler {
    HandlerMethod handlerMethod;

    public AccessHandler(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public void handleMessage(Message msg){
        //완료 후 실행할 처리 삽입

        switch(msg.what){
            case MainActivity.MSG_BEFORE_ACCESS:

                //"로그인 처리 중"
                this.handlerMethod.beforeAccess();
                break;
            case MainActivity.MSG_AFTER_ACCESS:

                //로그인 처리 후 결과값에 따라 처리
                this.handlerMethod.afterAccess(msg);
                break;

            case MainActivity.MSG_ACCESS_ERROR:

                //에러가 났을 경우
                this.handlerMethod.beforeAccess();
                break;

            default:
        }

    }
}
