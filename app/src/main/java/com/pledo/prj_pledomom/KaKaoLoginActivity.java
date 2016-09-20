package com.pledo.prj_pledomom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class KaKaoLoginActivity extends Activity {

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        callback = new SessionCallback();
        //현재 새션을 받아온뒤 callback 지정
        Session.getCurrentSession().addCallback(callback);

        //현재 세션의 상태 체크후 Login 진행
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            //이미 열려 있을경우 callback을 호출
            Log.d("SessionOpen", "checkAndImplicitOpen");
        } else {
            //AuthType을 지정하여 원하는 플랫폼(카카오톡, 링크 , 스토리) 계정으로 로그인이 가능하다.
            Session.getCurrentSession().open(AuthType.KAKAO_TALK, this);
        }
        Log.d("Kakao Login OnCreate", "[KakaoLoginActivity]onCreate");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //Kakao SDK를 활용하는 Activity의 경우 setCurrentActivity를 꼭 지정해주어한다.
        GlobalApplication.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("Kakao OnDestory", "[KakaoLoginActivity]onDestroy");
        super.onDestroy();
        //CookieSyncManager.createInstance(getApplicationContext());
        //CookieSyncManager.getInstance().startSync();
        //CookieSyncManager.getInstance().sync();

        Session.getCurrentSession().removeCallback(callback);
        Session.getCurrentSession().close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("kakao", "[KakaoLoginActivity]onActivityResult");

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("kakao", "[KakaoLoginActivity]onActivityResult - handleActivityResult");
            return;
        }
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ISessionCallback을 활용한 Callback 구현
    // Session에서 발생하는 이벤트를 핸들링 하기위해 구현.
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.d("kakao", "[SessionCallback]onSessionOpened");
            //Session Opened 이후 바로 requestMe 호출.
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("kakao", "[SessionCallback]onSessionOpenFailed");
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d("kakao", "[requestMe]onFailure");

                String message = "failed to get user info. msg=" + errorResult;

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    Log.d("kakao", "[requestMe]ErrorCode.CLIENT_ERROR_CODE : " + message);
                } else {
                    Log.d("kakao", "[requestMe]Error : " + message);
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("kakao", "[requestMe]onSessionClosed");
                String auth_result = "kakao_auth_result=N";
                String serverUrl = getResources().getString(R.string.serverUrl);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setCookie(serverUrl, auth_result);
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.d("kakao", "[requestMe]onSuccess");
                String serverUrl = getResources().getString(R.string.serverUrl);
                //String kakaoId = "kakaoId=" + String.valueOf(userProfile.getId()) + "_kakao";
                String kakaoPw = "kakaoPwd=" + String.valueOf(userProfile.getId());
                        //Session.getCurrentSession().getAccessToken();
                String auth_result = "kakao_auth_result=Y";
                //추후 보안 문제에서 해결
                //int nCheckToken = Session.getCurrentSession().hasValidAccessToken() ? 1 : -1;
                //String strCheckToken = "has_valid_access_token=" + nCheckToken;

                CookieManager cookieManager = CookieManager.getInstance();
                //CookieSyncManager.createInstance(getApplicationContext());
                cookieManager.setAcceptCookie(true);
                //cookieManager.setAcceptCookie(false);
                //cookieManager.setCookie(serverUrl, kakaoId);
                cookieManager.setCookie(serverUrl, kakaoPw);
                cookieManager.setCookie(serverUrl, auth_result);
                //cookieManager.setCookie(serverUrl, strCheckToken);

                //CookieSyncManager.getInstance().startSync();
                //CookieSyncManager.getInstance().sync();
                Log.d("cookie value", cookieManager.getCookie(serverUrl));

                setResult(RESULT_OK);

                finish();
            }

            @Override
            public void onNotSignedUp() {
                Log.d("kakao", "[requestMe]onNotSignedUp");
            }


        });
    }
}