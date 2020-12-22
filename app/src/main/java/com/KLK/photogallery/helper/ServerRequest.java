package com.KLK.photogallery.helper;

import android.app.Activity;
import android.util.Log;
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
    int response;
    String message;
    private ArrayList<String> images_from_server = null;
    private String avatar_from_server = null;
    JSONObject user_info = null;
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
                    public void onResponse(String response_) {
                        try {
                            JSONObject obj = new JSONObject(response_);
                            message = obj.getString("message");
                            response = obj.getInt("response");
                            displayText(message);
                            if (obj.has("images")) {
                                JSONArray images = obj.getJSONArray("images");
                                getImageGrid(images);
                            }
                            if (obj.has("avatar")) {
                                String avatar = obj.getString("avatar");
                                setAvatar(avatar);
                            }
                            if (obj.has("user_info")){
                                JSONObject info = obj.getJSONObject("user_info");
                                setUserInfo(info);
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

    public void setUserInfo(JSONObject info){
        this.user_info = info;
        if (this.user_info != null) {
            Log.e(TAG, "User info received");
        }
    }

    public JSONObject getUserInfo(){
        return this.user_info;
    }

    public void setAvatar(String avatar64Base) {
        avatar_from_server = avatar64Base;
        Log.e(TAG, "avatar received");
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
    public String getAvatarBase64String(){ return avatar_from_server; }

    public int getResponse(){
        return response;
    }

    public String getMessage(){
        return message;
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