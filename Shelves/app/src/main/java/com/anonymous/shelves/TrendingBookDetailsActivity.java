package com.anonymous.shelves;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.shelves.Classes.TrendingBookClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.CDATASection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TrendingBookDetailsActivity extends AppCompatActivity {

    ImageView mBookCover;
    TextView toolbarBookTitle, bookTitle, bookAuthor, bookRatingTV, bookDescription;
    Button addToShelf;
    ProgressDialog progressDialog;
    Toolbar mToolbar;
    RatingBar bookRatingBar;
    Intent intent;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_book_details);

        intent = getIntent();

        TrendingBookClass book = (TrendingBookClass) intent.getParcelableExtra("book");

        // Toast.makeText(this, "" + book.getmBookISBN(), Toast.LENGTH_SHORT).show();

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        flag = true;

        mBookCover = findViewById(R.id.book_detail_cover_photo);

        toolbarBookTitle = findViewById(R.id.book_detail_toolbar_title);
        bookTitle = findViewById(R.id.book_detail_book_title);
        bookAuthor = findViewById(R.id.book_detail_book_author);
        bookRatingTV = findViewById(R.id.book_detail_book_rating_text);
        bookDescription = findViewById(R.id.book_detail_book_description);

        addToShelf = findViewById(R.id.book_detail_add_to_shelf);

        mToolbar = findViewById(R.id.book_detail_toolbar);
        setSupportActionBar(mToolbar);

        bookRatingBar = findViewById(R.id.book_detail_book_rating);

        toolbarBookTitle.setText(book.getmBookName());
        bookTitle.setText(book.getmBookName());
        bookAuthor.setText("by " + book.getmBookAuthor());

        getExternalResources(book.getmBookISBN());

    }

    private void getExternalResources(String isbn) {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("getting resources...");
//        progressDialog.show();
        new GetGoogleResources().execute(isbn);
//        progressDialog.dismiss();
    }

    private class GetGoogleResources extends AsyncTask<String, Void, String>{

        public String isbn;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TrendingBookDetailsActivity.this);
            progressDialog.setMessage("getting book details...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            isbn = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.get_google_books_base_url) + isbn);
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
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
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

            // Toast.makeText(TrendingBookDetailsActivity.this, "" + s, Toast.LENGTH_SHORT).show();

            if(s.isEmpty()){

                // Toast.makeText(TrendingBookDetailsActivity.this, "s is empty", Toast.LENGTH_SHORT).show();

            } else {

                try {

                    JSONObject root = new JSONObject(s);

                    // Toast.makeText(TrendingBookDetailsActivity.this, "" + root.getInt("totalItems"), Toast.LENGTH_SHORT).show();

                    if(root.getInt("totalItems") == 0){

                        getDetailsFromGoodreads(isbn);
                        // Toast.makeText(TrendingBookDetailsActivity.this, "" + root.getInt("totalItems"), Toast.LENGTH_SHORT).show();

                    } else {

                        JSONArray items = root.getJSONArray("items");
                        JSONObject book = items.getJSONObject(0);
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                        TrendingBookClass bookClicked = getIntent().getParcelableExtra("book");

                        if (!(volumeInfo.getJSONArray("authors").get(0).toString().toLowerCase().contains(bookClicked.getmBookAuthor().toLowerCase()))) {

                            getDetailsFromGoodreads(isbn);

                        } else {

                            String description = volumeInfo.getString("description").replaceAll("\"\"", "");

                            if (volumeInfo.has("averageRating")) {

                                Float averageRating = Float.parseFloat(volumeInfo.getString("averageRating"));
                                String ratingCount = volumeInfo.getString("ratingsCount");
                                bookRatingBar.setRating(averageRating);
                                bookRatingTV.setText(averageRating.toString() + " (" + ratingCount + ")");

                            }

                            JSONObject images = volumeInfo.getJSONObject("imageLinks");

                            if (images.length() > 0) {
                                Picasso.get().load(images.getString("smallThumbnail")).into(mBookCover);
                            } else {
                                mBookCover.setImageResource(R.drawable.image_not_found);
                            }

                            bookDescription.setText(description);

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Toast.makeText(TrendingBookDetailsActivity.this, "json exception : " + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    private void getDetailsFromGoodreads(String isbn) {
        new GetGoodreadsResources().execute(isbn);
    }

    private class GetGoodreadsResources extends AsyncTask<String, Void, String>{

        public String isbn;
        public final String KEY = "?key=PD86Pp9gK5x9cTiskvdRQ";

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TrendingBookDetailsActivity.this);
            progressDialog.setMessage("getting book details...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            isbn = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.get_goodreads_books_base_url) + isbn + KEY);
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
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
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

            String ratingsCount = null;

            if(s.isEmpty()){

                // TODO

            } else {

                StringReader stringReader = new StringReader(s);
                XmlPullParserFactory factory = null;

                try {
                    factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();

                    parser.setInput(stringReader);

                    int event = parser.getEventType();

                    int count = 0;

                    String currentTag = "", currentTagValue = "";

                    while (event != XmlPullParser.END_DOCUMENT){

                        currentTag = parser.getName();

                        switch (event){

                            case XmlPullParser.START_TAG:
                                if(currentTag.equalsIgnoreCase("work")){

                                }
                                break;

                            case XmlPullParser.TEXT:
                                currentTagValue = parser.getText();
                                break;

                            case XmlPullParser.END_TAG:

                                switch (currentTag){

                                    case "description":
                                        if(!(count == 1)) {
                                            bookDescription.setText(currentTagValue.replaceAll("<[^>]+>", ""));
                                            count += 1;
                                        }
                                        break;

                                    case "ratings_count":
                                        ratingsCount = currentTagValue;
                                        break;

                                    case "average_rating":
                                        bookRatingBar.setRating(Float.parseFloat(currentTagValue));
                                        bookRatingTV.setText(currentTagValue + " (" + ratingsCount + ")");
                                        break;

                                    case "image_url":
                                        if(currentTagValue.contains("books")) {
                                            Picasso.get().load(currentTagValue).into(mBookCover);
                                        }
                                        break;

                                }

                                break;

                        }

                        event = parser.next();

                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public class Escape {
        /**
         *
         */
        public final String LINE_SEPARATOR = System.getProperty("line.separator");

        /**
         * Empty constructor
         */
        public Escape() {
            // left blank on purpose
        }

        /**
         * Do the escaping.
         *
         * @param st
         * @return The escaped text.
         */
        public final String getText(String st) {
            StringBuffer buff = new StringBuffer();
            char[] block = st.toCharArray();
            String stEntity = null;
            int i, last;

            for (i = 0, last = 0; i < block.length; i++) {
                switch (block[i]) {
                    case '<':
                        stEntity = "&lt;";
                        break;
                    case '>':
                        stEntity = "&gt;";
                        break;
                    case '&':
                        stEntity = "&amp;";
                        break;
                    case '"':
                        stEntity = "&quot;";
                        break;
                    case '\n':
                        stEntity = LINE_SEPARATOR;
                        break;
                    default:
        /* no-op */
                        break;
                }
                if (stEntity != null) {
                    buff.append(block, last, i - last);
                    buff.append(stEntity);
                    stEntity = null;
                    last = i + 1;
                }
            }
            if (last < block.length) {
                buff.append(block, last, i - last);
            }
            return buff.toString();
        }
    }

    public static String stripCDATA(String s) {
        s = s.trim();
        if (s.startsWith("<![CDATA[")) {
            s = s.substring(9);
            int i = s.indexOf("]]&gt;");
            if (i == -1) {
                throw new IllegalStateException(
                        "argument starts with <![CDATA[ but cannot find pairing ]]&gt;");
            }
            s = s.substring(0, i);
        }
        return s;
    }
}
