package com.anonymous.shelves;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    List<AuthUI.IdpConfig> providers;

    Button signInUp;

    ProgressDialog progressDialog, progressDialog_get;

    public SharedPreferences current_user;
    public SharedPreferences.Editor editor;

    public String uuid;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current_user = this.getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        editor = current_user.edit();

        signInUp = findViewById(R.id.sign_in_up);

        mAuth = FirebaseAuth.getInstance();

        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        if(mAuth.getCurrentUser() != null){
            // already signed in
            Intent toHome = new Intent(this, HomeActivity.class);
            toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toHome);
        }

        signInUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                // Successfully signed in
                // ...
                StringBuilder username = new StringBuilder();
                String name = mAuth.getCurrentUser().getDisplayName();
                String[] split_name = name.split(" ");
                for(int i = 0; i < split_name.length; i++){
                    username.append(split_name[i].toLowerCase());
                    if(!(i == split_name.length - 1)){
                        // not last element
                        username.append("_");
                    }
                }

                checkIfExists(username.toString());

            } else {

                // Sign in failed, check response for error code
                // ...
                Toast.makeText(this, "" + requestCode, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void checkIfExists(String username) {

        new CheckUsername().execute(username);

    }

    private void createUser(String username) {

        new CreateUser().execute(username);

    }

    private class CheckUsername extends AsyncTask<String, Void, String>{

        public String username;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("checking credentials...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            username = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;

            try {

                URL url = new URL(getString(R.string.check_username_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                String data = URLEncoder.encode("username", "UTF-8") +"="+ URLEncoder.encode(username, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());

                bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){

                    response.append(line);

                }

                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url";
            } catch (IOException e) {
                e.printStackTrace();
                return "io";
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();
            if(s.contains("exists")){

                // user exists in the database
                // set the shared preference variable to the existing username
                editor.putString(getString(R.string.shared_preference_username), username);
                editor.commit();

                try {
                    editor.putString(getString(R.string.shared_preference_user_uuid), new GetUUID().execute(username).get());
                    editor.commit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "InterruptedException : " + e.toString(), Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "ExecutionException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }

                editor.putBoolean(getString(R.string.shared_preference_login_boolean), true);
                editor.commit();

                Intent toHome = new Intent(MainActivity.this, HomeActivity.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toHome);

            } else if(s.contains("fail")){

                // user does not exist in the database
                // therefore, new user must be created
                createUser(username);

            }

        }
    }

    private class CreateUser extends AsyncTask<String, Void, String>{

        public String username;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("creating new user...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            username = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.create_user_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                String data = URLEncoder.encode("username", "UTF-8") +"="+ URLEncoder.encode(username, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){

                    response.append(line);

                }

                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url";
            } catch (IOException e) {
                e.printStackTrace();
                return "io";
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            if(s.contains("success")){

                // new user successfully created
                editor.putString(getString(R.string.shared_preference_username), username);
                editor.commit();

                try {
                    editor.putString(getString(R.string.shared_preference_user_uuid), new GetUUID().execute(username).get());
                    editor.commit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "InterruptedException : " + e.toString(), Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "ExecutionException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }

                editor.putBoolean(getString(R.string.shared_preference_login_boolean), true);
                editor.commit();

                Intent toHome = new Intent(MainActivity.this, HomeActivity.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toHome);

            } else {

                Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class GetUUID extends AsyncTask<String, Void, String>{

        public String username;

        @Override
        protected void onPreExecute() {
            progressDialog_get = new ProgressDialog(MainActivity.this);
            progressDialog_get.setMessage("getting credentials");
            progressDialog_get.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            username = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;

            try {

                URL url = new URL(getString(R.string.get_user_uuid_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                String data = URLEncoder.encode("username", "UTF-8") +"="+ URLEncoder.encode(username, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){

                    response.append(line);

                }

                String[] response_array = response.toString().split(":");
                uuid = response_array[1];

                return uuid; // response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "url";
            } catch (IOException e) {
                e.printStackTrace();
                return "io";
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog_get.dismiss();

        }
    }
}
