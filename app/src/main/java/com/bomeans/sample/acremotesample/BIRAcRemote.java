package com.bomeans.sample.acremotesample;

import com.bomeans.IRKit.ACStoreDataItem;
import com.bomeans.IRKit.BIRGUIFeature;
import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.BIRRemote;
import com.bomeans.IRKit.IRKit;

import java.util.ArrayList;

/**
 * Just an wrapper for the AC remote (BIRRemote).
 *
 * Some users prefer having the traditional TEMP UP/DOWN button instead of setting the temperature
 * directly. Just wrap the original BIRRemote in this class you will get TEMP_UP/DOWN keys instead
 * of a signal TEMP key.
 */
public class BIRAcRemote implements BIRRemote {

    private BIRRemote mBirRemote;
    private boolean mConvertTempKey = false;

    private final static String IR_ACKEY_TEMP = "IR_ACKEY_TEMP";
    private final static String IR_ACKEY_TEMP_UP = "IR_ACKEY_TEMP_UP";
    private final static String IR_ACKEY_TEMP_DOWN = "IR_ACKEY_TEMP_DOWN";

    public BIRAcRemote(BIRRemote birRemote) {

        mBirRemote = birRemote;
        mConvertTempKey = false;

        if (null == mBirRemote) {
            return;
        }

        // if this AC remote contains
        String[] keys = mBirRemote.getAllKeys();
        for (String key : keys) {
            if (key.equalsIgnoreCase(IR_ACKEY_TEMP)) {
                mConvertTempKey = true;
                break;
            }
        }

        // give default values for the AC remote
        // default to power-off (not necessary, but may be good for most cases)
        for (String key : keys) {
            // does it contains power key?
            if (key.equalsIgnoreCase("IR_ACKEY_POWER")) {

                BIRKeyOption keyOptions = mBirRemote.getKeyOption("IR_ACKEY_POWER");

                for (String keyOption : keyOptions.options) {
                    // does it contains power off state?
                    if (keyOption.equalsIgnoreCase("IR_ACOPT_POWER_OFF")) {
                        mBirRemote.setKeyOption("IR_ACKEY_POWER", "IR_ACOPT_POWER_OFF");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String[] getAllKeys() {
        if (null == mBirRemote) {
            return new String[0];
        }

        if (mConvertTempKey) {
            String[] originalKeys = mBirRemote.getAllKeys();

            // replace the IR_ACKEY_TEMP with IR_ACKEY_TEMP_UP & IR_ACKEY_TEMP_DOWN
            ArrayList<String> newKeyList = new ArrayList<>();
            for (String originalKey : originalKeys) {
                if (originalKey.equalsIgnoreCase(IR_ACKEY_TEMP)) {
                    newKeyList.add(IR_ACKEY_TEMP_UP);
                    newKeyList.add(IR_ACKEY_TEMP_DOWN);
                } else {
                    newKeyList.add(originalKey);
                }
            }

            return newKeyList.toArray(new String[newKeyList.size()]);

        } else  {
            return mBirRemote.getAllKeys();
        }
    }

    @Override
    public int transmitIR(String keyId, String optionId) {

        if (mBirRemote == null) {
            return IRKit.BIRNoImplement;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {
                // handling the temp down
                // get the current temp
                BIRKeyOption currentKeyOptions = mBirRemote.getKeyOption(IR_ACKEY_TEMP);
                String nextTempOptionId;
                if (currentKeyOptions.currentOption > 0) {  // not yet reach the front
                    nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption - 1];
                } else {
                    nextTempOptionId = currentKeyOptions.options[0];
                }
                return mBirRemote.transmitIR(IR_ACKEY_TEMP, nextTempOptionId);

            } else if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP)) {
                // handling the temp up: just call the original temp key (which is temp up)
                // get the current temp
                BIRKeyOption currentKeyOptions = mBirRemote.getKeyOption(IR_ACKEY_TEMP);
                String nextTempOptionId;
                if (currentKeyOptions.currentOption < currentKeyOptions.options.length - 1) {  // not yet reach the end
                    nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption + 1];
                } else {
                    nextTempOptionId = currentKeyOptions.options[currentKeyOptions.options.length - 1];
                }
                return mBirRemote.transmitIR(IR_ACKEY_TEMP, nextTempOptionId);
            } else {

                // for all other keys, just relay to the original function
                return mBirRemote.transmitIR(keyId, optionId);
            }

        } else {
            return mBirRemote.transmitIR(keyId, optionId);
        }
    }

    @Override
    public int setKeyOption(String keyId, String optionId) {
        if (null == mBirRemote) {
            return IRKit.BIRNoImplement;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP) || keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {
                return mBirRemote.setKeyOption(IR_ACKEY_TEMP, optionId);
            }
        }

        return mBirRemote.setKeyOption(keyId, optionId);
    }

    @Override
    public int beginTransmitIR(String keyId) {

        // AC remote does not support repeated transmission.
        if (null == mBirRemote) {
            return IRKit.BIRNoImplement;
        }

        return mBirRemote.beginTransmitIR(keyId);
    }

    @Override
    public void endTransmitIR() {
        // AC remote does not support repeated transmission.
        if (null != mBirRemote) {
            mBirRemote.endTransmitIR();
        }
    }

    @Override
    public String getModuleName() {

        if (null == mBirRemote) {
            return "";
        }

        return mBirRemote.getModuleName();
    }

    @Override
    public String getBrandName() {
        if (null == mBirRemote) {
            return "";
        }

        return mBirRemote.getBrandName();
    }

    @Override
    public String[] getMachineModels() {
        if (null == mBirRemote) {
            return new String[0];
        }

        return mBirRemote.getMachineModels();
    }

    @Override
    public void setRepeatCount(int i) {

        if (null != mBirRemote) {
            setRepeatCount(i);
        }
    }

    @Override
    public int getRepeatCount() {
        if (null == mBirRemote) {
            return 0;
        }

        return mBirRemote.getRepeatCount();
    }

    @Override
    public String[] getActiveKeys() {
        if (null == mBirRemote) {
            return new String[0];
        }

        if (mConvertTempKey) {
            String[] originalKeys = mBirRemote.getActiveKeys();

            // replace the IR_ACKEY_TEMP with IR_ACKEY_TEMP_UP & IR_ACKEY_TEMP_DOWN
            ArrayList<String> newKeyList = new ArrayList<>();
            for (String originalKey : originalKeys) {
                if (originalKey.equalsIgnoreCase(IR_ACKEY_TEMP)) {
                    newKeyList.add(IR_ACKEY_TEMP_UP);
                    newKeyList.add(IR_ACKEY_TEMP_DOWN);
                } else {
                    newKeyList.add(originalKey);
                }
            }

            return newKeyList.toArray(new String[newKeyList.size()]);

        } else  {
            return mBirRemote.getActiveKeys();
        }
    }

    @Override
    public BIRKeyOption getKeyOption(String keyId) {

        if (null == mBirRemote) {
            return null;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP) || keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {
                return mBirRemote.getKeyOption(IR_ACKEY_TEMP);
            } else {
                return mBirRemote.getKeyOption(keyId);
            }
        } else {
            return mBirRemote.getKeyOption(keyId);
        }
    }

    @Override
    public BIRGUIFeature getGuiFeature() {
        if (null == mBirRemote) {
            return null;
        }

        return mBirRemote.getGuiFeature();
    }

    @Override
    public String[] getTimerKeys() {
        if (null == mBirRemote) {
            return new String[0];
        }

        return mBirRemote.getTimerKeys();
    }

    @Override
    public void setOffTime(int hour, int min, int sec) {
        if (null != mBirRemote) {
            mBirRemote.setOffTime(hour,min, sec);
        }
    }

    @Override
    public void setOnTime(int hour, int min, int sec) {
        if (null != mBirRemote) {
            mBirRemote.setOnTime(hour, min, sec);
        }
    }

    @Override
    public ACStoreDataItem[] getACStoreDatas() {
        if (null == mBirRemote) {
            return new ACStoreDataItem[0];
        }

        return mBirRemote.getACStoreDatas();
    }

    @Override
    public boolean restoreACStoreDatas(ACStoreDataItem[] acStoreDataItems) {

        if (null == mBirRemote) {
            return false;
        }

        return mBirRemote.restoreACStoreDatas(acStoreDataItems);
    }
}
