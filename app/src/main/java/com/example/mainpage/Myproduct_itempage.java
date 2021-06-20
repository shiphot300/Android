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

public class Myproduct_itempage extends AppCompatActivity {

    public static final int REQUEST_CODE = 1244;

    Map<String, Object> mItem;
    ViewPager viewPager;
    Button back_btn;

    private String mJson;

    TextView Tv_t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myproduct_itempage);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ItemAdapter itemAdapter = new ItemAdapter(this);
        viewPager.setAdapter(itemAdapter);

        Tv_t = (TextView) findViewById(R.id.tv_name);

        back_btn = (Button) findViewById(R.id.back_icon);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent(); //보내온 인탠트 얻기

        mItem = new Gson().fromJson(mJson = intent.getStringExtra("item"), new TypeToken<HashMap<String, Object>>() {}.getType());

        try {
            Glide.with(this).load("http://192.168.234.1:3000/" + (String) mItem.get("thumbnail")).into((ImageView) findViewById(R.id.iv_profile));
        } catch (Exception e) {

        }

        ((TextView) findViewById(R.id.tv_name)).setText((String) mItem.get("title"));
        ((TextView) findViewById(R.id.tv_goods_name)).setText((String) mItem.get("title"));
        ((TextView) findViewById(R.id.tv_price)).setText(((Double) mItem.get("price")).intValue() + "원");
        ((TextView) findViewById(R.id.tv_goods_category)).setText((String) mItem.get("category_large_name") + " > " + (String) mItem.get("category_medium_name"));
        ((TextView) findViewById(R.id.tv_goods_content)).setText((String) mItem.get("content"));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.myproduct_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case WriteActivity.REQUEST_CODE:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.re_myproduct:
                Intent intent = new Intent(this, WriteActivity.class);

                intent.putExtra("mode", "update");
                intent.putExtra("item", mJson);

                startActivityForResult(intent, WriteActivity.REQUEST_CODE);
                return true;

            case R.id.delete_myproduct:
                Observable.fromCallable(new Callable<Document>() {
                    @Override
                    public Document call() throws Exception {
                        return Jsoup
                                .connect("http://192.168.234.1:3000/api/mypage/myproduct/delete/")
                                .ignoreContentType(true)
                                .data("id", String.format("%d", ((Double) mItem.get("id")).intValue()))
                                .post();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Document>() {
                            @Override
                            public void accept(Document response) throws Throwable {
                                finish();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                throwable.printStackTrace();
                                Toast.makeText(Myproduct_itempage.this, "삭제 실패", Toast.LENGTH_LONG).show();
                            }
                        });
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}

