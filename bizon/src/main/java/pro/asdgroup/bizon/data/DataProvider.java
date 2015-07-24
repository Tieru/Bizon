package pro.asdgroup.bizon.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import pro.asdgroup.bizon.BizonApp;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */

public class DataProvider {

    protected RequestCallback callback;

    public interface RequestCallback{
        public void onResult(JSONObject object);
        public void onFailRequest(String errorMessage);
    }

    public void setCallback(RequestCallback callback) {
        this.callback = callback;
    }

    public JSONObject get(String funcUrl){
        String url = HttpHelper.BASE_URL + funcUrl;


        RequestQueue queue = Volley.newRequestQueue(BizonApp.getAppContext());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        queue.add(request);

        try {
            JSONObject object = future.get();
            Log.d("pro.asdgroup.bizon.BizonApp", "JSON response: " + object.toString());

            if (callback != null) {
                callback.onResult(object);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            if(callback != null){
                callback.onFailRequest(e.getMessage());
            }
        }

        return null;
    }

    public JSONObject get(String funcUrl, HashMap<String, String> params){

        String url = getUrl(funcUrl, params);
        Log.d("pro.asdgroup.bizon.BizonApp", "Sending request: " + url);

        RequestQueue queue = Volley.newRequestQueue(BizonApp.getAppContext());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), future, future);
        queue.add(request);

        try {
            JSONObject object = future.get(10, TimeUnit.SECONDS);
            Log.d("pro.asdgroup.bizon.BizonApp", "JSON response: " + object.toString());
            if (callback != null) {
                callback.onResult(object);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            if(callback != null){
                callback.onFailRequest(e.getLocalizedMessage());
            }
        }

        return null;
    }

    public void getAsync(String funcUrl, HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(BizonApp.getAppContext());

        String url = getUrl(funcUrl, params);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("pro.asdgroup.bizon.BizonApp", "JSON response: " + response);

                        if (callback == null){
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(response);

                            callback.onResult(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback == null){
                    return;
                }

                callback.onFailRequest(error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    protected String getUrl(String funcUrl, HashMap<String, String> params){
        StringBuilder url = new StringBuilder();

        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(url.length() != 0){
                url.append("&");
            }

            url.append(entry.getKey());
            url.append("=");
            url.append(entry.getValue());
        }

        url.insert(0, HttpHelper.BASE_URL + funcUrl + "?");


        return url.toString();
    }
}
