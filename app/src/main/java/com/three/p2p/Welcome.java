package com.three.p2p;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Welcome extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        progressBar=(ProgressBar)findViewById(R.id.progressbarId) ;

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                start();
            }
        });
        thread.start();
    }



    private void doWork() {
        for(progress=20;progress<=100;progress+=20)
        {
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            }catch (Exception e)
            {
             e.printStackTrace();
            }
        }
    }
    private void start() {
        Intent intent=new Intent(Welcome.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
