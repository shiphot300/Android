package com.example.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangeInfor extends AppCompatActivity {

    private boolean saveLoginData;
    private SharedPreferences appData;
    private String id, pwd, email;

    EditText My_ID, My_PW, My_EMAIL;
    Button Edit_btn, Close_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();


        My_ID = (EditText) findViewById(R.id.my_id);
        My_PW = (EditText) findViewById(R.id.my_password);
        My_EMAIL = (EditText) findViewById(R.id.my_email);

        Edit_btn = (Button) findViewById(R.id.btn_edit);
        Close_btn = (Button) findViewById(R.id.btn_close);

        if (saveLoginData) {
            My_ID.setText(id);
            My_PW.setText(pwd);
            My_EMAIL.setText(email);
        }

        Edit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
// SharedPreferences 객체만으론 저장 불가능 Editor 사용
                    SharedPreferences.Editor editor = appData.edit();

                    String m_id = My_ID.getText().toString().trim();
                    String m_pw = My_PW.getText().toString().trim();
                    String m_email = My_EMAIL.getText().toString().trim();

                    editor.putString("ID", My_ID.getText().toString().trim());
                    editor.putString("PWD", My_PW.getText().toString().trim());
                    editor.putString("EMAIL", My_EMAIL.getText().toString().trim());

                    editor.apply();

                    JSONObject jsonob = new JSONObject();

                    jsonob.accumulate("member_id", m_id);
                    jsonob.accumulate("member_pw", m_pw);
                    jsonob.accumulate("member_email", m_email);

                    String url = "http://192.168.234.1:3000/api/mobile/memberapp";

                    new JSONTask().execute(url,jsonob.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        Close_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id = appData.getString("ID", "");
        pwd = appData.getString("PWD", "");
        email = appData.getString("EMAIL", "");

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


        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d(result,"yes");

            Log.d(id,"");
            Log.d(pwd,"");
            Log.d(email,"");

            if(result.equals("Edit Success")) {
                Log.d(result,"yesyes");
                finish();
            }else{
                Log.d(result,"nono");
            }
        }
    }



}
