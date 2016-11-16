package com.example.admin.pagination.Activities.SecondActivities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.admin.pagination.R;

public class HotLineSecondActivity extends AppCompatActivity {
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_line_second);
        TextView tvDescription=(TextView) findViewById(R.id.tv_second_hot_line_description);
        TextView tvNumber=(TextView) findViewById(R.id.tv_phone_hot);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Горячие Линии");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        String  description=getIntent().getStringExtra("description");
         phoneNumber=getIntent().getStringExtra("phone_number");
        tvDescription.setText(description);
        tvNumber.setText(phoneNumber);
    }

    public void onClickHot(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phoneNumber , null)));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

}
