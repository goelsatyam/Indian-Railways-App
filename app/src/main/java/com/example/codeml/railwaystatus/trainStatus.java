package com.example.codeml.railwaystatus;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class trainStatus extends AppCompatActivity {
    private static String key;
    private static String url_1 = "http://api.railwayapi.com/v2/suggest-train/train/";
    private static String url_2 = "/apikey/";
    private static int select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_status);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        SearchView search = (SearchView) findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(trainStatus.this, key, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getJson(newText);
                return false;
            }
        });


    }

    public void getJson(String text) {
        String url = url_1 + text + url_2 + key + "/";

        //Fetching list of trains
        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            processResult(result);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public void processResult(String result) throws JSONException {

        JSONObject json = new JSONObject(result);
        JSONArray jsonArray = json.getJSONArray("trains");

        ArrayList<String> array = new ArrayList<String>();
        final ArrayList<String> trainNumber = new ArrayList<String>();

        //Array of all train with train number
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String train_no = object.getString("number");
            trainNumber.add(train_no);
            String name = object.getString("name");
            train_no = train_no + "\n" + name;
            array.add(train_no);
        }

        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
        ListView listView = (ListView) findViewById(R.id.alltrains);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Fetch Data
                Date date = new Date();
                String todate= new SimpleDateFormat("dd-MM-yyyy").format(date);

                //Train number
                String trainnumber =  trainNumber.get(i);

                //Result from api
                String url = "http://api.railwayapi.com/v2/live/train/" + trainnumber + "/date/" + todate + "/apikey/" + key + "/";

                Ion.with(trainStatus.this)
                        .load(url)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                adapter.clear();
                                try {
                                    showResult(result);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            }
        });
    }

    public void showResult(String result) throws JSONException {
        JSONObject json = new JSONObject(result);

        int code = json.getInt("response_code");
        if(code == 210){
            Toast.makeText(this, "Train do not run Today", Toast.LENGTH_SHORT).show();
            return;
        }

       JSONArray array = json.getJSONArray("route");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        linearLayout.removeAllViews();

        for(int i=0; i<array.length();i++){
            JSONObject item = array.getJSONObject(i);
            String output = getStringToDisplay(item);
            TextView newView = new TextView(this);
            newView.setText(output);
            newView.setTextSize(25);
            //newView.setTextColor(Color.parseColor("#FFFFFF"));
            if(select == 0) {
                newView.setTextColor(Color.parseColor("#ff2052"));
            }
            else if(select == 1){
                newView.setTextColor(Color.parseColor("#FFFF66"));
            }
            else {
                newView.setTextColor(Color.parseColor("#00FF00"));
            }


            newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(newView);
        }

    }

    public String getStringToDisplay(JSONObject object) throws JSONException {
        String result = "Station - ";

        //fetchint station name
        JSONObject stationName = object.getJSONObject("station");
        String name = stationName.getString("name") + "(";
        name+= stationName.getString("code") + ")";
        result += name + "\n";

        //Status
        result+= "Status - ";
        result+= object.getString("status") +"\n";

        //actarr(Expected Arrival)
        result+="Expected arrival - ";
        result+= object.getString("actarr") + "\n";

        //actdep(Expected departure)
        result+="Expected departure - ";
        result+= object.getString("actdep") + "\n";

        //Select color
        Boolean hasArrived = object.getBoolean("has_arrived");
        Boolean hasDeparted = object.getBoolean("has_departed");

        if(hasArrived == false){
            select = 2;
        }
        else if(hasArrived != hasDeparted){
            select = 1;
        }
        else{
            select = 0;
        }

        return result;

    }
}
