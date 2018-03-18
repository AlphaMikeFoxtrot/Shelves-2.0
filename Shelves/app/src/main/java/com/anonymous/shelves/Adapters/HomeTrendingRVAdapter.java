package com.anonymous.shelves.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.shelves.Classes.TrendingBookClass;
import com.anonymous.shelves.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ANONYMOUS on 16-Mar-18.
 */

public class HomeTrendingRVAdapter extends RecyclerView.Adapter<HomeTrendingRVAdapter.HomeTrendingRVViewHolder> {

    Context mContext;
    ArrayList<TrendingBookClass> mBooks = new ArrayList<>();

    public HomeTrendingRVAdapter(Context mContext, ArrayList<TrendingBookClass> mBooks) {
        this.mContext = mContext;
        this.mBooks = mBooks;
    }

    @Override
    public HomeTrendingRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItemView;
        listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_fragment_card_view, parent, false);

        return new HomeTrendingRVViewHolder(listItemView, mContext, mBooks);

    }

    @Override
    public void onBindViewHolder(HomeTrendingRVViewHolder holder, int position) {

        holder.mBookName.setText(this.mBooks.get(position).getmBookName());
        holder.mBookAuthor.setText(this.mBooks.get(position).getmBookAuthor());
        holder.mBookRank.setText(this.mBooks.get(position).getRank());
        holder.mBookRankLastWeek.setText(this.mBooks.get(position).getRankLastWeek());
        if(this.mBooks.get(position).getmBookCoverURL().isEmpty()) {
            holder.mBookCover.setImageResource(R.drawable.image_not_found);
        } else {
            Picasso.get().load(this.mBooks.get(position).getmBookCoverURL()).into(holder.mBookCover);
        }

    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class HomeTrendingRVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mBookCover;
        TextView mBookName, mBookAuthor, mBookRank, mBookRankLastWeek;
        ArrayList<TrendingBookClass> books;
        Context context;

        public HomeTrendingRVViewHolder(View itemView, Context context, ArrayList<TrendingBookClass> books) {
            super(itemView);

            itemView.setOnClickListener(this);

            this.context = context;
            this.books = books;

            this.mBookCover = itemView.findViewById(R.id.book_cover_photo);

            this.mBookName = itemView.findViewById(R.id.book_title);
            this.mBookAuthor = itemView.findViewById(R.id.book_author);
            this.mBookRank = itemView.findViewById(R.id.book_rank);
            this.mBookRankLastWeek = itemView.findViewById(R.id.book_rank_last_week);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Toast.makeText(context, "name of book clicked : " + this.books.get(position).getmBookName(), Toast.LENGTH_SHORT).show();

        }
    }

}
