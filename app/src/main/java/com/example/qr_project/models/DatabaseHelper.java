package com.example.qr_project.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface DatabaseHelper {
    void getDocument(String collectionName, String documentId,
                     OnSuccessListener<DocumentSnapshot> successListener,
                     OnFailureListener failureListener);

    void getAllDocuments(String collectionName,
                         OnCompleteListener<QuerySnapshot> completeListener);


    void getAllDocumentsOrdered(String collectionName, String orderBy, boolean ascending,
                               OnCompleteListener<QuerySnapshot> completeListener);
}

