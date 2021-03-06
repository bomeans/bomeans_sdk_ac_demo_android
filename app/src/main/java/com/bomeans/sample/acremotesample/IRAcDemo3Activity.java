package com.bomeans.sample.acremotesample;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.IRKit.IRemoteCreateCallBack;
import com.bomeans.irapi.ACKeyOptions;
import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.IIRRemote;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.IRRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/7/20.
 */
public class IRAcDemo3Activity extends AppCompatActivity{
    private IIRRemote mMyAcRemote = null;

    // GUI: fixed buttons (common for most air-conditioners)
    // These are most common keys for AC remote controllers.
    // power
    Boolean mHasPowerKey = true;
    TextView mCurrentPowerText;
    Button mPowerButton;
    // temp +/-
    Boolean mHasTemperatureKey = true;
    TextView mCurrentTempText;
    SeekBar mTempBar;
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

    int mMinTemp;
    int mMaxTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_demo3);
// add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // create remote controller
        createRemoteController();

        mCurrentPowerText = (TextView) findViewById(R.id.current_power);
        mPowerButton = (Button) findViewById(R.id.button_power);

        mCurrentTempText = (TextView) findViewById(R.id.current_temp);
        mTempBar = (SeekBar) findViewById(R.id.seekbar_temp);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
        String modelId = "PANASONIC-A75C2412";//"PANASONIC-SONGXIA_2";
        //String modelId = "PANASONIC-A75C2044";

        final Activity thisActivity = this;
        IRAPI.createRemote(
                typeId,
                brandId,
                modelId,
                true, //always re-download from cloud, or false if using cached data
                new ICreateRemoteCallback() {
                    @Override
                    public void onRemoteCreated(IRRemote irRemote) {
                        mMyAcRemote = (IIRRemote) irRemote;

                        thisActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createMyGUI();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(int i) {
                        if (i != ConstValue.BIRNoError) {
                            new AlertDialog.Builder(IRAcDemo3Activity.this)
                                    .setTitle("Server Access Error!")
                                    .setMessage("Failed to retrieve data from the server!")
                                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            IRAcDemo3Activity.this.finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .show();
                        }
                    }
                });

    }

    private void createMyGUI() {
        if (null == mMyAcRemote) {
            return;
        }

        String[] allSupportedKeys = mMyAcRemote.getKeyList();

        // power key
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_POWER", allSupportedKeys);
            mPowerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        // get the power state
                        //BIRKeyOption currentKeyOptions = mMyAcRemote.getKeyOption("IR_ACKEY_POWER");
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

            ACKeyOptions tempKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
            // get the min/max temp
            final int minTemp = 100;
            int maxTemp = -100;
            for (String optionId : tempKeyOptions.options) {
                extractTempStringFromOptionString(optionId);
            }

            mTempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    // we just update the display, not transmitting the IR
                    int newTempIndex = mTempBar.getProgress();
                    mCurrentTempText.setText(String.format("%d", newTempIndex + mMinTemp));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    int newTempIndex = mTempBar.getProgress();
                    ACKeyOptions currentKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
                    if (newTempIndex < currentKeyOptions.options.length) {
                        String newOptionId = currentKeyOptions.options[newTempIndex];
                        mMyAcRemote.transmitIR("IR_ACKEY_TEMP", newOptionId);

                        // update GUI
                        updateGUI();
                    }
                }
            });

        } else {
            mHasTemperatureKey = false;
            mTempBar.setEnabled(false);
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

        String[] allSupportedKeys = mMyAcRemote.getKeyList();

        // power
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_POWER");
            showPowerText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // temp
        if (containString("IR_ACKEY_TEMP", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
            updateTempGUI(newKeyOptions);
        }

        // mode
        if (containString("IR_ACKEY_MODE", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_MODE");
            showModeText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // fanspeed
        if (containString("IR_ACKEY_FANSPEED", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_FANSPEED");
            showFanSpeedText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // air-swing
        if (containString("IR_ACKEY_AIRSWING_LR", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_AIRSWING_LR");
            showAirSwingText(newKeyOptions.options[newKeyOptions.currentOption]);
        }
        if (containString("IR_ACKEY_AIRSWING_UD", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_AIRSWING_UD");
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
            if (mMyAcRemote.acGetGuiFeatures() != null) {
                switch (mMyAcRemote.acGetGuiFeatures().displayMode) {
                    case ValidWhilePoweredOn:  // has normal display (display on when power on, off when power off
                        isOn = powerString.equalsIgnoreCase("ON");
                        break;

                    case NoDisplay:   // no display
                        // this type of remote controller does not have LCD display.
                        isOn = false;
                        break;

                    case AlwaysOn:   // has always on display
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
            mTempBar.setEnabled(mHasTemperatureKey ? isOn : false);
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
    private void updateTempGUI(ACKeyOptions tempKeyOptions) {

        // get the min/max temp
        int minTemp = 100;
        int maxTemp = -100;
        int currentTemp;
        String currentTempOption = tempKeyOptions.options[tempKeyOptions.currentOption];
        String tempString = extractTempStringFromOptionString(currentTempOption);

        if (tempString.startsWith("P") || tempString.startsWith("N")) {

            currentTemp = Integer.parseInt(tempString.substring(1));
            if (tempString.startsWith("N")) {
                currentTemp *= (-1);
            }

            int temp;
            for (String optionId : tempKeyOptions.options) {
                try {
                    tempString = extractTempStringFromOptionString(optionId);
                    temp = Integer.parseInt(tempString.substring(1));
                    if (tempString.startsWith("N")) {
                        temp *= (-1);
                    }

                    if (temp > maxTemp) {
                        maxTemp = temp;
                    }
                    if (temp < minTemp) {
                        minTemp = temp;
                    }
                } catch (Exception e) {

                }
            }
        } else {
            currentTemp = Integer.parseInt(tempString);

            int temp;
            for (String optionId : tempKeyOptions.options) {
                try {
                    temp = Integer.parseInt(extractTempStringFromOptionString(optionId));
                    if (temp > maxTemp) {
                        maxTemp = temp;
                    }
                    if (temp < minTemp) {
                        minTemp = temp;
                    }
                } catch (Exception e){
                }
            }
        }
        mMinTemp = minTemp;
        mMaxTemp = maxTemp;
        mTempBar.setMax(maxTemp - minTemp);
        mTempBar.setProgress(currentTemp - minTemp);

        try {
            int temp = Integer.parseInt(tempString);
            mCurrentTempText.setText(String.format("%d", temp));
        } catch (Exception e) {
            if (tempString.startsWith("P")) {
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
