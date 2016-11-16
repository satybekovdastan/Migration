package com.example.admin.pagination.Activities;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.pagination.Adapters.RVEmbassyAdapter;
import com.example.admin.pagination.Helpers.DataHelper;
import com.example.admin.pagination.Helpers.OnLoadMoreListener;
import com.example.admin.pagination.R;
import com.example.admin.pagination.Serializables.Embassy;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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

public class EmbassyActivity extends AppCompatActivity {
    EditText editSearch;
    String country="";
    DataHelper dataHelper;
    boolean bool=false;
    String TAG="TAG_EMBASSY";
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private RVEmbassyAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    int total_count=100000;
    private List<Embassy> studentList;
    TextView title;
    ProgressBar progressBar;
    protected Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embassy);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        title= (TextView) findViewById(R.id.toolbar_title);
//        title.setText("Посольства");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Консульства");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dataHelper=new DataHelper(this);
        // editSearch=(EditText) findViewById(R.id.tv_embassy_search);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        studentList = new ArrayList<>();
        handler = new Handler();
        mAdapter = new RVEmbassyAdapter(studentList, mRecyclerView, this);
        progressBar=(ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        if (toolbar != null) {


        }
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        pizdec(1);



        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        //  searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
//                        .show();
                country=query;
                studentList.clear();
                String countryDataHelper;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    pizdec(3);
                }
                else {
                    Cursor cursor=dataHelper.getDataEmbassy();
                    if (cursor.getCount()>0){
                        while (cursor.moveToNext()){
                            countryDataHelper=cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_COUNTRY_COLUMN)).toLowerCase();
                            if(countryDataHelper.contains(country.toLowerCase())){
                                Embassy istories=new Embassy();
                                istories.setEmail(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_EMAIL_COLUMN)));
                                istories.setSite(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_SITE_COLUMN)));
                                istories.setRegion(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_ADDRESS_COLUMN)));
                                istories.setId(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_JSON_ID_COLUMN)));
                                istories.setFax(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_FAX_COLUMN)));
                                istories.setCountry(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_COUNTRY_COLUMN)));
                                istories.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_PHONE_COLUMN)));
                                studentList.add(istories);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });


    }

    public void pizdec(int b){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Toast.makeText(this,"RAbotaet",Toast.LENGTH_SHORT).show();
            studentList.add(null);
            mAdapter = new RVEmbassyAdapter(studentList, mRecyclerView, this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
            //  mAdapter.notifyDataSetChanged();
            loadData(b);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView


            // create an Object for Adapter
            mAdapter = new RVEmbassyAdapter(studentList, mRecyclerView, this);

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
            Cursor cursor = dataHelper.getDataEmbassy();
            Log.e("TAG_NEWS",cursor.getCount()+" kol");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Embassy istories=new Embassy();
                    istories.setEmail(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_EMAIL_COLUMN)));
                    istories.setSite(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_SITE_COLUMN)));
                    istories.setRegion(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_ADDRESS_COLUMN)));
                    istories.setId(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_JSON_ID_COLUMN)));
                    istories.setFax(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_FAX_COLUMN)));
                    istories.setCountry(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_COUNTRY_COLUMN)));
                    istories.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_PHONE_COLUMN)));
                    studentList.add(istories);
                }
                mAdapter=new RVEmbassyAdapter(studentList,mRecyclerView,this);
                mRecyclerView.setAdapter(mAdapter);


            }


        }
    }
    // load initial data
    private void loadData(int b) {
        new ParseTask(0,b).execute();


    }

//    public void onClickSearch(View view) {
     /*   if (!bool){
            title.setVisibility(View.GONE);
            editSearch.setVisibility(View.VISIBLE);
            bool=true;

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch, 0);
        }
        else {
            title.setVisibility(View.VISIBLE);
            editSearch.setVisibility(View.GONE);
            bool=false;*/

     /*   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);*/
    //country=editSearch.getText().toString();
//        studentList.clear();
//        String countryDataHelper;
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            pizdec(3);
//        }
//        else {
//            Cursor cursor=dataHelper.getDataEmbassy();
//            if (cursor.getCount()>0){
//                while (cursor.moveToNext()){
//                    countryDataHelper=cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_COUNTRY_COLUMN)).toLowerCase();
//                    if(countryDataHelper.contains(country.toLowerCase())){
//                        Embassy istories=new Embassy();
//                        istories.setEmail(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_EMAIL_COLUMN)));
//                        istories.setSite(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_SITE_COLUMN)));
//                        istories.setRegion(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_ADDRESS_COLUMN)));
//                        istories.setId(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_JSON_ID_COLUMN)));
//                        istories.setFax(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_FAX_COLUMN)));
//                        istories.setCountry(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_COUNTRY_COLUMN)));
//                        istories.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataHelper.EMBASSY_PHONE_COLUMN)));
//                        studentList.add(istories);
//                    }
//                }
//            }
//            mAdapter.notifyDataSetChanged();
//        }
//        }
//    }}

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

                URL url = new URL("http://176.126.167.231:8000/api/v1/embassy/?country__contains="+country+"&offset="+a+"&limit=15&format=json");

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
            if (b==1||b==3) {
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



                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Log.d(TAG, "1: " );
                    Embassy student = new Embassy();
                    Log.d(TAG, "2: ");
                    student.setPhoneNumber(menu.getString("phone_number"));
                    student.setCountry(menu.getString("country"));
                    student.setEmail(menu.getString("email"));
                    student.setFax(menu.getString("fax"));
                    student.setId(menu.getString("id"));
                    student.setRegion(menu.getString("address"));
                    student.setSite(menu.getString("site"));

                    if (i==0&&b==1){dataHelper.deleteEmbassy();
                        Log.e("TAG_NEWS","DELETE");
                    }
                    dataHelper.insertEmbassy(student);
                    studentList.add(student);





                }

                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "NET NET dsfsadadsgf: ");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "JSON_PIZDEC_EMBASSY");
            }
            Log.e("TAG_1","NORM");





        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

}