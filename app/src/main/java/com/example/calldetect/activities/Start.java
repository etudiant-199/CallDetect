package com.example.calldetect.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.calldetect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Start extends AppCompatActivity {
    private Button startBtnAnim, startBtn;
    private Handler handlerAnimation = new Handler();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        ImageView icon_image = findViewById(R.id.icon_image);
        Glide.with(this)
                .load(R.drawable.logo)
                .into(icon_image);
        startBtn = findViewById(R.id.start_btn_id);
        startBtnAnim = findViewById(R.id.start_btn_anim_id);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        handlerAnimation.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    startActivity(new Intent(Start.this, Home.class));
                    finish();
                }else {
                    startBtn.setVisibility(View.VISIBLE);
                    startBtnAnim.setVisibility(View.VISIBLE);
                    startAnimation();




                }




            }
        }, 1000);
    }

    private void startAnimation () {
        runnableAnimate.run();
    }
    private void stopAnimation () {
        handlerAnimation.removeCallbacks(runnableAnimate);
    }
    private Runnable runnableAnimate = new Runnable() {
        @Override
        public void run() {
            startBtnAnim.animate()
                    .scaleX(1.8f)
                    .scaleY(2.2f)
                    .alpha(0f)
                    .setDuration(700)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            startBtnAnim.setScaleX(1f);
                            startBtnAnim.setScaleY(1f);
                            startBtnAnim.setAlpha(1f);
                        }
                    });
            handlerAnimation.postDelayed(runnableAnimate, 1000);
        }
    };
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setFullScreen () {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void startActivity() {
        startActivity(new Intent(this, Login.class));
        stopAnimation();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAnimation();
    }
}
