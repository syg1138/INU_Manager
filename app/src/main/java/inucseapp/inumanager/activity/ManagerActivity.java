package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
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
import inucseapp.inumanager.accessServer.AccessDB;
import inucseapp.inumanager.accessServer.AccessHandler;
import inucseapp.inumanager.accessServer.HandlerMethod;
import inucseapp.inumanager.damain.Classroom;

/**
 * Created by syg11 on 2017-11-11.
 */

public class ManagerActivity extends AppCompatActivity{
    Context mContext;
    String name, type, id;
    Intent intent;
    TextView textView;

    ArrayList<Classroom> items = new ArrayList();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        mContext = getApplicationContext();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String param = "id="+id;
        if(type.equals("0")||type.equals("1")){
            param = "id=admin";
        }

        ClassHandlerMethod classHandlerMethod = new ClassHandlerMethod();
        AccessHandler accessHandler = new AccessHandler(classHandlerMethod);
        AccessDB accessDB = new AccessDB(accessHandler,getString(R.string.server_url),AccessDB.CLASSMANAGER_PHP,param);
        accessDB.execute();
    }

    private void init() {
        //액션바 세팅
        intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        ImageView imageView =(ImageView)findViewById(R.id.type_manager);
        textView = (TextView)findViewById(R.id.name_manager);
        if(type.equals("0")){
            imageView.setImageResource(R.mipmap.ic_admin);
        }        else if(type.equals("1")){
            imageView.setImageResource(R.mipmap.ic_assistant);
        }else{
            imageView.setImageResource(R.mipmap.ic_student);
        }
        textView.setText(name);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80D8FF")));
        actionbar.setElevation(0f);

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton floatBtn = (FloatingActionButton)findViewById(R.id.floating_btn);
        if(type.equals("0")||type.equals("1")){
            floatBtn.setVisibility(View.GONE);
        }
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,AddClassActivity.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });

    }

    private void removeItem(int position){
        items.remove(position);
        adapter.notifyDataSetChanged();
    }


    public void deleteClass(String classNumber,String pos){
        String param = "id="+id+"&classnumber="+classNumber;
        int position = Integer.parseInt(pos);
        DeleteHandlerMethod deleteHandlerMethod = new DeleteHandlerMethod(position);
        AccessHandler accessHandler = new AccessHandler(deleteHandlerMethod);
        AccessDB accessDB = new AccessDB(accessHandler,getString(R.string.server_url),AccessDB.CLASSDELETE_PHP,param);
        accessDB.execute();
    }

    public class ClassHandlerMethod implements HandlerMethod{
        @Override
        public void beforeAccess() {

        }

        @Override
        public void afterAccess(Message msg) {
            String result = msg.obj.toString();

            items.clear();
            try{
                JSONArray jsonArray = new JSONArray(result);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new Classroom(jsonObject.getString("roomnumber"), jsonObject.getString("roomtype")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            adapter = new Myadapter(items,mContext);
            recyclerView.setAdapter(adapter);

        }

        @Override
        public void accessError() {
            Toast.makeText(getApplicationContext(),"정보를 불러올 수 없습니다.(error)",Toast.LENGTH_LONG).show();
        }
    }


    public class DeleteHandlerMethod implements HandlerMethod{
        int pos;


        public DeleteHandlerMethod(int pos){
            this.pos = pos;
        }

        @Override
        public void beforeAccess() {

        }

        @Override
        public void afterAccess(Message msg) {
            String result = msg.obj.toString();

            if(result.equals("0")){
                removeItem(pos);
                Toast.makeText(mContext,"삭제가 완료 되었습니다.",Toast.LENGTH_SHORT).show();
            }
            else {

            }
        }

        @Override
        public void accessError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
            builder.setMessage("잠시 후 다시 시도해주세요");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    }

    public  class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder>{
        private Context context;
        private ArrayList<Classroom> mItems;

        public Myadapter(ArrayList<Classroom> items, Context mContext){
            mItems = items;
            this.context = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_manager,parent,false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Classroom classroom = mItems.get(position);

            final String mType = classroom.getRoomType();
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
                    Toast.makeText(mContext,classroom.getRoomName()+"을 선택",Toast.LENGTH_SHORT).show();
                    Intent classIntent = new Intent(mContext,ClassinfoActivity.class);
                    classIntent.putExtra("id",id);
                    classIntent.putExtra("type",type);
                    classIntent.putExtra("classnumber",classroom.getRoomName());
                    classIntent.putExtra("classtype",classroom.getRoomType());
                    startActivity(classIntent);
                }
            });
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
                    builder.setMessage(holder.textView.getText()+" 강의실을 삭제 하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteClass(holder.textView.getText().toString(),String.valueOf(position));
                                }
                            });
                    builder.setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                    return true;
                }
            });
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
