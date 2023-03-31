package com.example.qr_project.utils;

import com.example.qr_project.models.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreDBHelper implements DatabaseHelper {

    private FirebaseFirestore firebaseFirestore;

    public FirestoreDBHelper() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void getDocument(String collectionName, String documentId,
                            OnSuccessListener<DocumentSnapshot> successListener,
                            OnFailureListener failureListener) {
        firebaseFirestore.collection(collectionName)
                .document(documentId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getAllDocuments(String collectionName,
                                OnCompleteListener<QuerySnapshot> completeListener) {
        firebaseFirestore.collection(collectionName)
                .get()
                .addOnCompleteListener(completeListener);
    }

    public void getAllDocumentsOrdered(String collectionName, String orderBy, boolean ascending,
                                       OnCompleteListener<QuerySnapshot> completeListener) {
        firebaseFirestore.collection(collectionName)
                .orderBy(orderBy, ascending ? Query.Direction.ASCENDING : Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(completeListener);
    }

}

