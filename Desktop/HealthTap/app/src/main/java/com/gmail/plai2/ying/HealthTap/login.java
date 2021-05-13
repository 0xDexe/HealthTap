package com.gmail.plai2.ying.HealthTap;

import android.content.Intent;
import android.os.Bundle;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gmail.plai2.ying.HealthTap.R;
import com.gmail.plai2.ying.HealthTap.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class login extends AppCompatActivity {
    Button button;
    EditText u,p;
    TextView reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
                LinearLayout layout = (LinearLayout) findViewById(R.id.LoginLayout);
        button = (Button)findViewById(R.id.button);
        u = (EditText)findViewById(R.id.user);
        p = (EditText)findViewById(R.id.pass);
        reg = (TextView)findViewById(R.id.register);
        final String user = u.getText().toString();
        final String pass = p.getText().toString();
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        Pattern checkRegex = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
        Matcher regexMatcher = checkRegex.matcher(user);
        Pattern checkRegex2 = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
        Matcher regexMatcher2 = checkRegex2.matcher(pass);



        button.setOnClickListener(v -> {
                if(regexMatcher.matches() && regexMatcher2.matches() )
                {
                    Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                }
                else if(pass.equals("") || pass.length() == 0 || user.equals("") || user.length() == 0)
                {
                    layout.startAnimation(animation);
                    Toast.makeText(getApplicationContext(),"empty fields",Toast.LENGTH_LONG).show();
                }
                else if(!regexMatcher.matches() || !regexMatcher2.matches() )
                {
                    layout.startAnimation(animation);
                    Toast.makeText(getApplicationContext(),"Invalid details",Toast.LENGTH_LONG).show();
                }


        });



    }

}