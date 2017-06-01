package com.example.hatic.venus;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.io.IOException;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG="MyFirebaseInsIDService";
    @Override
    public void onTokenRefresh(){
        String token = FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
        Log.d("TOKEN Verildi", token);
    }

    private void registerToken(String token){

    }}