package com.cooling.artifact.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 网络PCM数据提供者，用于从网络流中读取PCM数据
 */
public class NetworkPcmDataProvider implements PcmDataSource.PcmDataProvider {
    
    private static final String TAG = "NetworkPcmDataProvider";
    private static final int BUFFER_SIZE = 8192; // 8KB缓冲区
    
    private final String streamUrl;
    private HttpURLConnection connection;
    private InputStream inputStream;
    private final AtomicBoolean isStreaming;
    private final AtomicBoolean isConnected;
    
    // 流媒体相关参数
    private long totalBytesRead = 0;
    private long estimatedLength = -1; // 对于流媒体，长度通常是未知的
    
    public NetworkPcmDataProvider(String streamUrl) {
        this.streamUrl = streamUrl;
        this.isStreaming = new AtomicBoolean(false);
        this.isConnected = new AtomicBoolean(false);
    }
    
    @Override
    public int readPcmData(byte[] buffer, int offset, int length) {
        if (!isConnected.get() || inputStream == null) {
            return -1;
        }
        
        try {
            int bytesRead = inputStream.read(buffer, offset, length);
            if (bytesRead > 0) {
                totalBytesRead += bytesRead;
            }
            return bytesRead;
        } catch (IOException e) {
            Log.e(TAG, "Error reading PCM data from network stream", e);
            return -1;
        }
    }
    
    @Override
    public long getPcmDataLength() {
        // 对于网络流，返回估计长度或-1表示未知
        if (estimatedLength > 0) {
            return estimatedLength;
        }
        
        // 尝试从HTTP头获取内容长度
        if (connection != null) {
            try {
                String contentLength = connection.getHeaderField("Content-Length");
                if (contentLength != null) {
                    estimatedLength = Long.parseLong(contentLength);
                    return estimatedLength;
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to get content length from header", e);
            }
        }
        
        return -1; // 未知长度
    }
    
    @Override
    public boolean hasMoreData() {
        return isStreaming.get() && isConnected.get();
    }
    
    @Override
    public void reset() {
        totalBytesRead = 0;
        // 对于网络流，重置通常意味着重新连接
        reconnect();
    }
    
    /**
     * 开始连接网络流
     */
    public boolean connect() {
        if (isConnected.get()) {
            return true;
        }
        
        try {
            URL url = new URL(streamUrl);
            connection = (HttpURLConnection) url.openConnection();
            
            // 设置连接参数
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(30000);    // 30秒读取超时
            connection.setDoInput(true);
            
            // 设置请求头
            connection.setRequestProperty("User-Agent", "ExoPlayer-PCM-Streamer");
            connection.setRequestProperty("Accept", "*/*");
            
            // 连接
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                isConnected.set(true);
                isStreaming.set(true);
                
                Log.i(TAG, "Successfully connected to PCM stream: " + streamUrl);
                return true;
            } else {
                Log.e(TAG, "Failed to connect to stream, response code: " + responseCode);
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to PCM stream: " + streamUrl, e);
            return false;
        }
    }
    
    /**
     * 重新连接网络流
     */
    public boolean reconnect() {
        disconnect();
        return connect();
    }
    
    /**
     * 断开网络连接
     */
    public void disconnect() {
        isStreaming.set(false);
        isConnected.set(false);
        
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.w(TAG, "Error closing input stream", e);
            }
            inputStream = null;
        }
        
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
        
        Log.i(TAG, "Disconnected from PCM stream");
    }
    
    /**
     * 停止流媒体
     */
    public void stopStreaming() {
        isStreaming.set(false);
    }
    
    /**
     * 获取已读取的总字节数
     */
    public long getTotalBytesRead() {
        return totalBytesRead;
    }
    
    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return isConnected.get();
    }
    
    /**
     * 检查是否正在流媒体
     */
    public boolean isStreaming() {
        return isStreaming.get();
    }
}