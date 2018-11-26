package com.sscctv.tdr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sscctv.seeeyes.SpiPort;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private SpiPort mSpi;
    private Spinner cableTypeSpinner, menuSpinner;
    private TextView stat, len;
    private int value;
    private float value1 = 2.f;
    private ArrayList<String> cableList;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.cable_image);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.cable_anim);
        imageView.startAnimation(animation);

        ImageView imageView1 = findViewById(R.id.monitor_image);
        final AnimationDrawable drawable = (AnimationDrawable) imageView1.getBackground();
        drawable.start();

        findViewById(R.id.start_button).setOnClickListener(this);
        stat = findViewById(R.id.cable_status_value);
        len = findViewById(R.id.cable_length_value);
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
//
        mSpi = new SpiPort();
        mSpi.open();
//
        selectmenufacture();
        selectCableType();

        mContext = this;

//
//        Button test1 = findViewById(R.id.button);
//        test1.setOnClickListener(this);
//
//        Button test2 = findViewById(R.id.button1);
//        test2.setOnClickListener(this);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.v(TAG, "" + getCurrentFocus());
//        if (menuSpinner.isFocusable()) {
//            Log.d(TAG, "Key menuSpinner");
//
//        } else if (cableTypeSpinner.isFocusable()) {
//            Log.d(TAG, "Key cableTypeSpinner");
//
//        }
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
                        mSpi.setCableType(0.82);
                        break;
                    case "5C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.82);
                        break;
                    case "7C-FBT (KUMKANG)":
                        mSpi.setCableType(0.82);
                        break;
                    case "7C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.82);
                        break;
                    case "10C-HFBT (KUMKANG)":
                        mSpi.setCableType(0.82);
                        break;
                    case "3C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.66);
                        break;
                    case "5C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.66);
                        break;
                    case "7C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.66);
                        break;
                    case "10C-2V ECX (LS Cable)":
                        mSpi.setCableType(0.66);
                        break;
                    case "5C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        break;
                    case "7C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        break;
                    case "10C-HFBT (LS Cable)":
                        mSpi.setCableType(0.83);
                        break;
                    case "RG-6/U PE (Belden 8215)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-6/U Foam (Belden 9290)":
                        mSpi.setCableType(0.81);
                        break;
                    case "RG-8/U PE (Belden 8237)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-8/U Foam (Belden 8214)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-8/U (Belden 9913)":
                        mSpi.setCableType(0.84);
                        break;
                    case "RG-8X (Belden 9258)":
                        mSpi.setCableType(0.82);
                        break;
                    case "RG-11/U Foam HPDE (Belden 9292)":
                        mSpi.setCableType(0.84);
                        break;
                    case "RG-58/U PE (Belden 9201)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-58A/U Foam (Belden 8219)":
                        mSpi.setCableType(0.73);
                        break;
                    case "RG-59A/U Foam (Belden 8241)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-59A/U Foam (Belden 8241F)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-174 PE (Belden 8216)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-174 Foam (Belden 7805R)":
                        mSpi.setCableType(0.735);
                        break;
                    case "RG-213/U (Belden 8267)":
                        mSpi.setCableType(0.66);
                        break;
                    case "L-2.5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "L-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "L-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "L-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "L-7CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "LV-77S (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "LV-61S (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V3-3C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V4-3C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V5-3C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V3-5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V4-5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V5-5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V3-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V4-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V5-3CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V3-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V4-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V5-4CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V3-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V4-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V5-5CFB (CANARE)":
                        mSpi.setCableType(0.79);
                        break;
                    case "V3-1.5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V4-1.5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "V5-1.5C (CANARE)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-6/U/T (KUNKANG)":
                        mSpi.setCableType(0.82);
                        break;
                    case "LMR-195":
                        mSpi.setCableType(0.83);
                        break;
                    case "LMR-240":
                        mSpi.setCableType(0.84);
                        break;
                    case "LMR-240UF":
                        mSpi.setCableType(0.84);
                        break;
                    case "LMR-400":
                        mSpi.setCableType(0.85);
                        break;
                    case "LMR-400UF":
                        mSpi.setCableType(0.85);
                        break;
                    case "Davis BurryFlex":
                        mSpi.setCableType(0.82);
                        break;
                    case "RG-6U (Foam)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-8U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-8U (Foam)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-8":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-8X":
                        mSpi.setCableType(0.84);
                        break;
                    case "RG-9U":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-11":
                        mSpi.setCableType(0.75);
                        break;
                    case "RG-11U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-11U (Foam)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-58 (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-58 (Foam)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-58":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-59 (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-59 (Foam)":
                        mSpi.setCableType(0.78);
                        break;
                    case "RG-62U (Air&Poly)":
                        mSpi.setCableType(0.84);
                        break;
                    case "RG-71U (Air&Poly)":
                        mSpi.setCableType(0.84);
                        break;
                    case "RG-122U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-141U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-142U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-174U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-178U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-179U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-180U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-187U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-188U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-196U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-213U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-214U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-217U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-218U (Poly)":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-223U":
                        mSpi.setCableType(0.66);
                        break;
                    case "RG-303U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-316U (Teflon)":
                        mSpi.setCableType(0.69);
                        break;
                    case "RG-400 (Teflon)":
                        mSpi.setCableType(0.69);
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
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
//        mSpi.close();
        mSpi.GpioOff();
        openPoeView();
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
            progressDialog.setMessage(getResources().getText(R.string.please_wait).toString());

            progressDialog.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            mSpi.start();

            value = mSpi.getStat();
            value1 = mSpi.getLength();

            Log.d(TAG, "str1: " + mSpi.getLength() + " str2: " + mSpi.getStat());

            return null;
        }

        @Override
        @SuppressLint("DefaultLocale")
        protected void onPostExecute(Void aVoid) {
            String text = "";
            String text1 = Float.toString(value1);
            String text2 = String.format("%.2f", value1);

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
            }

            stat.setText(text);

            if (value == 2) {
                stat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                len.setText("");
            } else {
                stat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                len.setText(text2);

            }
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

}


