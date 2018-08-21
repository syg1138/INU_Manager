package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateClassActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;

    String roomnumber,type="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        mContext = getApplicationContext();

        init();
    }

    private void init() {
        actionbarInit();

        Button signBtn = (Button)findViewById(R.id.sign_up_btn_create_class);
        final EditText editText = (EditText)findViewById(R.id.roomnumber_et);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomnumber = editText.getText().toString();
                if (roomnumber.equals("")){
                    Toast.makeText(mContext,"강의실 이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                else if (type.equals("0")){
                    Toast.makeText(mContext,"강의실 타입을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    SubmitDB submitDB = new SubmitDB();
                    submitDB.execute();
                }
            }
        });
    }

    private void actionbarInit(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("새 강의실 추가");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80D8FF")));
    }

    public class SubmitDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "roomnumber="+roomnumber+"&type="+type;
            Log.i("test","메소드 실행 :" + param);

            try{
                URL url = new URL(getString(R.string.server_url)+"create_class.php");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
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
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.i("test",data+"");


            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //test code
            if(data.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this);
                builder.setMessage("강의실이 추가 되었습니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this);
                builder.setMessage("입력 정보를 다시 확인 해주세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }


        }
    }
}
