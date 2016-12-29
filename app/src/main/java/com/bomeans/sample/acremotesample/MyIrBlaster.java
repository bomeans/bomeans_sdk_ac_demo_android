package com.bomeans.sample.acremotesample;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;

import com.bomeans.IRKit.BIRIrHW;
import com.bomeans.IRKit.BIRReceiveDataCallback2;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;

import java.util.ArrayList;

/**
 * Created by ray on 2016/11/14.
 */

public class MyIrBlaster implements BIRIrHW {

    private ConsumerIrManager mGoogleCIRMgr = null;

    // the callback function (assigned by SDK) for handling the received data from hardware.
    // Since the Google ConsumerIrManager does not have interface for doing data receiving such
    // as learning, so we just have some suedo-code here.
    private BIRReceiveDataCallback2 mReceiveDataCallback;

    public MyIrBlaster(Context context) {
        mGoogleCIRMgr = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int SendIR(final int freq, final int[] pattern) {
        if (null != mGoogleCIRMgr && mGoogleCIRMgr.hasIrEmitter()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleCIRMgr.transmit(freq, pattern);
                }
            }).start();

            return ConstValue.BIROK;
        } else {
            return ConstValue.BIRTransmitFail;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int sendMultipIR(final int[] freqArray, final ArrayList<int[]> patternArray) {
        if (null != mGoogleCIRMgr && mGoogleCIRMgr.hasIrEmitter()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < freqArray.length; i++) {
                        mGoogleCIRMgr.transmit(freqArray[i], patternArray.get(i));
                    }
                }
            }).start();

            return IRKit.BIROK;
        } else {
            return IRKit.BIRTransmitFail;
        }
    }

    @Override
    public int sendUARTCommand(byte[] bytes) {
        // The data to be sent to the MCU, usually the user-defined commands for the MCU with customized functionality.
        // The data should be routed to the MCU.

        return IRKit.BIRTransmitFail;
    }

    @Override
    public int getHwType() {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int isConnection() {
        return (null != mGoogleCIRMgr && mGoogleCIRMgr.hasIrEmitter()) ? ConstValue.BIROK : ConstValue.BIRNoImplement;
    }

    @Override
    public void setReceiveDataCallback(BIRReceiveDataCallback2 birReceiveDataCallback2) {
        // The callback is passing from SDK. You should keep this callback.
        // When the learning data is received, call callback.onLearningDataReceived(),
        // or callback.onLearningFailed() if failed.
        // For all other traffic, call onLearningFailed() with received raw data bytes as parameter.
        mReceiveDataCallback = birReceiveDataCallback2;
    }

    // if the learning data is received from hardware, invoke this function for further handling.
    public void onLearningDataReceived(int freq, int[] patterns) {
        if (null != mReceiveDataCallback) {
            mReceiveDataCallback.onLearningDataReceived(freq, patterns);
        }
    }

    // if leaning is failed (time-out or signal incorrect, depending on the response data from the hardware),
    // invoke this method for further handling.
    public void onLearningDataFailed() {
        if (null != mReceiveDataCallback) {
            mReceiveDataCallback.onLearningFailed();
        }
    }

    // all general command data (except for the learning-data/learning-fail) should be passed back to
    // the SDK for further handling by invoking this method.
    public void onGeneralCommandDataReceived(byte[] data) {
        if (null != mReceiveDataCallback) {
            mReceiveDataCallback.onDataReceived(data);
        }
    }
}
