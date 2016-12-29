package com.bomeans.sample.acremotesample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Bomeans Design 2016
 *
 * Sample code: using Bomeans IRKit SDK for AC remote controller.
 * Please read the in-code comments for the tricky part of AC remote controllers.
 *
 */
public class MainActivity extends AppCompatActivity {

    // apply an SDK key from Bomeans and apply it below
    private String API_KEY = "";
    private BIRRemote mMyAcRemote = null;

    // GUI: fixed buttons (common for most air-conditioners)
    // These are most common keys for AC remote controllers.
    // power
    Boolean mHasPowerKey = true;
    TextView mCurrentPowerText;
    Button mPowerButton;
    // temp +/-
    Boolean mHasTemperatureKey = true;
    TextView mCurrentTempText;
    Button mTempUpButton;
    Button mTempDownButton;
    // mode
    Boolean mHasModeKey = true;
    TextView mCurrentModeText;
    Button mModeButton;
    // fan-speed
    Boolean mHasFanSpeedKey = true;
    TextView mCurrentFanSpeedText;
    Button mFanSpeedButton;
    // air-sweep
    Boolean mHasVerticalAirSwingKey = true;
    Boolean mHasHorizontalAirSwingKey = true;
    TextView mCurrentVerticalAirSwingText;
    TextView mCurrentHorizontalAirSwingText;
    Button mVerticalAirSwingButton;
    Button mHorizontalAirSwingButton;
    // others
    LinearLayout mExtraKeyLayout;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check API Key
        if (API_KEY == null || API_KEY.isEmpty()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Invalid API Key!")
                    .setMessage("No valid API key is assigned!\n\nTo apply an API key:\nhttp://www.bomeans.com/Mainpage/Apply/apikey")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Apply Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bomeans.com/Mainpage/Apply/apikey"));
                            startActivity(browserIntent);
                            MainActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        } else {

            // initialize IRKit
            IRKit.setup(API_KEY, getApplicationContext());  // use application context

            // setup the IR Blaster, write your own IR Blaster code by creating a class implements BIRIrHW or BIRIRBlaster
            // BIRIrHW is for general IR waveform data format, BIRIRBlaster is for special format suitable for Bomeans IR Blaster MCU.
            MyIrBlaster myIrBlaster = new MyIrBlaster(this);
            IRKit.setIRHW(myIrBlaster);

            // create remote controller
            createRemoteController();

            mCurrentPowerText = (TextView) findViewById(R.id.current_power);
            mPowerButton = (Button) findViewById(R.id.button_power);

            mCurrentTempText = (TextView) findViewById(R.id.current_temp);
            mTempUpButton = (Button) findViewById(R.id.button_temp_up);
            mTempDownButton = (Button) findViewById(R.id.button_temp_down);

            mCurrentModeText = (TextView) findViewById(R.id.current_mode);
            mModeButton = (Button) findViewById(R.id.button_mode);

            mCurrentFanSpeedText = (TextView) findViewById(R.id.current_fan_speed);
            mFanSpeedButton = (Button) findViewById(R.id.button_fanspeed);

            mCurrentVerticalAirSwingText = (TextView) findViewById(R.id.current_air_ud);
            mVerticalAirSwingButton = (Button) findViewById(R.id.button_air_ud);
            mCurrentHorizontalAirSwingText = (TextView) findViewById(R.id.current_air_lr);
            mHorizontalAirSwingButton = (Button) findViewById(R.id.button_air_lr);

            mExtraKeyLayout = (LinearLayout) findViewById(R.id.extend_keys);
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        }

    }

    private void createRemoteController() {

        /* these typeId, brandId, modelId should be retrieved from SDK.
            webGetTypeList() for type IDs
            webGetBranList() for brand IDs
            webGetRemoteModelList() for mode IDs
         */
        String typeId = "2";
        String brandId = "1496";

        /* if modelId is null, a combo remote controller will be created with minumum keys abailable.
            for example, the AC remote controller with model left as null will create a 4-most-popular-remote-controllers-in-one
            controller with only POWER, MODE, and TEMPERATURE keys.
         */
        String modelId = "PANASONIC-SONGXIA_2";
        //String modelId = "PANASONIC-A75C2044";

        final Activity thisActivity = this;

        IRKit.createRemote(
                typeId,
                brandId,
                modelId,
                true, //always re-download from cloud, or false if using cached data
                new IRemoteCreateCallBack() {


            @Override
            public void onCreateResult(Object remoteController, int resultCode) {
                if (resultCode == ConstValue.BIRNoError) {
                    mMyAcRemote = (BIRRemote) remoteController;

                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createMyGUI();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Server Access Error!")
                            .setMessage("Failed to retrieve data from the server!")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onPreCreate() {

            }

            @Override
            public void onProgressUpdate(Integer... integers) {

            }
        });
    }

    private void createMyGUI() {
        if (null == mMyAcRemote) {
            return;
        }

        String[] allSupportedKeys = mMyAcRemote.getAllKeys();

        // power key
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_POWER", allSupportedKeys);
            mPowerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        // get the power state
                        BIRKeyOption currentKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_POWER");
                        mMyAcRemote.transmitIR("IR_ACKEY_POWER", null); // passing null will cycle the internal state

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasPowerKey = false;
            mPowerButton.setEnabled(false);
        }

        // do we have temp key?
        if (containString("IR_ACKEY_TEMP", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_TEMP", allSupportedKeys);
            mTempUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {

                        // get the current temp
                        BIRKeyOption currentKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_TEMP");
                        if (currentKeyOptions.currentOption < currentKeyOptions.options.length - 1) {  // not yet reach the end
                            mMyAcRemote.transmitIR("IR_ACKEY_TEMP", null);

                            // update GUI
                            updateGUI();
                        }
                    }
                }
            });

            mTempDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the current temp
                    BIRKeyOption currentKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_TEMP");
                    if (currentKeyOptions.currentOption > 0) {  // not yet reach the front
                        String nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption - 1];
                        mMyAcRemote.transmitIR("IR_ACKEY_TEMP", nextTempOptionId);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasTemperatureKey = false;
            mTempUpButton.setEnabled(false);
            mTempDownButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_MODE", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_MODE", allSupportedKeys);
            mModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_MODE", null);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasModeKey = false;
            mModeButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_FANSPEED", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_FANSPEED", allSupportedKeys);
            mFanSpeedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_FANSPEED", null);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasFanSpeedKey = false;
            mFanSpeedButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_AIRSWING_LR", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_AIRSWING_LR", allSupportedKeys);
            mHorizontalAirSwingButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_AIRSWING_LR", null);

                        updateGUI();
                    }
                }
            });
        } else {
            mHasHorizontalAirSwingKey = false;
            mHorizontalAirSwingButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_AIRSWING_UD", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_AIRSWING_UD", allSupportedKeys);
            mVerticalAirSwingButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_AIRSWING_UD", null);

                        updateGUI();
                    }
                }
            });
        } else {
            mHasVerticalAirSwingKey = false;
            mVerticalAirSwingButton.setEnabled(false);
        }

        createNonFixedKeysGUI(allSupportedKeys);

        updateGUI();
    }

    private void createNonFixedKeysGUI(String[] keyIDs) {

        mExtraKeyLayout.removeAllViews();

        // handle all the "extension keys" here.
        // extension keys are those not in common keys of remote controllers.
        for (int i = 0; i < keyIDs.length; i++) {

            final String keyId = keyIDs[i];

            Button button = new Button(this);
            button.setText(keyIDs[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyAcRemote.transmitIR(keyId, null);
                }
            });

            mExtraKeyLayout.addView(button);
        }
    }

    private void updateGUI() {

        String[] allSupportedKeys = mMyAcRemote.getAllKeys();

        // power
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_POWER");
            showPowerText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // temp
        if (containString("IR_ACKEY_TEMP", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_TEMP");
            showTempText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // mode
        if (containString("IR_ACKEY_MODE", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_MODE");
            showModeText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // fanspeed
        if (containString("IR_ACKEY_FANSPEED", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_FANSPEED");
            showFanSpeedText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // air-swing
        if (containString("IR_ACKEY_AIRSWING_LR", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_AIRSWING_LR");
            showAirSwingText(newKeyOptions.options[newKeyOptions.currentOption]);
        }
        if (containString("IR_ACKEY_AIRSWING_UD", allSupportedKeys)) {
            BIRKeyOption newKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_AIRSWING_UD");
            showAirSwingText(newKeyOptions.options[newKeyOptions.currentOption]);
        }
    }

    private String extractTempStringFromOptionString(String tempOptionString) {

        /* for the temperature, the option could be
            (a) IR_ACOPT_TEMP_P1, _0, _N1 which represents +1/0/-1 for adjusting limited
            temperature range in auto mode for some specific AC remote controllers.
            (b) IR_ACSTATE_TEMP_XX which is normal temperature degrees.
        */
        if (tempOptionString.startsWith("IR_ACOPT_TEMP_")) {
            return tempOptionString.substring("IR_ACOPT_TEMP_".length());
        } else if (tempOptionString.startsWith("IR_ACSTATE_TEMP_")) {
            return tempOptionString.substring("IR_ACSTATE_TEMP_".length());
        }

        return "";
    }

    // power key GUI
    private void showPowerText(String currentPowerOption) {
        if (currentPowerOption.contains("IR_ACOPT_POWER_")) {
            String powerString = currentPowerOption.substring("IR_ACOPT_POWER_".length());
            mCurrentPowerText.setText(powerString);

            Boolean isOn = true;
            if (mMyAcRemote.getGuiFeature() != null) {
                switch (mMyAcRemote.getGuiFeature().displayType) {
                    case ConstValue.BIRGuiDisplayType_YES:  // has normal display (display on when power on, off when power off
                        if (powerString.equalsIgnoreCase("OFF")) {
                            isOn = false;
                        } else {
                            isOn = true;
                        }
                        break;

                    case ConstValue.BIRGuiDisplayType_NO:   // no display
                        // this type of remote controller does not have LCD display.
                        isOn = false;
                        break;

                    case ConstValue.BIRGuiDisplayType_ALWAYS:   // has always on display
                        // this kind of remote controller does not maintain the power on/off state,
                        // power on/off sends out the same IR signal (so it's toggle type). Thus the power
                        // state is maintained by the air conditioner itself, not in the remote controller.
                        isOn = true;
                        break;
                }
            }

            /*
                disable all key buttons (except the power key) if the display is off
                (so it's power off state, not allow keys to be pressed)

                Note: in power off state, the remote controller can still send out IR signals, but
                unless the signal contains power-on command (the pressed key is POWER key),
                the air-conditioner will receive the IR signal but not doing anything (won't power on)

                So you should disable the the keys when power is off.
             */
            mCurrentTempText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mTempUpButton.setEnabled(mHasTemperatureKey ? isOn : false);
            mTempDownButton.setEnabled(mHasTemperatureKey ? isOn : false);
            mCurrentModeText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mModeButton.setEnabled(mHasModeKey ? isOn : false);
            mCurrentFanSpeedText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mFanSpeedButton.setEnabled(mHasFanSpeedKey ? isOn : false);
            mCurrentHorizontalAirSwingText.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
            mHorizontalAirSwingButton.setEnabled(mHasHorizontalAirSwingKey ? isOn : false);
            mCurrentVerticalAirSwingText.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
            mVerticalAirSwingButton.setEnabled(mHasVerticalAirSwingKey ? isOn : false);

            for (int i = 0; i < mExtraKeyLayout.getChildCount(); i++) {
                mExtraKeyLayout.getChildAt(i).setEnabled(isOn);
            }
        }
    }

    // temperature key GUI
    private void showTempText(String currentTempOption) {

        String tempString = extractTempStringFromOptionString(currentTempOption);

        try {
            int temp = Integer.parseInt(tempString);
            mCurrentTempText.setText(Integer.toString(temp));
        } catch (Exception e) {
            if (tempString.startsWith("P1")) {
                mCurrentTempText.setText(tempString.replace("P", "+"));
            } else if (tempString.startsWith("N")) {
                mCurrentTempText.setText(tempString.replace("N", "-"));
            } else {
                mCurrentTempText.setText(tempString);
            }
        }
    }

    // mode key GUI
    private void showModeText(String currentModeOption) {
        if (currentModeOption.contains("IR_ACOPT_MODE_")) {
            String modeString = currentModeOption.substring("IR_ACOPT_MODE_".length());
            mCurrentModeText.setText(modeString);
        }
    }

    // fan-speed key GUI
    private void showFanSpeedText(String currentFanOption) {

        if (currentFanOption.contains("IR_ACOPT_FANSPEED_")) {
            String fanString = currentFanOption.substring("IR_ACOPT_FANSPEED_".length());

            // map the ID into specific string (or icon)
            if (fanString.equalsIgnoreCase("A")) {
                mCurrentFanSpeedText.setText("Auto");
            } else if (fanString.equalsIgnoreCase("L")) {
                mCurrentFanSpeedText.setText("Low");
            } else if (fanString.equalsIgnoreCase("M")) {
                mCurrentFanSpeedText.setText("Middle");
            } else if (fanString.equalsIgnoreCase("H")) {
                mCurrentFanSpeedText.setText("High");
            } else {
                mCurrentFanSpeedText.setText(fanString);
            }
        }
    }

    // air-swing key GUI
    private void showAirSwingText(String currentOption) {
        if (currentOption.contains("IR_ACOPT_AIRSWING_LR_")) {
            mCurrentHorizontalAirSwingText.setText(
                    currentOption.substring("IR_ACOPT_AIRSWING_LR_".length()));
        } else if (currentOption.contains("IR_ACOPT_AIRSWING_UD_")) {
            mCurrentVerticalAirSwingText.setText(
                    currentOption.substring("IR_ACOPT_AIRSWING_UD_".length()));
        }
    }

    private Boolean containString(String targetString, String[] stringArray) {
        for(String srcString : stringArray) {
            if (targetString.equalsIgnoreCase(srcString)) {
                return true;
            }
        }

        return false;
    }

    private String[] removeString(String targetString, String[] stringArray) {
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            if (!targetString.equalsIgnoreCase(stringArray[i])) {
                newList.add(stringArray[i]);
            }
        }

        return newList.toArray(new String[newList.size()]);
    }
}
