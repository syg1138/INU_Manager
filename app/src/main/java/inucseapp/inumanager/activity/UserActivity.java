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
import android.widget.ImageView;
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
import inucseapp.inumanager.damain.User;

public class UserActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String id;

    ArrayList<User> items = new ArrayList();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mContext = getApplicationContext();
        intent = getIntent();

        init();
    }

    private void init() {
        intent = getIntent();
        id = intent.getStringExtra("id");
        actionbarInit();

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        UserDB userDB = new UserDB();
        userDB.execute();
    }

    private void actionbarInit() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle("전체 회원 메뉴");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public class UserDB extends AsyncTask<Void,Integer,Void> {
        public String data;

        @Override
        protected Void doInBackground(Void... params) {
            String param = "id="+id;
            Log.i("test","메소드 실행 id="+id);

            try{
                URL url = new URL(getString(R.string.server_url)+"user_list.php");
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
                    items.add(new User(jsonObject.getString("id"),jsonObject.getString("name"), jsonObject.getString("type")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new Myadapter(items,mContext);
            recyclerView.setAdapter(adapter);


        }
    }

    public  class Myadapter extends RecyclerView.Adapter<UserActivity.Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<User> mItems;

        public Myadapter(ArrayList<User> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public UserActivity.Myadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_manager,parent,false);
            UserActivity.Myadapter.ViewHolder holder = new UserActivity.Myadapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final UserActivity.Myadapter.ViewHolder holder, final int position) {
            final User user = mItems.get(position);

            final String mType = user.getType();
            if(mType.equals("0")){
                holder.imageView.setImageResource(R.mipmap.ic_admin);
                holder.textView1.setText("ID: "+user.getId());
            }else if(mType.equals("1")){
                holder.imageView.setImageResource(R.mipmap.ic_assistant);
                holder.textView1.setText("ID: "+user.getId());
            }else{
                holder.imageView.setImageResource(R.mipmap.ic_student);
                holder.textView1.setText("ID: "+user.getId());
            }
            holder.textView.setText(user.getName());

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView;
            public TextView textView1;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.imageView = (ImageView)itemView.findViewById(R.id.type_iv);
                this.textView = (TextView)itemView.findViewById(R.id.roomname_tv);
                this.textView1 = (TextView)itemView.findViewById(R.id.type_tv);
                this.cardView = (CardView)itemView.findViewById(R.id.manager_cv);
            }
        }
    }
}
