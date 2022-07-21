package com.octoreach.staff_reg.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;

import com.octoreach.staff_reg.R;
import com.octoreach.poct.ascensia.cpo.util.DebugLog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends RxAppCompatActivity {
    private static Logger _log = LoggerFactory.getLogger(MainActivity.class);

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_LOCATION = 2;
    private static final int REQUEST_ENABLE_NFC = 3;

    protected BluetoothAdapter mBluetoothAdapter;
    protected NfcAdapter mNfcAdapter;

    private int versionCode;
    private String versionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DebugLog.logFileDelete();

        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            finish();
            return;
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            finish();
            return;
        }

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            finish();
            return;
        }

        boolean isNetworkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!mBluetoothAdapter.isEnabled()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.display_DialogTitle))
                    .setMessage("This app requires bluetooth permissions")
                    .setPositiveButton(getString(R.string.display_DialogPositiveButton),
                            (dialog1, which) -> {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            })
                    .show();
        } else if (!isNetworkProvider) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.display_DialogTitle))
                    .setMessage("This app requires location permissions")
                    .setPositiveButton(getString(R.string.display_DialogPositiveButton),
                            (dialog1, which) -> {
                                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(enableLocationIntent);
                            })
                    .show();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        if (requestCode == REQUEST_ENABLE_LOCATION && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        while (!mBluetoothAdapter.isEnabled()) {
            requestPermissions();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

//        return;

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @SuppressLint("CheckResult")
    protected void requestPermissions() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.NFC,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .doOnComplete(() -> {
                    _log.info("permission granted. startActivity MeterActivity");
//                    checkRelease();
                    startActivity(new Intent(MainActivity.this, StaffLoginActivity.class));
                })
                .subscribe(
                        granted -> {
                            if (!granted) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                                dialog.setTitle(getString(R.string.display_DialogTitle))
                                        .setMessage("Permission fail.")
                                        .setPositiveButton(getString(R.string.display_DialogPositiveButton),
                                                (dialog1, which) -> finish()).show();
                            }
                        });
    }
}
