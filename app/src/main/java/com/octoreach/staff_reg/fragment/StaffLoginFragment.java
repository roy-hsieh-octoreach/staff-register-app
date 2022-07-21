package com.octoreach.staff_reg.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.octoreach.staff_reg.R;
import com.octoreach.staff_reg.activity.StaffLoginActivity;
import com.octoreach.staff_reg.network.Api;
import com.trello.rxlifecycle2.components.RxFragment;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class StaffLoginFragment extends RxFragment {
    private static final int REQ_FROM_ACTIVITY = 1;

    private static Logger _log = LoggerFactory.getLogger(StaffLoginFragment.class);

    private StaffLoginActivity _activity;


    @BindView(R.id.ImageButton_setting)
    AppCompatImageButton ibSetting;
    @BindView(R.id.textView_version_name)
    TextView tv_versionName;
    @BindView(R.id.fragment_et_nfc_id)
    EditText etNfcResult;
    @BindView(R.id.fragment_et_staff_id)
    EditText etStaffId;
    @BindView(R.id.fragment_staff_nfc_post)
    AppCompatButton btnPost;

    public static StaffLoginFragment newInstance() {

        return new StaffLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_nfc, container, false);
        ButterKnife.bind(this, view);
        _activity = (StaffLoginActivity) getActivity();

        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Intent intent = new Intent(_activity, StaffLoginActivity.class);
//        startActivityForResult(intent, REQ_FROM_ACTIVITY);
        Bundle bundle = getArguments();

        if (bundle != null) {
            String value = bundle.getString(_activity.NFC_ID);
            setEtNfcResult(value);

        }

        PackageManager pm = _activity.getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(_activity.getApplicationContext().getPackageName(), 0);
            tv_versionName.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        RxView.clicks(btnPost)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        v -> {
                            _activity.showProgressDialog("Register data...");

                            JSONObject jsonObject = new JSONObject();
                            String nfcId = etNfcResult.getText().toString().trim();
                            String nurseId = etStaffId.getText().toString().trim();

                            if (StringUtils.isBlank(nfcId) ) {
                                Toast.makeText(_activity, "", Toast.LENGTH_SHORT).show();

                                return;
                            }


                            if (StringUtils.isBlank(nurseId)) {
                                return;
                            }

                            jsonObject.put("card_id", nfcId);
                            jsonObject.put("nurse_id", nurseId);

                            String jsonString = jsonObject.toString();
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
                            _log.debug(jsonString);
                            Api.getDomainApi()
                                    .uploadNFCId(requestBody)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnTerminate(() -> _activity.hideProgressDialog())
                                    .subscribe(
                                            responseBody -> {
                                                Log.i("uploadVitalData", responseBody.toString());
                                                Toast.makeText(_activity, "Register success", Toast.LENGTH_SHORT).show();

                                            },
                                            throwable -> {
                                                _log.warn(throwable.getLocalizedMessage(), throwable);
                                                Toast.makeText(_activity, "Register Error", Toast.LENGTH_SHORT).show();


                                            });
//                            doConnect();
//                            subscribeTopic(topicVitalSign);
                        },
                        throwable ->
                                _log.warn(throwable.getLocalizedMessage(), throwable)
                );

        RxView.clicks(ibSetting)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        v -> {
                            ConfigurationFragment configurationFragment = ConfigurationFragment.newInstance(_activity);
                            configurationFragment.show(getFragmentManager(), "ConfigurationFragment");
                        });


    }

    public void setEtNfcResult(String result) {
        this.etNfcResult.setText(result);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}

