package inucseapp.inumanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import inucseapp.inumanager.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {
    public static final int MSG_BEFORE_ACCESS = 0 ;
    public static final int MSG_AFTER_ACCESS = 1;
    public static final int MSG_ACCESS_ERROR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        loading();
    }

    private void loading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },750);
    }
}
