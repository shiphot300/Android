package com.example.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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


public class MainActivity extends AppCompatActivity {

    private boolean saveLoginData;
    private SharedPreferences appData;
    private String id;
    private String pwd;

    private CheckBox checkBox;

    EditText ID, PW;
    Button btn_sign, btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        ID = (EditText) findViewById(R.id.login_id_edit_text);
        PW = (EditText) findViewById(R.id.login_pw_edit_text);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        btn_sign = (Button) findViewById(R.id.btnsignup);
        btn_login = (Button) findViewById(R.id.sign_in);

        // 이전에 로그인 정보를 저장시킨 기록이 있다면
        if (saveLoginData) {
            ID.setText(id);
            PW.setText(pwd);
            checkBox.setChecked(saveLoginData);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    String mid = ID.getText().toString().trim();
                    String mpw = PW.getText().toString().trim();

                    JSONObject jsonob = new JSONObject();

                    jsonob.accumulate("member_id", mid);
                    jsonob.accumulate("member_pw", mpw);

                    String url = "http://192.168.234.1:3000/api/mobile/loginapp";

                    new JSONTask().execute(url,jsonob.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                save();
            }
        });

        btn_sign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", ID.getText().toString().trim());
        editor.putString("PWD", PW.getText().toString().trim());



        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    protected void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id = appData.getString("ID", "");
        pwd = appData.getString("PWD", "");
    }


    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection nect = null;
            BufferedReader rea = null;
            try {
                URL url = new URL(urls[0]);
                nect = (HttpURLConnection) url.openConnection();
                nect.setRequestMethod("POST");
                nect.setRequestProperty("Cache-Control", "no-cache");
                nect.setRequestProperty("Content-Type", "application/json");

                nect.setRequestProperty("Accept", "text/html");
                nect.setDoOutput(true);
                nect.setDoInput(true);
                nect.connect();

                OutputStream out = nect.getOutputStream();
                out.write(urls[1].getBytes("utf-8"));
                out.flush();
                out.close();

                InputStream stream = nect.getInputStream();
                rea = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = rea.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
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
                        rea.close();
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
            Log.d(result,"nonono");

            if(result.equals("success")) {
                Log.d(result,"yesyes");
                finish();
                Intent intent = new Intent(getApplicationContext(), thirdActivity.class);
                startActivity(intent);
            }
            else{
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Temp");
                alert.setMessage("No user or fail id, password");
                alert.setPositiveButton("check",null);
                alert.show();
            }
        }
    }
}
