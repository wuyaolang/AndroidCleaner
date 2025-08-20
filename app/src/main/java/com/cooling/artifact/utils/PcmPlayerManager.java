package com.cooling.artifact.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.C;

/**
 * PCM播放器管理器，整合所有PCM播放相关组件
 */
public class PcmPlayerManager {
    
    private static final String TAG = "PcmPlayerManager";
    
    private Context context;
    private ExoPlayer exoPlayer;
    private PcmDataSourceFactory pcmDataSourceFactory;
    private NetworkPcmDataProvider networkDataProvider;
    private MediaSource currentMediaSource;
    
    // PCM音频参数
    private int sampleRate = 44100;
    private int channelCount = 2;
    private int bitsPerSample = 16;
    
    // 播放状态监听器
    private Player.Listener playerListener;
    
    public PcmPlayerManager(Context context) {
        this.context = context;
        initializePlayer();
    }
    
    /**
     * 初始化ExoPlayer
     */
    private void initializePlayer() {
        // 创建轨道选择器
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        
        // 创建ExoPlayer实例
        exoPlayer = new ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build();
        
        // 设置音频属性
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes, true);
        
        // 设置播放状态监听器
        setupPlayerListener();
    }
    
    /**
     * 设置播放状态监听器
     */
    private void setupPlayerListener() {
        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_IDLE:
                        Log.d(TAG, "Player state: IDLE");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "Player state: BUFFERING");
                        break;
                    case Player.STATE_READY:
                        Log.d(TAG, "Player state: READY");
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "Player state: ENDED");
                        break;
                }
            }
            
            @Override
            public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                Log.e(TAG, "Player error: " + error.getMessage(), error);
            }
        };
        
        exoPlayer.addListener(playerListener);
    }
    
    /**
     * 开始播放网络PCM流
     * 
     * @param streamUrl PCM流URL
     * @return 是否成功开始播放
     */
    public boolean startPcmStream(String streamUrl) {
        try {
            // 创建网络PCM数据提供者
            networkDataProvider = new NetworkPcmDataProvider(streamUrl);
            
            // 连接网络流
            if (!networkDataProvider.connect()) {
                Log.e(TAG, "Failed to connect to PCM stream: " + streamUrl);
                return false;
            }
            
            // 创建PCM数据源工厂
            pcmDataSourceFactory = new PcmDataSourceFactory(
                    networkDataProvider, sampleRate, channelCount, bitsPerSample);
            
            // 创建PCM媒体源
            currentMediaSource = PcmMediaSourceFactory.createPcmMediaSource(
                    pcmDataSourceFactory, sampleRate, channelCount, bitsPerSample);
            
            // 设置媒体源并开始播放
            exoPlayer.setMediaSource(currentMediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            
            Log.i(TAG, "Started PCM stream playback: " + streamUrl);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting PCM stream: " + streamUrl, e);
            return false;
        }
    }
    
    /**
     * 开始播放本地PCM文件
     * 
     * @param filePath PCM文件路径
     * @return 是否成功开始播放
     */
    public boolean startPcmFile(String filePath) {
        try {
            // 创建本地PCM数据提供者（这里需要实现LocalPcmDataProvider）
            // 暂时使用网络提供者作为示例
            networkDataProvider = new NetworkPcmDataProvider("file://" + filePath);
            
            // 创建PCM数据源工厂
            pcmDataSourceFactory = new PcmDataSourceFactory(
                    networkDataProvider, sampleRate, channelCount, bitsPerSample);
            
            // 创建PCM媒体源
            currentMediaSource = PcmMediaSourceFactory.createPcmMediaSource(
                    pcmDataSourceFactory, sampleRate, channelCount, bitsPerSample);
            
            // 设置媒体源并开始播放
            exoPlayer.setMediaSource(currentMediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            
            Log.i(TAG, "Started PCM file playback: " + filePath);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting PCM file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * 停止播放
     */
    public void stopPlayback() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
        }
        
        if (networkDataProvider != null) {
            networkDataProvider.stopStreaming();
            networkDataProvider.disconnect();
        }
        
        Log.i(TAG, "Stopped PCM playback");
    }
    
    /**
     * 暂停播放
     */
    public void pausePlayback() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
        Log.d(TAG, "Paused PCM playback");
    }
    
    /**
     * 恢复播放
     */
    public void resumePlayback() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
        Log.d(TAG, "Resumed PCM playback");
    }
    
    /**
     * 设置PCM音频参数
     * 
     * @param sampleRate 采样率
     * @param channelCount 声道数
     * @param bitsPerSample 位深度
     */
    public void setPcmParameters(int sampleRate, int channelCount, int bitsPerSample) {
        this.sampleRate = sampleRate;
        this.channelCount = channelCount;
        this.bitsPerSample = bitsPerSample;
        
        Log.d(TAG, String.format("PCM parameters updated: %dHz, %d channels, %d bits", 
                sampleRate, channelCount, bitsPerSample));
    }
    
    /**
     * 获取ExoPlayer实例
     */
    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
    
    /**
     * 检查是否正在播放
     */
    public boolean isPlaying() {
        return exoPlayer != null && exoPlayer.isPlaying();
    }
    
    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return networkDataProvider != null && networkDataProvider.isConnected();
    }
    
    /**
     * 获取播放位置（毫秒）
     */
    public long getCurrentPosition() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }
    
    /**
     * 获取总时长（毫秒）
     */
    public long getDuration() {
        return exoPlayer != null ? exoPlayer.getDuration() : 0;
    }
    
    /**
     * 设置播放位置
     * 
     * @param position 位置（毫秒）
     */
    public void seekTo(long position) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(position);
        }
    }
    
    /**
     * 设置音量
     * 
     * @param volume 音量（0.0 - 1.0）
     */
    public void setVolume(float volume) {
        if (exoPlayer != null) {
            exoPlayer.setVolume(volume);
        }
    }
    
    /**
     * 释放资源
     */
    public void release() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        
        if (networkDataProvider != null) {
            networkDataProvider.disconnect();
            networkDataProvider = null;
        }
        
        Log.i(TAG, "PCM player manager released");
    }
}