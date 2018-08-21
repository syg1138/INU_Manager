package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import inucseapp.inumanager.damain.ObjectName;

public class ListObjectActivity extends AppCompatActivity {
    Context mContext;
    ArrayList<ObjectName> listArray;
    Intent intent;

    String roomnumber;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_object);
        mContext = getApplicationContext();
        listArray = new ArrayList<ObjectName>();
        intent = getIntent();

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LoadObjectDB loadObjectDB = new LoadObjectDB();
        loadObjectDB.execute();
    }

    private void init() {
        roomnumber = intent.getStringExtra("classnumber");
        actionbarInit();

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton floatBtn = (FloatingActionButton)findViewById(R.id.object_floating_btn);
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,AddObjectActivity.class);
                startActivity(i);
            }
        });
    }

    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        if(roomnumber.equals("none")){
            actionbar.setTitle("전체 물품 목록");
        }else {
            actionbar.setTitle(roomnumber + "물품 추가");
        }
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class LoadObjectDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "roomnumber="+roomnumber;
            Log.i("test","메소드 실행");

            try{
                URL url = new URL(getString(R.string.server_url)+"object_list_load.php");
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
            listArray.clear();
            try{
                JSONArray jsonArray = new JSONArray(data);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listArray.add(new ObjectName(jsonObject.getString("objectname"),jsonObject.getString("kname")) );
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new ListObjectActivity.Myadapter(listArray,mContext);
            recyclerView.setAdapter(adapter);
        }
    }

    public class RegObjectDB extends AsyncTask<String,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(String... params) {
            String param = "roomnumber="+params[0] +"&objectname="+params[1];
            Log.i("test","메소드 실행" + param);

            try{
                URL url = new URL(getString(R.string.server_url)+"roomobject_reg.php");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ListObjectActivity.this);
                builder.setMessage("강의실에 물품 등록 완료");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"강의실에 물품 등록이 완료 되었습니다.",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                builder.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListObjectActivity.this);
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

    public  class Myadapter extends RecyclerView.Adapter<ListObjectActivity.Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<ObjectName> mItems;

        public Myadapter(ArrayList<ObjectName> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public ListObjectActivity.Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_string,parent,false);
            ListObjectActivity.Myadapter.ViewHolder holder = new ListObjectActivity.Myadapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ListObjectActivity.Myadapter.ViewHolder holder, final int position) {
            holder.textView.setText(mItems.get(position).getObjectNmaeKr());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(roomnumber.equals("none")){
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListObjectActivity.this);
                    builder.setMessage(mItems.get(position).getObjectNmaeKr()+"을 등록 하시겠습니까?");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    RegObjectDB regObjectDB = new RegObjectDB();
                                    regObjectDB.execute(roomnumber,mItems.get(position).getObjectName());
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textView = (TextView)itemView.findViewById(R.id.object_tv);
                this.cardView = (CardView)itemView.findViewById(R.id.object_cv);
            }
        }
    }
}
