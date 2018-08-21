package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import inucseapp.inumanager.damain.History;

public class HistoryListActivity extends AppCompatActivity {
    Context mContext;

    ArrayList<History> items = new ArrayList();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        mContext = getApplicationContext();

        init();
    }

    private void init() {
        ActionbarInit();

        recyclerView = (RecyclerView)findViewById(R.id.history_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        HistoryDB historyDB = new HistoryDB();
        historyDB.execute();


    }

    private void ActionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle("최근 점검 기록");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80D8FF")));
    }

    public class HistoryDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "";
            Log.i("test","메소드 실행");

            try{
                URL url = new URL(getString(R.string.server_url)+"history_list.php");
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
            items.clear();
            try{
                JSONArray jsonArray = new JSONArray(data);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new History(jsonObject.getString("roomnumber"),jsonObject.getString("user"), jsonObject.getString("time"),jsonObject.getString("checklist_history_id")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new Myadapter(items,mContext);
            recyclerView.setAdapter(adapter);


        }
    }

    public  class Myadapter extends RecyclerView.Adapter<HistoryListActivity.Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<History> mItems;

        public Myadapter(ArrayList<History> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public HistoryListActivity.Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_history,parent,false);
            HistoryListActivity.Myadapter.ViewHolder holder = new HistoryListActivity.Myadapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final HistoryListActivity.Myadapter.ViewHolder holder, final int position) {
            final History history = mItems.get(position);

            holder.textView.setText(history.getRoomnumber());
            holder.textView1.setText(history.getUserId());
            holder.textView2.setText(history.getTime());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent classIntent = new Intent(mContext,HistoryObjectActivity.class);
                    classIntent.putExtra("id","admin");
                    classIntent.putExtra("date",history.getTime());
                    classIntent.putExtra("checklist_history_id",history.getCheck_list_history_id());
                    startActivity(classIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;
            public TextView textView1;
            public TextView textView2;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textView = (TextView)itemView.findViewById(R.id.roomname_tv);
                this.textView1 = (TextView)itemView.findViewById(R.id.user_tv);
                this.textView2 = (TextView)itemView.findViewById(R.id.time_tv);
                this.cardView = (CardView)itemView.findViewById(R.id.cardview);
            }
        }
    }
}
