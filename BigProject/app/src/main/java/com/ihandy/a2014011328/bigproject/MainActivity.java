package com.ihandy.a2014011328.bigproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] data = {"Favorites","Category Management","About me"};
    static MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this, "NewsData.db", null, 1);
        dbHelper.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        //Fragment+ViewPager+FragmentViewPager组合的使用
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        ImageButton imageButton1 = (ImageButton) findViewById(R.id.button1);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.button2);
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.button3);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(MainActivity.this, Favorites.class);
                startActivity(reg);
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(MainActivity.this, Setting.class);
                startActivityForResult(reg,6);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent reg = new
                        Intent(MainActivity.this, AboutMe.class);
                startActivity(reg);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 6:
                if(resultCode == RESULT_OK){
                    String gl = data.getStringExtra("gl");
                    Log.i("here","Get?");
                    finish();
                    Intent intent0 = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent0);
                    Log.i("here", "Get!");
                }
                break;
            default:
        }
    }

}
