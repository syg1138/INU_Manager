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
import inucseapp.inumanager.damain.CheckListHistory;

public class HistoryActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String id,classnumber,classtype;

    RecyclerView recyclerView;
    HistoryActivity.Myadapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mContext = getApplicationContext();
        intent = getIntent();

        init();
    }

    private void init() {
        id = intent.getStringExtra("id");
        classnumber = intent.getStringExtra("classnumber");
        classtype = intent.getStringExtra("classtype");
        actionbarInit();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_history);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SubmitListDB submitListDB = new SubmitListDB();
        submitListDB.execute();
    }

    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle(classnumber+" 점검 기록");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class SubmitListDB extends AsyncTask<Void,Integer,Void> {
        public String data;
        ArrayList<CheckListHistory> items = new ArrayList();

        @Override
        protected Void doInBackground(Void... params) {
            String param = "id=" + id + "&roomnumber=" + classnumber;
            Log.i("test", "점검사항 제출 \nid=" + id +
                    "\nroomnumber=" + classnumber
            );

            try {
                URL url = new URL(getString(R.string.server_url) + "checklist_history_load.php");
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
                    items.add(new CheckListHistory(jsonObject.getString("time"), jsonObject.getString("check_user_id"),jsonObject.getString("checklist_history_id")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new HistoryActivity.Myadapter(items,mContext);
            recyclerView.setAdapter(adapter);

        }
    }

    public  class Myadapter extends RecyclerView.Adapter<HistoryActivity.Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<CheckListHistory> mItems;

        public Myadapter(ArrayList<CheckListHistory> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public HistoryActivity.Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_checklist_history,parent,false);
            HistoryActivity.Myadapter.ViewHolder holder = new HistoryActivity.Myadapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final HistoryActivity.Myadapter.ViewHolder holder, int position) {
            final CheckListHistory checkListHistory = mItems.get(position);

            holder.textView.setText(checkListHistory.getTime());
            holder.textView1.setText(checkListHistory.getCheckUser());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"선택",Toast.LENGTH_SHORT).show();
                    Intent classIntent = new Intent(mContext,HistoryObjectActivity.class);
                    classIntent.putExtra("id",id);
                    classIntent.putExtra("date",holder.textView.getText().toString());
                    classIntent.putExtra("checklist_history_id",checkListHistory.getCheckHistoryId());
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
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView)itemView.findViewById(R.id.check_history_cv);
                this.textView = (TextView)itemView.findViewById(R.id.time_tv);
                this.textView1 = (TextView)itemView.findViewById(R.id.name_tv);
            }
        }
    }
}
