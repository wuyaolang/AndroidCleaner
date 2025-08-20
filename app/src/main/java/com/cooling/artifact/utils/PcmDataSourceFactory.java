package com.cooling.artifact.utils;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.TransferListener;

/**
 * PCM DataSource工厂类，用于创建自定义的PCM数据源
 */
public class PcmDataSourceFactory implements Factory {

    private final PcmDataSource.PcmDataProvider dataProvider;
    private final int sampleRate;
    private final int channelCount;
    private final int bitsPerSample;

    public PcmDataSourceFactory(PcmDataSource.PcmDataProvider dataProvider) {
        this(dataProvider, 44100, 2, 16);
    }

    public PcmDataSourceFactory(PcmDataSource.PcmDataProvider dataProvider, 
                               int sampleRate, int channelCount, int bitsPerSample) {
        this.dataProvider = dataProvider;
        this.sampleRate = sampleRate;
        this.channelCount = channelCount;
        this.bitsPerSample = bitsPerSample;
    }

    @Override
    public DataSource createDataSource() {
        PcmDataSource pcmDataSource = new PcmDataSource(dataProvider);
        pcmDataSource.setSampleRate(sampleRate);
        pcmDataSource.setChannelCount(channelCount);
        pcmDataSource.setBitsPerSample(bitsPerSample);
        return pcmDataSource;
    }

    @Override
    public DataSource createDataSource(TransferListener transferListener) {
        // PCM数据源不需要传输监听器，直接返回普通的数据源
        return createDataSource();
    }
}