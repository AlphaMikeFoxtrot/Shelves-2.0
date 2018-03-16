package com.anonymous.shelves;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anonymous.shelves.Adapters.HomeTrendingRVAdapter;
import com.anonymous.shelves.Classes.BookClass;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTrendingListFragment extends Fragment {

    RecyclerView mRecyclerView;
    HomeTrendingRVAdapter adapter;
    ArrayList<BookClass> books;

    public HomeTrendingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRecyclerView = container.findViewById(R.id.home_trending_fragment_rv);
        adapter = new HomeTrendingRVAdapter(container.getContext(), books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        return inflater.inflate(R.layout.fragment_home_trending_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        books = new ArrayList<>();
        books.add(new BookClass("The Hobbit", "someone", 6.5f));
        books.add(new BookClass("The Hobbit2", "someone", 6.5f));
        books.add(new BookClass("The Hobbit3", "someone", 6.5f));
        books.add(new BookClass("The Hobbit4", "someone", 6.5f));
        books.add(new BookClass("The Hobbit5", "someone", 6.5f));
    }
}
