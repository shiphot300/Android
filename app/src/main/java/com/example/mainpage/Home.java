package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Home extends Fragment {
    private static final int REQUEST_CODE_WRITE_ACTIVITY = 113;

    private View view = null;
    private FirstAdapter firstAdapter;
    private HomeAdapter homeAdapter;
    private CategoryAdapter categoryAdapter;
    private SpeedDialView mSpeedDialView;

    private ListView listView;
    private RecyclerView rvContentView;
    private RecyclerView rvCategoryView;
//    private ConstraintLayout viewSearch;
    private ProgressBar loading;

//    private Button btn_category;

    private CardView btnSearch = null;
    private AppCompatSpinner spCategory = null;
    private AppCompatAutoCompleteTextView tvKeyword = null;

    private final int PAGE_UNIT = 20; // 한번에 가져올 데이터 개수
    private int currentPage = 0; // 현재 페이지 index
    private String searchKey = "가"; // 검색 키워드

    private static HashMap<String, String> CATEGORY_ID = new HashMap<>();
    static {
        CATEGORY_ID.put("전체", "all");
        CATEGORY_ID.put("디지털/가전", "10001");
        CATEGORY_ID.put("여성의류", "10002");
        CATEGORY_ID.put("남성의류", "10003");
        CATEGORY_ID.put("패션잡화", "10004");
        CATEGORY_ID.put("뷰티미용", "10005");
        CATEGORY_ID.put("취미애완", "10006");
        CATEGORY_ID.put("스포츠레저", "10007");
        CATEGORY_ID.put("생활문구/가구식품", "10008");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.hom, container, false);

            listView = view.findViewById(R.id.list);
            mSpeedDialView = view.findViewById(R.id.speedDial);
            rvContentView = view.findViewById(R.id.rv_content);
            rvCategoryView = view.findViewById(R.id.rv_category);
//            viewSearch = view.findViewById(R.id.view_search);
            loading = view.findViewById(R.id.loading);
//            btn_category = view.findViewById(R.id.btn_category);

            spCategory = view.findViewById(R.id.spCategories);
            tvKeyword = view.findViewById(R.id.tvKeywords);
            btnSearch = view.findViewById(R.id.btnSearch);

            tvKeyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean has) {
                    if (has) {
                        tvKeyword.setThreshold(1);
                        tvKeyword.showDropDown();

                    }
                }
            });
            tvKeyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvKeyword.setThreshold(1);
                    tvKeyword.showDropDown();
                }
            });
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search();
                }
            });

            updateRecentKeyword();
        }

        return view;
    }


    private void search() {
        String keyword = tvKeyword.getText().toString().trim();


        if (keyword.isEmpty()) {
            Toast.makeText(getContext(), "검색어를 입력 해주세요", Toast.LENGTH_SHORT).show();
        } else {
            String category = spCategory.getSelectedItem().toString();
            String categoryLargeId = CATEGORY_ID.get(category);

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tvKeyword.getWindowToken(), 0);

            Observable.fromCallable(new Callable<Document>() {
                @Override
                public Document call() throws Exception {
                    Log.d("BYSON", "search :  " + keyword + ", " + "category : " + category + ", " + "categoryLargeId : " + categoryLargeId);
                    return Jsoup
                            .connect("http://192.168.234.1:3000/api/board/bysearch/list")
                            .ignoreContentType(true)
                            .data("limit", "10")
                            .data("offset", "0")
                            .data("no", "1")
                            .data("categoryLargeId", categoryLargeId)
                            .data("search", keyword)
                            .get();
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Document>() {
                @Override
                public void accept(Document response) throws Throwable {
                    HashMap<String, Object> raw = new Gson().fromJson(response.text(), new TypeToken<HashMap<String, Object>>() {}.getType());

                    List<Map<String, Object>> products = (List<Map<String, Object>>) raw.get("product");

                    if (products.size() > 0) {
                        homeAdapter.update(products == null ? new ArrayList<>() : products);

                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("KEYWORDS", Context.MODE_PRIVATE);

                        Set<String> recents = sharedPreferences.getStringSet("recents", new HashSet<>());

                        if (recents.add(keyword)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putStringSet("recents", recents);
                            editor.commit();

                            updateRecentKeyword();
                        }
                    } else {
                        Toast.makeText(getContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                    }

//                    loading.setVisibility(View.GONE);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Throwable {
//                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "검색 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateRecentKeyword() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("KEYWORDS", Context.MODE_PRIVATE);

        ArrayList<String> recentKeywords = new ArrayList<String>(sharedPreferences.getStringSet("recents", new HashSet<>()));

        tvKeyword.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, recentKeywords));
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


        initDialVieW();

        initCategoryRecyclerView();
        //클릭이벤트
        listView.setOnItemClickListener((parent, view1, position, id) -> Toast.makeText(getActivity(), position + " 번째 아이템 선택", Toast.LENGTH_SHORT).show());


//        viewSearch.setOnClickListener(view12 -> startActivity(new Intent(getActivity(), SearchActivity.class)));



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

    private void initCategoryRecyclerView() {

//        btn_category.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(String.valueOf(btn_category), "정상작동: ");
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                Category_List category_list = new Category_List();
//                transaction.replace(R.id.main_frame, category_list);
//                transaction.commit();
//                Log.d(String.valueOf(btn_category), "정상작동: ");
//            }
//        });
    }

    private void updateRecyclerView() {
        Observable.fromCallable(new Callable<Document>() {
            @Override
            public Document call() throws Exception {
                return Jsoup
                        .connect("http://192.168.234.1:3000/api/board/getMainList")
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

                        List<Map<String, Object>> products = (List<Map<String, Object>>) raw.get("newProduct");

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
                Intent intent = new Intent(getActivity(), Itempage.class);
                intent.putExtra("item", new Gson().toJson(product));
                getActivity().startActivity(intent);
            }
        };
        //리사이클러뷰의 타입을 선택할수있다 gird,,, linear(세로 가로).. 디폴트는 세로 스크롤
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentView.setLayoutManager(linearLayoutManager);
        rvContentView.setAdapter(homeAdapter);
    }


    private void initDialVieW() {
        //fab 버튼에 추가할 item...   id 값을 다르게 해주세요. 그옆 drawable을 이미지.
        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_no_label, R.drawable.ic_link_white_24dp).create());
        mSpeedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_no_label2, R.drawable.ic_link_white_24dp).create());

        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_no_label:
                        Intent intent = new Intent(getActivity(), WriteActivity.class);
                        intent.putExtra("mode", "upload");

                        startActivityForResult(intent, REQUEST_CODE_WRITE_ACTIVITY);
                        return true;
                    case R.id.fab_no_label2:
                        return true;
                }
                return false;
            }
        });

    }



}
