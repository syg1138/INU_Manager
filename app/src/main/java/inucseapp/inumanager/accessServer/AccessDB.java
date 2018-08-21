package inucseapp.inumanager.accessServer;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import inucseapp.inumanager.MainActivity;

/**
 * Created by syg11 on 2018-04-30.
 */

public class AccessDB extends AsyncTask<Void,Integer,Void> {
    public static final String LOGIN_PHP = "login.php";
    public static final String JOIN_PHP = "join.php";
    public static final String CLASSMANAGER_PHP = "classmanager.php";
    public static final String CLASSDELETE_PHP = "classmanager_delete.php";
    private String result;
    private String url;
    private String php;
    private String param;
    private Handler mHandler;

    public AccessDB(Handler handler, String url, String php, String param) {
        this.mHandler = handler;
        this.url = url;
        this.php = php;
        this.param = param;
        this.result = "-2";
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Message msg1 = Message.obtain(mHandler, MainActivity.MSG_BEFORE_ACCESS);
            mHandler.sendEmptyMessage(msg1.what);
            msg1.recycle();

            URL accessUrl = new URL(this.url+this.php);
            HttpURLConnection conn = (HttpURLConnection)accessUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();

            OutputStream outs = conn.getOutputStream();
            outs.write(param.getBytes("UTF-8"));
            outs.flush();
            outs.close();

            InputStream is = null;
            BufferedReader in = null;

            is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is),8*1024);
            String line = null;
            StringBuffer buff = new StringBuffer();
            while((line = in.readLine())!=null){
                buff.append(line+"\n");
            }
            result = buff.toString().trim();
            Log.i("songyg",result);


        }catch (Exception e){
            //에러가 나면 어떻게 하는가
            e.printStackTrace();
            Message msg3 = Message.obtain(mHandler, MainActivity.MSG_ACCESS_ERROR);
            mHandler.sendEmptyMessage(msg3.what);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Message msg2 = Message.obtain(mHandler, MainActivity.MSG_AFTER_ACCESS, result);
        mHandler.sendMessage(msg2);
    }
}
