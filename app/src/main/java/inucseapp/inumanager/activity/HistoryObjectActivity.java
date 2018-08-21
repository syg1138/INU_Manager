package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import inucseapp.inumanager.damain.CheckList;

/**
 * Created by syg11 on 2017-12-06.
 */

public class HistoryObjectActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String id,checkListHistoryId,date;

    RecyclerView recyclerView;
    HistoryObjectActivity.Myadapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_object);

        mContext = getApplicationContext();
        intent = getIntent();

        init();

    }

    private void init() {
        id = intent.getStringExtra("id");
        checkListHistoryId = intent.getStringExtra("checklist_history_id");
        date = intent.getStringExtra("date");
        actionbarInit();

        TextView dateTv = (TextView)findViewById(R.id.date_tv);
        dateTv.setText(date);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_history_detail);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DetailLoadDB detailLoadDB = new DetailLoadDB();
        detailLoadDB.execute();
    }
    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle("상세 정보");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class DetailLoadDB extends AsyncTask<Void,Integer,Void> {
        public String data;
        ArrayList<CheckList> items = new ArrayList();

        @Override
        protected Void doInBackground(Void... params) {
            String param = "checklist_history_id=" +checkListHistoryId;

            try {
                URL url = new URL(getString(R.string.server_url) + "checklist_history_detail_load.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.i("test", "data : " + data);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                JSONArray jsonArray = new JSONArray(data);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new CheckList(
                            "0",
                            jsonObject.getString("objectname"),
                            jsonObject.getString("kname"),
                            jsonObject.getString("checklist"),
                            jsonObject.getString("check_bol"),
                            jsonObject.getString("reason")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new HistoryObjectActivity.Myadapter(items,mContext);
            recyclerView.setAdapter(adapter);

        }
    }

    public  class Myadapter extends RecyclerView.Adapter<HistoryObjectActivity.Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<CheckList> mItems;

        public Myadapter(ArrayList<CheckList> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public HistoryObjectActivity.Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_checklist_history_detail,parent,false);
            HistoryObjectActivity.Myadapter.ViewHolder holder = new HistoryObjectActivity.Myadapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(HistoryObjectActivity.Myadapter.ViewHolder holder, int position) {
            final CheckList checkList = mItems.get(position);

            holder.textView.setText(checkList.getObjectName_kr());
            holder.textView1.setText(checkList.getCheckList());
            holder.textViewReason.setText(checkList.getReason());
            if(!(checkList.getReason().equals("이상 없음"))){
                holder.textViewLight.setTextColor(Color.parseColor("#ffcc2222"));
                holder.textViewReason.setTextColor(Color.parseColor("#ff000000"));
                holder.textViewReason.setTextSize(16);
            }else{
                holder.textViewLight.setTextColor(Color.parseColor("#ff22cc22"));
                holder.textViewReason.setTextColor(Color.parseColor("#ff555555"));
                holder.textViewReason.setTextSize(12);
            }

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;
            public TextView textView1;
            public TextView textViewLight;
            public TextView textViewReason;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView)itemView.findViewById(R.id.check_object_history_cv);
                this.textView = (TextView)itemView.findViewById(R.id.object_name_detail);
                this.textView1 = (TextView)itemView.findViewById(R.id.check_list_detail);
                this.textViewLight = (TextView)itemView.findViewById(R.id.light_detail);
                this.textViewReason = (TextView)itemView.findViewById(R.id.reason_detail);
            }
        }
    }


}
