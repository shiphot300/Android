package com.example.mainpage.write;

import android.content.SharedPreferences;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainpage.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WriteActivity extends AppCompatActivity {
    public static int OPEN_IMAGE_REQUEST_CODE = 49018;
    public static final int REQUEST_CODE = 113;
    private RelativeLayout mBtnAddCamera1;
    private RecyclerView rvContentView1;
    private PhotoAdapter photoAdapter1;
    private TextView tvPhotoLabel1;

    private boolean saveLoginData;
    private SharedPreferences appData;
    private String id;



    Spinner mSpinnerCategoryLarge = null;
    Spinner mSpinnerCategoryMedium = null;

    EditText Edt_t, Edt_p, Edt_c;
    Button et_btn, back_btn;

    private String mMode;
    String mMedium = null;
    Map<String, Object> mItem;

    private List<Map<String, List<List<String>>>> mCategories = null;
    private ArrayList<String> mCategoryLarges = null;

    // 설정값을 불러오는 함수
    protected void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id = appData.getString("ID", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        mMode = getIntent().getStringExtra("mode");

        mBtnAddCamera1 = findViewById(R.id.view_camera);
        rvContentView1 = findViewById(R.id.rv_content);
        tvPhotoLabel1 = findViewById(R.id.tv_photo_number);

        Edt_t = (EditText) findViewById(R.id.et_title);
        Edt_p = (EditText) findViewById(R.id.et_price);
        Edt_c = (EditText) findViewById(R.id.et_content);

        et_btn = (Button) findViewById(R.id.upload);
        back_btn = (Button) findViewById(R.id.iv_back);

        mSpinnerCategoryLarge = findViewById(R.id.spinnerCategoryLarge);
        mSpinnerCategoryMedium = findViewById(R.id.spinnerCategoryMedium);

        mSpinnerCategoryLarge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(WriteActivity.this, R.layout.support_simple_spinner_dropdown_item);
                for (List<String> item : mCategories.get(position).get("medium")) {
                    String small = item.get(1);

                    adapter.add(small);
                }

                mSpinnerCategoryMedium.setAdapter(adapter);

                if (mMedium != null) {
                    mSpinnerCategoryMedium.setSelection(adapter.getPosition(mMedium));
                    mMedium = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String edt_t = Edt_t.getText().toString().trim();
                    final String edt_p = Edt_p.getText().toString().trim();
                    final String edt_c = Edt_c.getText().toString().trim();

                    final String categoryLarge = mSpinnerCategoryLarge.getSelectedItem().toString();    // 대분류
                    final String categoryMedium = mSpinnerCategoryMedium.getSelectedItem().toString();    // 소분류

                    final ArrayList<InputStream> inputStreams = new ArrayList<>();

                    Log.d(id,"wow");

                    switch (mMode) {
                        case "upload":
                            Observable.fromCallable(new Callable<Document>() {
                                @Override
                                public Document call() throws Exception {

                                    Connection connection = Jsoup
                                            .connect( "http://192.168.234.1:3000/api/mobile/uploadapp")
                                            .header("content-type", "multipart/form-data")
                                            .ignoreContentType(true)
                                            .data("member_id", id)
                                            .data("title", edt_t)
                                            .data("price", edt_p)
                                            .data("content", edt_c)
                                            .data("state", "새상품")
                                            .data("select_category_large", categoryLarge)
                                            .data("select_category_medium", categoryMedium);

                                    if (photoAdapter1.getItemCount() > 0) {
                                        for (String item : photoAdapter1.getItemList()) {
                                            Uri uri = Uri.parse(item);
                                            InputStream inputStream = getContentResolver().openInputStream(uri);

                                            connection = connection.data(
                                                    "files",
                                                    System.currentTimeMillis() + ".jpg",
                                                    inputStream);

                                            inputStreams.add(inputStream);
                                        }
                                    }

                                    return connection.post();
                                }
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Document>() {
                                        @Override
                                        public void accept(Document response) throws Throwable {
                                            for (InputStream inputStream : inputStreams) {
                                                inputStream.close();
                                            }
                                            finish();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Throwable {
                                            for (InputStream inputStream : inputStreams) {
                                                inputStream.close();
                                            }

                                            Toast.makeText(WriteActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;

                        case "update":
                            Observable.fromCallable(new Callable<Document>() {
                                @Override
                                public Document call() throws Exception {
                                    return Jsoup
                                            .connect( "http://192.168.234.1:3000/api/board/product/" + String.format("%d", ((Double) mItem.get("id")).intValue()))
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

                                            List<Map<String, Object>> products = (List<Map<String, Object>>) raw.get("product");

                                            Observable.fromCallable(new Callable<Document>() {
                                                @Override
                                                public Document call() throws Exception {

                                                    Connection connection = Jsoup
                                                            .connect( "http://192.168.234.1:3000/api/board/update/" + String.format("%d", ((Double) mItem.get("id")).intValue()))
                                                            .header("content-type", "multipart/form-data")
                                                            .ignoreContentType(true)
                                                            .data("title", edt_t)
                                                            .data("price", edt_p)
                                                            .data("content", edt_c)
                                                            .data("image_name", "")
                                                            .data("deleteImage", products.get(0).get("image_name").toString())
                                                            .data("selectLargeName", categoryLarge)
                                                            .data("selectMediumName", categoryMedium);

                                                    if (photoAdapter1.getItemCount() > 0) {
                                                        for (String item : photoAdapter1.getItemList()) {
                                                            Uri uri = Uri.parse(item);
                                                            InputStream inputStream = getContentResolver().openInputStream(uri);

                                                            connection = connection.data(
                                                                    "files",
                                                                    System.currentTimeMillis() + ".jpg",
                                                                    inputStream);

                                                            inputStreams.add(inputStream);
                                                        }
                                                    }

                                                    return connection.post();
                                                }
                                            })
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Document>() {
                                                        @Override
                                                        public void accept(Document response) throws Throwable {
                                                            for (InputStream inputStream : inputStreams) {
                                                                inputStream.close();
                                                            }
                                                            finish();
                                                        }
                                                    }, new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable throwable) throws Throwable {
                                                            Log.d("BYSON", throwable.getMessage());

                                                            for (InputStream inputStream : inputStreams) {
                                                                inputStream.close();
                                                            }

                                                            Toast.makeText(WriteActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        initRecyclerView();

        mBtnAddCamera1.setOnClickListener(view -> getGallaryIntent(WriteActivity.this));

        Observable.fromCallable(new Callable<Document>() {
            @Override
            public Document call() throws Exception {
                return Jsoup
                        .connect("http://192.168.234.1:3000/api/board/getCategoryInfo")
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

                        mCategories = (List<Map<String, List<List<String>>>>) raw.get("categoryInfo");
                        mCategoryLarges = new ArrayList<>();

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WriteActivity.this, R.layout.support_simple_spinner_dropdown_item);
                        for (Map<String, List<List<String>>> item : mCategories) {
                            String large = item.get("large").get(0).get(1);

                            adapter.add(large);
                            mCategoryLarges.add(large);
                        }
                        mSpinnerCategoryLarge.setAdapter(adapter);

                        switch (mMode) {
                            case "update":
                                mItem = new Gson().fromJson(getIntent().getStringExtra("item"), new TypeToken<HashMap<String, Object>>() {}.getType());

                                et_btn.setText("수정");
                                ((TextView) findViewById(R.id.login_id_edit_text)).setText("중고거래 글 수정");
                                Edt_t.setText(mItem.get("title").toString());
                                Edt_p.setText(mItem.get("price").toString());
                                Edt_c.setText(mItem.get("content").toString());

                                mMedium = mItem.get("category_medium_name").toString();
                                mSpinnerCategoryLarge.setSelection(mCategoryLarges.indexOf(mItem.get("category_large_name")));

                                break;
                        }

                        findViewById(R.id.relativeLayoutCover).setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        throwable.printStackTrace();
                        Toast.makeText(WriteActivity.this, "카테고리를 가져올 수 없습니다", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );

        cursor.moveToNext();

        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

        cursor.close();
        return path;
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection nect = null;
            BufferedReader rea = null;
            try{
                URL url = new URL(urls[0]);
                nect = (HttpURLConnection) url.openConnection();
                nect.setRequestMethod("POST");//POST방식으로 보냄
                nect.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                nect.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

                nect.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                nect.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                nect.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                nect.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream out = nect.getOutputStream();
                out.write(urls[1].getBytes("utf-8"));
                out.flush();
                out.close();//버퍼를 받아줌

                //서버로 부터 데이터를 받음
                InputStream stream = nect.getInputStream();

                rea = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = rea.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (nect != null) {
                    nect.disconnect();
                }
                try {
                    if (rea != null) {
                        rea.close();//버퍼를 닫아줌
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void initRecyclerView() {

        photoAdapter1 = new PhotoAdapter(this) {
            @Override
            public void itemCallback(int position) {
                photoAdapter1.removeItem(position);
                updatePhotoIndexLabel();
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rvContentView1.setLayoutManager(linearLayoutManager);
        rvContentView1.setAdapter(photoAdapter1);
    }

    private void getGallaryIntent(Activity activity) {
        Dexter.withActivity(activity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, OPEN_IMAGE_REQUEST_CODE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_IMAGE_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();

            photoAdapter1.addItem(String.valueOf(uri));
            updatePhotoIndexLabel();
            return;
        }
    }

    private void updatePhotoIndexLabel() {
        if (photoAdapter1.getItemList() == null) {
            tvPhotoLabel1.setText(String.format("%s/%s",0,10));
        } else {
            int registerPhotoAmount = photoAdapter1.getItemList().size();
            tvPhotoLabel1.setText(String.format("%s/%s",registerPhotoAmount,10));
        }
    }



}