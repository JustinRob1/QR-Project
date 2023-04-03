package com.example.qr_project.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public interface DatabaseHelper {
    void getDocument(String collectionName, String documentId,
                     OnSuccessListener<DocumentSnapshot> successListener,
                     OnFailureListener failureListener);

    void getAllDocuments(String collectionName,
                         OnCompleteListener<QuerySnapshot> completeListener);


    void getAllDocumentsOrdered(String collectionName, String orderBy, boolean ascending,
                               OnCompleteListener<QuerySnapshot> completeListener);

    void appendMapToArrayField(String collectionName, String documentId,
                               String arrayFieldName, Map<String, Object> mapObject,
                               OnSuccessListener<Void> successListener,
                               OnFailureListener failureListener);

    void removeMapFromArrayField(String collectionName, String documentId,
                                 String arrayFieldName, Map<String, Object> mapObject,
                                 OnSuccessListener<Void> successListener,
                                 OnFailureListener failureListener);

    void getCollection(String collectionName,
                       OnSuccessListener<QuerySnapshot> successListener,
                       OnFailureListener failureListener);

    void setDocumentSnapshotListener(String collectionName, String documentId,
                                     EventListener<DocumentSnapshot> eventListener);

    void setCollectionSnapshotListener(String collectionName,
                                       EventListener<QuerySnapshot> eventListener);
}

