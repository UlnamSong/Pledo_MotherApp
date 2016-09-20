package com.pledo.prj_pledomom;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by saltfactory on 6/8/15.
 */
public class RemoteIntentService extends IntentService {

    private static final String TAG = "RemoteIntentService";

    public RemoteIntentService() {
        super(TAG);
    }

    /**
     * GCM을 위한 Instance ID의 토큰을 생성하여 가져온다.
     * @param intent
     */
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent 0");

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(QuickstartPreferences.JAVASCRIPT_CALL));

//        // GCM을 위한 Instance ID를 가져온다.
//        InstanceID instanceID = InstanceID.getInstance(this);
//        String token = null;
//        try {
//            synchronized (TAG) {
//                // GCM 앱을 등록하고 획득한 설정파일인 google-services.json을 기반으로 SenderID를 자동으로 가져온다.
//                String default_senderId = getString(R.string.gcm_defaultSenderId);
//                // GCM 기본 scope는 "GCM"이다.
//                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
//                // Instance ID에 해당하는 토큰을 생성하여 가져온다.
//                token = instanceID.getToken(default_senderId, scope, null);
//
//                Log.i(TAG, "GCM Registration Token: " + token);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "onHandleIntent 1");
//
//        // GCM Instance ID에 해당하는 토큰을 획득하면 LocalBoardcast에 COMPLETE 액션을 알린다.
//        // 이때 토큰을 함께 넘겨주어서 UI에 토큰 정보를 활용할 수 있도록 했다.
//        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", token);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
