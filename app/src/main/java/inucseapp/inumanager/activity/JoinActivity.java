package inucseapp.inumanager.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import inucseapp.inumanager.R;
import inucseapp.inumanager.accessServer.AccessDB;
import inucseapp.inumanager.accessServer.AccessHandler;
import inucseapp.inumanager.accessServer.HandlerMethod;

/**
 * Created by syg11 on 2017-11-07.
 */

public class JoinActivity extends AppCompatActivity {
    EditText name,joinId,passwd1,passwd2;
    RadioButton assistant,student;
    Button signUpBtn;
    String nm,id,ps1,ps2,type;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
    }

    private void init() {
        getSupportActionBar().hide();

        name = (EditText)findViewById(R.id.name);
        joinId = (EditText)findViewById(R.id.join_id);
        passwd1 = (EditText)findViewById(R.id.passwd1);
        passwd2 = (EditText)findViewById(R.id.passwd2);
        assistant = (RadioButton)findViewById(R.id.assistant);
        student = (RadioButton)findViewById(R.id.student);
        signUpBtn = (Button)findViewById(R.id.sign_up_btn);


        passwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ps1 = passwd1.getText().toString();
                ps2 = passwd2.getText().toString();
                if(ps1.equals(ps2)){
                    passwd1.setTextColor(Color.rgb(63,81,181));
                    passwd2.setTextColor(Color.rgb(63,81,181));
                }
                else {
                    passwd1.setTextColor(Color.rgb(255,64,129));
                    passwd2.setTextColor(Color.rgb(255,64,129));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nm = name.getText().toString();
                id = joinId.getText().toString();
                ps1 = passwd1.getText().toString();
                ps2 = passwd2.getText().toString();
                if(assistant.isChecked()){
                    type = "1";
                }else type = "2";


                //검사
                if(nm.equals("")||id.equals("")||ps1.equals("")){
                    Toast.makeText(getApplicationContext(),"입력란을 채워주세요.",Toast.LENGTH_SHORT).show();
                }
                else if(id.length()<6){
                    Toast.makeText(getApplicationContext(),"아이디를 6글자 이상 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else if(passwd1.length()<3){
                    Toast.makeText(getApplicationContext(),"비밀번호를 3글자 이상 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else if(!(ps1.equals(ps2))){
                    Toast.makeText(getApplicationContext(),"비밀번호를 확인 해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    //registDB rdb = new registDB();
                    //rdb.execute();
                    JoinHandlerMethod joinHandlerMethod = new JoinHandlerMethod();
                    AccessHandler accessHandler = new AccessHandler(joinHandlerMethod);
                    AccessDB accessDB = new AccessDB(accessHandler
                            ,getString(R.string.server_url)
                            ,AccessDB.JOIN_PHP
                            ,"id="+id+"&ps="+ps1+"&name="+nm+"&type="+type);
                    accessDB.execute();
                }
            }
        });
    }

    public class JoinHandlerMethod implements HandlerMethod{

        @Override
        public void beforeAccess() {
            progressDialog = new ProgressDialog(JoinActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("가입정도를 등록 중 입니다..");
            progressDialog.show();
        }

        @Override
        public void afterAccess(Message msg) {
            String result = msg.obj.toString();

            if(result.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setMessage("회원가입 완료");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"회원가입이 완료 되었습니다.",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                builder.show();
            }
            else if(result.equals("1062")){
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setMessage("아이디가 중복 됩니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"아이디가 중복 됩니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        }

        @Override
        public void accessError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
            builder.setMessage("회원정보를 다시 입력해주세요.(에러)");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"회원정보를 다시 입력해주세요.",Toast.LENGTH_LONG).show();
                        }
                    });
            builder.show();
        }
    }
}

