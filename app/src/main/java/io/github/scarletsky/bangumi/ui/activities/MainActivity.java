package io.github.scarletsky.bangumi.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.webkit.WebView;

import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.ui.fragments.DrawerFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Transition exitTrans = new Explode();
        getWindow().setExitTransition(exitTrans);


        DrawerFragment mDrawerFragment = new DrawerFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.frame_main, mDrawerFragment).commit();
    }

    @Override
    public void onBackPressed() {
        WebView mWebview = (WebView) this.findViewById(R.id.webview);
        if (mWebview != null && mWebview.canGoBack()){
            mWebview.goBack();
        }
        else if (mFragmentManager.getBackStackEntryCount() != 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }
}
