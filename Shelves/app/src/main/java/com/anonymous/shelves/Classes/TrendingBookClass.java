package com.anonymous.shelves.Classes;

/**
 * Created by ANONYMOUS on 16-Mar-18.
 */

public class TrendingBookClass {

    private String mBookName, mBookAuthor, rank, rankLastWeek, mBookISBN, mBookCoverURL;
    private float mBookRating;
    private int mBookGenreId;

    public TrendingBookClass(String mBookName, String mBookAuthor, float mBookRating, String rank, String rankLastWeek, String mBookISBN, String mBookCoverURL) {
        this.mBookName = mBookName;
        this.mBookAuthor = mBookAuthor;
        this.mBookRating = mBookRating;
        this.rank = rank;
        this.rankLastWeek = rankLastWeek;
        this.mBookISBN = mBookISBN;
        this.mBookCoverURL = mBookCoverURL;
    }

    public String getmBookISBN() {
        return mBookISBN;
    }

    public String getmBookCoverURL() {
        return mBookCoverURL;
    }

    public String getRank() {
        return rank;
    }

    public String getRankLastWeek() {
        return rankLastWeek;
    }

    public String getmBookName() {
        return mBookName;
    }

    public void setmBookName(String mBookName) {
        this.mBookName = mBookName;
    }

    public String getmBookAuthor() {
        return mBookAuthor;
    }

    public void setmBookAuthor(String mBookAuthor) {
        this.mBookAuthor = mBookAuthor;
    }

    public int getmBookGenreId() {
        return mBookGenreId;
    }

    public void setmBookGenreId(int mBookGenreId) {
        this.mBookGenreId = mBookGenreId;
    }

    public float getmBookRating() {
        return mBookRating;
    }

    public void setmBookRating(float mBookRating) {
        this.mBookRating = mBookRating;
    }
}
