package inucseapp.inumanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import inucseapp.inumanager.R;
import inucseapp.inumanager.accessServer.AccessDB;
import inucseapp.inumanager.accessServer.AccessHandler;
import inucseapp.inumanager.accessServer.HandlerMethod;

/**
 * Created by syg11 on 2017-11-06.
 */

public class LoginActivity extends AppCompatActivity {
    EditText id,passwd;
    Button joinBtn,loginBtn;
    String strId,strPs;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        getSupportActionBar().hide();

        id = (EditText)findViewById(R.id.id);
        passwd = (EditText)findViewById(R.id.passwd);
        loginBtn = (Button)findViewById(R.id.login_btn);
        joinBtn = (Button)findViewById(R.id.join_btn);

        final LoginClick loginClick = new LoginClick();

        // 테스트코드
        //id.setText("admin");
        //passwd.setText("132465");
        //


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJoinActivity();
            }
        });

        passwd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    loginClick.onClick(loginBtn);
                    return true;
                }
                return false;
            }
        });

        loginBtn.setOnClickListener(loginClick);
    }

    private class LoginClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            strId = id.getText().toString();
            strPs = passwd.getText().toString();

            if(strId.length() < 5){
                Toast.makeText(getApplicationContext(),"아이디는 5글자 이상입니다.",Toast.LENGTH_SHORT).show();
            }else if(strPs.length() < 3){
                Toast.makeText(getApplicationContext(),"비밀번호는 3글자 이상입니다.",Toast.LENGTH_SHORT).show();
            }else {
                MyHandlerMethod myHandlerMethod = new MyHandlerMethod();
                AccessHandler accessHandler = new AccessHandler(myHandlerMethod);
                AccessDB accessDB = new AccessDB(accessHandler,getString(R.string.server_url), AccessDB.LOGIN_PHP,"id="+strId+"&ps="+strPs);
                accessDB.execute();
            }
        }
    }

    private void startJoinActivity(){
        Intent intent = new Intent(getApplicationContext(),JoinActivity.class);
        startActivity(intent);
    }

    private class MyHandlerMethod implements HandlerMethod{

        @Override
        public void beforeAccess() {

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("로그인 중 입니다.");
            progressDialog.show();
        }

        @Override
        public void afterAccess(Message msg) {
            String result = msg.obj.toString();
            String type="-1",name="";

            progressDialog.dismiss();
            //제이슨파싱
            try{
                JSONObject jsonObject = new JSONObject(result);
                result = jsonObject.getString("result");
                type = jsonObject.getString("type");
                name = jsonObject.getString("name");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if(result.equals("1")){

                Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ManagerActivity.class);
                intent.putExtra("id",strId);
                intent.putExtra("type",type);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
            }
            else if(result.equals("0")){
                Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("-1")){
                Toast.makeText(getApplicationContext(),"아이디를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void accessError() {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"에러발생.",Toast.LENGTH_SHORT).show();
        }
    }

}
