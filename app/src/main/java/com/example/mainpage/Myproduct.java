package com.example.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainpage.write.WriteActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Myproduct extends Fragment {
    private static final int REQUEST_CODE_WRITE_ACTIVITY = 113;

    private View view = null;
    private FirstAdapter firstAdapter;
    private HomeAdapter homeAdapter;
    private CategoryAdapter categoryAdapter;
    private SpeedDialView mSpeedDialView;

    private ListView listView;
    private RecyclerView rvContentView;
    private RecyclerView rvCategoryView;
    private ConstraintLayout viewSearch;
    private ProgressBar loading;


    private final int PAGE_UNIT = 20; // 한번에 가져올 데이터 개수
    private int currentPage = 0; // 현재 페이지 index
    private String searchKey = "가"; // 검색 키워드

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.myproduct, container, false);

            listView = view.findViewById(R.id.list);
            mSpeedDialView = view.findViewById(R.id.speedDial);
            rvContentView = view.findViewById(R.id.rv_content);
            rvCategoryView = view.findViewById(R.id.rv_category);
            viewSearch = view.findViewById(R.id.view_search);
            loading = view.findViewById(R.id.loading);
        }

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

        firstAdapter = new FirstAdapter(data);
        listView.setAdapter(firstAdapter);

        //클릭이벤트
        listView.setOnItemClickListener((parent, view1, position, id) -> Toast.makeText(getActivity(), position + " 번째 아이템 선택", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_ACTIVITY:
                updateRecyclerView();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateRecyclerView() {
        Observable.fromCallable(new Callable<Document>() {
            @Override
            public Document call() throws Exception {
                return Jsoup
                        .connect("http://192.168.234.1:3000/api/mobile/myproductapp")
                        .ignoreContentType(true)
                        .get();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Document>() {
                    @Override
                    public void accept(Document response) throws Throwable {
                        HashMap<String, Object> raw = new Gson().fromJson(response.text(), new TypeToken<HashMap<String, Object>>() {}.getType());

                        List<Map<String, Object>> products = (List<Map<String, Object>>) raw.get("productInfo");

                        homeAdapter.update(products == null ? new ArrayList<>() : products);

                        loading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        loading.setVisibility(View.GONE);
                    }
                });
    }
    private void initRecyclerView() {
        homeAdapter = new HomeAdapter(getActivity()) {
            @Override
            public void onClickProduct(Map<String, Object> product) {
                Intent intent = new Intent(getActivity(), Myproduct_itempage.class);
                intent.putExtra("item", new Gson().toJson(product));
                getActivity().startActivity(intent);
            }
        };
        //리사이클러뷰의 타입을 선택할수있다 gird,,, linear(세로 가로).. 디폴트는 세로 스크롤
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentView.setLayoutManager(linearLayoutManager);
        rvContentView.setAdapter(homeAdapter);
    }

}
