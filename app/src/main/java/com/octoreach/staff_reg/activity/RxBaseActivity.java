package com.octoreach.staff_reg.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.octoreach.staff_reg.R;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;

public class RxBaseActivity extends RxAppCompatActivity {
    private static Logger _log = LoggerFactory.getLogger(RxBaseActivity.class);

    //    @BindView(R.id.activity_base_Toolbar)
    public static Toolbar toolbar;

    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;
    public boolean isNetworkLink = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.activity_base_Toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    public void setSubtitle(String subTitle) {
        toolbar.setSubtitle(subTitle);
    }

    public void changeProgressDialogMsg(String message) {
        if (progressDialog.isShowing())
            progressDialog.setMessage(message);
    }

    public void showProgressDialog(String message) {
//        progressDialog.setTitle("Dialog");
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    public void switchFragment(Fragment fragment) {
        switchFragment(fragment, null);
    }

    public void switchFragment(Fragment fragment, Bundle bundle) {

        if (null == fragment) return;

        FragmentManager fragmentManager = getFragmentManager();

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        String tag = fragment.getClass().getSimpleName();

        Fragment f = fragmentManager.findFragmentByTag(tag);
        _log.debug("f: {}, Tag: {}", f, tag);
        if (null != f)
            _log.debug("f.isVisible: {}", f.isVisible());

        fragmentManager.beginTransaction()
                .replace(R.id.activity_base_FrameLayout, fragment, tag)
                .commitAllowingStateLoss();
    }

    public void switchFragmentWithBackStack(Fragment fragment) {

        if (null == fragment) return;

        FragmentManager fragmentManager = getFragmentManager();

        String tag = fragment.getClass().getSimpleName();

        Fragment f = fragmentManager.findFragmentByTag(tag);
        _log.debug("f: {}, Tag: {}", f, tag);
        if (null != f)
            _log.debug("f.isVisible: {}", f.isVisible());

        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.activity_base_FrameLayout, fragment, tag)
                .commitAllowingStateLoss();
    }

    public void switchFragmentWithBackStack(Fragment fragment, Bundle bundle) {

        if (null == fragment) return;

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getFragmentManager();

        String tag = fragment.getClass().getSimpleName();

        Fragment f = fragmentManager.findFragmentByTag(tag);
        _log.debug("f: {}, Tag: {}", f, tag);
        if (null != f)
            _log.debug("f.isVisible: {}", f.isVisible());

        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.activity_base_FrameLayout, fragment, tag)
                .commitAllowingStateLoss();
    }

    public void setToolbarNormal() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
    }

}
