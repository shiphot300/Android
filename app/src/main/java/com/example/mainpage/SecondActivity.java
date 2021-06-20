package com.example.mainpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
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

public class SecondActivity extends AppCompatActivity {

    private SharedPreferences appData;

    EditText ETI, ETP, ETE;
    Button btnClose, btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        appData = getSharedPreferences("appData", MODE_PRIVATE);

        ETI = (EditText) findViewById(R.id.et_id);
        ETP = (EditText) findViewById(R.id.et_password);
        ETE = (EditText) findViewById(R.id.et_email);

        btnClose = (Button) findViewById(R.id.btn_close);
        btnSign = (Button) findViewById(R.id.btn_sign);

        btnSign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {

                    // SharedPreferences 객체만으론 저장 불가능 Editor 사용
                    SharedPreferences.Editor editor = appData.edit();

                    editor.putString("ID", ETI.getText().toString().trim());
                    editor.putString("PWD", ETP.getText().toString().trim());
                    editor.putString("EMAIL", ETE.getText().toString().trim());

                    // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                    editor.apply();

                    String eti = ETI.getText().toString().trim();
                    String etp = ETP.getText().toString().trim();
                    String ete = ETE.getText().toString().trim();

                    JSONObject jsonob = new JSONObject();

                    jsonob.accumulate("member_id", eti);
                    jsonob.accumulate("member_pw", etp);
                    jsonob.accumulate("member_email", ete);


                    String url = "http://192.168.234.1:3000/api/mobile/signupapp";

                    new JSONTask().execute(url,jsonob.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

            HttpURLConnection nect = null;
            BufferedReader rea = null;

            try {
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(result,"yes");

            if(result.equals("Create success")) {
                finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }else{
                AlertDialog.Builder alert = new AlertDialog.Builder(SecondActivity.this);
                alert.setTitle("Temp");
                alert.setMessage("No user or fail id, password");
                alert.setPositiveButton("check",null);
                alert.show();
            }
        }
    }
}