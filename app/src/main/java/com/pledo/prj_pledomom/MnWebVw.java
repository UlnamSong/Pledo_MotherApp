package com.pledo.prj_pledomom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
//import android.widget.WebView;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kakao.auth.Session;

import java.io.File;
import java.io.IOException;
import java.net.CookieStore;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MnWebVw {
    private final String TAG = "MnWebVw";

    private int testInt = 3;
    private WebView	mWebVw;
    public static Context mContext;
    private Activity mListener;
    private Handler handler;

	/** webview에서 input file 태그에 대응하여 사진 등록 기능 처리를 위한 변수들 **/
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mFilePathCallback;

	private final static int FILECHOOSER_RESULTCODE = 10;
	public static final int INPUT_FILE_REQUEST_CODE = 20;

	private String mCameraPhotoPath;
	/***/

	private String setWebViewGcmTokenJavascript; // WebView에 gcmToken을 저장하는데 사용할 javascript
	private boolean isSetWebViewGcmToken = false; // WebView 에 gcmToken을 보냈는지 저장

    private BackPressCloseHandler backPressCloseHandler;



	public String getmCameraPhotoPath() {
		return mCameraPhotoPath;
	}
	public ValueCallback<Uri[]> getmFilePathCallback() {
		return mFilePathCallback;
	}
	public void setmFilePathCallback(ValueCallback<Uri[]> mFilePathCallback) {
		this.mFilePathCallback = mFilePathCallback;
	}

	public void createView( Context context, WebView s_webVw ){


        mListener = (Activity) context;


        int appVersionCode   = 0;
        String appVersion   = "";
        try {
            appVersionCode  = mListener.getPackageManager().getPackageInfo(mListener.getPackageName(), 0).versionCode;
            appVersion = mListener.getPackageManager().getPackageInfo(mListener.getPackageName(), 0).versionName;
        }catch(PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        String t_version    = appVersionCode + "." + appVersion;
        Log.d(TAG, "createView  0  version: " + appVersionCode + "  " + appVersion + "  " + t_version );



        //	WebView for hint.
        mWebVw = s_webVw;
        // 웹뷰에서 자바스크립트실행가능
        mWebVw.getSettings().setJavaScriptEnabled(true);
        mWebVw.getSettings().setBuiltInZoomControls(true);
        mWebVw.getSettings().setSupportMultipleWindows(false);
        mWebVw.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebVw.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebVw.getSettings().setDomStorageEnabled(true);
        mWebVw.getSettings().setDatabaseEnabled(true);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
            mWebVw.setWebContentsDebuggingEnabled(true); // remote debugging
        }

        mWebVw.clearCache(true);
        mWebVw.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebVw.getSettings().setDatabasePath("/data/data/" + mWebVw.getContext().getPackageName() + "/databases/");
        }

        mWebVw.setWebViewClient(new MyWebViewClient());
        mWebVw.setWebChromeClient(new WebChromeClient(){
	        public boolean onShowFileChooser(
			        WebView webView, ValueCallback<Uri[]> filePathCallback,
			        WebChromeClient.FileChooserParams fileChooserParams) {
		        Log.d(TAG, "onShowFileChooser");

		        if(mFilePathCallback != null) {
			        mFilePathCallback.onReceiveValue(null);
		        }
		        mFilePathCallback = filePathCallback;

		        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        if (takePictureIntent.resolveActivity(mListener.getPackageManager()) != null) {
			        Log.d(TAG, "MediaStore.ACTION_IMAGE_CAPTURE");

			        // Create the File where the photo should go
			        File photoFile = null;
			        try {
				        Log.d(TAG, "MediaStore.ACTION_IMAGE_CAPTURE  createImageFile");
				        photoFile = createImageFile();
				        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
			        } catch (IOException ex) {
				        // Error occurred while creating the File
				        Log.e(TAG, "Unable to create Image File", ex);
			        }

			        // Continue only if the File was successfully created
			        if (photoFile != null) {
				        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
				        Log.d(TAG, "mCameraPhotoPath"+ mCameraPhotoPath);
				        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			        } else {
				        takePictureIntent = null;
			        }
		        }

		        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		        contentSelectionIntent.setType("image/*");

		        Intent[] intentArray;
		        if(takePictureIntent != null) {
			        intentArray = new Intent[]{takePictureIntent};
		        } else {
			        intentArray = new Intent[0];
		        }

		        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
		        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

		        mListener.startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

		        return true;
	        }
	    });

        mWebVw.addJavascriptInterface(new AndroidBridge(), "android");

        mWebVw.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        mContext = mListener.getBaseContext();

        handler = new Handler();

        backPressCloseHandler = new BackPressCloseHandler(mListener);


        getHash();

		gotoMainPage();
    }

	/**
	 * Webview의 input file 태그에 대응하는 사진 등록용 함수
	 *
	 * More info this method can be found at
	 * http://developer.android.com/training/camera/photobasics.html
	 *
	 * @return
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);
		return imageFile;
	}

    public void gotoMainPage()
    {

        String url = mContext.getResources().getString(R.string.loadUrl);
        Log.d(TAG, "gotoMainPage  0  mWebVw: " + mWebVw + "  testInt: " + testInt );
        //mWebVw.loadUrl("http://codeartist.co.kr/PledoMotherApp/appnew/main/main.html"); //  2016. 8.18 이전
        //mWebVw.loadUrl("http://103.60.126.195/appnew/login/login.html");    //  2016. 8.19 이후

        //mWebVw.loadUrl("http://121.128.220.59/appnew/main/main.html");    //  2016. 8.19 : develop version

        //mWebVw.loadUrl("http://103.60.126.195/appnew/main/main.html");    //  2016. 8.19 : test version
        //mWebVw.loadUrl("http://103.60.126.195/appnew/login/login.html");    //  2016. 8.23 : test version
        //mWebVw.loadUrl("http://103.60.126.195/appnew/login/login_type.html");    //  2016. 8.24 : test version

        mWebVw.loadUrl(url);

    }

    private void getHash() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    public void onWindowFocusChanged(boolean hasFocus){
        Log.d(TAG, "onWindowFocusChanged  0  mWebVw: " + mWebVw + "  testInt: " + testInt );
    }

    public int getVisibility(){
        return	mWebVw.getVisibility();
    }

    public void setVisibility( int s_value ){
        mWebVw.setVisibility(s_value);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

	    // WebView 페이지 로딩 완료 시
	    @Override
	    public void onPageFinished(WebView view, String url) {
		    Log.d("WebView", "onPageFinished " + url);
            CookieSyncManager.getInstance().startSync();
            CookieSyncManager.getInstance().sync();

		    if(!isSetWebViewGcmToken){ // WebView에 gcmToken을 한번만 보냄
			    Log.d("WebView", "setWebViewGcmTokenJavascript " + setWebViewGcmTokenJavascript);
			    view.loadUrl(setWebViewGcmTokenJavascript);
			    isSetWebViewGcmToken = true;
		    }
	    }
    }


    class AndroidBridge {
        @JavascriptInterface
        public void callVideoPlay(final String s_url ){
            Log.d("AndroidBridge", "callVideoPlay: " 	+ s_url);

            mListener.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mVdPlayVw.setVisibility(View.VISIBLE);
//                    mVdPlayVw.playVideo(s_url);
                }
            });
        }

        @JavascriptInterface
        public void kakaoLogin() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "kakaoLogin: ");
                    Intent intent = new Intent(mContext.getApplicationContext(), KaKaoLoginActivity.class);
                    mListener.startActivityForResult(intent, 0);
                }
            });
        }

        @JavascriptInterface
        public void kakaoJoin() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "kakaoJoin: ");
                    Intent intent = new Intent(mContext.getApplicationContext() ,KaKaoLoginActivity.class);
                    mListener.startActivityForResult(intent, 1);
                }
            });
        }

        @JavascriptInterface
        public void facebookLogin() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "facebookLogin: ");
                    Intent intent = new Intent(mContext.getApplicationContext() ,FacebookLoginActivity.class);
//					startActivity(intent);
                    mListener.startActivityForResult(intent, 0);
                }
            });
        }

        @JavascriptInterface
        public void facebookJoin() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "facebookJoin: ");
                    Intent intent = new Intent(mContext.getApplicationContext() ,FacebookLoginActivity.class);
//					startActivity(intent);
                    mListener.startActivityForResult(intent, 1); // 1로 join 에서 호출한 것임을 표시

                }
            });
        }

        //  현재 version 정보.
        @JavascriptInterface
        public void callCurrentAppVersion( ) {
            handler.post(new Runnable() {
                public void run() {

                    int appVersionCode   = 0;
                    String appVersion   = "";
                    appVersionCode  = BuildConfig.VERSION_CODE;
                    appVersion = BuildConfig.VERSION_NAME;
                    /*
                    try {
                        appVersionCode  = mListener.getPackageManager().getPackageInfo(mListener.getPackageName(), 0).versionCode;
                        appVersion = mListener.getPackageManager().getPackageInfo(mListener.getPackageName(), 0).versionName;
                    }catch(PackageManager.NameNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    */
                    String t_version    = appVersionCode + "." + appVersion;
                    Log.d("AndroidBridge", "callCurrentAppVersion  0  version: " + appVersionCode + "  " + appVersion + "  " + t_version );

                    String[]  t_arrParam    = new String[] {t_version};
                    callJavascript("callCurrentAppVersionCallback", t_arrParam );
                }
            });
        }

        //  back key 제어.
        @JavascriptInterface
        public void callPressedBackKeyDone(final String s_isValid ) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "callPressedBackKeyDone   s_isValid: " + s_isValid + "  equal: " + s_isValid.equals("true") );
                    if( s_isValid.equals("true") ) {
//                        Intent intent = new Intent(mContext.getApplicationContext(), FacebookLoginActivity.class);
//                        //					startActivity(intent);
//                        mListener.startActivityForResult(intent, 0);

                        backPressCloseHandler.onBackPressed();


//                        String  s_url   =  "https://www.youtube.com/embed/tKW6louhuSY";
//                        String[] t_arrtoken = s_url.split("/");
//                        Intent intent = new Intent(mListener, VideoActivity.class);
//                        intent.putExtra("URL_DATA",  t_arrtoken[t_arrtoken.length-1]);
//                        //intent.putExtra("URL_DATA", "tKW6louhuSY" );
//                        mListener.startActivity(intent);

                    }
//                    backPressCloseHandler.onBackPressed();    //  for test.
                }
            });
        }

        //  프레도TV(유튜브 Video) full play.
        @JavascriptInterface
        public void callVideoFullplay(final String s_url ) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "callVideoFullplay   s_url: " + s_url );



                    Toast   toast = Toast.makeText(mListener,
                            "프레도TV 전체화면  " + s_url, Toast.LENGTH_LONG);
                    toast.show();


                    String[] t_arrtoken = s_url.split("/");
                    Intent intent = new Intent(mListener, VideoActivity.class);
                    intent.putExtra("URL_DATA",  t_arrtoken[t_arrtoken.length-1]);
                    mListener.startActivity(intent);

                }
            });
        }

        //  증강현실게임연동.
        @JavascriptInterface
        public void callLinkContent(final String s_name ) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "callLinkContent   s_name: " + s_name );

                    //(MainActivity)mListener.startNewActivity(mListener, s_name);


                    Toast   toast = Toast.makeText(mListener,
                            "증강현실게임 실행: " + s_name, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        //  프레도 로봇조종..
        @JavascriptInterface
        public void callRobotControlView() {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("AndroidBridge", "callRobotControlView" );



                    Toast   toast = Toast.makeText(mListener,
                            "로봇 조종", Toast.LENGTH_SHORT);
                    toast.show();



                }
            });
        }










//        @JavascriptInterface
//        public void setAnswer(final String answer,
//                              final int width,
//                              final int height,
//                              final int getTop,
//                              final int getLeft) {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
////	  				rightAnswer = answer;
//                    Log.d("AndroidBridge", "answer: " 	+ answer);
//                    Log.d("AndroidBridge", "width : " + width);
//                    Log.d("AndroidBridge", "height : " + height);
//                    Log.d("AndroidBridge", "getTop : " 	+ getTop);
//                    Log.d("AndroidBridge", "getLeft : " + getLeft);
//
////	  				coordinateX = getLeft;
////	  				coordinateY = getTop;
//
//                }
//            });
//        }
    }


    public void onPause()
    {
//        super.onPause();
        Log.d(TAG, "onPause 0  mWebVw: " + mWebVw);

        try {
//			Class.forName("android.webkit.WebView")
//					.getMethod("onPause", (Class[]) null)
//					.invoke(mWebVw, (Object[]) null);

//			mWebVw.class.getMethod("onPause").invoke(this);//stop flash

            mWebVw.onPause();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void onResume()
    {
        Log.d(TAG, "onResume 0  mWebVw: " + mWebVw );
        //super.onResume();
        Log.d(TAG, "onResume 1  mWebVw: " + mWebVw);

        try {
//			Class.forName("android.webkit.WebView")
//					.getMethod("onResume", (Class[]) null)
//					.invoke(mWebVw, (Object[]) null);

        mWebVw.onResume();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void callJavascript( String s_fnNm, String[] s_arrParam ){
        Log.d(TAG, "callJavascript 0  s_fnNm: " + s_fnNm + "  s_arrParam: " + s_arrParam + "  testInt: " + testInt  + "  mWebVw: " + mWebVw );
        Log.d(TAG, "callJavascript 1  mWebVw: " + mWebVw);

        String	t_str	= "";

        if( s_arrParam != null ) {
            int m;
            for (m = 0; m < s_arrParam.length - 1; m++) {
                t_str += s_arrParam[m];
                t_str += ",";
            }
            if (s_arrParam.length > 0) {
                t_str += s_arrParam[s_arrParam.length - 1];
            }
        }

        String	t_fullNm	= "javascript:" + s_fnNm + "('" + t_str + "')";
	    Log.d(TAG, "callJavascript  1  t_fullNm: " + t_fullNm );

	    // WebView에 gcmToken을 보낼 때만 따로 저장해서 차후에 호출함
	    if(s_fnNm == "callSendPushToken"){
		    setWebViewGcmTokenJavascript = t_fullNm;
	    }else{
		    mWebVw.loadUrl(t_fullNm);
	    }


    }

    public void testFunc(){
        Log.d(TAG, "testFunc  1  testInt: " + testInt + "  mWebVw: " + mWebVw );
        testInt = 7;

    }

}
