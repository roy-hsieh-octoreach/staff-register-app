package com.octoreach.poct.ascensia.mapper.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octoreach.poct.ascensia.mapper.R;
import com.octoreach.poct.ascensia.mapper.activity.MainActivity;
import com.trello.rxlifecycle2.components.RxDialogFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public class BarcodeCaptrueFragment extends RxDialogFragment implements QRCodeView.Delegate {

    private static Logger _log = LoggerFactory.getLogger(BarcodeCaptrueFragment.class);

    @BindView(R.id.fragment_barcode_capture_zbarview)
    ZBarView zBarView;

    private MainActivity _activity;
    private Ringtone ringtone;
    public static final String BarcodeResult = "BarcodeResult";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_captrue, container, false);
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
        ringtone.play();
        Vibrator vibrator = (Vibrator) _activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        _log.info("Result: {}", result);
        vibrate();

        zBarView.stopSpotAndHiddenRect();
        zBarView.stopCamera();

        Intent intent = new Intent();
        intent.putExtra(BarcodeResult, result);
        getTargetFragment()
                .onActivityResult(
                        getTargetRequestCode(),
                        Activity.RESULT_OK,
                        intent);
        dismiss();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        _log.error("ScanQRCodeOpenCameraError");
    }
}
