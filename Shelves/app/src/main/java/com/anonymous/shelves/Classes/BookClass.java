package com.anonymous.shelves.Classes;

/**
 * Created by ANONYMOUS on 16-Mar-18.
 */

public class BookClass {

    private String mBookName, mBookAuthor;
    private float mBookRating;
    private int mBookCoverId, mBookGenreId;

    public BookClass(String mBookName, String mBookAuthor, float mBookRating) {
        this.mBookName = mBookName;
        this.mBookAuthor = mBookAuthor;
        this.mBookRating = mBookRating;
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

    public int getmBookCoverId() {
        return mBookCoverId;
    }

    public void setmBookCoverId(int mBookCoverId) {
        this.mBookCoverId = mBookCoverId;
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
