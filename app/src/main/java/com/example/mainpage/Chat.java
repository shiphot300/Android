package com.example.mainpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Chat extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat, container, false);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        updateRecyclerView();

        ArrayList<MainList> data = new ArrayList<>();
        data.add(new MainList("상품1", "상품제목", "상품설명"));
        data.add(new MainList("상품2", "상품제목", "상품설명"));
        data.add(new MainList("상품3", "상품제목", "상품설명"));
        data.add(new MainList("상품4", "상품제목", "상품설명"));
        data.add(new MainList("상품5", "상품제목", "상품설명"));
        data.add(new MainList("상품6", "상품제목", "상품설명"));
        data.add(new MainList("상품7", "상품제목", "상품설명"));
        data.add(new MainList("상품8", "상품제목", "상품설명"));
        
    }

    private void initRecyclerView() {

    }

    private void updateRecyclerView(){

    }
}
