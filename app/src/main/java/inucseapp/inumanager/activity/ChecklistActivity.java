package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import inucseapp.inumanager.R;
import inucseapp.inumanager.damain.CheckList;

/**
 * Created by syg11 on 2017-12-03.
 */

public class ChecklistActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String id,type,classnumber,classtype;
    String jsonStr,time;

    RecyclerView recyclerView;
    CheckListAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        mContext = getApplicationContext();
        intent = getIntent();

        init();
    }

    private void init() {
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        classnumber = intent.getStringExtra("classnumber");
        classtype = intent.getStringExtra("classtype");
        actionbarInit();

        Button signUpBtn = (Button)findViewById(R.id.sign_up_btn);

        recyclerView = (RecyclerView)findViewById(R.id.checklist_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        CheckListDB checkListDB = new CheckListDB();
        checkListDB.execute();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CheckList> checkLists = adapter.getItem();
                ArrayList<CheckList> submitLists = new ArrayList<>();
                int size = checkLists.size();
                jsonStr = "";

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                time = sdfNow.format(date);

                for(int i = 0;i<size;i++){
                    CheckList tempCheckList = checkLists.get(i);
                    if(!(checkLists.get(i).getReason().equals(""))){
                        submitLists.add(new CheckList(tempCheckList.getCheckID(),tempCheckList.getObjectName(),tempCheckList.getObjectName_kr(),
                                tempCheckList.getCheckList(),"1",tempCheckList.getReason()));
                    }else {
                        submitLists.add(new CheckList(tempCheckList.getCheckID(),tempCheckList.getObjectName(),tempCheckList.getObjectName_kr(),
                                tempCheckList.getCheckList(),"0","이상 없음"));
                    }
                }
                for(int i = 0;i<submitLists.size();i++){
                    CheckList t = submitLists.get(i);
                    if(!(jsonStr.equals(""))){
                        jsonStr = jsonStr + ",";
                    }
                    jsonStr = jsonStr + "{\"check_id\":\""+ t.getCheckID()+"\",\"check\":\""+t.getCheck()+"\",\"reason\":\""+t.getReason() +"\"}";
                }
                jsonStr = "["+jsonStr+"]";
                Log.i("test","현재시간"+time);
                Log.i("test",jsonStr);

                SubmitListDB submitListDB = new SubmitListDB();
                submitListDB.execute();
            }
        });
    }

    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        if(classtype.equals("1")){
            actionbar.setIcon(R.mipmap.ic_classroom1);
            actionbar.setSubtitle("일반강의실");
        }
        else if(classtype.equals("2")){
            actionbar.setIcon(R.mipmap.ic_computer);
            actionbar.setSubtitle("컴퓨터실");
        }else{
            actionbar.setIcon(R.mipmap.ic_laboratory);
            actionbar.setSubtitle("실험실");
        }
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle(classnumber);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class SubmitListDB extends AsyncTask<Void,Integer,Void> {
        public String data;
        ArrayList<CheckList> items = new ArrayList();
        @Override
        protected Void doInBackground(Void... params) {
            String param = "id="+id+"&roomnumber="+classnumber+"&time="+time+"&json_submit="+jsonStr;
            Log.i("test","점검사항 제출 \nid="+id+
                    "\nroomnumber="+classnumber+
                    "\ntime=" + time+
                    "\njson_submit=" + jsonStr
            );

            try{
                URL url = new URL(getString(R.string.server_url)+"checklist_submit.php");
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
                Log.i("test","data : "+data);


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
            if(data.equals("error")){
                //에러
                AlertDialog.Builder builder = new AlertDialog.Builder(ChecklistActivity.this);
                builder.setMessage("잠시 후 다시 시도해주세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"네트워크 이상.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }else{
                //정상
                AlertDialog.Builder builder = new AlertDialog.Builder(ChecklistActivity.this);
                builder.setMessage("점검사항이 제출 되었습니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }


        }
    }



    public class CheckListDB extends AsyncTask<Void,Integer,Void> {
        public String data;
        ArrayList<CheckList> items = new ArrayList();
        @Override
        protected Void doInBackground(Void... params) {
            String param = "id="+id+"&roomnumber="+classnumber;

            try{
                URL url = new URL(getString(R.string.server_url)+"checklist_load.php");
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
                Log.i("test","받은 jsonArray:\n"+data+"");


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
                    items.add(new CheckList(jsonObject.getString("check_id"),jsonObject.getString("objectname") ,jsonObject.getString("kname"),jsonObject.getString("checklist")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new CheckListAdapter(items,mContext);
            recyclerView.setAdapter(adapter);


        }
    }

    public static class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ChecklistViewHolder>{
        private Context context;
        private ArrayList<CheckList> mItems;

        private RadioButton lastCheckedRB = null;

        public CheckListAdapter(ArrayList<CheckList> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        public ArrayList<CheckList> getItem(){
            return mItems;
        }

        @Override
        public ChecklistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_check_list,parent,false);
            ChecklistViewHolder holder = new ChecklistViewHolder(v,new MyCustomEditTextListener());
            return holder;
        }

        @Override
        public void onBindViewHolder(ChecklistViewHolder holder, final int position) {
            final CheckList checkList = mItems.get(position);

            holder.tvObjectName.setText(checkList.getObjectName_kr());
            holder.tvCheckList.setText(checkList.getCheckList());
            holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
            holder.etReason.setText(mItems.get(holder.getAdapterPosition()).getReason());


        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class ChecklistViewHolder extends RecyclerView.ViewHolder{;
            public TextView tvObjectName;
            public TextView tvCheckList;
            public EditText etReason;
            public MyCustomEditTextListener myCustomEditTextListener;

            public ChecklistViewHolder(View itemView,MyCustomEditTextListener myCustomEditTextListener) {
                super(itemView);
                this.tvObjectName = (TextView)itemView.findViewById(R.id.object_name);
                this.tvCheckList = (TextView)itemView.findViewById(R.id.check_list);


                this.etReason = (EditText)itemView.findViewById(R.id.reason);
                this.myCustomEditTextListener = myCustomEditTextListener;
                this.etReason.addTextChangedListener(myCustomEditTextListener);


            }
        }

        private class MyCustomEditTextListener implements TextWatcher {
            private int position;

            public void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // no op
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mItems.get(position).setReason(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // no op
            }
        }
    }
}
