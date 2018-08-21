package inucseapp.inumanager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inucseapp.inumanager.R;

public class AssistantActivity extends AppCompatActivity {
    Context mContext;
    Intent intent;
    String a_id,a_type,a_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        mContext = getApplicationContext();
        intent = getIntent();

        init();
    }

    private void init() {
        a_id = intent.getStringExtra("id");
        a_type = intent.getStringExtra("type");
        a_name = intent.getStringExtra("name");
        actionbarInit();
    }

    private void actionbarInit() {
        ImageView imageView =(ImageView)findViewById(R.id.type_manager);
        TextView textView = (TextView)findViewById(R.id.name_manager);

        ActionBar actionbar = getSupportActionBar();
        if(a_type.equals("0")){
            imageView.setImageResource(R.mipmap.ic_admin);
        }else if(a_type.equals("1")){
            imageView.setImageResource(R.mipmap.ic_assistant);
        }else{
            imageView.setImageResource(R.mipmap.ic_student);
        }
        textView.setText(a_name);
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setTitle("관리자 메뉴");
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                Intent intent = new Intent(getApplicationContext(),ManagerActivity.class);
                intent.putExtra("id",a_id);
                intent.putExtra("type",a_type);
                intent.putExtra("name",a_name);
                startActivity(intent);
                break;
            case R.id.button1_1:
                Intent intent1_1 = new Intent(getApplicationContext(),CreateClassActivity.class);
                intent1_1.putExtra("id",a_id);
                intent1_1.putExtra("type",a_type);
                intent1_1.putExtra("name",a_name);
                startActivity(intent1_1);
                break;
            case R.id.button2:
                Intent intent1 = new Intent(getApplicationContext(),ListObjectActivity.class);
                intent1.putExtra("id",a_id);
                intent1.putExtra("type",a_type);
                intent1.putExtra("name",a_name);
                intent1.putExtra("classnumber","none");
                startActivity(intent1);
                break;
            case R.id.button3:
                Intent intent2 = new Intent(getApplicationContext(),UserActivity.class);
                intent2.putExtra("id",a_id);
                intent2.putExtra("type",a_type);
                intent2.putExtra("name",a_name);
                intent2.putExtra("classnumber","none");
                startActivity(intent2);
                break;
            case R.id.button4:
                Intent intent3 = new Intent(getApplicationContext(),HistoryListActivity.class);
                intent3.putExtra("id",a_id);
                intent3.putExtra("type",a_type);
                intent3.putExtra("name",a_name);
                intent3.putExtra("classnumber","none");
                startActivity(intent3);
                break;
        }
    }
}
