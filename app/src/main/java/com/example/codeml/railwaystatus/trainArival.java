package com.example.codeml.railwaystatus;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class trainArival extends AppCompatActivity {
    private static String key;
    private static String url_1 = "http://api.railwayapi.com/v2/suggest-station/name/";
    private static String url_2 = "/apikey/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_arival);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        SearchView search = (SearchView) findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(trainArival.this, key, Toast.LENGTH_SHORT).show();
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
        JSONArray jsonArray = json.getJSONArray("station");

        ArrayList<String> array = new ArrayList<String>();
        final ArrayList<String> trainNumber = new ArrayList<String>();

        //Array of all train with train number
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String train_no = object.getString("code");
            trainNumber.add(train_no);
            String name = object.getString("name");
            train_no = "code -     " + train_no + "\n" + "Station - " + name;
            array.add(train_no);
        }

        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
        ListView listView = (ListView) findViewById(R.id.alltrains);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Train number
                String trainnumber =  trainNumber.get(i);

                //Result from api
                String url = "http://api.railwayapi.com/v2/arrivals/station/" + trainnumber + "/hours/4/apikey/"  + key + "/";

                Ion.with(trainArival.this)
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
            Toast.makeText(this, "Some Problem", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray array = json.getJSONArray("train");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        linearLayout.removeAllViews();


        for(int i=0; i<array.length();i++){
            JSONObject item = array.getJSONObject(i);
            String output = getStringToDisplay(item);
            TextView newView = new TextView(this);
            newView.setText(output);
            newView.setTextSize(20);
            newView.setTextColor(Color.parseColor("#0000FF"));
            newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(newView);
        }

    }

    public String getStringToDisplay(JSONObject object) throws JSONException {
        String result = "Name -  ";

        //fetching train name
        result+=object.getString("name") +"\n";

        //number
        result+= "Train.no - ";
        result+= object.getString("number") +"\n";

        //actarr(Expected Arrival)
        result+="Ex. arrival - ";
        result+= object.getString("actarr") + "\n";

        //actdep(Expected departure)
        result+="Ex. departure - ";
        result+= object.getString("actdep") + "\n";


        return result;

    }

}
