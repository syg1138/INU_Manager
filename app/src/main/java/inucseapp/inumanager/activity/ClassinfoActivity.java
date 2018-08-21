package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import inucseapp.inumanager.R;

/**
 * Created by syg11 on 2017-12-01.
 */

public class ClassinfoActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String id,type,classnumber,classtype;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classinfo);
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

        ImageView imageView = (ImageView)findViewById(R.id.type_class_info);
        TextView textView1 = (TextView)findViewById(R.id.text1_room);
        TextView textView2 = (TextView)findViewById(R.id.text2_type);
        TextView textView3 = (TextView)findViewById(R.id.text3_below);

        Button checkBtn = (Button)findViewById(R.id.check_btn);
        Button historyBtn = (Button)findViewById(R.id.history_btn);
        Button checklistAddBtn = (Button)findViewById(R.id.checklist_add_btn);
        Button objectAddBtn = (Button)findViewById(R.id.object_add_btn);

        if(type.equals("0")||type.equals("1")){
            checklistAddBtn.setVisibility(View.VISIBLE);
            objectAddBtn.setVisibility(View.VISIBLE);
        }

        textView1.setText(this.classnumber);
        if(classtype.equals("1")){
            textView2.setText("일반강의실");
            imageView.setImageResource(R.mipmap.ic_classroom1);
        }
        else if(classtype.equals("2")){
            textView2.setText("컴퓨터실");
            imageView.setImageResource(R.mipmap.ic_computer);
        }
        else if(classtype.equals("3")){
            textView2.setText("실　험　실");
            imageView.setImageResource(R.mipmap.ic_laboratory);
        }
        textView3.setText("컴퓨터공학과");
    }

    private void actionbarInit(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("강의실 정보");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80D8FF")));
        actionbar.setElevation(0f);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.history_btn:
                Intent i = new Intent(mContext,HistoryActivity.class);
                i.putExtra("id",id);
                i.putExtra("type",type);
                i.putExtra("classnumber",classnumber);
                i.putExtra("classtype",classtype);
                startActivity(i);
                break;
            case R.id.check_btn:
                Intent i1 = new Intent(mContext,ChecklistActivity.class);
                i1.putExtra("id",id);
                i1.putExtra("type",type);
                i1.putExtra("classnumber",classnumber);
                i1.putExtra("classtype",classtype);
                startActivity(i1);
                break;
            case R.id.checklist_add_btn:
                Intent i2 = new Intent(mContext,AddCheckListActivity.class);
                i2.putExtra("id",id);
                i2.putExtra("type",type);
                i2.putExtra("classnumber",classnumber);
                i2.putExtra("classtype",classtype);
                startActivity(i2);
                break;
            case R.id.object_add_btn:
                Intent i3 = new Intent(mContext,ListObjectActivity.class);
                i3.putExtra("id",id);
                i3.putExtra("type",type);
                i3.putExtra("classnumber",classnumber);
                i3.putExtra("classtype",classtype);
                startActivity(i3);
                break;

        }
    }
}
