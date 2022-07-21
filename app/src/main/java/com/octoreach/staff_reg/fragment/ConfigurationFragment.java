package com.octoreach.staff_reg.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.octoreach.staff_reg.R;
import com.octoreach.staff_reg.activity.RxBaseActivity;
import com.octoreach.staff_reg.network.Api;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.octoreach.staff_reg.custom.PreferenceConstant.PREFERENCE_APPCONFIG_LOCATION_ID;
import static com.octoreach.staff_reg.custom.PreferenceConstant.PREFERENCE_APPCONFIG_SERVER_IP;
import static com.octoreach.staff_reg.custom.PreferenceConstant.PREFERENCE_APPCONFIG_SERVER_PORT;

public class ConfigurationFragment extends DialogFragment {
    //    protected MeterActivity _activity;
    private static RxBaseActivity _activity = null;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @BindView(R.id.btn_done)
    AppCompatButton btnDone;

    @BindView(R.id.editText_server_location)
    EditText etLocation;

    @BindView(R.id.editText_server_ip)
    EditText etServerIp;

    @BindView(R.id.editText_server_port)
    EditText etServerPort;


    public static ConfigurationFragment newInstance(RxBaseActivity activity) {
        _activity = activity;
        return new ConfigurationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);
//        _activity = (MeterActivity) getActivity();
        ButterKnife.bind(this, view);

        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        et_serverIp.setOnEditorActionListener(onEditorActionListener);

        preferences = _activity.getSharedPreferences(_activity.getPackageName(), Context.MODE_PRIVATE);
        editor = preferences.edit();

        etLocation.setText(preferences.getString(PREFERENCE_APPCONFIG_LOCATION_ID, ""));
        etServerIp.setText(preferences.getString(PREFERENCE_APPCONFIG_SERVER_IP, "192.168.3.186"));
        etServerPort.setText(preferences.getString(PREFERENCE_APPCONFIG_SERVER_PORT, "8001"));

        RxView.clicks(btnDone)
                .subscribe(
                        v -> {
                            String ipWithPort;
                            String LOCATION = PREFERENCE_APPCONFIG_LOCATION_ID;
                            String SERVER_IP = PREFERENCE_APPCONFIG_SERVER_IP;
                            String SERVER_PORT = PREFERENCE_APPCONFIG_SERVER_PORT;

                            if (StringUtils.isNotBlank(etLocation.getText().toString())) {
                                editor.putString(LOCATION, etLocation.getText().toString());
                            }

                            if (StringUtils.isNotBlank(etServerIp.getText().toString())) {
                                //有分號的，IP:PORT
                                if (etServerIp.getText().toString().contains(":")) {
                                    ipWithPort = etServerIp.getText().toString();

                                    editor.putString(SERVER_IP, ipWithPort.split(":")[0]);
                                    editor.putString(SERVER_PORT, ipWithPort.split(":")[1]);
                                } else
                                    editor.putString(SERVER_IP, etServerIp.getText().toString());
                            }

                            if (StringUtils.isNotBlank(etServerPort.getText().toString())) {
                                editor.putString(SERVER_PORT, etServerPort.getText().toString());
                            }

                            boolean save = editor.commit();


                            String ip = preferences.getString(SERVER_IP, "192.168.3.186");
                            String port = preferences.getString(SERVER_PORT, "8001");

                            Api.setDomainHost(ip + ":" + port);


//                            Thread.sleep(3000);

                            dismiss();
//
//                            if (save) {
//                                Intent intent = _activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(_activity.getBaseContext().getPackageName());
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                _activity.getApplicationContext().startActivity(intent);
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                            }
                        }
                );

    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window == null)
            return;

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(params);
        getDialog().setCanceledOnTouchOutside(false);
    }
}