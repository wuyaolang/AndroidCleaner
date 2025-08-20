package com.cooling.artifact.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;

/**
 * PCM DataSource 使用示例
 * 展示如何使用自定义的PCM DataSource来实现PCM格式的音频推流
 */
public class PcmUsageExample {
    
    private static final String TAG = "PcmUsageExample";
    
    private Context context;
    private PcmPlayerManager playerManager;
    
    public PcmUsageExample(Context context) {
        this.context = context;
        this.playerManager = new PcmPlayerManager(context);
    }
    
    /**
     * 示例1: 基本PCM流播放
     */
    public void basicPcmStreamExample() {
        Log.i(TAG, "=== 基本PCM流播放示例 ===");
        
        // 设置PCM参数（44.1kHz, 2声道, 16位）
        playerManager.setPcmParameters(44100, 2, 16);
        
        // 开始播放网络PCM流
        String streamUrl = "http://example.com/pcm_stream";
        boolean success = playerManager.startPcmStream(streamUrl);
        
        if (success) {
            Log.d(TAG, "成功开始播放PCM流");
            
            // 设置音量
            playerManager.setVolume(0.8f);
            
            // 检查播放状态
            if (playerManager.isPlaying()) {
                Log.d(TAG, "PCM流正在播放");
            }
        } else {
            Log.e(TAG, "无法连接到PCM流");
        }
    }
    
    /**
     * 示例2: 高保真PCM播放
     */
    public void highQualityPcmExample() {
        Log.i(TAG, "=== 高保真PCM播放示例 ===");
        
        // 设置高保真PCM参数（48kHz, 2声道, 24位）
        playerManager.setPcmParameters(48000, 2, 24);
        
        // 开始播放
        String streamUrl = "http://example.com/high_quality_pcm";
        boolean success = playerManager.startPcmStream(streamUrl);
        
        if (success) {
            Log.d(TAG, "开始播放高保真PCM流");
            
            // 设置较低音量以保护听力
            playerManager.setVolume(0.6f);
        }
    }
    
    /**
     * 示例3: 单声道PCM播放
     */
    public void monoPcmExample() {
        Log.i(TAG, "=== 单声道PCM播放示例 ===");
        
        // 设置单声道PCM参数（44.1kHz, 1声道, 16位）
        playerManager.setPcmParameters(44100, 1, 16);
        
        // 开始播放
        String streamUrl = "http://example.com/mono_pcm";
        boolean success = playerManager.startPcmStream(streamUrl);
        
        if (success) {
            Log.d(TAG, "开始播放单声道PCM流");
        }
    }
    
    /**
     * 示例4: 播放控制演示
     */
    public void playbackControlExample() {
        Log.i(TAG, "=== 播放控制演示 ===");
        
        // 开始播放
        String streamUrl = "http://example.com/pcm_stream";
        boolean success = playerManager.startPcmStream(streamUrl);
        
        if (success) {
            Log.d(TAG, "开始播放，演示播放控制");
            
            // 等待一段时间后暂停
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // 等待5秒
                    
                    // 暂停播放
                    playerManager.pausePlayback();
                    Log.d(TAG, "播放已暂停");
                    
                    // 等待3秒后恢复
                    Thread.sleep(3000);
                    
                    // 恢复播放
                    playerManager.resumePlayback();
                    Log.d(TAG, "播放已恢复");
                    
                } catch (InterruptedException e) {
                    Log.e(TAG, "播放控制演示被中断", e);
                }
            }).start();
        }
    }
    
    /**
     * 示例5: 自定义PCM数据提供者
     */
    public void customDataProviderExample() {
        Log.i(TAG, "=== 自定义PCM数据提供者示例 ===");
        
        // 创建自定义PCM数据提供者
        PcmDataSource.PcmDataProvider customProvider = new PcmDataSource.PcmDataProvider() {
            private byte[] mockPcmData = generateMockPcmData();
            private int currentPosition = 0;
            
            @Override
            public int readPcmData(byte[] buffer, int offset, int length) {
                if (currentPosition >= mockPcmData.length) {
                    return -1; // 没有更多数据
                }
                
                int bytesToRead = Math.min(length, mockPcmData.length - currentPosition);
                System.arraycopy(mockPcmData, currentPosition, buffer, offset, bytesToRead);
                currentPosition += bytesToRead;
                
                return bytesToRead;
            }
            
            @Override
            public long getPcmDataLength() {
                return mockPcmData.length;
            }
            
            @Override
            public boolean hasMoreData() {
                return currentPosition < mockPcmData.length;
            }
            
            @Override
            public void reset() {
                currentPosition = 0;
            }
            
            // 生成模拟PCM数据（1秒的44.1kHz 16位立体声静音）
            private byte[] generateMockPcmData() {
                int sampleRate = 44100;
                int channels = 2;
                int bitsPerSample = 16;
                int durationSeconds = 1;
                
                int bytesPerSample = bitsPerSample / 8;
                int frameSize = channels * bytesPerSample;
                int totalFrames = sampleRate * durationSeconds;
                int totalBytes = totalFrames * frameSize;
                
                return new byte[totalBytes]; // 全零数据（静音）
            }
        };
        
        // 创建自定义数据源工厂
        PcmDataSourceFactory customFactory = new PcmDataSourceFactory(customProvider, 44100, 2, 16);
        
        // 创建PCM媒体源
        MediaSource customMediaSource = PcmMediaSourceFactory.createPcmMediaSource(
                customFactory, 44100, 2, 16);
        
        Log.d(TAG, "自定义PCM数据提供者创建成功");
    }
    
    /**
     * 示例6: 错误处理和重连
     */
    public void errorHandlingExample() {
        Log.i(TAG, "=== 错误处理和重连示例 ===");
        
        // 尝试连接一个可能不存在的URL来演示错误处理
        String invalidUrl = "http://invalid-url-that-does-not-exist.com/pcm";
        boolean success = playerManager.startPcmStream(invalidUrl);
        
        if (!success) {
            Log.w(TAG, "连接失败，这是预期的行为");
            
            // 在实际应用中，这里可以实现重连逻辑
            // 例如：等待一段时间后重试，或者切换到备用服务器
        }
    }
    
    /**
     * 示例7: 资源管理
     */
    public void resourceManagementExample() {
        Log.i(TAG, "=== 资源管理示例 ===");
        
        // 开始播放
        String streamUrl = "http://example.com/pcm_stream";
        boolean success = playerManager.startPcmStream(streamUrl);
        
        if (success) {
            Log.d(TAG, "开始播放PCM流");
            
            // 模拟播放一段时间
            new Thread(() -> {
                try {
                    Thread.sleep(10000); // 播放10秒
                    
                    // 停止播放并释放资源
                    playerManager.stopPlayback();
                    Log.d(TAG, "播放已停止");
                    
                    // 释放播放器资源
                    playerManager.release();
                    Log.d(TAG, "播放器资源已释放");
                    
                } catch (InterruptedException e) {
                    Log.e(TAG, "资源管理示例被中断", e);
                }
            }).start();
        }
    }
    
    /**
     * 运行所有示例
     */
    public void runAllExamples() {
        Log.i(TAG, "开始运行所有PCM DataSource示例");
        
        // 注意：这些示例不能同时运行，因为它们会相互干扰
        // 在实际使用中，应该根据需要选择合适的示例
        
        basicPcmStreamExample();
        
        // 等待一段时间后运行下一个示例
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                highQualityPcmExample();
                
                Thread.sleep(2000);
                monoPcmExample();
                
                Thread.sleep(2000);
                customDataProviderExample();
                
                Thread.sleep(2000);
                errorHandlingExample();
                
                Thread.sleep(2000);
                resourceManagementExample();
                
            } catch (InterruptedException e) {
                Log.e(TAG, "示例运行被中断", e);
            }
        }).start();
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        if (playerManager != null) {
            playerManager.release();
            playerManager = null;
        }
        Log.d(TAG, "PCM使用示例资源已清理");
    }
}