package com.bomeans.sample.acremotesample;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;

import com.bomeans.IRKit.BIRIrHW;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.IRKit;

import java.util.ArrayList;

/**
 * Created by ray on 2016/11/14.
 */

public class MyIrBlaster implements BIRIrHW {

    private ConsumerIrManager mGoogleCIRMgr = null;

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
    public int getHwType() {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int isConnection() {
        return (null != mGoogleCIRMgr && mGoogleCIRMgr.hasIrEmitter()) ? ConstValue.BIROK : ConstValue.BIRNoImplement;
    }
}
