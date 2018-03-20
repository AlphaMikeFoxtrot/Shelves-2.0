package com.anonymous.shelves;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.anonymous.shelves.Adapters.HomeActivityViewPagerAdapter;
import com.anonymous.shelves.Classes.Book;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {

    Toolbar mToolbar;

    SharedPreferences current_user;
    SharedPreferences.Editor editor;

    ProgressDialog progressDialog;

    TabLayout mTabLayout;

    ViewPager mViewPager;
    com.anonymous.shelves.Adapters.HomeActivityViewPagerAdapter homeActivityViewPagerAdapter;

    NetworkChangeReceiver receiver;
    Boolean flag = false;
    IntentFilter filter;

    @Override
    protected void onStop() {
        super.onStop();
        if(flag) {
            unregisterReceiver(receiver);
            flag = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(flag) {
            unregisterReceiver(receiver);
            flag = false;
        }
    }

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
        setContentView(R.layout.activity_home);



        mTabLayout = findViewById(R.id.home_activity_tab_layout);

        mViewPager = findViewById(R.id.home_activity_view_pager);

        homeActivityViewPagerAdapter = new HomeActivityViewPagerAdapter(getSupportFragmentManager());
        homeActivityViewPagerAdapter.addFragment(new HomeTrendingListFragment(), "Trending Books");
        homeActivityViewPagerAdapter.addFragment(new HomeUserShelvesFragment(), "My Shelves");
        mViewPager.setAdapter(homeActivityViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        current_user = this.getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        editor = current_user.edit();

        mToolbar = findViewById(R.id.home_act_tool);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_signout, menu);
        MenuItem menuItem = menu.findItem(R.id.sign_out_option);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomeActivity.this, "" + query, Toast.LENGTH_SHORT).show();
                Intent toSearchResults = new Intent(HomeActivity.this, SearchResultsActivity.class);
                toSearchResults.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toSearchResults.putExtra("query", query);
                startActivity(toSearchResults);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sign_out_option){

            editor.putString(getString(R.string.shared_preference_username), "");
            editor.commit();

            AuthUI.getInstance()
                    .signOut(HomeActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {

                            editor.putBoolean(getString(R.string.shared_preference_login_boolean), true);
                            editor.commit();

                            editor.putString(getString(R.string.shared_preference_user_uuid), "");
                            editor.commit();

                            Intent toMain = new Intent(HomeActivity.this, MainActivity.class);
                            toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(toMain);
                        }
                    });

        }

        return true;
    }
}
