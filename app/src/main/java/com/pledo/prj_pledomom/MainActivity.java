package com.pledo.prj_pledomom;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;


public class MainActivity extends FragmentActivity {
    private MnWebVw mMnWebVw;
    //private BackPressCloseHandler backPressCloseHandler;
    private Handler handler = new Handler();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /** webview에서 input file 태그에 대응하여 사진 등록 기능 처리를 위한 변수들 **/
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private final static int FILECHOOSER_RESULTCODE = 10;
    public static final int INPUT_FILE_REQUEST_CODE = 20;

	private WebView t_webVw;
    private Button btnVideo;
    private Button btnGame;

	/***/

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            Log.d(TAG, "getInstanceIdToken 0" );

            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        Log.d(TAG, "registBroadcastReceiver 0" );

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "registBroadcastReceiver.onReceive" );

            if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                Log.d(TAG, "registBroadcastReceiver.REGISTRATION_READY" );

                // 액션이 READY일 경우
//                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                    mInformationTextView.setVisibility(View.GONE);
            } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                Log.d(TAG, "registBroadcastReceiver.REGISTRATION_GENERATING" );

                // 액션이 GENERATING일 경우
//                    mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
//                    mInformationTextView.setVisibility(View.VISIBLE);
//                    mInformationTextView.setText(getString(R.string.registering_message_generating));
            } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                String t_token = intent.getExtras().getString("token");
                String[]  t_arrParam    = new String[] {t_token};
                Log.d(TAG, "registBroadcastReceiver.REGISTRATION_COMPLETE  t_arrParam: " + t_arrParam + "  t_token: " + t_token);
                reqJavascript("callSendPushToken", t_arrParam );

                // 액션이 COMPLETE일 경우
//                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                    mRegistrationButton.setText(getString(R.string.registering_message_complete));
//                    mRegistrationButton.setEnabled(false);
//                    String token = intent.getStringExtra("token");
//                    mInformationTextView.setText(token);
            }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnVideo = (Button)findViewById(R.id.btn_video);
        btnGame = (Button)findViewById(R.id.btn_game);

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
        btnVideo.setVisibility(View.INVISIBLE);

        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(MainActivity.this, "com.example.ulnamsong.testanotherapp");
            }
        });
        btnGame.setVisibility(View.INVISIBLE);


        LayoutInflater  mInflater	=  (LayoutInflater) getLayoutInflater();
        Log.d(TAG, "onCreateView 0  mInflater: " + mInflater );

	    t_webVw = (WebView)findViewById(R.id.popwebview);

        mMnWebVw    = new MnWebVw();
        mMnWebVw.createView(this, t_webVw);

        //CookieSyncManager.createInstance(this);
        //CookieSyncManager.getInstance().sync();
        //CookieSyncManager.getInstance().startSync();

        //backPressCloseHandler = new BackPressCloseHandler(this);

        //  GCM control.
        registBroadcastReceiver();

//        // 토큰을 보여줄 TextView를 정의
//        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
//        mInformationTextView.setVisibility(View.GONE);
//        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
//        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
//        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//        // 토큰을 가져오는 Button을 정의
//        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
//        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
//            /**
//             * 버튼을 클릭하면 토큰을 가져오는 getInstanceIdToken() 메소드를 실행한다.
//             * @param view
//             */
//            @Override
//            public void onClick(View view) {
//                getInstanceIdToken();
//            }
//        });
    }



    // Start New Activity
    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onWindowFocusChanged(boolean hasFocus){
        Log.d(TAG, "onWindowFocusChanged 0");

//        mMnWebVw.gotoMainPage();

        getInstanceIdToken();
    }

    /*private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

//        @Override
//        public void onPageFinished(WebView view, String url) {
//        	Log.d(TAG, "onPageFinished 0   url: " + url );
//
//
//
////            if(url.endsWith(".mp4")) {
////                Intent i = new Intent(Intent.ACTION_VIEW);
////                Uri uri = Uri.parse(url);
////                i.setDataAndType(uri, "video/mp4");
////                startActivity(i);
////            }
//
//            String	tmpUrl	= "file:///android_asset/video.mp4";
//
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.parse(tmpUrl);
//            i.setDataAndType(uri, "video*//*");
//            startActivity(i);
//
//            super.onPageFinished(view, url);
//        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart 0");
        //getInstanceIdToken();     //  fragment 준비 안 됨.
    }

    @Override
    protected void onStop() {
        super.onStop();
        //CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        CookieSyncManager.getInstance().startSync();

        //CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //cookieManager.setAcceptCookie(false);
        //cookieManager.removeSessionCookie();
        //cookieSyncManager.sync();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume 0");
        super.onResume();
        //CookieSyncManager.getInstance().startSync();

        Log.d(TAG, "onResume 1");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        mMnWebVw.onResume();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        CookieSyncManager.getInstance().stopSync();
//    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed 0");
        reqJavascript("callPressedBackKey", null );



        //backPressCloseHandler.onBackPressed();





    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause 0" );
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        //CookieSyncManager.getInstance().stopSync();

        mMnWebVw.onPause();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode, resultCode  "+ requestCode +','+ resultCode +','+ data);
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){ // -1
            /*
            if(requestCode == 0){ // login 에서 호출한 경우
	            t_webVw.loadUrl("javascript:socialLoginCallBack()");
            }
            else if(requestCode == 1){ // join 에서 호출한 경우
	            t_webVw.loadUrl("javascript:socialJoinCallBack()");
            }
            */

            t_webVw.loadUrl("javascript:socialLoginCallBack()");
        }

	    if(requestCode == INPUT_FILE_REQUEST_CODE){ // 사진 선택일 때
		    Uri[] results = null;

		    if(resultCode == Activity.RESULT_OK) { // -1
			    if (data == null) {
				    // If there is not data, then we may have taken a photo
				    if (mMnWebVw.getmCameraPhotoPath() != null) {
					    results = new Uri[]{Uri.parse(mMnWebVw.getmCameraPhotoPath())};
				    }
			    } else {
				    String dataString = data.getDataString();
				    if (dataString != null) {
					    results = new Uri[]{Uri.parse(dataString)};
				    }
			    }
		    }

		    mFilePathCallback = mMnWebVw.getmFilePathCallback();
		    Log.d(TAG, "mFilePathCallback" + mFilePathCallback);
		    mFilePathCallback.onReceiveValue(results);
		    mMnWebVw.setmFilePathCallback(null);
	    }
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void reqJavascript(String s_fnNm, String[] s_arrParam ){
        Log.d(TAG, "reqJavascript 0  s_fnNm: " + s_fnNm);
        mMnWebVw.callJavascript(s_fnNm, s_arrParam);
    }


    /**
     * 앱 캐시를 가차없이 지운다.
     */
    public static void clearApplicationCache(Context context, File file) {

//        File dir = null;
//
//        if (file == null) {
//            dir = context.getCacheDir();
//        } else {
//            dir = file;
//        }
//
//        if (dir == null)
//            return;
//
//        File[] children = dir.listFiles();
//        try {
//            for (int i = 0; i < children.length; i++)
//                if (children[i].isDirectory())
//                    Util.clearApplicationCache(context, children[i]);
//                else
//                    children[i].delete();
//        } catch (Exception e) {
//        }
    };
}
