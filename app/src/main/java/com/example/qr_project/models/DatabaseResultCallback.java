package com.example.qr_project.models;

public interface DatabaseResultCallback<T>{
    void onSuccess(T result);
    void onFailure(Exception e);
}
