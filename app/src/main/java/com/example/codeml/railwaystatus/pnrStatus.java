package com.example.codeml.railwaystatus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

public class pnrStatus extends AppCompatActivity {

    private static String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnr_status);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

    }

    public void showStatus(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String text = String.valueOf(editText.getText());

        String url = "http://api.railwayapi.com/v2/pnr-status/pnr/" + text + "/apikey/" + key;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            showPNR(result);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public void showPNR(String res) throws JSONException {
        JSONObject object = new JSONObject(res);
        int response_code = object.getInt("response_code");

        if(response_code != 200){
            Toast.makeText(this, "PNR Invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = "";
        JSONObject json ;

        json = object.getJSONObject("train");
        result+= "train - " + json.getString("name") + "-" + json.getString("number") + "\n";

        json = object.getJSONObject("to_station");
        result+= "to_station - " + json.getString("name") + "-" + json.getString("code") + "\n";

        json = object.getJSONObject("boarding_point");
        result+= "boarding_point - " + json.getString("name") + "-" + json.getString("code") + "\n";

        json = object.getJSONObject("reservation_upto");
        result+= "reservation_upto - " + json.getString("name") + "-" + json.getString("code") + "\n";

        json = object.getJSONObject("journey_class");
        result+= "journey_class - " + json.getString("name") + "-" + json.getString("code") + "\n";

        TextView text = (TextView) findViewById(R.id.pnr);
        text.setText(result);
    }
}
