package com.ducku.chatapplication.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static DocumentReference getDocumentReference(String collectionPath, String documentId) {
        return FirebaseFirestore.getInstance().collection(collectionPath).document(documentId);
    }

    public static DocumentReference currentUserDetails() {
        String collectionPath = "users";
        String documentId = currentUserId();
        return getDocumentReference(collectionPath, documentId);
    }

    public static CollectionReference allUsersCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        String collectionPath = "chatrooms";
        return getDocumentReference(collectionPath, chatroomId);
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + '_' + userId2;
        } else
            return userId2 + '_' + userId1;
    }
}
