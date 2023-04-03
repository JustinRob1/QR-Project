package com.example.qr_project.utils;

import com.example.qr_project.models.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

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

    /**
     * Attempts to update a document with the given updates (key value pairs to be stored in the
     * document)
     * @param collectionName
     * @param documentId
     * @param updates
     * @param successListener
     * @param failureListener
     */
    public void updateDocument(String collectionName,
                               String documentId,
                               Map<String, Object> updates,
                               OnSuccessListener<Void> successListener,
                               OnFailureListener failureListener) {
        firebaseFirestore.collection(collectionName)
                .document(documentId)
                .update(updates)
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

    @Override
    public void appendMapToArrayField(String collectionName, String documentId,
                                      String arrayFieldName, Map<String, Object> mapObject,
                                      OnSuccessListener<Void> successListener,
                                      OnFailureListener failureListener) {
        firebaseFirestore.collection(collectionName)
                .document(documentId)
                .update(arrayFieldName, FieldValue.arrayUnion(mapObject))
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    @Override
    public void removeMapFromArrayField(String collectionName, String documentId,
                                        String arrayFieldName, Map<String, Object> mapObject,
                                        OnSuccessListener<Void> successListener,
                                        OnFailureListener failureListener) {
        firebaseFirestore.collection(collectionName)
                .document(documentId)
                .update(arrayFieldName, FieldValue.arrayRemove(mapObject))
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    @Override
    public void getCollection(String collectionName,
                              OnSuccessListener<QuerySnapshot> successListener,
                              OnFailureListener failureListener) {
        firebaseFirestore.collection(collectionName)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    @Override
    public void setDocumentSnapshotListener(String collectionName,
                                            String documentId,
                                            EventListener<DocumentSnapshot> eventListener) {
        firebaseFirestore.collection(collectionName)
                .document(documentId)
                .addSnapshotListener(eventListener);
    }

    @Override
    public void setCollectionSnapshotListener(String collectionName,
                                              EventListener<QuerySnapshot> eventListener) {
        firebaseFirestore.collection(collectionName)
                .addSnapshotListener(eventListener);
    }
}

