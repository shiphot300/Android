package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.mainpage.write.WriteActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Itempage extends AppCompatActivity {

    public static final int REQUEST_CODE = 1244;

    Map<String, Object> mItem;
    ViewPager viewPager;
    Button back_btn, btn_chat;

    private String mJson;

    TextView Tv_t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itempage);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ItemAdapter itemAdapter = new ItemAdapter(this);
        viewPager.setAdapter(itemAdapter);

        Tv_t = (TextView) findViewById(R.id.tv_name);

        back_btn = (Button) findViewById(R.id.back_icon1);
        btn_chat = (Button) findViewById(R.id.chat);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(String.valueOf(back_btn), "정상작동");
                finish();
            }
        });

        Intent intent = getIntent();
        Log.d(String.valueOf(getIntent()), "정상작동");//보내온 인탠트 얻기

        mItem = new Gson().fromJson(mJson = intent.getStringExtra("item"), new TypeToken<HashMap<String, Object>>() {}.getType());

        try {
            Glide.with(this).load("http://192.168.234.1:3000/" + (String) mItem.get("thumbnail")).into((ImageView) findViewById(R.id.iv_profile));
        } catch (Exception e) {

        }

        ((TextView) findViewById(R.id.tv_name1)).setText((String) mItem.get("title"));
        ((TextView) findViewById(R.id.tv_goods_name1)).setText((String) mItem.get("title"));
        ((TextView) findViewById(R.id.tv_price1)).setText(((Double) mItem.get("price")).intValue() + "원");
        ((TextView) findViewById(R.id.tv_goods_time1)).setText((String) mItem.get("category_large_name") + " > " + (String) mItem.get("category_medium_name"));
        ((TextView) findViewById(R.id.tv_goods_content1)).setText((String) mItem.get("content"));

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_Chatting.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case WriteActivity.REQUEST_CODE:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

