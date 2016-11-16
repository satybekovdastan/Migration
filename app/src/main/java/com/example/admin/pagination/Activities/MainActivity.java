package com.example.admin.pagination.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.admin.pagination.*;
import com.example.admin.pagination.Serializables.EAEU;
import com.example.admin.pagination.Serializables.Embassy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_faq:
                startActivity(new Intent(MainActivity.this, FAQActivity.class));
                break;
            case R.id.bt_diaspory:
                startActivity(new Intent(MainActivity.this, DiasporyActivity.class));
                break;
            case R.id.bt_zapret_v_RF:
                startActivity(new Intent(MainActivity.this,ProhibitionRFActivity.class));
                break;
            case  R.id.bt_news:
                startActivity(new Intent(MainActivity.this,NewsActivity.class));
                break;
            case R.id.bt_tel_doveriya:
                startActivity(new Intent(MainActivity.this,HotLineActivity.class));
                break;
            case R.id.bt_konsulstva_diaspory:
                startActivity(new Intent(MainActivity.this, EmbassyActivity.class));
                break;
            case R.id.bt_instruction:
                startActivity(new Intent(MainActivity.this,EAEUActivity.class));
                break;
            case R.id.bt_rules_of_migration:
                startActivity(new Intent(MainActivity.this,AbroadActivity.class));
                break;
            case R.id.bt_torgovlya:
                startActivity(new Intent(MainActivity.this,HumanTraffickingActivity.class));
            break;
            case R.id.bt_trudoustiystvo:
                startActivity(new Intent(MainActivity.this,EmployementActivity.class));
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();



        // Операции для выбранного пункта меню
        switch (id) {

            case R.id.about_project:
                startActivity(new Intent(MainActivity.this, AboutProjectActivity.class));
                return true;
            case  R.id.action_settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}