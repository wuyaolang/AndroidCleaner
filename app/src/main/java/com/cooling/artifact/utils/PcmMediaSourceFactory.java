package com.cooling.artifact.utils;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.MimeTypes;

/**
 * PCM媒体源工厂，用于创建PCM格式的媒体源
 */
public class PcmMediaSourceFactory {
    
    /**
     * 创建PCM媒体源
     * 
     * @param dataSourceFactory PCM数据源工厂
     * @param sampleRate 采样率
     * @param channelCount 声道数
     * @param bitsPerSample 位深度
     * @return PCM媒体源
     */
    public static MediaSource createPcmMediaSource(DataSource.Factory dataSourceFactory,
                                                  int sampleRate, int channelCount, int bitsPerSample) {
        
        // 创建PCM格式
        Format pcmFormat = Format.createAudioSampleFormat(
                "pcm_audio",                    // id
                MimeTypes.AUDIO_RAW,            // mimeType
                null,                           // codecs
                Format.NO_VALUE,                // bitrate
                Format.NO_VALUE,                // maxInputSize
                channelCount,                   // channelCount
                sampleRate,                     // sampleRate
                C.ENCODING_PCM_16BIT,          // pcmEncoding (16位PCM)
                null,                           // initializationData
                null                            // drmInitData
        );
        
        // 创建媒体项
        MediaItem mediaItem = MediaItem.fromUri("pcm://custom");
        
        // 创建单样本媒体源
        return new SingleSampleMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem, pcmFormat, C.TIME_UNSET);
    }
    
    /**
     * 创建默认参数的PCM媒体源（44.1kHz, 2声道, 16位）
     * 
     * @param dataSourceFactory PCM数据源工厂
     * @return PCM媒体源
     */
    public static MediaSource createDefaultPcmMediaSource(DataSource.Factory dataSourceFactory) {
        return createPcmMediaSource(dataSourceFactory, 44100, 2, 16);
    }
    
    /**
     * 创建高保真PCM媒体源（48kHz, 2声道, 24位）
     * 
     * @param dataSourceFactory PCM数据源工厂
     * @return PCM媒体源
     */
    public static MediaSource createHighQualityPcmMediaSource(DataSource.Factory dataSourceFactory) {
        return createPcmMediaSource(dataSourceFactory, 48000, 2, 24);
    }
    
    /**
     * 创建单声道PCM媒体源（44.1kHz, 1声道, 16位）
     * 
     * @param dataSourceFactory PCM数据源工厂
     * @return PCM媒体源
     */
    public static MediaSource createMonoPcmMediaSource(DataSource.Factory dataSourceFactory) {
        return createPcmMediaSource(dataSourceFactory, 44100, 1, 16);
    }
}