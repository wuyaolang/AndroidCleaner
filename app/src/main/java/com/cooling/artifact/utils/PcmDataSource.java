package com.cooling.artifact.utils;

import android.net.Uri;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义PCM DataSource，用于处理PCM格式的音频推流
 */
public class PcmDataSource implements DataSource {

    private static final String TAG = "PcmDataSource";
    
    // PCM音频参数
    private int sampleRate = 44100; // 采样率
    private int channelCount = 2;   // 声道数
    private int bitsPerSample = 16; // 位深度
    private int frameSize;          // 帧大小
    
    private ByteBuffer pcmBuffer;
    private long position = 0;
    private long length = 0;
    private boolean isOpen = false;
    
    // PCM数据源（可以是网络流、本地文件等）
    private PcmDataProvider dataProvider;
    
    public interface PcmDataProvider {
        /**
         * 读取PCM数据
         * @param buffer 数据缓冲区
         * @param offset 偏移量
         * @param length 要读取的长度
         * @return 实际读取的字节数
         */
        int readPcmData(byte[] buffer, int offset, int length);
        
        /**
         * 获取PCM数据总长度
         * @return 数据总长度（字节）
         */
        long getPcmDataLength();
        
        /**
         * 是否还有更多数据
         * @return true表示还有数据
         */
        boolean hasMoreData();
        
        /**
         * 重置数据源
         */
        void reset();
    }

    public PcmDataSource(PcmDataProvider provider) {
        this.dataProvider = provider;
        this.frameSize = channelCount * (bitsPerSample / 8);
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        if (dataProvider == null) {
            throw new IOException("PCM data provider is null");
        }
        
        this.position = dataSpec.position;
        this.length = dataProvider.getPcmDataLength();
        this.isOpen = true;
        
        // 重置数据提供者
        dataProvider.reset();
        
        // 跳过指定位置的数据
        if (position > 0) {
            skipBytes(position);
        }
        
        return length - position;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (!isOpen) {
            throw new IOException("DataSource is not open");
        }
        
        if (dataProvider == null || !dataProvider.hasMoreData()) {
            return C.RESULT_END_OF_INPUT;
        }
        
        int bytesRead = dataProvider.readPcmData(buffer, offset, readLength);
        if (bytesRead > 0) {
            position += bytesRead;
        }
        
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return Uri.parse("pcm://custom");
    }

    @Override
    public void close() throws IOException {
        isOpen = false;
        if (dataProvider != null) {
            // 可以在这里添加清理逻辑
        }
    }

    private void skipBytes(long bytesToSkip) throws IOException {
        byte[] skipBuffer = new byte[1024];
        long remaining = bytesToSkip;
        
        while (remaining > 0) {
            int toRead = (int) Math.min(remaining, skipBuffer.length);
            int read = dataProvider.readPcmData(skipBuffer, 0, toRead);
            if (read == -1) {
                break;
            }
            remaining -= read;
        }
    }

    // Getter和Setter方法
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        this.frameSize = channelCount * (bitsPerSample / 8);
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
        this.frameSize = channelCount * (bitsPerSample / 8);
    }

    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
        this.frameSize = channelCount * (bitsPerSample / 8);
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getFrameSize() {
        return frameSize;
    }
}