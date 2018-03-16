package com.anonymous.shelves.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anonymous.shelves.Classes.BookClass;
import com.anonymous.shelves.R;

import java.util.ArrayList;

/**
 * Created by ANONYMOUS on 16-Mar-18.
 */

public class HomeTrendingRVAdapter extends RecyclerView.Adapter<HomeTrendingRVAdapter.HomeTrendingRVViewHolder> {

    Context mContext;
    ArrayList<BookClass> mBooks = new ArrayList<>();

    public HomeTrendingRVAdapter(Context mContext, ArrayList<BookClass> mBooks) {
        this.mContext = mContext;
        this.mBooks = mBooks;
    }

    @Override
    public HomeTrendingRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItemView;
        listItemView = LayoutInflater.from(mContext).inflate(R.layout.trending_fragment_card_view, parent, false);

        return new HomeTrendingRVViewHolder(listItemView);

    }

    @Override
    public void onBindViewHolder(HomeTrendingRVViewHolder holder, int position) {

        holder.mBookName.setText(this.mBooks.get(position).getmBookName());
        holder.mBookAuthor.setText(this.mBooks.get(position).getmBookAuthor());
        // holder.mBookCover.setImageResource(this.mBooks.get(position).getmBookGenreId());
        // holder.mBookGenreIcon.setImageResource(this.mBooks.get(position).getmBookGenreId());

    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class HomeTrendingRVViewHolder extends RecyclerView.ViewHolder{

        ImageView mBookCover, mBookGenreIcon;
        TextView mBookName, mBookAuthor;

        public HomeTrendingRVViewHolder(View itemView) {
            super(itemView);

            this.mBookCover = itemView.findViewById(R.id.book_cover_photo);
            this.mBookGenreIcon = itemView.findViewById(R.id.book_genre_icon);

            this.mBookName = itemView.findViewById(R.id.book_title);
            this.mBookAuthor = itemView.findViewById(R.id.book_author);
        }
    }

}
