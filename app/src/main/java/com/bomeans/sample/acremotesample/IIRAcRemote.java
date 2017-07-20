package com.bomeans.sample.acremotesample;

import com.bomeans.IRKit.ACStoreDataItem;
import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;
import com.bomeans.irapi.ACGUIFeatures;
import com.bomeans.irapi.ACKeyOptions;
import com.bomeans.irapi.IIRRemote;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by admin on 2017/7/19.
 */
public class IIRAcRemote implements IIRRemote{

    private IIRRemote mIirRemote;
    private boolean mConvertTempKey = false;

    private final static String IR_ACKEY_TEMP = "IR_ACKEY_TEMP";
    private final static String IR_ACKEY_TEMP_UP = "IR_ACKEY_TEMP_UP";
    private final static String IR_ACKEY_TEMP_DOWN = "IR_ACKEY_TEMP_DOWN";

    public IIRAcRemote(IIRRemote iirRemote){

        mIirRemote =iirRemote;
        mConvertTempKey = false;

        if(null == mIirRemote){
            return;
        }

        // if this AC remote contains
        String[] keys = mIirRemote.getKeyList();
        for(String key:keys){
            if(key.equalsIgnoreCase(IR_ACKEY_TEMP)){
                mConvertTempKey = true;
                break;
            }
        }

        // give default values for the AC remote
        // default to power-off (not necessary, but may be good for most cases)
        for(String key:keys){
            // does it contains power key?
            if(key.equalsIgnoreCase("IR_ACKEY_POWER")){
                ACKeyOptions keyOptions = mIirRemote.acGetKeyOption("IR_ACKEY_POWER");

                for(String keyOption : keyOptions.options){
                    // does it contains power off state?
                    if(keyOption.equalsIgnoreCase("IR_ACOPT_POWER_OFF")){
                        mIirRemote.acSetKeyOption("IR_ACKEY_POWER", "IR_ACOPT_POWER_OFF");
                        break;
                    }
                }

            }
        }
    }

    @Override
    public String[] acGetActiveKeys() {
        if (null == mIirRemote) {
            return new String[0];
        }

        if (mConvertTempKey) {
            String[] originalKeys = mIirRemote.acGetActiveKeys();

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
            return mIirRemote.acGetActiveKeys();
        }
    }

    @Override
    public ACGUIFeatures acGetGuiFeatures() {
        if (null == mIirRemote) {
            return null;
        }

        return mIirRemote.acGetGuiFeatures();
    }

    @Override
    public ACKeyOptions acGetKeyOption(String keyId) {

        if (null == mIirRemote) {
            return null;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP) || keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {
                return mIirRemote.acGetKeyOption(IR_ACKEY_TEMP);
            } else {
                return mIirRemote.acGetKeyOption(keyId);
            }
        } else {
            return mIirRemote.acGetKeyOption(keyId);
        }
    }

    @Override
    public byte[] acGetStateData() {
        if (null == mIirRemote) {
            return null;
        }

        return mIirRemote.acGetStateData();
    }

    @Override
    public String[] acGetTimerKeys() {
        if (null == mIirRemote) {
            return new String[0];
        }

        return mIirRemote.acGetTimerKeys();
    }

    @Override
    public Boolean acSetKeyOption(String keyId, String optionId) {
        if (null == mIirRemote) {
            return null;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP) || keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {
                return mIirRemote.acSetKeyOption(IR_ACKEY_TEMP, optionId);
            }
        }

        return mIirRemote.acSetKeyOption(keyId, optionId);
    }

    @Override
    public void acSetOffTime(int hour, int minute, int sec) {
        if (null != mIirRemote) {
            mIirRemote.acSetOffTime(hour, minute, sec);
        }
    }

    @Override
    public void acSetOnTime(int hour, int minute, int sec) {
        if (null != mIirRemote) {
            mIirRemote.acSetOnTime(hour, minute, sec);
        }
    }

    @Override
    public Boolean acSetStateData(byte[] bytes) {
        if (null == mIirRemote) {
            return false;
        }
        return mIirRemote.acSetStateData(bytes);
    }

    @Override
    public void endTransmitRepeatedIR() {
        if (null != mIirRemote) {
            mIirRemote.endTransmitRepeatedIR();
        }
    }

    @Override
    public String getBrandId() {
        if (null == mIirRemote) {
            return "";
        }

        return mIirRemote.getBrandId();
    }

    @Override
    public String[] getKeyList() {
        if(null == mIirRemote){
            return new String[0];
        }

        if(mConvertTempKey){
            String[] originalKeys = mIirRemote.getKeyList();

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
            return mIirRemote.getKeyList();

        }
    }

    @Override
    public String getRemoteId() {
        if (null == mIirRemote) {
            return "";
        }

        return mIirRemote.getRemoteId();
    }

    @Override
    public String[] getModels() {
        if (null == mIirRemote) {
            return new String[0];
        }

        return mIirRemote.getModels();
    }

    @Override
    public int getRepeatCount() {
        if (null == mIirRemote) {
            return 0;
        }

        return mIirRemote.getRepeatCount();
    }

    @Override
    public void setRepeatCount(int count) {
        if (null != mIirRemote) {
            setRepeatCount(count);
        }
    }

    @Override
    public Boolean startTransmitRepeatedIR(String keyId) {
        if (null == mIirRemote) {
            return null;
        }
        mIirRemote.startTransmitRepeatedIR(keyId);
        return true;

    }

    @Override
    public Boolean transmitIR(String keyId, String optionId) {
        if (mIirRemote == null) {
            return false;
        }

        if (mConvertTempKey) {
            if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_DOWN)) {

                if (optionId != null) {
                    return mIirRemote.transmitIR(IR_ACKEY_TEMP, optionId);
                } else {
                    // handling the temp down
                    // get the current temp
                    ACKeyOptions currentKeyOptions = mIirRemote.acGetKeyOption(IR_ACKEY_TEMP);
                    String nextTempOptionId;
                    if (currentKeyOptions.currentOption > 0) {  // not yet reach the front
                        nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption - 1];
                    } else {
                        nextTempOptionId = currentKeyOptions.options[0];
                    }
                    return mIirRemote.transmitIR(IR_ACKEY_TEMP, nextTempOptionId);
                }

            } else if (keyId.equalsIgnoreCase(IR_ACKEY_TEMP_UP)) {

                if (optionId != null) {
                    return mIirRemote.transmitIR(IR_ACKEY_TEMP, optionId);
                } else {
                    // handling the temp up: just call the original temp key (which is temp up)
                    // get the current temp
                    ACKeyOptions currentKeyOptions = mIirRemote.acGetKeyOption(IR_ACKEY_TEMP);
                    String nextTempOptionId;
                    if (currentKeyOptions.currentOption < currentKeyOptions.options.length - 1) {  // not yet reach the end
                        nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption + 1];
                    } else {
                        nextTempOptionId = currentKeyOptions.options[currentKeyOptions.options.length - 1];
                    }
                    return mIirRemote.transmitIR(IR_ACKEY_TEMP, nextTempOptionId);
                }
            } else {

                // for all other keys, just relay to the original function
                return mIirRemote.transmitIR(keyId, optionId);
            }

        } else {
            return mIirRemote.transmitIR(keyId, optionId);
        }    }

    @Override
    public Boolean transmitIR(String keyId) {
        if(null != this.mIirRemote) {
            boolean errCode = this.mIirRemote.transmitIR(keyId, null);
            return Boolean.valueOf(errCode);
        } else {
            return Boolean.valueOf(false);
        }
    }
}
