package com.ducku.chatapplication;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ducku.chatapplication.adapter.SearchUserRecyclerAdapter;
import com.ducku.chatapplication.databinding.ActivitySearchUserBinding;
import com.ducku.chatapplication.model.UserModel;
import com.ducku.chatapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    ActivitySearchUserBinding activitySearchUserBinding;
    EditText searchInput;
    ImageButton searchBtn;
    ImageButton backBtn;
    RecyclerView searchUserRecyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchUserBinding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(activitySearchUserBinding.getRoot());

        searchInput = activitySearchUserBinding.seachUsernameInput;
        searchBtn = activitySearchUserBinding.searchUserBtn;
        backBtn = activitySearchUserBinding.backBtn;
        searchUserRecyclerView = activitySearchUserBinding.searchUserRecyclerView;

        searchInput.requestFocus();

        backBtn.setOnClickListener(v -> {
            //back to main activity
            finish();
            //onBackPressed();
        });

        searchBtn.setOnClickListener(v -> {
            String input = searchInput.getText().toString();
            if (input.isEmpty() || input.length() < 3) {
                searchInput.setError("Invalid username");
                return;
            }
            setUpSearchRecyclerView(input);
        });

    }

    void setUpSearchRecyclerView(String input) {
        Query query = FirebaseUtils.allUsersCollectionReference()
                .whereGreaterThanOrEqualTo("username", input);
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        searchUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchUserRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}