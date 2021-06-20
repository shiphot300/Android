package com.example.mainpage;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class thirdActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; //하단다
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Chat chat;
    private Mypage mypage;
    private Home hom;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomnavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_chat:
                        setFrag(1);
                        break;
                    case R.id.action_mypage:
                        setFrag(2);
                        break;

                }
                return true;
            }
        });
        chat = new Chat();
        mypage = new Mypage();
        hom = new Home();
        setFrag(0); //첫화면을 무엇으로 지정해줄것인지 선택


    }
    //프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, hom);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, chat);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, mypage);
                ft.commit();
                break;
        }
    }



}
