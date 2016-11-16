package com.example.admin.pagination.Activities.SecondActivities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.pagination.Adapters.RVEmploymentSecondAdapter;
import com.example.admin.pagination.Adapters.RVForumAdapter;
import com.example.admin.pagination.Adapters.RVStorySecondAdapter;
import com.example.admin.pagination.Helpers.DataHelper;
import com.example.admin.pagination.Helpers.OnLoadMoreListener;
import com.example.admin.pagination.R;
import com.example.admin.pagination.Serializables.Employment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EmploymentSecondActivity extends AppCompatActivity {
    DataHelper dataHelper;

    String TAG="TAG";
    private Toolbar toolbar;
    String idUser="d";
    Button button;
    EditText editText;
    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private RVEmploymentSecondAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    int total_count=100000;
    private List<Employment> studentList;
    String category="";
    ProgressBar progressBar;
    protected Handler handler;
    ArrayList<String> arrayList;
    String position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storyfrom_life_second);
        position=getIntent().getStringExtra("position");

        Log.e("POSITION_SUKA",position+"");
        category=getIntent().getStringExtra("text");
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(category+"");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dataHelper=new DataHelper(this);

        button=(Button) findViewById(R.id.new_story_button);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_forum);
        studentList = new ArrayList<Employment>();
        handler = new Handler();
        progressBar=(ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        if (toolbar != null) {


        }

       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idUser.equals("d")){
                    getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("user", "/api/v1/user/"+idUser+"/");
                    if (editText.getText().toString().length()>99){
                        title[0] =editText.getText().toString().substring(0,99);
                    }
                    else title[0]=editText.getText().toString();
                    obj.put("title",title[0]);
                    obj.put("text",editText.getText().toString());
                    obj.put("publish",false);

                    Log.e("TAG", "asd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                new SendJsonDataToServer().execute(String.valueOf(obj));
            }
        });*/
      /*  button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idUser.equals("d")){
                    startActivity(new Intent(StoryfromLifeSecondActivity.this, RegisterActivity.class));
                }
                Intent intent=new Intent(StoryfromLifeSecondActivity.this,NewStoryActivity.class);
                intent.putExtra("text",position);

                startActivity(intent);
            }
        });*/
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Toast.makeText(this,"RAbotaet",Toast.LENGTH_SHORT).show();
            studentList.add(null);
            mAdapter = new RVEmploymentSecondAdapter(studentList, mRecyclerView);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
            //  mAdapter.notifyDataSetChanged();
            loadData();

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView


            // create an Object for Adapter
            mAdapter = new RVEmploymentSecondAdapter(studentList, mRecyclerView);

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
            Cursor cursor = dataHelper.getDataEmployment();
            studentList.clear();
            Employment istories=new Employment();


            Log.e("TAG_FORUM",cursor.getCount()+" kol");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    istories=new Employment();
                    istories.setName(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_NAME_COLUMN)));
                    istories.setAdress(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_ADDRESS_COLUMN)));
                    istories.setManager(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_MANAGER_COLUMN)));
                    istories.setPhone_number(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_NUMBER_COLUMN)));
                    istories.setPhone_number1(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_NUMBER1_COLUMN)));
                    istories.setPhone_number2(cursor.getString(cursor.getColumnIndex(DataHelper.EMPLOYMENT_NUMBER2_COLUMN)));
                    studentList.add(istories);
                }



            }
            mAdapter=new RVEmploymentSecondAdapter(studentList,mRecyclerView);
            mRecyclerView.setAdapter(mAdapter);

        }
    }
    private void loadData() {
        new ParseTask(0,1).execute();


    }
    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor=dataHelper.getDataUser();
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            idUser=cursor.getString(cursor.getColumnIndex(DataHelper.USER_ID_COLUMN));
        }

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

                URL url = new URL("http://176.126.167.231:8000/api/v1/employment/?country__id="+position+"&offset="+a+"&limit=5&format=json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.e("TAG_S_SUKA",""+a);
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
            JSONObject user;
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
                    Employment student = new Employment();
                    Log.d(TAG, "2: ");

                    if (i==0&&b==1){dataHelper.deleteEmployment();
                        Log.e("TAG_NEWS","DELETE");
                    }


                    student.setPhone_number2(menu.getString("phone_number"));
                    student.setPhone_number1(menu.getString("phone_number_1"));
                    student.setPhone_number2(menu.getString("phone_number_2"));
                    student.setName(menu.getString("name"));
                    student.setManager(menu.getString("manager"));
                    student.setAdress(menu.getString("address"));



                    dataHelper.insertEmployment(student);

                    studentList.add(student);






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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}
