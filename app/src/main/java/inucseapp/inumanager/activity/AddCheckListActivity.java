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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import inucseapp.inumanager.R;

public class AddCheckListActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    ArrayList<String> spinnerArray;
    ArrayList<String> spinnerArray_EN;
    Spinner spinner;
    SpinnerAdapter spinnerAdapter;

    String id,classnumber,objectName,objectChecklist;
    int position_i=0;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_list);
        mContext = getApplicationContext();
        intent = getIntent();

        init();
    }

    private void init() {
        id = intent.getStringExtra("id");
        classnumber = intent.getStringExtra("classnumber");
        actionbarInit();

        Button signUpBtn = (Button)findViewById(R.id.sign_up_btn_add_checklist);
        editText = (EditText)findViewById(R.id.check_list_text);

        spinnerArray = new ArrayList<String>();
        spinnerArray.add("물품을 선택 해주세요.");
        spinnerArray_EN = new ArrayList<String >();
        spinnerArray_EN.add("none");
        spinner = (Spinner)findViewById(R.id.spinner);

        ObjectDB objectDB = new ObjectDB();
        objectDB.execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position_i = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectName=spinnerArray_EN.get(position_i);
                objectChecklist=editText.getText().toString();
                Log.i("test",objectName +" / " + objectChecklist);
                if(position_i == 0){
                    Toast.makeText(mContext,"물품을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(objectChecklist.equals("")){
                    Toast.makeText(mContext,"점검사항을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    // 데이터 보내기
                    SubmitDB submitDB = new SubmitDB();
                    submitDB.execute();
                }
            }
        });

    }

    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle(classnumber+" 점검 목록 추가");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class ObjectDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "id="+id+"&roomnumber="+classnumber;
            Log.i("test","메소드 실행 :" + param);

            try{
                URL url = new URL(getString(R.string.server_url)+"object_load.php");
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
            try{
                JSONArray jsonArray = new JSONArray(data);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    spinnerArray.add(jsonObject.getString("kname"));
                    spinnerArray_EN.add(jsonObject.getString("objectname"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            spinnerAdapter = new ArrayAdapter(mContext,
                    R.layout.support_simple_spinner_dropdown_item,spinnerArray);
            spinner.setAdapter(spinnerAdapter);


        }
    }

    public class SubmitDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "objectname="+objectName+"&checklist="+objectChecklist;
            Log.i("test","메소드 실행 :" + param);

            try{
                URL url = new URL(getString(R.string.server_url)+"checkobject_reg.php");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCheckListActivity.this);
                builder.setMessage("점검사항이 등록 되었습니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCheckListActivity.this);
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
