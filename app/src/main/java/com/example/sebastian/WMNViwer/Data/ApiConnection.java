package com.example.sebastian.WMNViwer.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sebastian on 18.02.17.
 */
public class ApiConnection extends AsyncTask<Context,Double,Boolean> {
    AllNodes allnodes;
    public AsyncResponse delegate = null;
    private JsonParsing jsonParsing=new JsonParsing();
    private String jsonData;
    Toast toast;

    public ApiConnection(AsyncResponse delegate){
        this.delegate = delegate;
    }



    @Override
    protected Boolean doInBackground(Context... params) {
        System.out.println("Async task started");

        allnodes = AllNodes.getInstance();
        loadjson(params[0]);

        //wait for parsing to be finished
        //while(!allnodes.isFinished()){}
        System.out.println("Async task finished");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        delegate.processFinish(result);

        if(toast != null) toast.cancel();


    }

    /**
     * loads the json from a the API with the Url defined in the settings.
     * loads the json from the local dummy file if the setting is checked.
     *
     * @param c Context
     * @return  JSONObject with the information about the WMN
     */
    private void loadjson(Context c) {
        //jsonData = new JsonParsing();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(c);
        String apiUrl= SP.getString("ApiUrl","http://192.168.1.99/node");
        System.out.println("Apiurl: " + apiUrl);

        if(SP.getBoolean("loadFromFile",false)){
             loadJSONfromFile(c);

        } else {  loadJSONfromServer(c,apiUrl);}


    }


    /**
     * loads the json from a local file, was used in the beginning to start working on the application without a functioning REST API
     *
     * @param c Context
     * @return  JSONObject with the information about the WMN
     */
    public void loadJSONfromFile(Context c) {
        JSONObject object=new JSONObject();

        //sleep 10 seconds to test if waiting for api call is working
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            InputStream is = c.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            is.close();
            jsonData= new String(buffer, "UTF-8");
            object = new JSONObject(jsonData);                //alles


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //savedNodes.setFinished(false);
        jsonParsing.getNodes(object);

    }

    /**
     * loads the json from the REST API
     * @param c Context
     * @return JSONObject with the infromation about the WMNW
     */

    public void loadJSONfromServer(final Context c,String apiUrl) {

        RequestQueue queue = Volley.newRequestQueue(c);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            System.out.println("response: " + response);
                            //savedNodes.setFinished(false);

                            jsonParsing.getNodes(new JSONObject(response));

                            //send message that parsing is finished



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                displayToast("No Connection to the API",c);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    /**
     * Method to display a toast. If a toast is already being shown, the old one gets cancelled and the new one is shown instead
     * @param message message that is to be shown in the toast
     * @param c
     */
    public void displayToast(String message, Context c) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(c, message, Toast.LENGTH_SHORT);
        toast.show();
    }




}
