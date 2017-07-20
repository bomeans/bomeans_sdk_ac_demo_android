package com.bomeans.sample.acremotesample;

import android.util.Log;

import com.bomeans.IRKit.BIRIRBlaster;
import com.bomeans.IRKit.BIRReceiveDataCallback;
import com.bomeans.IRKit.IRKit;

/**
 * Created by ray on 2017/6/22.
 */

public class DummyIrBlaster implements BIRIRBlaster {

    private static String TAG = "DummyIrBlaster";
    private BIRReceiveDataCallback mReceiveDataCallback;

    @Override
    public int sendData(byte[] bytes) {

        // just print out the data to be sent.
        // you should route these data bytes to your hardware for sending
        String info = "";
        for (int i = 0; i < bytes.length; i++) {
            info += String.format("0x%02X,", bytes[i]);
        }

        Log.i(TAG, info);

        return IRKit.BIROK;
    }

    @Override
    public int isConnection() {
        return IRKit.BIROK;
    }

    @Override
    public void setReceiveDataCallback(BIRReceiveDataCallback birReceiveDataCallback) {
        // The callback is passing from SDK. You should keep this callback.
        // When the learning data is received, call callback.onLearningDataReceived(),
        // or callback.onLearningFailed() if failed.
        // For all other traffic, call onLearningFailed() with received raw data bytes as parameter.
        mReceiveDataCallback = birReceiveDataCallback;
    }

    // whenever there is data to pass back to SDK, call this.
    public void onLearningDataReceived(byte[] receivedDataBytes) {
        if (null != mReceiveDataCallback) {
            mReceiveDataCallback.onDataReceived(receivedDataBytes);
        }
    }
}
