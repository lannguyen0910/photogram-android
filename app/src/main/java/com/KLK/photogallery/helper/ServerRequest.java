package com.KLK.photogallery.helper;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest {
    private static final String TAG = "Server Request";
    Activity activity;
    private ArrayList<String> images_from_server = null;
    private Button test_btn = null;

    public ServerRequest(Activity activity){
        this.activity = activity;
        if (activity == null){
            Log.e(TAG, "activity is null");
        }
    }

    public void sendRequestToServer(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("message", "None");
        sendRequestToServer(url, params);
    }

    public void sendRequestToServer(String url, Map<String, String> params)  {
        RequestQueue queue = Volley.newRequestQueue(this.activity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("message");
                            //displayText(message);
                            if (obj.has("images")) {
                                JSONArray images = obj.getJSONArray("images");
                                getImageGrid(images);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayText("Failed to login");
                error.printStackTrace();
            }

        }) {
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {

                return params;
            }
        };
        queue.add(stringRequest);
    }


    public void getImageGrid(JSONArray images) throws JSONException {
        images_from_server = new ArrayList<>();
        for (int i =0;i<images.length();i++){
            String response_image_string = images.getString(i);
            Log.e(TAG, "image received");
            images_from_server.add(response_image_string);
        }
        //gridview.setAdapter(new ImageAdapter(ImageGrid.this));
    }

    public ArrayList<String> getImageBase64Strings(){
        return images_from_server;
    }



    private void displayText(String response){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, response,Toast.LENGTH_SHORT). show();
            }
        });
    }
}