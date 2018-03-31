package com.anonymous.shelves.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.shelves.Classes.Book;
import com.anonymous.shelves.R;

import java.util.ArrayList;

public class SearchResultRVAdapter extends RecyclerView.Adapter<SearchResultRVAdapter.SearchResultViewHolder> {

    Context context;
    ArrayList<Book> books;

    public SearchResultRVAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView;
        listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_card_view, parent, false);
        return new SearchResultViewHolder(listItemView, context, books);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {

        holder.name.setText(this.books.get(position).getName());
        holder.author.setText(this.books.get(position).getAuthor());
        holder.ratingText.setText(this.books.get(position).getRatingsCount());
        holder.bar.setRating(Float.parseFloat(this.books.get(position).getRatings()));

    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Context context;
        ArrayList<Book> books;
        TextView name, author, ratingText;
        RatingBar bar;

        public SearchResultViewHolder(View itemView, Context context, ArrayList<Book> books) {
            super(itemView);

            this.context = context;
            this.books = books;

            itemView.setOnClickListener(this);

            this.name = itemView.findViewById(R.id.search_result_book_title);
            this.author = itemView.findViewById(R.id.search_result_book_author);
            this.ratingText = itemView.findViewById(R.id.search_result_rating_text);
            this.bar = itemView.findViewById(R.id.search_result_rating_bar);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            Toast.makeText(context, "" + this.books.get(position).getName(), Toast.LENGTH_SHORT).show();

        }
    }

}
