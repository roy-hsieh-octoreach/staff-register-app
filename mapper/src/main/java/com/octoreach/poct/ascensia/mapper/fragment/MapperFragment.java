package com.octoreach.poct.ascensia.mapper.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.octoreach.poct.ascensia.mapper.R;
import com.octoreach.poct.ascensia.mapper.activity.MainActivity;
import com.octoreach.poct.ascensia.mapper.custom.Constant;
import com.octoreach.poct.ascensia.mapper.network.Api;
import com.octoreach.poct.ascensia.mapper.to.GlucoseMapperTo;
import com.octoreach.poct.ascensia.mapper.to.StaffTo;
import com.trello.rxlifecycle2.components.RxFragment;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapperFragment extends RxFragment {

    private static Logger _log = LoggerFactory.getLogger(MapperFragment.class);

    @BindView(R.id.fragment_mapper_tv_StaffName)
    AppCompatTextView tvStaffName;
    @BindView(R.id.fragment_mapper_tv_StaffBarcode)
    AppCompatTextView tvStaffBarcode;
    @BindView(R.id.fragment_mapper_imgBtn_PatientBarcodeScan)
    AppCompatImageButton imgBtnPatientBarcodeScan;
    @BindView(R.id.fragment_mapper_tv_PatientName)
    AppCompatTextView tvPatientName;
    @BindView(R.id.fragment_mapper_tv_PatientBarcode)
    AppCompatTextView tvPatientBarcode;
    @BindView(R.id.fragment_mapper_imgBtn_DeviceBarcodeScan)
    AppCompatImageButton imgBtnDeviceBarcodeScan;
    @BindView(R.id.fragment_mapper_tv_DeviceLabel)
    AppCompatTextView tvDeviceLabel;
    @BindView(R.id.fragment_mapper_tv_DeviceBarcode)
    AppCompatTextView tvDeviceBarcode;
    @BindView(R.id.fragment_mapper_cb_DeviceReserved)
    AppCompatCheckBox cbDeviceReserved;
    @BindView(R.id.fragment_mapper_btn_UploadMapper)
    AppCompatButton btnUploadMapper;

    private MainActivity _activity;
    public static final int SCANNER_PATIENT_BARCODE_REQUEST_CODE = 1;
    public static final int SCANNER_MONITOR_BARCODE_REQUEST_CODE = 3;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.TAIWAN);
    private Date staffIdTime;
    private Date patientIdTime;
    private Date monitorIdTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapper, container, false);
        ButterKnife.bind(this, view);
        _activity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        _activity.setSubtitle(getString(R.string.mapper_subtitle));

        Bundle bundle = getArguments();
        if (null != bundle) {
            StaffTo staffTo = bundle.getParcelable(Constant.TO_STAFF);
            if (null != staffTo) {
                tvStaffBarcode.setText(staffTo.getStaffId());
                tvStaffName.setText(staffTo.getStaffName());
                staffIdTime = new Date();
            }
        }

        RxView.clicks(imgBtnPatientBarcodeScan)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        v -> {
                            BarcodeCaptrueFragment barcodeCaptrueFragment = new BarcodeCaptrueFragment();
                            barcodeCaptrueFragment.setCancelable(true);
                            barcodeCaptrueFragment.setTargetFragment(MapperFragment.this, SCANNER_PATIENT_BARCODE_REQUEST_CODE);
                            barcodeCaptrueFragment.show(_activity.getFragmentManager(), BarcodeCaptrueFragment.class.getSimpleName());
                        },
                        throwable -> _log.warn(throwable.getLocalizedMessage(), throwable)
                );

        RxView.clicks(imgBtnDeviceBarcodeScan)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        v -> {
                            BarcodeCaptrueFragment barcodeCaptrueFragment = new BarcodeCaptrueFragment();
                            barcodeCaptrueFragment.setCancelable(true);
                            barcodeCaptrueFragment.setTargetFragment(MapperFragment.this, SCANNER_MONITOR_BARCODE_REQUEST_CODE);
                            barcodeCaptrueFragment.show(_activity.getFragmentManager(), BarcodeCaptrueFragment.class.getSimpleName());
                        },
                        throwable -> _log.warn(throwable.getLocalizedMessage(), throwable)
                );

        RxView.clicks(btnUploadMapper)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        v -> {
                            if (!checkBarcode()) {
                                Snackbar.make(btnUploadMapper, getString(R.string.upload_data_fail_miss_data), Snackbar.LENGTH_LONG).show();
                                _log.warn(getString(R.string.upload_data_fail_miss_data));
                            } else {
                                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                                GlucoseMapperTo glucoseMapperTo = new GlucoseMapperTo();
                                glucoseMapperTo.setStaffId(tvStaffBarcode.getText().toString());
                                glucoseMapperTo.setStaffIdTime(df.format(staffIdTime));

                                if (null != patientIdTime) {
                                    glucoseMapperTo.setPatientId(tvPatientBarcode.getText().toString());
                                    glucoseMapperTo.setPatientIdTime(df.format(patientIdTime));
                                }

                                if (null != monitorIdTime) {
                                    glucoseMapperTo.setDeviceId(tvDeviceBarcode.getText().toString());
                                    glucoseMapperTo.setDeviceIdTime(df.format(monitorIdTime));
                                }

                                glucoseMapperTo.setRecordTime(df.format(new Date()));

                                _activity.showProgressDialog(getString(R.string.message_upload_mapper_result));
                                Api.getDomainApi()
                                        .mapper(glucoseMapperTo)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .compose(bindToLifecycle())
                                        .doOnSuccess(avoid -> btnUploadMapper.setEnabled(false))
                                        .doFinally(() -> {
                                            _activity.hideProgressDialog();
                                            btnUploadMapper.setEnabled(true);
                                        })
                                        .doAfterSuccess(aVoid -> {
                                            tvPatientBarcode.setText("");
                                            tvPatientName.setText("");
                                            patientIdTime = null;

                                            if (!cbDeviceReserved.isChecked()) {
                                                tvDeviceBarcode.setText("");
                                                monitorIdTime = null;
                                            }
                                        })
                                        .subscribe(
                                                to ->
                                                        Snackbar.make(btnUploadMapper, getString(R.string.upload_data_success), Snackbar.LENGTH_LONG).show(),
                                                throwable -> {
                                                        _log.error(throwable.getLocalizedMessage(), throwable);
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                                                        builder.setMessage(
                                                                String.format(getString(R.string.message_upload_mapper_result_error), throwable.getLocalizedMessage()))
                                                                .setIcon(ContextCompat.getDrawable(_activity, R.mipmap.ico_warning))
                                                                .setPositiveButton(getString(R.string.i_am_clear), (dialog, id) -> dialog.dismiss())
                                                                .setCancelable(false)
                                                                .create()
                                                                .show();
                                                }
                                        );
                            }
                        },
                        throwable -> _log.warn(throwable.getLocalizedMessage(), throwable)
                );

    }

    //
    private boolean checkBarcode() {
        return StringUtils.isNoneBlank(
                tvPatientBarcode.getText(),
                tvDeviceBarcode.getText()
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final String barcodeResult = data.getExtras().getString(BarcodeCaptrueFragment.BarcodeResult);
        switch (requestCode) {
            case SCANNER_PATIENT_BARCODE_REQUEST_CODE:
                if (!(barcodeResult.length() >= 8 && barcodeResult.length() <= 10) ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                    builder.setMessage(getString(R.string.patient_barcode_error))
                            .setIcon(ContextCompat.getDrawable(_activity, R.mipmap.ico_warning))
                            .setPositiveButton(getString(R.string.i_am_clear), (dialog, id) -> dialog.dismiss())
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    _activity.showProgressDialog(getString(R.string.message_load_patient_data));
                    Api.getDomainApi().findPatientName(barcodeResult)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .doFinally(() -> _activity.hideProgressDialog())
                            .subscribe(
                                    patientName -> {
                                        tvPatientName.setText(patientName);
                                        tvPatientBarcode.setText(barcodeResult);
                                        patientIdTime = new Date();
                                    },
                                    throwable -> _log.warn(throwable.getLocalizedMessage(), throwable)
                            );
                }
                break;

            case SCANNER_MONITOR_BARCODE_REQUEST_CODE:
                if (!barcodeResult.startsWith("7600P")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                    builder.setMessage(getString(R.string.glucose_barcode_error))
                            .setIcon(ContextCompat.getDrawable(_activity, R.mipmap.ico_warning))
                            .setPositiveButton(getString(R.string.i_am_clear), (dialog, id) -> dialog.dismiss())
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    tvDeviceBarcode.setText(barcodeResult);
                    monitorIdTime = new Date();
                }
                break;

            default:
                break;
        }

        getActivity().setResult(Activity.RESULT_OK, data);
    }

}
