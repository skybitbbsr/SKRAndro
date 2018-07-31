package com.skybit.skrandro;

import android.app.ProgressDialog;
import android.content.Intent;
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
    String[] params = null;

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

    private void login(String bus_no, String pass) {
        final String url_suffix = "?busno=" + bus_no + "&pass=" + pass;
        class LoginUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Please wait...", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "Network error occured!", Toast.LENGTH_SHORT).show();
                } else if (result.equals("Login Failed!")) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else if (result.contains("Success")) {
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(Login.this, "Unknown error occurred!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(String... strings) {

                BufferedReader bufferedReader;
                try {
                    URL url = new URL(LOGIN_URL + url_suffix);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = bufferedReader.readLine();
                    return result;

                } catch (Exception e) {
                    return null;
                }
            }
        }
        LoginUser lu = new LoginUser();
        lu.execute(url_suffix);
    }
}
