package com.example.codeml.railwaystatus;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class cancelT extends AppCompatActivity {

    private  static String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_t);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        Date date = new Date();
        String todate= new SimpleDateFormat("dd-MM-yyyy").format(date);

        String url = "http://api.railwayapi.com/v2/cancelled/date/" + todate + "/apikey/" + key;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            getResult(result);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    public void getResult(String result) throws JSONException {

        JSONObject object = new JSONObject(result);
        JSONArray array = object.getJSONArray("trains");
        JSONObject json;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        linearLayout.removeAllViews();
        for(int i=0 ;i< array.length();i++){
            json = array.getJSONObject(i);
            String ans = "";
            TextView textView = new TextView(this);

            ans+= "Source -   " + json.getJSONObject("source").getString("name") +"\n";
            ans+= "Dest   -   " + json.getJSONObject("dest").getString("name") +"\n";
            ans+= "Name   -   " + json.getJSONObject("train").getString("name") +"\n";
            ans+= "Start  -   " + json.getJSONObject("train").getString("start_time") +"\n";
            ans+= "Train.no - " + json.getJSONObject("train").getString("number") +"\n";

            textView.setTextColor(Color.parseColor("#000000"));
            textView.setTextSize(15);
            textView.setText(ans);
            linearLayout.addView(textView);
        }

    }

}
