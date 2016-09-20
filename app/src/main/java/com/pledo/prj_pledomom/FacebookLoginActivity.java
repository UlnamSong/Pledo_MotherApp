package com.pledo.prj_pledomom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.GraphJSONObjectCallback;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;


public class FacebookLoginActivity extends Activity {

    private CallbackManager callbackManager;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("facebook test", "ok!");

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(FacebookLoginActivity.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {

                        String serverUrl = getResources().getString(R.string.serverUrl);

                        if (response.getError() != null) {

                        } else {
                            Log.d("facebook", "[requestMe]onSuccess");
                            //String facebookId = "facebookId=" + user.optString("id") + "_facebook";
                            String facebookPw =  "facebookPwd=" + user.optString("id");
                            String auth_result = "facebook_auth_result=Y";
                                    //result.getAccessToken().getToken();
                            //String userId = user.optString("id") + "_facebook";
                            //String faceBookToken =  result.getAccessToken().getToken();
                            CookieManager cookieManager = CookieManager.getInstance();
                            //CookieSyncManager.createInstance(getApplicationContext());
                            cookieManager.setAcceptCookie(true);
                            //cookieManager.setAcceptCookie(false);
                            //cookieManager.setCookie(serverUrl, facebookId);
                            cookieManager.setCookie(serverUrl, facebookPw);
                            cookieManager.setCookie(serverUrl, auth_result);
                            //CookieSyncManager.getInstance().startSync();
                            //CookieSyncManager.getInstance().sync();
                            Log.d("cookie value", cookieManager.getCookie(serverUrl));

//                            webView.loadUrl("javascript:facebookScriptCall(userId, faceBookToken)");

                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                //CookieSyncManager.createInstance(getApplicationContext());
                //CookieSyncManager.getInstance().startSync();
                //CookieSyncManager.getInstance().sync();
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                finish();
            }

            @Override
            public void onCancel(){
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //CookieSyncManager.createInstance(getApplicationContext());
        //CookieSyncManager.getInstance().startSync();
        //CookieSyncManager.getInstance().sync();
    }
}