package com.octoreach.staff_reg.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.octoreach.poct.ascensia.cpo.util.Converter;
import com.octoreach.staff_reg.fragment.StaffLoginFragment;
import com.octoreach.staff_reg.network.Api;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.octoreach.staff_reg.custom.PreferenceConstant.PREFERENCE_APPCONFIG_SERVER_IP;
import static com.octoreach.staff_reg.custom.PreferenceConstant.PREFERENCE_APPCONFIG_SERVER_PORT;

public class StaffLoginActivity extends RxBaseActivity {
    public static String NFC_ID = "NFC_ID";


    private static Logger _log = LoggerFactory.getLogger(StaffLoginActivity.class);
    protected NfcAdapter mNfcAdapter;
    private boolean userValidation;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public boolean isNfcSupper() {
        return null != mNfcAdapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);


        String ip = preferences.getString(PREFERENCE_APPCONFIG_SERVER_IP, "192.168.3.186");
        String port = preferences.getString(PREFERENCE_APPCONFIG_SERVER_PORT, "8001");
        if (StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(port))
            Api.setDomainHost(ip + ":" + port);


    }

    private String resolveIntent(Intent intent) {
        String action = intent.getAction();
        if ("android.nfc.action.TAG_DISCOVERED".equals(action) || "android.nfc.action.TECH_DISCOVERED".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            if (rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; ++i) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                Parcelable parcelable = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                dumpTagData_Dec(parcelable);

//                FragmentManager fragmentManager = getFragmentManager();
//                StaffLoginFragment staffLoginFragment = (StaffLoginFragment) fragmentManager.findFragmentById(R.id.fragment_staff_register);
//                staffLoginFragment.setEtNfcResult(dumpTagData_Dec(parcelable));
                Bundle bundle = new Bundle();
                bundle.putString("NFC_ID", dumpTagData_Dec(parcelable));
                switchFragment(new StaffLoginFragment(), bundle);
            }
        }
        return null;
    }


    private String dumpTagData_Dec(Parcelable p) {
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        String result = String.valueOf(Converter.getDec(id));
        return result;
    }


    public NfcAdapter getmNfcAdapter() {
        return this.mNfcAdapter;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (isNfcSupper() && mNfcAdapter.isEnabled()) {
            switchFragment(new StaffLoginFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNfcSupper()) {
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            mNfcAdapter.enableForegroundDispatch(StaffLoginActivity.this, pendingIntent, intentFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isNfcSupper()) {
            mNfcAdapter.disableForegroundDispatch(StaffLoginActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

}
