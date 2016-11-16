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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.admin.pagination.Adapters.RVFAQAdapter;
import com.example.admin.pagination.Adapters.RVForumAdapter;
import com.example.admin.pagination.Adapters.RVForumQuestionsAndAnswersAdapter;
import com.example.admin.pagination.Adapters.RVNewsAdapter;
import com.example.admin.pagination.Helpers.DataHelper;
import com.example.admin.pagination.Helpers.OnLoadMoreListener;
import com.example.admin.pagination.R;
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

public class FAQActivity extends AppCompatActivity {


    DataHelper dataHelper;

    String TAG="TAG";
    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private RVFAQAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    int total_count=100000;
    private List<Istories> studentList;

    ProgressBar progressBar;
    protected Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Вопросы и ответы");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dataHelper=new DataHelper(this);

        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        studentList = new ArrayList<>();
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
            mAdapter = new RVFAQAdapter(studentList, mRecyclerView, this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
            //  mAdapter.notifyDataSetChanged();
            loadData();

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView


            // create an Object for Adapter
            mAdapter = new RVFAQAdapter(studentList, mRecyclerView, this);

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
            Cursor cursor = dataHelper.getDataFaq();
            Log.e("TAG_NEWS",cursor.getCount()+" kol");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Istories istories=new Istories();
                    istories.setNickName(cursor.getString(cursor.getColumnIndex(DataHelper.FAQ_QUESTION_COLUMN)));
                    istories.setText(cursor.getString(cursor.getColumnIndex(DataHelper.FAQ_ANSWER_COLUMN)));
                    studentList.add(istories);
                }
                mAdapter=new RVFAQAdapter(studentList,mRecyclerView,this);
                mRecyclerView.setAdapter(mAdapter);


            }


        }

    }


    // load initial data
    private void loadData() {
        new ParseTask(0,1).execute();


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
        int a,b;
        public ParseTask(int a,int b){
            this.a=a;this.b=b;
        }
        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.231:8000/api/v1/faq/?offset="+a+"&limit=15&format=json");

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
                    Istories student = new Istories();
                    Log.d(TAG, "2: ");
                    student.setText(menu.getString("answer_ru"));
                    student.setNickName(menu.getString("question_ru"));

                    if (i==0&&b==1){dataHelper.deleteFAQ();
                        Log.e("TAG_NEWS","DELETE");
                    }
                    dataHelper.insertFAQ(student);

                    studentList.add(student);

                    Log.e("TAG_IS",student.getNickName()+"   "+student.getText());



                }

                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "NET NET dsfsadadsgf: ");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "JSON_PIZDEC");
            }
            Log.e("TAG_1","NORM");





        }
    }

}