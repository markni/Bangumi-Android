package io.github.scarletsky.bangumi.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.api.BangumiApi;
import io.github.scarletsky.bangumi.utils.ToastManager;

/**
 * Created by scarlex on 15-7-16.
 */
public class WebFragment extends BaseToolbarFragment {

    private WebView mWebview;
    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWebview =  (WebView) getView().findViewById(R.id.webview);
        mWebview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_dark));

        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        mProgress = (ProgressBar) getView().findViewById(R.id.progress);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                ToastManager.show(getActivity(), description);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                Log.d("Progress-----", Integer.toString(newProgress));
                if (newProgress == 100){
                    ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", newProgress);
                    Animator.AnimatorListener animatorListener
                            = new Animator.AnimatorListener() {
                        public void onAnimationEnd(Animator animation) {
                            mProgress.setVisibility(View.GONE);
                        }
                        public void onAnimationCancel(Animator animation) {
                            Log.d("Canceled-----", Integer.toString(newProgress));
                        }
                        public void onAnimationStart(Animator animation) {
                        }
                        public void onAnimationRepeat(Animator animation) {
                        }
                    };
                    animation.addListener(animatorListener);
                    animation.setDuration(500); // 0.5 second
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }
                else {
                    mProgress.setProgress(newProgress);
                    if (mProgress.getVisibility() == View.GONE) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        String subjectUrl = getActivity().getIntent().getStringExtra("subjectUrl");
        if (subjectUrl != null) {
            mWebview.loadUrl(subjectUrl);
        } else {
            mWebview.loadUrl(BangumiApi.URL_RAKUEN);
        }


}

    @Override
    protected void setToolbarTitle() {
        getToolbar().setTitle(getString(R.string.title_rakuen));
    }


}
