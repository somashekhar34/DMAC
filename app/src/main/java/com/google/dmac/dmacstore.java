package com.google.dmac;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class dmacstore extends Fragment {
    private WebView webview;
    ProgressBar prog;
    View view;
    public dmacstore(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_dmacstore,container,false);
        prog=(ProgressBar)view.findViewById(R.id.progressdmacstore);
        webview =(WebView) view.findViewById(R.id.webView);
        prog.setMax(100);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://www.amazon.in/");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
       // webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //prog.setProgress(newProgress);
                  prog.setProgress(newProgress*100);
                  if(newProgress==100)
                  {
                      prog.setVisibility(View.INVISIBLE);
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
        return view;
    }
}