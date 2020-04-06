package com.google.dmac;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.google.dmac.R;
import com.google.dmac.Utils;

public class WebViewActivity extends AppCompatActivity {
    Button btnRetry ;
    String barCode = "" ;
    ProgressBar progweb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        progweb = (ProgressBar)findViewById(R.id.progresssearch);
        //progweb.setMax(100);
        btnRetry = findViewById(R.id.btnRetry);
        loadAdd();

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            barCode= intent.getStringExtra("product_id");
            loadWebView(barCode);
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebView(barCode);
            }
        });

    }

    private void loadAdd() {
      //  AdView mAdView = findViewById(R.id.adView);
      //  AdRequest adRequest = new AdRequest.Builder().build();
       // mAdView.loadAd(adRequest);

    }


    private void loadWebView(String barCode) {
        if(Utils.isNetworkAvailable(this)){
            btnRetry.setVisibility(View.INVISIBLE);
            WebView myWebView = findViewById(R.id.google_webview);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                   // progweb.setProgress(newProgress);
                    setTitle("Loading...");
                    progweb.setProgress(newProgress*100);
                    if(newProgress==100){
                        progweb.setVisibility(View.INVISIBLE);
                        setTitle("Product View");
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }

                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    super.onReceivedIcon(view, icon);
                }
            });

            if(Utils.isValidURL(barCode)){
                myWebView.loadUrl(barCode);
            }else{
                myWebView.loadUrl("https://www.google.com/search?q="+barCode);
            }

        }
        else{
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }



    }




}