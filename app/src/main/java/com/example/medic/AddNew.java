package com.example.medic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class AddNew extends AppCompatActivity {

    private EditText name;
    private EditText definition;
    private EditText cause;
    private EditText symptoms;
    private EditText treatment;
    private EditText diagnosis;

    private int nameCount;
    private int descCount;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // make popup

        definition = findViewById(R.id.definitionIn);
        cause = findViewById(R.id.causeIn);
        symptoms = findViewById(R.id.symptomsIn);
        treatment = findViewById(R.id.treatmentIn);
        diagnosis = findViewById(R.id.diagnosisIn);

        name = findViewById(R.id.nameIn);


        //get extras if there are any

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            String[] data = extras.getString("count").split("_");
            nameCount = Integer.parseInt(data[0]);
            descCount = Integer.parseInt(data[1]);
        }
        System.out.println(nameCount);
        System.out.println(descCount);

    }




    public void goBack(View view) {
        finish();
    }

    public void commitNew(View view) {

        String missing = "";

        Context context = getApplicationContext();

        if(name.getText().length() == 0) {

            CharSequence text = "Please enter a name.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;

        }

        String defTxt = definition.getText().toString();
        String causeTxt = cause.getText().toString();
        String symptomsTxt = symptoms.getText().toString();
        String treatTxt = treatment.getText().toString();
        String diagTxt = diagnosis.getText().toString();

        if(defTxt.equals("")) {
            missing = missing + "Definition\n";
        }

        if(causeTxt.equals("")) {
            missing = missing + "Causes\n";
        }

        if(symptomsTxt.equals("")) {
            missing = missing + "Symptons and signs\n";
        }

        if(treatTxt.equals("")) {
            missing = missing + "Treatment\n";
        }

        if(diagTxt.equals("")) {
            missing = missing + "Diagnosis\n";
        }

        if(!missing.equals("")) {
            cDiag confirm = new cDiag(this, missing, new AddNew.OnDialogClickListener() {
                @Override
                public void onDialogClick(View view1) {
                    switch (view1.getId()) {
                        case R.id.yesBut:
                            System.out.println("User will not finish the form");
                            // commit new data to server
                            uploadHandler();
                            break;
                        case R.id.noBut:
                            System.out.println("User wants to fill out form");
                            break;
                    }
                }
            });
            confirm.show();
            return;
        }

        uploadHandler();
        CharSequence text = "Uploading...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void uploadHandler() {

        String defTxt = definition.getText().toString();
        String causeTxt = cause.getText().toString();
        String symptomsTxt = symptoms.getText().toString();
        String treatTxt = treatment.getText().toString();
        String diagTxt = diagnosis.getText().toString();

        String newName = name.getText().toString();
        nameCount++;
        upload("name", newName, "1234");

        /*
        if(!defTxt.equals("")) {
            descCount++;
            upload("desc", defTxt, "1");
        } else {
            descCount++;
            upload("desc", "<MISSING>", "3");
        }

        if(!diagTxt.equals("")) {
            descCount++;
            upload("desc", diagTxt, "2");
        } else {
            descCount++;
            upload("desc", "<MISSING>", "3");
        }

        if(!treatTxt.equals("")) {
            descCount++;
            upload("desc", treatTxt, "3");
        } else {
            descCount++;
            upload("desc", "<MISSING>", "3");
        }

        if(!symptomsTxt.equals("")) {
            descCount++;
            upload("desc", symptomsTxt, "4");
        } else {
            descCount++;
            upload("desc", "<MISSING>", "3");
        }

        if(!causeTxt.equals("")) {
            descCount++;
            upload("desc", causeTxt, "5");
        } else {
            descCount++;
            upload("desc", "<MISSING>", "3");
        }
        */

    }

    public void upload(String type, String info, String id) {


        String URL = "";

        try {

            JSONObject jsonBody = new JSONObject();

            if(type.equals("name")) {
                jsonBody.put("name", info);
                //jsonBody.put("deseaseNameId", Integer.toString(nameCount));

                URL = "https://apiemedic20191214091237.azurewebsites.net/api/DeseaseNames"; //+ nameCount;

            } else if (type.equals("desc")) {

                jsonBody.put("description",info);
                //jsonBody.put("deseaseFactId",id);
                jsonBody.put("deseaseNameId", Integer.toString(nameCount));
                //jsonBody.put("deseaseDescritptionId",Integer.toString(descCount));

                URL = "https://apiemedic20191214091237.azurewebsites.net/api/DeseaseDescritptions"; //+ descCount;

            }


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public interface OnDialogClickListener {
        void onDialogClick(View view);
    }


}
