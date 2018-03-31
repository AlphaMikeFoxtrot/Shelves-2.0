package com.anonymous.shelves;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.shelves.Adapters.SearchResultRVAdapter;
import com.anonymous.shelves.Classes.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    public static final String LOG_TAG = "SEARCH RESULTS";
    SearchResultRVAdapter adapter;
    ArrayList<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        toolbar = findViewById(R.id.search_result_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.is_all:
                        getResults(getIntent().getStringExtra("query"), "");
                        break;

                    case R.id.is_title:
                        getResults(getIntent().getStringExtra("query"), "intitle:");
                        break;

                    case R.id.is_author:
                        getResults(getIntent().getStringExtra("query"), "inauthor:");
                        break;

                    case R.id.is_isbn:
                        getResults(getIntent().getStringExtra("query"), "isbn:");
                        break;

                    case R.id.is_genre:
                        getResults(getIntent().getStringExtra("query"), "subject:");
                        break;
                }
            }
        });

        recyclerView = findViewById(R.id.search_result_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getResults(getIntent().getStringExtra("query"), "all");
    }

    private void getResults(String query, String mode) {
        new GetResults().execute(query, mode);
    }

    private class GetResults extends AsyncTask<String, Void, String>{

        String query;
        String mode;
        String ratings;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SearchResultActivity.this);
            progressDialog.setMessage("please wait while we search the net......");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            query = strings[0];
            mode = strings[1];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.get_google_books) + mode + query);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
                    response.append(line);
                }

                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return "7993269429URL: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return "7993269429IO: " + e.toString();
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

            if(books.size() > 0){
                books.clear();
            }

            if(s.toLowerCase().contains("7993269429url:") || s.toLowerCase().contains("7993269429io:")){
                TextView error = findViewById(R.id.error);
                error.setVisibility(View.VISIBLE);
                error.setText(s);
                toolbar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(SearchResultActivity.this, "something went wrong: \n" + s, Toast.LENGTH_LONG).show();
            } else if(s.isEmpty()) {
                // TODO:
            } else {
                try {

                    JSONObject root = new JSONObject(s);
                    JSONArray items = root.getJSONArray("items");

                    for(int i = 0; i < items.length(); i++){

                        Book currentBook = new Book();

                        JSONObject book = items.getJSONObject(i);
                        currentBook.setSelfLink(book.getString("selfLink"));
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                        currentBook.setName(volumeInfo.getString("title"));

                        if(volumeInfo.has("authors")) {
                            JSONArray authors = volumeInfo.getJSONArray("authors");
                            if (authors.length() > 0) {
                                StringBuilder result = new StringBuilder();
                                for (int j = 0; j < authors.length(); j++) {
                                    result.append(authors.get(j));
                                    if (!(j == authors.length() - 1)) {
                                        result.append(", ");
                                    }
                                }
                                currentBook.setAuthor(result.toString());
                            } else {
                                currentBook.setAuthor("Unknown");
                            }
                        } else {
                            currentBook.setAuthor("Unknown");
                        }

                        if(volumeInfo.has("publishedDate")) {
                            currentBook.setPublishedDate(volumeInfo.getString("publishedDate"));
                        } else {
                            currentBook.setPublishedDate("UNKNOWN");
                        }

                        if(volumeInfo.has("description")) {
                            currentBook.setDescription(volumeInfo.getString("description"));
                        } else {
                            currentBook.setDescription("Not available");
                        }

                        if(volumeInfo.has("pageCount")){
                            currentBook.setPageCount(volumeInfo.getString("pageCount"));
                        } else {
                            currentBook.setPageCount("not available");
                        }

                        if(volumeInfo.has("categories")){
                            JSONArray categories = volumeInfo.getJSONArray("categories");
                            if(categories.length() > 0){
                                StringBuilder result = new StringBuilder();
                                for(int k = 0; k < categories.length(); k++){
                                    result.append(categories.getString(k));
                                    if (!(k == categories.length() - 1)) {
                                        result.append(", ");
                                    }
                                }
                                currentBook.setGenre(result.toString());
                            } else {
                                currentBook.setGenre("Unknown");
                            }
                        } else {
                            currentBook.setGenre("Unknown");
                        }

                        if(volumeInfo.has("averageRating")){
                            currentBook.setRatings(volumeInfo.getString("averageRating"));
                            ratings = volumeInfo.getString("averageRating");
                        } else {
                            currentBook.setRatings("0");
                        }

                        if(volumeInfo.has("ratingsCount")){
                            currentBook.setRatingsCount(ratings + " ( " + volumeInfo.getString("ratingsCount") + " ) ");
                        } else {
                            currentBook.setRatingsCount("0");
                        }

                        if (volumeInfo.has("imageLinks")) {
                            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                            currentBook.setImageUrl(imageLinks.getString("thumbnail"));
                        } else {
                            currentBook.setImageUrl("https://lifemissionusa.org/sites/default/files/field/product/booknocoverimage_0_1.jpg");
                        }

                        books.add(currentBook);

                    }

                    adapter = new SearchResultRVAdapter(SearchResultActivity.this, books);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    TextView error = findViewById(R.id.error);
                    error.setVisibility(View.VISIBLE);
                    error.setText(e.toString());
                    toolbar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
    }
}
