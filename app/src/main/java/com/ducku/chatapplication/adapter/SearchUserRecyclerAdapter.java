package com.ducku.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ducku.chatapplication.ChatActivity;
import com.ducku.chatapplication.R;
import com.ducku.chatapplication.model.UserModel;
import com.ducku.chatapplication.utils.AppUtils;
import com.ducku.chatapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.SearchUserRecyclerViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchUserRecyclerViewHolder searchUserRecyclerViewHolder, int i, @NonNull UserModel userModel) {
        searchUserRecyclerViewHolder.username.setText(userModel.getUsername());
        searchUserRecyclerViewHolder.phone.setText(userModel.getPhone());
        if (userModel.getUserId().equals(FirebaseUtils.currentUserId())) {
            searchUserRecyclerViewHolder.username.setText(userModel.getUsername() + " (Me)");
        }
        searchUserRecyclerViewHolder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppUtils.passUserModelAsIntent(intent, userModel);
            context.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public SearchUserRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new SearchUserRecyclerViewHolder(view);
    }

    static class SearchUserRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView phone;
        ImageView profilePic;

        public SearchUserRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_text);
            phone = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
