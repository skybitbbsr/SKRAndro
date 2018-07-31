package com.skybit.skrandro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {
    private static final String LOGIN_URL = "http://cetbusservice.000webhostapp.com/login.php/";
    EditText bus_number;
    EditText password;
    Button signin;
    String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signin = (Button) findViewById(R.id.signin_button);
        bus_number = (EditText) findViewById(R.id.user_field);
        password = (EditText) findViewById(R.id.password_field);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });
    }

    private void loginUser() {
        final String bus_no, pass;
        bus_no = bus_number.getText().toString();
        pass = password.getText().toString();
        login(bus_no, pass);
    }

    private void login(final String bus_no, String pass) {
        final String url_suffix = "?busno=" + bus_no + "&pass=" + pass;
        class LoginUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader;
                try {
                    URL url = new URL(LOGIN_URL + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = bufferedReader.readLine();
                    return result;

                } catch (Exception e) {
                    e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Please wait...", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPreExecute();

                if (loading.isShowing()) {
                    loading.dismiss();
                }

                Toast.makeText(Login.this, result, Toast.LENGTH_SHORT).show();

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "Network error occured!", Toast.LENGTH_SHORT).show();
                } else if (result.equals("Fail")) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else if (result.equals("Success")) {
                    saveInfo();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    i.putExtra("busNumber", bus_no);
                    startActivity(i);
                } else if (result.contains("refused")) {
                    Toast.makeText(Login.this, "Server refused to connect!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Nothing happened!", Toast.LENGTH_SHORT).show();
                }


            }


        }
        LoginUser lu = new LoginUser();
        lu.execute(url_suffix);
    }

    private void saveInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("busnumber", bus_number.getText().toString());
        editor.apply();
    }
}
