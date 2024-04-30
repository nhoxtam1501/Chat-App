package com.ducku.chatapplication;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ducku.chatapplication.databinding.ActivityChatBinding;
import com.ducku.chatapplication.model.ChatMessageModel;
import com.ducku.chatapplication.model.ChatroomModel;
import com.ducku.chatapplication.model.UserModel;
import com.ducku.chatapplication.utils.AppUtils;
import com.ducku.chatapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel destUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ActivityChatBinding chatBinding;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView destUsername;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(chatBinding.getRoot());

        destUser = AppUtils.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), destUser.getUserId());
        Log.d("chatroomId", chatroomId);

        messageInput = chatBinding.chatMessageInput;
        sendMessageBtn = chatBinding.messageSendBtn;
        backBtn = chatBinding.backBtn;
        destUsername = chatBinding.otherUsername;
        recyclerView = chatBinding.chatRecyclerView;

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        });

        destUsername.setText(destUser.getUsername());

        getOrCreateChatroomModel();
    }

    private void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimeStamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtils.currentUserId(), Timestamp.now());

        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    messageInput.setText("");
                }
            }
        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtils.currentUserId(), destUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                }
                FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
            }
        });
    }
}