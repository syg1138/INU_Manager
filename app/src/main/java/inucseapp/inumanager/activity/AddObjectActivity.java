package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddObjectActivity extends AppCompatActivity {
    Context mContext;
    EditText editText;
    EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);
        mContext = getApplicationContext();

        init();
    }

    private void init() {
        actionbarInit();
        Button sign_btn = (Button)findViewById(R.id.sign_up_btn);
        editText = (EditText)findViewById(R.id.object_id_text);
        editText1 = (EditText)findViewById(R.id.object_name_text);

        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String objectId = editText.getText().toString();
                String objectName = editText1.getText().toString();
                if (objectName.equals("")||objectId.equals("")) {
                    Toast.makeText(getApplicationContext(), "물품 이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                }else if (objectName.length()>10){
                    Toast.makeText(getApplicationContext(), "물품 이름은 10자 이하로 해주세요.", Toast.LENGTH_LONG).show();
                }else if (objectId.length() >10){
                    Toast.makeText(getApplicationContext(), "물품 아이디는 10자 이하로 해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    ObjectRegistDB objectRegistDB = new ObjectRegistDB();
                    objectRegistDB.execute(objectId,objectName);
                }
            }
        });
    }
    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle("물품 추가");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class ObjectRegistDB extends AsyncTask<String,Integer,Void> {
        public String data;
        @Override
        protected Void doInBackground(String... params) {
            String param = "objectname="+params[0]+"&kname="+params[1];
            Log.i("test","메소드 실행 :" + params[0] + "//" +params[1]);

            try{
                URL url = new URL(getString(R.string.server_url) + "object_reg.php");
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
            if(data.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddObjectActivity.this);
                builder.setMessage("물품 등록 완료");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"물품 등록이 완료 되었습니다.",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                builder.show();
            }
            else if(data.equals("1062")){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddObjectActivity.this);
                builder.setMessage("물품 이름이 중복 됩니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"물품 이름이 중복 됩니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddObjectActivity.this);
                builder.setMessage("잠시 후 다시 입력해주세요.(에러)");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"잠시 후 다시 입력해주세요.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }

        }
    }

}
