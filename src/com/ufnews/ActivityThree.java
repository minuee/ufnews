package com.ufnews;

import android.net.Uri;
import android.annotation.SuppressLint;
import android.app.*;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.provider.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.webkit.*;
import android.webkit.WebSettings.*;
import android.widget.*;

import java.io.*;
import java.util.*;

import com.kakao.*;
import com.kakao.kakaolink.*;
import com.kakao.util.*;
import com.ufnews.*;
import com.ufnews.MainActivity.*;

import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.*;
import android.graphics.*;
import android.telephony.TelephonyManager;
 

@SuppressLint("SetJavaScriptEnabled")
public class ActivityThree extends Activity {
 
	WebView webView;
	
    MyJSInterface myJSInterface; 
   
    private BackPressCloseHandler backPressCloseHandler;
    
    private final String imageSrc = "이미지URL";   
    
    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    
    String loadxurl;
       
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_two);
        Log.i("onCreate", "onCreate실행됨�");
        
        Intent intent = getIntent(); 
        loadxurl = intent.getStringExtra("reurl");
        
        backPressCloseHandler = new BackPressCloseHandler(this);
        
        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        //webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);        
        webSettings.setPluginsEnabled(true);
        // 4.0이상에서
        
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDefaultTextEncodingName("UTF-8");     
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        //webSettings.setDefaultFontSize(15);
        webView.loadUrl(loadxurl);
       
        webView.setWebChromeClient(new MyChromeClient());
        myJSInterface = new MyJSInterface();
        webView.addJavascriptInterface(myJSInterface, "myJSInterface");
        
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                        String contentDisposition, String mimetype,
                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        
        webView.setWebViewClient(new WebViewClient(){
        	public static final String INTENT_PROTOCOL_START = "intent:";
    		public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    		public static final String INTENT_PROTOCOL_END = ";end;";
    		public static final String INTENT_PHONE = "tel:";
    		public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
        	
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		
        		/*
    			 * android.os.Build.VERSION.SDK_INT >= 19 안드로이드 4.4 이상인 경우
    			 */
    			if (android.os.Build.VERSION.SDK_INT >= 19) {
    				if (url.startsWith(INTENT_PROTOCOL_START)) {
    					
    					final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
    					final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
    					if (customUrlEndIndex < 0) {
    						return false;
    					} else {
    						final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
    						Intent intent = new Intent(Intent.ACTION_VIEW);
    						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    						try {
    							intent.setData(Uri.parse(customUrl));
    							getBaseContext().startActivity(intent);
    						} catch (ActivityNotFoundException e) {
    							final int packageStartIndex = customUrlEndIndex+ INTENT_PROTOCOL_INTENT.length();
    							final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);
     
    							final String packageName = url.substring(packageStartIndex,	packageEndIndex < 0 ? url.length()	: packageEndIndex);
    							intent.setData(Uri.parse(GOOGLE_PLAY_STORE_PREFIX	+ packageName));
    							getBaseContext().startActivity( intent );
    						}
    						return true;
    					}
    				}else if (url.startsWith(INTENT_PHONE)) {
    					Intent dial = new Intent(Intent.ACTION_VIEW,Uri.parse(url));                        
                        startActivity(dial); 
                        return true;
    				} else {
    					return false;
    				}
    			} else {
    				if (url.startsWith("intent:") || url.startsWith("kakaolink:") || url.startsWith("market:")) {
    					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
    					startActivity(intent);
    				}else if (url.startsWith(INTENT_PHONE)) {
    					Intent dial = new Intent(Intent.ACTION_VIEW,Uri.parse(url));                        
                        startActivity(dial); 
                        return true;
    				} else {
    					view.loadUrl(url);
    				}
    				return super.shouldOverrideUrlLoading(view, url);
    			}
    		}
        	
        });
   	}
	
	public void loadkakao(String url){
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	intent.putExtra(Browser.EXTRA_APPLICATION_ID,getPackageName());
    	startActivity(intent);
    }
	 
    class MyChromeClient extends WebChromeClient {
	  @Override
	  public boolean onJsAlert(WebView view, String url, String message,final JsResult result) {

	    new AlertDialog.Builder(ActivityThree.this)
	       .setTitle("도시미래 알림!")
	       .setMessage(message)
	       .setPositiveButton(android.R.string.ok,
	           new AlertDialog.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) {
	                     result.confirm();
	               }
	           }).create().show();
	   return true;
	  }
	  
	// openFileChooser for Android 3.0+
      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
          // Update message
          mUploadMessage = uploadMsg;
          try {
            // Camera capture image intent
              final Intent captureIntent = new Intent(
                      MediaStore.ACTION_IMAGE_CAPTURE);
              captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
              Intent i = new Intent(Intent.ACTION_GET_CONTENT);
              i.addCategory(Intent.CATEGORY_OPENABLE);
              i.setType("image/*");
              // Create file chooser intent
              Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
              // Set camera intent to file chooser
              chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                      , new Parcelable[]{captureIntent});

              // On select image call onActivityResult method of activity
              startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

          } catch (Exception e) {
              Toast.makeText(getBaseContext(), "Exception:" + e,
                      Toast.LENGTH_LONG).show();
          }
      }
      // openFileChooser for Android < 3.0
      public void openFileChooser(ValueCallback<Uri> uploadMsg) {
          openFileChooser(uploadMsg, "");
      }
      //openFileChooser for other Android versions
      public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType,String capture) {
          openFileChooser(uploadMsg, acceptType);
      }
      public boolean onConsoleMessage(ConsoleMessage cm) {

          onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
          return true;
      }

      public void onConsoleMessage(String message, int lineNumber, String sourceID) {
          //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

      }
    }
    
    private void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = getCacheDir();
        else;
        if(dir==null)
            return;
        else;
        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearApplicationCache(null);
    } 
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if(requestCode==FILECHOOSER_RESULTCODE){
            if (null == this.mUploadMessage) {
                return;
            }
            Uri result=null;
            try{
                if (resultCode != RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    
   
    
    public class MyJSInterface{
    	MyJSInterface(){}
    	
    	@JavascriptInterface
    	void showLeftImage_three(){
    		webView.loadUrl("javascript:showLeftImage()");
    	}
    	void showRightImage_three(){
    		webView.loadUrl("javascript:showRightImage()");
    	}
    	@JavascriptInterface
    	public void vibration_three(){
	      Vibrator vib = (Vibrator)ActivityThree.this.getSystemService(Context.VIBRATOR_SERVICE);
	      vib.vibrate(500);
	      //vib.vibrate(new long[]{1000,1000,1000}, 0);
	    }
    	     	
     	@JavascriptInterface
     	public void app_exit_three(){
     		finish();
     	}
     	
     	@JavascriptInterface
     	public void app_exit(){
     		finish();
     	}
     	@JavascriptInterface
     	public void app_update_three(){
     		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ufnews"));
     		startActivity(intent);
     	}
     	@JavascriptInterface
     	public boolean win_cls_three(){
     		webView.goBack();
            return true;
     	}
     	
     	@JavascriptInterface
    	public void share_data(String murl,String mtext){
    		
    		Intent sendIntent = new Intent();
    		sendIntent.setAction(Intent.ACTION_SEND);
    		sendIntent.putExtra(Intent.EXTRA_SUBJECT, mtext);
    		sendIntent.putExtra(Intent.EXTRA_TEXT, murl);
    		sendIntent.setType("text/plain");
    		startActivity(Intent.createChooser(sendIntent, "데이타 공유"));
    	}
     	
     	@JavascriptInterface
     	public void alert_toast_three(String alertstr ){     		
     		backPressCloseHandler.onwebalert(alertstr );
     	}
     	
     	@JavascriptInterface
     	public void outbrowser( String xstr){
     		String sturl = "http://m.ufnews.co.kr";
     		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(xstr));
     		startActivity(intent);
     	}
     	
     	@JavascriptInterface
     	public void callufnews(){
     		Intent startxLink = getPackageManager().getLaunchIntentForPackage("com.urban114");
     		if ( startxLink != null ) {
     			PackageManager pm = getPackageManager();
     			Intent i = pm.getLaunchIntentForPackage("com.urban114");
     			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     			startActivity(i);
     		}else{
     			Intent sintent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.urban114"));
         		startActivity(sintent);
     		}
     	} 	 
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode==KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
        	webView.goBack();
            return true; 
        }
        return super.onKeyDown(keyCode,event);
    }
    
    @Override
    public void onBackPressed(){
    	
    	finish();
    }
}
