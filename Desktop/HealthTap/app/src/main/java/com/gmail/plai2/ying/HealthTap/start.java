package com.gmail.plai2.ying.HealthTap;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;



public class start extends AppCompatActivity {

    Handler handler;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        img = (ImageView) findViewById(R.id.logo_id);
        img.startAnimation(animation);



        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(start.this, login.class);
                startActivity(intent);
                finish();
            }

        },5000);
    }
}
