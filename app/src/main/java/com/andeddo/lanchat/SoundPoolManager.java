package com.andeddo.lanchat;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

public class SoundPoolManager {
    private static final String TAG = "SoundPoolManager";

    public final static int SEND = 1;
    public final static int LOAD = 2;

    private boolean playing = false;
    private boolean loaded = false;
    private float actualVolume;             //当前音量
    private float maxVolume;                //最大音量
    private float volume;                   //播放音量
    private AudioManager audioManager;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> musicMap;     //加载声音列表
    private int ringingStreamId;
    private static SoundPoolManager instance;

    private SoundPoolManager(Context context) {
        if (audioManager == null) {
            // AudioManager audio settings for adjusting the volume
            audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);      //初始化声音管理器
        }
        Log.d(TAG, "31 SoundPoolManager,audioManager :" + audioManager);
        if (audioManager != null) {
            actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);     //获取音乐当前音量
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);     //获取音乐最大音量
        }
        volume = actualVolume / maxVolume;      //得到播放音量
        Log.d(TAG, "SoundPoolManager: volume:" + volume + "actualVolume:" + actualVolume + "maxVolume:" + maxVolume);

        // Load the sounds
        //因为在5.0上new SoundPool();被弃用 5.0上利用Builder
        //创建SoundPool
        int maxStreams = 5;
        Log.d(TAG, "42 SoundPoolManager: build SDK:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }

        });
        //加载资源ID
        musicMap = new HashMap<Integer, Integer>();
        musicMap.put(1, soundPool.load(context, R.raw.msg_send, 1));
        musicMap.put(2, soundPool.load(context, R.raw.msg_load, 1));
    }

    public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPoolManager(context);
        }
        return instance;
    }

    //仅播放一次声音
    public void playSingle(int ringingSoundId) {
        //当前播放的音乐
        ringingStreamId = soundPool.play(musicMap.get(ringingSoundId), volume, volume, 1, 0, 1f);
    }

    /**
     * 可循环播放:
     * -1：一直循环
     * 0：播放一次
     * 1：播放两次
     * n：播放n+1次
     *
     * @param ringingSoundId 需要播放的声音
     * @param loop           循环次数
     */
    public void playLooping(int ringingSoundId, int loop) {
        ringingStreamId = soundPool.play(musicMap.get(ringingSoundId), volume, volume, 1, loop, 1f);
        playing = true;
    }

    //停止播放声音--适用于循环播放
    public void stopRinging() {
        if (playing) {
            soundPool.stop(ringingStreamId);
            playing = false;
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.unload(ringingStreamId);
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
