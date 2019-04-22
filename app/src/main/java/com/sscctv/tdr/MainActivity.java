package com.sscctv.tdr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sscctv.seeeyes.SpiPort;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sscctv.seeeyes.ptz.LevelMeterListener;
import com.sscctv.seeeyes.ptz.McuControl;
import com.sscctv.seeeyes.VideoSource;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TDRC_1903281900";
    private SpiPort mSpi;
    private Spinner cableTypeSpinner, menuSpinner;
    private TextView stat, len, loop;
    private int value;
    private float value1 = 2.f;
    private ArrayList<String> cableList;
    private Context mContext;
    private McuControl mMcuControl;
    private String master, sub, strLoopAD;
    private int intLoopAD = 0;
    private int result;
    private int master_result = 0;
    private int sub_result = 0;
    private LevelMeterListener mLevelMeterListener;
    private int cableType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.i(TAG, "TDRC App Data: 201903260922   Version: 1.0.0");
        ImageView imageView = findViewById(R.id.cable_image);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.cable_anim);
        imageView.startAnimation(animation);

        ImageView imageView1 = findViewById(R.id.monitor_image);
        final AnimationDrawable drawable = (AnimationDrawable) imageView1.getBackground();
        drawable.start();

        findViewById(R.id.start_button).setOnClickListener(this);
        stat = findViewById(R.id.cable_status_value);
        len = findViewById(R.id.cable_length_value);
        loop = findViewById(R.id.cable_loop_value);
        cableTypeSpinner = findViewById(R.id.cable_type);

        cableTypeSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    Log.e(TAG, "cable Type Focus!!!");
                    //TODO
                }

            }
        });

        menuSpinner = findViewById(R.id.cable_mf);
        ArrayList<String> mfList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menufacture)));
        Adapter_spinner mf_adapterSpinner = new Adapter_spinner(this, mfList);
        menuSpinner.setAdapter(mf_adapterSpinner);
        menuSpinner.setFocusable(true);
        menuSpinner.setFocusableInTouchMode(true);
        menuSpinner.requestFocus();
        menuSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    Log.e(TAG, "Menu Focus!!!");
                    //TODO
                }
            }
        });
        mSpi = new SpiPort();
        mSpi.open();

        mMcuControl = new McuControl();
        mMcuControl.start(VideoSource.ETC);
        selectmenufacture();
        selectCableType();

        mContext = this;
        getLoopLevel();


    }

    public void getLoopAD() throws IOException, InterruptedException {
        Log.d(TAG, "Start getLoopAD()");
        if(mMcuControl != null)
        mMcuControl.getLoopMaster();

    }

    public void getLoopLevel() {

        mLevelMeterListener = new LevelMeterListener() {
            @Override
            public void onLevelChanged(final int level, final int value) {
                switch (level) {
                    case MASTER_CHECK_LOOP:
//                        Log.d(TAG, "Master Value: " + value);
                        master_result = value * 100;
//                        master = Integer.toString(result);
                        break;
                    case SUB_CHECK_LOOP:
//                        Log.d(TAG, "Sub Value: " + value);
                        sub_result = value;
//                        sub = Integer.toString(value);
                        break;
                }
//                Log.d(TAG, "Master Value: " + master_result + " Sub Value: " + sub_result);

                if (sub_result != -1) {
//                    if(master == null) {
//                        try {
//                            getLoopAD();
//                        } catch (IOException | InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        return;
//                    }
                    intLoopAD = master_result + sub_result;
//                    strLoopAD = master.concat(sub);
//                    intLoopAD = Integer.parseInt(re);
                    Log.d(TAG, "Result: " + intLoopAD);

                }


            }
        };
        mMcuControl.addReceiveBufferListener(mLevelMeterListener);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private void openPoeView() {
        Intent sendIntent = new Intent("com.sscctv.poeView");
        sendIntent.putExtra("location", "open");
        sendBroadcast(sendIntent);
    }

    private void closePoeView() {
        Intent sendIntent = new Intent("com.sscctv.poeView");
        sendIntent.putExtra("location", "close");
        sendBroadcast(sendIntent);
    }


    private void selectmenufacture() {
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = menuSpinner.getSelectedItem().toString();
//                Log.d(TAG, "Parent: " + parent + " View: " + view);
                switch (type) {
                    case "KUMKANG Cable":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.kumkag)));
                        break;
                    case "LS Cable":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ls)));
                        break;
                    case "Belden":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.belden)));
                        break;
                    case "CANARE":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.canare)));
                        break;
                    case "Times Microwave System":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.time)));
                        break;
                    case "Davis Cable":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.davis)));
                        break;
                    case "UTP Cable - TG02":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.utp)));
                        break;
                    case "ETC Cable":
                        cableList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.etc)));
                        break;
                }
                Adapter_spinner adapterSpinner = new Adapter_spinner(mContext, cableList);
                cableTypeSpinner.setAdapter(adapterSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void selectCableType() {
        cableTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = cableTypeSpinner.getSelectedItem().toString();
                switch (type) {
                    case "5C-FBT (KUMKANG)":
                        mSpi.setCableType(0.835);
                        mSpi.setCableCheck(0);
                        break;
                    case "5C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.83);
                        mSpi.setCableCheck(0);
                        break;
                    case "7C-FBT (KUMKANG)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "7C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "10C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "3C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "5C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "7C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "10C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "5C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        mSpi.setCableCheck(0);
                        break;
                    case "7C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        mSpi.setCableCheck(0);
                        break;
                    case "10C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-6/U PE (Belden 8215)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-6/U Foam (Belden 9290)":
                        mSpi.setCableType(0.81);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8/U PE (Belden 8237)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8/U Foam (Belden 8214)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8/U (Belden 9913)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8X (Belden 9258)":
                        mSpi.setCableType(0.82);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-11/U Foam HPDE (Belden 9292)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-58/U PE (Belden 9201)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-58A/U Foam (Belden 8219)":
                        mSpi.setCableType(0.73);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-59A/U Foam (Belden 8241)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-59A/U Foam (Belden 8241F)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-174 PE (Belden 8216)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-174 Foam (Belden 7805R)":
                        mSpi.setCableType(0.735);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-213/U (Belden 8267)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "L-2.5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "L-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "L-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "L-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "L-7CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "LV-77S (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "LV-61S (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-3C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-3C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-3C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        mSpi.setCableCheck(0);
                        break;
                    case "V3-1.5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V4-1.5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "V5-1.5C (CANARE)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-6/U/T (KUNKANG)":
                        mSpi.setCableType(0.82);
                        mSpi.setCableCheck(0);
                        break;
                    case "LMR-195":
                        mSpi.setCableType(0.83);
                        mSpi.setCableCheck(0);
                        break;
                    case "LMR-240":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "LMR-240UF":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "LMR-400":
                        mSpi.setCableType(0.85);
                        mSpi.setCableCheck(0);
                        break;
                    case "LMR-400UF":
                        mSpi.setCableType(0.85);
                        mSpi.setCableCheck(0);
                        break;
                    case "Davis BurryFlex":
                        mSpi.setCableType(0.82);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-6U (Foam)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8U (Foam)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-8X":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-9U":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-11":
                        mSpi.setCableType(0.75);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-11U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-11U (Foam)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-58 (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-58 (Foam)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-58 A/U":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-59 (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-59 (Foam)":
                        mSpi.setCableType(0.78);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-62U (Air&Poly)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-71U (Air&Poly)":
                        mSpi.setCableType(0.84);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-122U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-141U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-142U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-174U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-178U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-179U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);
                        break;
                    case "RG-180U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-187U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-188U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-196U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-213U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-214U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-217U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-218U (Poly)":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-223U":
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-303U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-316U (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "RG-400 (Teflon)":
                        mSpi.setCableType(0.69);
                        mSpi.setCableCheck(0);

                        break;
                    case "ETC":
                        mSpi.setCableType(0.75);
                        mSpi.setCableCheck(0);

                        break;
                    case "Cat-7 Twisted Pair":
//                        mSpi.setCableType(0.528);
                        mSpi.setCableType(0.765);
                        mSpi.setCableCheck(1);
                        break;
                    case "Cat-6A Twisted Pair":
//                        mSpi.setCableType(0.527);
                        mSpi.setCableType(0.65);
                        mSpi.setCableCheck(1);
                        break;
                    case "Cat-5e Twisted Pair":
//                        mSpi.setCableType(0.526);
                        mSpi.setCableType(0.70);
                        mSpi.setCableCheck(1);
                        break;
                    case "Cat-3 Twisted Pair":
//                        mSpi.setCableType(0.524);
                        mSpi.setCableType(0.585);
                        mSpi.setCableCheck(1);
                        break;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        mSpi.GpioOn();
        closePoeView();
        Intent intent = new Intent();
        intent.setAction("com.sscctv.seeeyesmonitor");
        intent.putExtra("state", "resume");
        sendBroadcast(intent);

        try {
            getLoopAD();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
//        mSpi.close();
        mSpi.GpioOff();
        openPoeView();
        if (mMcuControl != null) {
            mMcuControl.stop();
            mMcuControl.removeReceiveBufferListener(mLevelMeterListener);
            mLevelMeterListener = null;
            mMcuControl = null;
        }
        Intent intent = new Intent();
        intent.setAction("com.sscctv.seeeyesmonitor");
        intent.putExtra("state", "pause");
        sendBroadcast(intent);
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                CheckTypesTask task = new CheckTypesTask();
                task.execute();

                break;

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);



        @Override
        protected void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getText(R.string.please_wait).toString());

            progressDialog.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {


            mSpi.start();

            value = mSpi.getStat();
            value1 = mSpi.getLength();



            try {
//                Thread.sleep(5000);
//                Log.d(TAG, "value: " + value);

                getLoopAD();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "State: " + mSpi.getLength() + " Length: " + mSpi.getStat());

            String text = "";
            String text1 = Float.toString(value1);
            String text2 = String.format("%.2f", value1);

            double ft = value1 * 3.281;
            String text3 = String.format("%.2f", ft);

            switch (value) {
                case -1:
                    text = (String) getResources().getText(R.string.none);
                    break;
                case 0:
                    text = (String) getResources().getText(R.string.cable_open);
                    break;
                case 1:
                    text = (String) getResources().getText(R.string.cable_short);
                    break;
                case 2:
                    text = (String) getResources().getText(R.string.matching_or_not);
                    break;
                case 3:
                    text = (String) getResources().getText(R.string.matching_or_not);
                    break;
            }

            stat.setText(text);

            if (value == 2) {
                stat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                len.setText("");
            } else {
                stat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                len.setText(text2 + "m / " + text3 + "ft");

            }

            if (intLoopAD != -1
//                    && value == 1
                    ) {
                double vAdc;
                double regLoop;
                double result;
                String type = menuSpinner.getSelectedItem().toString();


                vAdc = (3.3 * intLoopAD) / 4095;
                regLoop = (302 * vAdc) / (3.3 - vAdc);

                if (type.equals("UTP Cable - TG02")) {
                    result = (regLoop - 5) + 0.7;
                    Log.d(TAG, "type.equals : " + result);
                } else {
                    result = regLoop - 5;
                    Log.d(TAG, "type - : " + result);
                }

                if(result < 0) {
                    result = 0;
                } else if (result > 1000 || Double.isInfinite(result)) {
                    loop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    loop.setText(getResources().getText(R.string.not_measured));
                } else {
                    String resultValue = String.format("%.1f", result);
                    loop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    loop.setText(resultValue + " Ω");
                }


                Log.d(TAG, "LoopAD: " + intLoopAD + " vAdc: " + vAdc + " R2: " + regLoop + " result: " + result);


            }

            progressDialog.dismiss();


            super.onPostExecute(aVoid);
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler loopHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                getLoopAD();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

}


