package com.anonymous.shelves;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import java.util.concurrent.ExecutionException;

public class SearchResultsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
    }

    private void getBooks(String query) {
        new GetBooks().execute(query);
    }

    private class GetBooks extends AsyncTask<String, Void, String> {

        public String searchQuery;
        public ArrayList<Book> books = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SearchResultsActivity.this);
            progressDialog.setMessage("Searching shelves...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            searchQuery = strings[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                books = new GetGoodreadBooks().execute(searchQuery).get();

                URL url = new URL(getString(R.string.get_google_books) + searchQuery);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){

                    response.append(line);

                }

                JSONObject root = new JSONObject(response.toString());
                JSONArray items = root.getJSONArray("items");
                for(int i = 0; i < items.length(); i++){

                    Book currentBook = new Book();

                    JSONObject book = items.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    currentBook.setName(volumeInfo.getString("title"));
                    currentBook.setPublishedDate(volumeInfo.getString("publishedDate"));
                    currentBook.setDescription(volumeInfo.getString("description"));
                    currentBook.setPageCount(volumeInfo.getString("pageCount"));
                    currentBook.setRatings(volumeInfo.getString("averageRating"));
                    currentBook.setRatingsCount(volumeInfo.getString("ratingsCount"));

                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if(authors.length() > 1){

                        StringBuilder author = new StringBuilder();

                        for(int j = 0; j < authors.length(); j++){

                            if(j == authors.length() - 1){
                                author.append(authors.get(j));
                            } else {

                                author.append(authors.get(j));
                                author.append(" & ");

                            }

                        }

                        currentBook.setAuthor(author.toString());

                    } else {
                        currentBook.setAuthor(authors.getString(0));
                    }

                    JSONArray categories = volumeInfo.getJSONArray("categories");

                    StringBuilder genres = new StringBuilder();

                    for(int k = 0; k < categories.length(); k++){
                        if(k == categories.length() - 1){
                            genres.append(categories.getString(k));
                        } else {
                            genres.append(categories.getString(k));
                            genres.append("\n");
                        }
                    }

                    currentBook.setGenre(genres.toString());

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    currentBook.setImageUrl(imageLinks.getString("thumbnail"));

                    books.add(currentBook);

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";

        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }

    private class GetGoodreadBooks extends AsyncTask<String, Void, ArrayList<Book>>{

        @Override
        protected ArrayList<Book> doInBackground(String... strings) {
            return null;
        }
    }
}
