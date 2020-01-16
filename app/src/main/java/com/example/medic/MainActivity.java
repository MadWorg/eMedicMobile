package com.example.medic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.medic.data.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView bolezni;
    private TextView input;
    private int nameCount = 0;
    private int descCount = 0;
    private String lastCall;


    private Map<String, String> names = new HashMap<>();
    private Map<String, String> type = new HashMap<>();
    private Map<String, String> desc = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        bolezni = findViewById(R.id.diseaseDisp);
        input = findViewById(R.id.diseaseInput);
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            buildData(response);
        }
    };

    public void refresh(View view) {
        System.out.println(nameCount);
        System.out.println(descCount);
        if(lastCall.equals(null)) {
            printData("");
        } else {
            printData(lastCall);
        }

    }

    public void showData(View view) {

        names.clear();
        type.clear();
        desc.clear();
        nameCount = 0;
        descCount = 0;

        String req = input.getText().toString();
        loadData(view);
        printData(req);
        lastCall = req;

    }

    public void printData(String name) {

        // should only be called when you have all three dictionaries
        bolezni.setText("Press the refresh button if nothing is displayed");
        StringBuilder sb = new StringBuilder();
        String [] temp;

        try {
            if(name.equals("")) {
                for(String dName : names.keySet()) {

                    sb.append("<---" + dName + "--->" + "\n\n");

                    String s = desc.get(names.get(dName));
                    if(s ==null || s.equals("")) {
                        continue;
                    }
                    temp = s.split("_");

                    for(String is : temp) {
                        String [] temp2 = is.split(":");
                        String newName = type.get(temp2[0]);
                        sb.append(newName + ":\n" + temp2[1] + "\n\n");
                    }

                }
            } else if (names.containsKey(name)) {

                sb.append(name + "\n\n");

                temp = desc.get(names.get(name)).split("_");

                for(String is : temp) {

                    String [] temp2 = is.split(":");
                    String newName = type.get(temp2[0]);
                    sb.append(newName + ":\n" + temp2[1] + "\n\n");
                }

            } else {
                bolezni.setText("No such disease in the database.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String out = sb.toString();
        if(!out.equals("")) {
            bolezni.setText(sb.toString());
        }
        bolezni.setMovementMethod(new ScrollingMovementMethod());
    }


    private void buildData(JSONArray response) { // call this when you get a result from the server


        for (int i=0; i<response.length(); i++) {
            try {

                JSONObject object = response.getJSONObject(i);
                String jsonType = object.keys().next();

                if (jsonType.equals("deseaseNameId")) {

                    String id = object.getString("deseaseNameId");
                    String name = object.getString("name");

                    nameCount++;

                    names.put(name, id);

                } else if (jsonType.equals("deseaseFactId")) {

                    String id = object.getString("deseaseFactId");
                    String name = object.getString("fact");
                    type.put(id,name);

                } else if(jsonType.equals("deseaseDescritptionId")) {

                    String nameId = object.getString("deseaseNameId");
                    String typeId = object.getString("deseaseFactId");
                    String info = object.getString("description");
                    String comp = typeId + ":" + info;

                    descCount++;

                    if(!desc.containsKey(nameId)) {
                        desc.put(nameId, comp);
                    } else {
                        desc.put(nameId, desc.get(nameId) + "_" + comp);
                    }

                } else {
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void loadData(View view) {
        if (view != null) {

            String url = "https://apiemedic20191214091237.azurewebsites.net/api/DeseaseDescritptions";
            readUrl(url);

            url = "https://apiemedic20191214091237.azurewebsites.net/api/DeseaseNames";
            readUrl(url);

            url = "https://apiemedic20191214091237.azurewebsites.net/api/DeseaseFacts";
            readUrl(url);
        }
    }

    public void readUrl(String url) {

        /*
        GlobalURL.url = url;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestFuture<JSONArray> future = RequestFuture.newFuture();
                    JsonArrayRequest request = new JsonArrayRequest(GlobalURL.url, future, future);
                    requestQueue.add(request);
                    JSONArray result = future.get();
                    System.out.println(result.toString());
                    //done(result);
                } catch (Exception e) {

                }
            }

            public void done(JSONArray result) {
                //buildData(result);
            }
        });


         */

		/*
        RequestFuture<JSONArray> reqFuture = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(), reqFuture, reqFuture);
        requestQueue.add(request);
        try {
            JSONArray result = reqFuture.get(10, TimeUnit.SECONDS);
            buildData(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
		*/


		JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        requestQueue.add(request);


    }

    public void addDisease(View view) {
        Intent intent = new Intent(this, AddNew.class);
        // bundle data into intent here or whatever
        if(nameCount > 0 && descCount > 0) {
            intent.putExtra("count",nameCount + "_" + descCount);
        }
        startActivity(intent);
    }


}
