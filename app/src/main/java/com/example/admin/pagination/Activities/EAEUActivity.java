package com.example.admin.pagination.Activities;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.admin.pagination.Adapters.RVEAEUAdapter;
import com.example.admin.pagination.Helpers.DataHelper;
import com.example.admin.pagination.R;
import com.example.admin.pagination.Serializables.EAEU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EAEUActivity extends AppCompatActivity {
    DataHelper dataHelper;
    RVEAEUAdapter mAdapter;
    ArrayList<EAEU> studentList;
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_line);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Правила пребывания в ЕАЭС");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView=(RecyclerView) findViewById(R.id.my_recycler_view);
        ProgressBar progressBar;
        progressBar =(ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        dataHelper=new DataHelper(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
         studentList=new ArrayList<>();
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        EAEU eaeu=new EAEU();
        eaeu.setName("РФ");



        mAdapter=new RVEAEUAdapter(studentList,mRecyclerView,this,1);
        mRecyclerView.setAdapter(mAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                new ParseTask().execute();

        }else {
           Cursor cursor = dataHelper.getEAEU();
            studentList.clear();
            if (cursor.getCount()>0){
                while (cursor.moveToNext()){
                    eaeu=new EAEU();
                    eaeu.setPicture(cursor.getString(cursor.getColumnIndex(DataHelper.EAEU_PICTURE_COLUMN)));
                    eaeu.setName(cursor.getString(cursor.getColumnIndex(DataHelper.EAEU_TITLE_COLUMN)));
                    studentList.add(eaeu);
                    mAdapter=new RVEAEUAdapter(studentList,mRecyclerView,this,1);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

    public class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.231:8000/api/v1/country_eaes/?&limit=0&format=json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                jsonResult = builder.toString();
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);


            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    eaeu.setId(menu.getString("id"));
                    studentList.add(eaeu);
                    dataHelper.insertEAEU(eaeu);

                }
                mAdapter=new RVEAEUAdapter(studentList,mRecyclerView,EAEUActivity.this,1);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setLoaded();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("TAG_1","NORM");
            mAdapter.notifyDataSetChanged();




        }
    }
}

