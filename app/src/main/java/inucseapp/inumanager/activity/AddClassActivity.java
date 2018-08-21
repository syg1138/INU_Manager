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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;

import inucseapp.inumanager.R;
import inucseapp.inumanager.damain.Classroom;

/**
 * Created by syg11 on 2017-11-25.
 */

public class AddClassActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;

    String id;
    String cName;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        mContext = getApplicationContext();
        init();
    }

    private void init() {
        intent = getIntent();
        id = intent.getStringExtra("id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("강의실 추가");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));

        recyclerView = (RecyclerView)findViewById(R.id.add_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ClassDB classdb = new ClassDB();
        classdb.execute();

        Button saveButton = (Button)findViewById(R.id.save_btn);
        Button cancelButton = (Button)findViewById(R.id.cancel_btn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Addadapter addadapter = (Addadapter) adapter;
                ArrayList<Classroom> arrayList = addadapter.getItems();
                int j = arrayList.size();
                int checkCount = 0;
                cName = "";

                for(int i=0;i<j;i++){
                    Classroom classroom = arrayList.get(i);
                    if(classroom.isRoomCheck()){
                        checkCount++;
                        if(!(cName.equals(""))){
                            cName=cName+",";
                        }
                        cName = cName + "{\"roomnumber\":\""+classroom.getRoomName() + "\"}";
                    }
                }
                cName = "["+cName+"]";
                Log.i("test",cName+"");
                //아무것도 안누르고 저장을 누를 시 url 호출 ㄴㄴ
                if(checkCount==0){
                    Toast.makeText(getApplicationContext(),"한 개 이상 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    RegDB regDB = new RegDB();
                    regDB.execute();
                }
            }
        });


    }

    public class RegDB extends  AsyncTask<Void,Integer,Void>{
        public String data;

        @Override
        protected Void doInBackground(Void... voids) {
            String param = "id="+id+"&cname="+cName;
            try{
                URL url = new URL(getString(R.string.server_url)+"regclass.php");
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
            if(data.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddClassActivity.this);
                builder.setMessage("강의실 등록 완료");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"등록이 완료 되었습니다.",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddClassActivity.this);
                builder.setMessage("네트워크가 불안정 합니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"네트워크를 확인해주세요",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        }
    }

    public class ClassDB extends AsyncTask<Void,Integer,Void> {
        public String data;
        ArrayList<Classroom> items = new ArrayList();
        @Override
        protected Void doInBackground(Void... params) {
            String param = "id="+id;

            try{
                URL url = new URL(getString(R.string.server_url)+"unregclass.php");
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
                    items.add(new Classroom(jsonObject.getString("roomnumber"), jsonObject.getString("roomtype")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new AddClassActivity.Addadapter(items,mContext);
            recyclerView.setAdapter(adapter);


        }
    }

    public class Addadapter extends RecyclerView.Adapter<Addadapter.ViewHolder> {
        private Context mContext;
        private ArrayList<Classroom> mItems;

        public Addadapter(ArrayList<Classroom> mItems,Context mContext){
            this.mItems = mItems;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_manager,parent,false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Classroom classroom = mItems.get(position);

            String mType = classroom.getRoomType();
            if(mType.equals("1")){
                holder.imageView.setImageResource(R.mipmap.ic_classroom1);
                holder.textView1.setText("일반강의실");
            }else if(mType.equals("2")){
                holder.imageView.setImageResource(R.mipmap.ic_computer);
                holder.textView1.setText("컴퓨터실");
            }else{
                holder.imageView.setImageResource(R.mipmap.ic_laboratory);
                holder.textView1.setText("실험실");
            }
            holder.textView.setText(classroom.getRoomName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(classroom.isRoomCheck() == false){
                        classroom.setRoomCheck(true);
                        holder.cardView.setBackgroundColor(Color.parseColor("#55bbbbbb"));
                    }
                    else {
                        classroom.setRoomCheck(false);
                        holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public ArrayList<Classroom> getItems(){
            return this.mItems;
        }



        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView;
            public TextView textView1;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView)itemView.findViewById(R.id.type_iv);
                textView = (TextView)itemView.findViewById(R.id.roomname_tv);
                textView1 = (TextView)itemView.findViewById(R.id.type_tv);
                cardView = (CardView)itemView.findViewById(R.id.manager_cv);
            }
        }
    }
}
