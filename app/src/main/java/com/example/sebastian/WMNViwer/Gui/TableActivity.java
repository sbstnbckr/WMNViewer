package com.example.sebastian.WMNViwer.Gui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.sebastian.WMNViwer.Data.AllNodes;
import com.example.sebastian.WMNViwer.Data.ApiConnection;
import com.example.sebastian.WMNViwer.Data.AsyncResponse;
import com.example.sebastian.mapbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, AsyncResponse{

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private SearchView search;
    List<String> listDataHeader;
    HashMap<String,String> listDataChild;
    private AllNodes all;
    private ProgressBar spinner;

    private int requestCount=0;
    private int maxParallelRequest=5;

    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        spinner=(ProgressBar)findViewById(R.id.progressBar);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) findViewById(R.id.search);
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);

        all=AllNodes.getInstance();

        spinner.setVisibility(View.GONE);

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();


    }

    private void displayListData() {

        if (requestCount<maxParallelRequest){
            spinner.setVisibility(View.VISIBLE);
            requestCount++;
            System.out.println("request send number: " + requestCount);
            new ApiConnection(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getApplicationContext());

        } else {
            displayToast("too many requests already running");
        }

    }

    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(toast != null) toast.cancel();    }

    @Override
    protected void onStop(){
        super.onStop();
        if(toast != null) toast.cancel();    }

    @Override
    public void onResume() {
        super.onResume();

        displaystartupListData();
    }

    private void displaystartupListData() {

        listDataHeader.clear();
        listDataChild.clear();

        for( int i=0;i<all.getNodes().size();i++){
            listDataHeader.add(all.getNodes().get(i).gethostname()+" - IPV4: "+all.getNodes().get(i).getIpv4());
        }

        for( int i=0;i<all.getNodes().size(); i++) {
            List<String> data=new ArrayList<>();
            data.add(all.getNodes().get(i).toString());
            listDataChild.put(listDataHeader.get(i), all.getNodes().get(i).toString());
            System.out.println("contains node: "+ all.getNodes().get(i).toString().contains("Node"));

        }

        listAdapter = new ExpandableListAdapter(this,listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
    }


    /**
     * method changes to the MAp activity and is called by the Map-button on the bottom of the screen.
     * @param v the current View
     */
    public void changeToMapActivity(View v) {
        Intent myIntent = new Intent(v.getContext(), MapActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //If set, and the activity being launched is already running in the current task, then instead of launching a new instance of that activity, all of the other activities on top of it will be closed and this Intent will be delivered to the (now on top) old activity as a new Intent.
        startActivity(myIntent);
    }

    public void refreshInfo(View v) {

        displayListData();
    }

    @Override
    public boolean onClose() {

        listAdapter.filterData("");

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            listAdapter.filterData(query);
            return true;
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        try {
            listAdapter.filterData(query);
            return true;
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.preferences:
            {
                Intent intent = new Intent();
                intent.setClassName(this, "com.example.sebastian.WMNViwer.Settings.SettingsActivity");
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void processFinish(Boolean output) {

        listDataHeader.clear();
        listDataChild.clear();

        for( int i=0;i<all.getNodes().size();i++){
            listDataHeader.add(all.getNodes().get(i).gethostname()+" - IPV4: "+all.getNodes().get(i).getIpv4());
        }


        for( int i=0;i<all.getNodes().size(); i++) {
            List<String> data=new ArrayList<>();
            data.add(all.getNodes().get(i).toString());
            listDataChild.put(listDataHeader.get(i), all.getNodes().get(i).toString());
            System.out.println("contains node: "+ all.getNodes().get(i).toString().contains("Node"));

        }

        listAdapter = new ExpandableListAdapter(this,listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        if (all.getNodes().isEmpty()) {
            displayToast("json file is empty");
        }

        spinner.setVisibility(View.GONE);

        requestCount--;

    }
}
