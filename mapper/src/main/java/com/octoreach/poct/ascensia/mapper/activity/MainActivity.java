package com.octoreach.poct.ascensia.mapper.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.octoreach.poct.ascensia.mapper.R;
import com.octoreach.poct.ascensia.mapper.fragment.LoginCaptureFragment;
import com.octoreach.poct.ascensia.mapper.network.Api;
import com.octoreach.poct.ascensia.mapper.to.VersionTo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;

public class MainActivity extends RxAppCompatActivity {

    private static Logger _log = LoggerFactory.getLogger(MainActivity.class);

    @BindView(R.id.activity_base_Toolbar)
    Toolbar toolbar;

    private ProgressDialog progressDialog;
    private int versionCode;
    private String versionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
            versionName = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            _log.warn(e.getLocalizedMessage(), e);
        }

        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(getString(R.string.app_name) + " " + versionName);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPermissions();
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    public void setSubtitle(String subTitle) {
        toolbar.setSubtitle(subTitle);
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

    protected void getPermissions() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(
                        granted -> {
                            if (granted) {
                                checkRelease();
                            } else {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog
                                        .setMessage(getString(R.string.message_check_permission_error))
                                        .setPositiveButton(getString(R.string.close_application), (dialog1, which) -> finish())
                                        .setCancelable(false)
                                        .show();
                            }
                        });
    }

    protected void checkRelease() {

        showProgressDialog(getString(R.string.message_check_release));
        Api.getDomainApi()
                .getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doFinally(() -> hideProgressDialog())
                .subscribe(
                        to -> {
                            _log.debug("Local V: {}, Release V: {}", versionCode, to.getVersion());
                            if (versionCode < to.getVersion())
                            {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog
                                        .setMessage(getString(R.string.message_release_ready))
                                        .setPositiveButton(getString(R.string.download_and_install), (dialog1, which) -> downloadReleaseApk(to))
                                        .setCancelable(false)
                                        .show();
                            }
                            else
                            {
                                switchFragment(new LoginCaptureFragment());
                            }
                        },
                        throwable -> {
                            _log.error(throwable.getLocalizedMessage(), throwable);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog
                                    .setMessage(
                                            String.format(getString(R.string.message_check_release_error), throwable.getLocalizedMessage()))
                                    .setPositiveButton(getString(R.string.close_application), (dialog1, which) -> finish())
                                    .setCancelable(false)
                                    .show();
                        }
                );

    }

    protected void downloadReleaseApk(VersionTo to) {

        showProgressDialog(getString(R.string.message_download_release));
        RxDownload.getInstance(getApplicationContext())
                .defaultSavePath(getExternalCacheDir().getAbsolutePath())
                .maxRetryCount(3)
                .download(to.getNetworkPath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doOnTerminate(() -> hideProgressDialog())
                .subscribe(
                        downloadStatus -> progressDialog.setProgress((int) downloadStatus.getPercentNumber()),
                        throwable -> _log.error(throwable.getLocalizedMessage(), throwable),
                        () -> {
                            _log.debug("RxDownload [{}] Complete", to.getNetworkPath());
                            try {
                                String fileName = new File(new URL(to.getNetworkPath()).getFile()).getName();
                                File f = new File(getExternalCacheDir().getAbsolutePath() + File.separator + fileName);
                                upgradeReleaseApk(f);
                            } catch (Exception e) {
                                _log.warn(e.getLocalizedMessage(), e);
                            }
                        }
                );
    }

    protected void upgradeReleaseApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW); // 執行動作
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive"); //執行的數據類型
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //安裝完成後啟動app
        startActivity(intent);
        finish();
    }
}
