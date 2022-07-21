package com.octoreach.poct.ascensia.mapper.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octoreach.poct.ascensia.mapper.R;
import com.octoreach.poct.ascensia.mapper.activity.MainActivity;
import com.octoreach.poct.ascensia.mapper.custom.Constant;
import com.octoreach.poct.ascensia.mapper.network.Api;
import com.trello.rxlifecycle2.components.RxFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class LoginCaptureFragment extends RxFragment implements QRCodeView.Delegate {

    private static Logger _log = LoggerFactory.getLogger(LoginCaptureFragment.class);

    @BindView(R.id.fragment_login_capture_zbarview)
    ZBarView zBarView;

    private MainActivity _activity;
    private Ringtone ringtone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_capture, container, false);
        ButterKnife.bind(this, view);
        _activity = (MainActivity) getActivity();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(_activity.getApplicationContext(), notification);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        zBarView.setDelegate(this);
//        _activity.setSubtitle(getString(R.string.login_subtitle));
    }

    @Override
    public void onStart() {
        super.onStart();

        zBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK); // 打开后置摄像头开始预览，但是并未开始识别
//        zBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
//        zBarView.getScanBoxView().setBarCodeTipText("");
        zBarView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
        zBarView.setType(BarcodeType.ALL, null); // 只识别 CODE_128

        zBarView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别
    }

    @Override
    public void onStop() {
        zBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    public void onDestroy() {
        zBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) _activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        ringtone.play();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        _log.info("Result: {}", result);
        vibrate();
        zBarView.stopSpotAndHiddenRect();
        zBarView.stopCamera();

        _activity.showProgressDialog(getString(R.string.message_check_staff_information));
        Api.getDomainApi()
                .findStaffById(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doFinally(() -> _activity.hideProgressDialog())
                .subscribe(
                        to -> {

                            _log.debug("result length :{}", result.length());
                            if (null == to
                                    || !isNoneBlank(to.getStaffId(), to.getStaffIdNo(), to.getStaffName())
                                    || to.getStaffId().length() != 11) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                                builder.setMessage(getString(R.string.staff_barcode_error))
                                        .setIcon(ContextCompat.getDrawable(_activity, R.mipmap.ico_warning))
                                        .setPositiveButton(getString(R.string.rescan), (dialog, id) -> {
                                            // FIRE ZE MISSILES!
                                            zBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK); // 打开后置摄像头开始预览，但是并未开始识别
                                            zBarView.startSpotAndShowRect();
                                            dialog.dismiss();
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            } else {
                                Bundle b = new Bundle();
                                b.putParcelable(Constant.TO_STAFF, to);
                                _activity.switchFragment(new MapperFragment(), b);
                            }
                        },
                        throwable -> {
                            _log.warn(throwable.getLocalizedMessage(), throwable);
                            // Use the Builder class for convenient dialog construction
                            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                            builder.setMessage(
                                    String.format(getString(R.string.message_check_staff_information_error), throwable.getLocalizedMessage()))
                                    .setIcon(ContextCompat.getDrawable(_activity, R.mipmap.ico_warning))
                                    .setPositiveButton(getString(R.string.rescan), (dialog, id) -> {
                                        // FIRE ZE MISSILES!
                                        zBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK); // 打开后置摄像头开始预览，但是并未开始识别
                                        zBarView.startSpotAndShowRect();
                                        dialog.dismiss();
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }

                );
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        _log.error("ScanQRCodeOpenCameraError");
    }
}
