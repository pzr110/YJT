package com.linkflow.fitt360sdk.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.text.DecimalFormat;

public class TimeUtils implements Handler.Callback, Runnable {

    private static final int MSG_SEND_SIGNAL = 0;

    private Handler mUiHandler;
    private HandlerThread mInternalThread;
    private Handler mInternalHandler;
    private UpdateUiCallBack callBack;
    private long mDelay;

    private long mTime = 0;
    private String mCurrentTime = "00:00:00";

    private DecimalFormat mFormat = new DecimalFormat("00");

    public TimeUtils(UpdateUiCallBack callBack, long delay){
        this.callBack = callBack;
        this.mDelay = delay;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        // 1.获取一个HandlerThread
        mInternalThread = new HandlerThread("");
        // 2.开启这个线程
        mInternalThread.start();
        // 3.获取到HandlerThread线程的Handler
        mInternalHandler = new Handler(mInternalThread.getLooper(), this);
        // 4.使用mInternalHandler定时发送消息
        mInternalHandler.sendEmptyMessageAtTime(MSG_SEND_SIGNAL, SystemClock.uptimeMillis() + mDelay);
    }


    public void stop() {
        // 将UI线程 消息队列中的任务移除掉
        mUiHandler.removeCallbacks(this);
        // 移除消息
        if (mInternalHandler!=null){
            mInternalHandler.removeMessages(MSG_SEND_SIGNAL);
        }
    }

    public void destroy() {
        stop();
        mInternalThread.quit();
        mInternalThread = null;
        mInternalHandler = null;
        mUiHandler = null;
        callBack = null;
    }

    public void restart() {
        mInternalHandler.sendEmptyMessageAtTime(MSG_SEND_SIGNAL, SystemClock.uptimeMillis() + mDelay);
    }


    // mInternalHandler发送消息之后，由HandlerThread对应的looper处理下面的消息
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_SEND_SIGNAL) {
            if (callBack != null) {
                // post会调用sendMessageAtTime方法，将该任务放入到消息队列中
                // 由主线程的looper去处理
                mUiHandler.post(this);
            }
        }
        return false;
    }

    // 主线程处理的任务
    @Override
    public void run() {
        // 更新界面
        mCurrentTime = makeTime(mTime, 0);
        mTime++;
        callBack.updateUI(mCurrentTime);
        // 继续使用mInternalHandler发送消息
        mInternalHandler.sendEmptyMessageAtTime(MSG_SEND_SIGNAL, SystemClock.uptimeMillis() + mDelay);
    }

    private String makeTime(long value, int cnt) {
        if (value / 60 > 0) {
            return makeTime(value / 60, ++cnt) + ":" + mFormat.format(value % 60);
        } else {
            String fill = "";
            if (cnt == 0) {
                fill = "00:00:";
            } else if (cnt == 1) {
                fill = "00:";
            }
            return fill + mFormat.format(value);
        }
    }

    public interface UpdateUiCallBack {
        void updateUI(String text);
    }

}
