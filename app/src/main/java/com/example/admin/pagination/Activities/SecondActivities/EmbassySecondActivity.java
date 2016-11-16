package com.example.admin.pagination.Activities.SecondActivities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.admin.pagination.Adapters.RVEmbassyAdapter;
import com.example.admin.pagination.Adapters.RVEmbassySecondAdapter;
import com.example.admin.pagination.Adapters.RVNewsAdapter;
import com.example.admin.pagination.Helpers.DataHelper;
import com.example.admin.pagination.Helpers.OnLoadMoreListener;
import com.example.admin.pagination.R;

import com.example.admin.pagination.Serializables.Consulate;
import com.example.admin.pagination.Serializables.Istories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EmbassySecondActivity extends AppCompatActivity {

    String id="-1";
    DataHelper dataHelper;
    int id1;
    String TAG="TAG_EMBASSY";
    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private RVEmbassySecondAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    int total_count=100000;
    private List<Consulate> studentList;

    ProgressBar progressBar;
    protected Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embassy_second);
        TextView tvTitle=(TextView) findViewById(R.id.tv_embassy_second);
        TextView tvPhone=(TextView) findViewById(R.id.tv_phone_number_second);
        TextView tvFax=(TextView) findViewById(R.id.tv_fax_second);
        TextView tvSite=(TextView) findViewById(R.id.tv_site_second);
        TextView tvAddress=(TextView) findViewById(R.id.tv_address_second);
        final String phoneNumber;
        String s=getIntent().getStringExtra("phone_number");
        tvPhone.setText(s);
        Log.e("TAG_S",s);
        phoneNumber=s;

        s=getIntent().getStringExtra("fax");
        tvFax.setText(s);
        s=getIntent().getStringExtra("country");
        tvTitle.setText(s);
        s=getIntent().getStringExtra("site");
        tvSite.setText(s);
        s=getIntent().getStringExtra("address");
        tvAddress.setText(s);
        id=getIntent().getStringExtra("id");


        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phoneNumber , null)));
            }
        });
        Log.e("TAG_S_ID",id);
        id1=Integer.parseInt(id);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Посольства");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dataHelper=new DataHelper(this);

        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_consulate);
        mRecyclerView.setVisibility(View.VISIBLE);
        studentList = new ArrayList<Consulate>();
        handler = new Handler();
        progressBar=(ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        if (toolbar != null) {


        }
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Toast.makeText(this,"RAbotaet",Toast.LENGTH_SHORT).show();
            studentList.add(null);
            mAdapter = new RVEmbassySecondAdapter(studentList, mRecyclerView, this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
            //  mAdapter.notifyDataSetChanged();
            loadData();

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView


            // create an Object for Adapter
            mAdapter = new RVEmbassySecondAdapter(studentList, mRecyclerView, this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
            //  mAdapter.notifyDataSetChanged();


            if (studentList.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                tvEmptyView.setVisibility(View.VISIBLE);

            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);
            }

            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add null , so the adapter will check view_type and show progress bar at bottom

                    Log.e("TAG_SUKA", "SUKA_RABOTAET");
                    int start = studentList.size();
                    if (start < total_count - 1)
                        progressBar.setVisibility(View.VISIBLE);
                    else progressBar.setVisibility(View.GONE);
                    new ParseTask(start, 0).execute();


                }
            });

        } else {
            Toast.makeText(this,"NeRAbotaet",Toast.LENGTH_SHORT).show();
            Cursor cursor = dataHelper.getDataConsulate();
            Log.e("TAG_NEWS",cursor.getCount()+" kol");
            if (cursor != null || cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Consulate istories=new Consulate();
                    istories.setAddress(cursor.getString(cursor.getColumnIndex(DataHelper.CONSULATE_ADDRESS_COLUMN)));
                    istories.setRegion(cursor.getString(cursor.getColumnIndex(DataHelper.CONSULATE_REGION_COLUMN)));
                    istories.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataHelper.CONSULATE_PHONE_COLUMN)));
                    studentList.add(istories);
                }
                mAdapter=new RVEmbassySecondAdapter(studentList,mRecyclerView,this);
                mRecyclerView.setAdapter(mAdapter);


            }


        }
    }


    // load initial data
    private void loadData() {
        new ParseTask(0,1).execute();


    }
    public class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";
        int a,b;
        public ParseTask(int a,int b){
            this.a=a;this.b=b;
        }
        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.231:8000/api/v1/consulate/?embassy__id="+id1+"&offset="+a+"&limit=15&format=json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.e("TAG_S",""+a);
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
            if (b==1) {
                studentList.remove(studentList.size() - 1);
                mAdapter.notifyItemRemoved(studentList.size());
            }
            Log.e("TAG", json);

            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");
                total_count=meta.getInt("total_count");
                Log.e(TAG+"Total",total_count+""+a);
                JSONObject secondObject = menus.getJSONObject(0);
                secondName = secondObject.getString("id");
                Log.d(TAG, "Второе имя: " + secondName);

                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Log.d(TAG, "1: " );
                    Consulate student = new Consulate();
                    Log.d(TAG, "2: ");
                    student.setPhoneNumber(menu.getString("phone_number"));

                    student.setRegion(menu.getString("region"));

                    student.setAddress(menu.getString("address"));

                    Log.e(TAG+"ADD",student.getAddress());

                    if (i==0&&b==1){dataHelper.deleteConsulate();
                        Log.e("TAG_NEWS","DELETE");
                    }
                    dataHelper.insertConsulate(student);
                    studentList.add(student);





                }
                if (b==1){
                    mAdapter=new RVEmbassySecondAdapter(studentList,mRecyclerView,EmbassySecondActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                else
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();

                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "NET NET dsfsadadsgf: ");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "JSON_PIZDEC_EMBASSY_second");
            }
            Log.e("TAG_1","NORM");
            Log.e("GANDON",""+studentList.size());
            if (studentList.size()==0) mRecyclerView.setVisibility(View.GONE);



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}