package com.skybit.skrandro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends AppCompatActivity {
    TextView loginBu;
    private static final String LOGIN_URL = "http://cetbusservice.000webhostapp.com/register.php/";
    EditText bus_number;
    EditText password;
    EditText masterPassword;
    Button register;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        loginBu = (TextView) findViewById(R.id.login_user);
        register = (Button) findViewById(R.id.register_button);
        bus_number = (EditText) findViewById(R.id.user_field);
        password = (EditText) findViewById(R.id.password_field);
        masterPassword = (EditText) findViewById(R.id.master_password);

        loginBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Register.this, Login.class);
                startActivity(in);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });
    }

    private void registerUser() {
        final String bus_no, pass, masterpwd;
        bus_no = bus_number.getText().toString();
        pass = password.getText().toString();
        masterpwd = masterPassword.getText().toString();
        register(bus_no, pass, masterpwd);
    }

    private void register(String bus_no, String pass, String masterpwd) {
        final String url_suffix = "?busno=" + bus_no + "&pass=" + pass + "&master=" + masterpwd;
        class RegisterUser extends AsyncTask<String, Void, String> {
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
                loading = ProgressDialog.show(Register.this, "Please wait...", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPreExecute();

                if (loading.isShowing()) {
                    loading.dismiss();
                }

                if (result == null) {
                    Toast.makeText(getApplicationContext(), "Network error occured!", Toast.LENGTH_SHORT).show();
                } else if (result.equals("Failed")) {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                } else if (result.equals("Registered")) {
                    Toast.makeText(Register.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Register.this, Login.class);
                    startActivity(i);
                    finish();
                } else if (result.contains("refused")) {
                    Toast.makeText(Register.this, "Server refused to connect!", Toast.LENGTH_SHORT).show();
                } else if (result.equals("Wrong Master Password")) {
                    Toast.makeText(Register.this, "Wrong Master Password!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, "Nothing happened!", Toast.LENGTH_SHORT).show();
                }


            }


        }
        RegisterUser ru = new RegisterUser();
        ru.execute(url_suffix);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                // User chose the "Settings" item, show the app settings UI...
                Intent in = new Intent(Register.this, SettingsActivity.class);
                startActivity(in);
            }

            case R.id.action_check_update: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.skybit.cetbbus.driver"));
                startActivity(intent);
            }

            case R.id.action_about: {

            }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
