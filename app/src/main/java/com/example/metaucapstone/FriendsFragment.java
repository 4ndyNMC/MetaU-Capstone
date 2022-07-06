package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FriendsFragment extends Fragment {

    FragmentManager fragmentManager;
    RecyclerView rvFriends;
    FloatingActionButton fabSearch;

    List<String> friends;

    public FriendsFragment() { }

    public FriendsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriends = view.findViewById(R.id.rvFriends);
        fabSearch = view.findViewById(R.id.fabSearchFriends);



        fabSearch.setOnClickListener(fabSearchClicked);

        getFriends();
    }

    private View.OnClickListener fabSearchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    new FriendsSearchFragment()).commit();
        }
    };

    private void getFriends() {

    }
}