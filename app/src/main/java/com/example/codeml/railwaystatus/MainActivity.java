package com.example.codeml.railwaystatus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String key = "8k48k51j5f";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void liveStatus(View view) {
        Intent intent = new Intent(this, trainStatus.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public void trainArrivals(View view) {
        Intent intent = new Intent(this, trainArival.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public void pnrShow(View view) {
        Intent intent = new Intent(this, pnrStatus.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public void CanceTrains(View view) {
        Intent intent = new Intent(this, cancelT.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }
}
