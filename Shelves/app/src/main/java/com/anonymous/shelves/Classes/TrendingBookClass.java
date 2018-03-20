package com.anonymous.shelves.Classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ANONYMOUS on 16-Mar-18.
 */

public class TrendingBookClass implements Parcelable{

    private String mBookName, mBookDescription, mBookAuthor, rank, rankLastWeek, mBookISBN, mBookCoverURL;
    private float mBookRating;
    private int mBookGenreId;

    public TrendingBookClass(String mBookName, String mBookAuthor, float mBookRating, String rank, String rankLastWeek, String mBookISBN, String mBookCoverURL, String mBookDescription) {
        this.mBookName = mBookName;
        this.mBookAuthor = mBookAuthor;
        this.mBookRating = mBookRating;
        this.rank = rank;
        this.rankLastWeek = rankLastWeek;
        this.mBookISBN = mBookISBN;
        this.mBookDescription = mBookDescription;
        this.mBookCoverURL = mBookCoverURL;
    }

    protected TrendingBookClass(Parcel in) {
        mBookName = in.readString();
        mBookDescription = in.readString();
        mBookAuthor = in.readString();
        rank = in.readString();
        rankLastWeek = in.readString();
        mBookISBN = in.readString();
        mBookCoverURL = in.readString();
        mBookRating = in.readFloat();
        mBookGenreId = in.readInt();
    }

    public static final Creator<TrendingBookClass> CREATOR = new Creator<TrendingBookClass>() {
        @Override
        public TrendingBookClass createFromParcel(Parcel in) {
            return new TrendingBookClass(in);
        }

        @Override
        public TrendingBookClass[] newArray(int size) {
            return new TrendingBookClass[size];
        }
    };

    public String getmBookDescription() {
        return mBookDescription;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mBookName);
        parcel.writeString(mBookDescription);
        parcel.writeString(mBookAuthor);
        parcel.writeString(rank);
        parcel.writeString(rankLastWeek);
        parcel.writeString(mBookISBN);
        parcel.writeString(mBookCoverURL);
        parcel.writeFloat(mBookRating);
        parcel.writeInt(mBookGenreId);
    }
}
