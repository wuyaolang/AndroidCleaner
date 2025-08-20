package com.cooling.artifact.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cooling.artifact.R;
import com.cooling.artifact.utils.PcmPlayerManager;

/**
 * PCM播放器示例Activity
 */
public class PcmPlayerActivity extends AppCompatActivity {
    
    private static final String TAG = "PcmPlayerActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private PcmPlayerManager pcmPlayerManager;
    
    // UI组件
    private EditText etStreamUrl;
    private Button btnStartStream;
    private Button btnStopStream;
    private Button btnPauseResume;
    private SeekBar seekBarVolume;
    private TextView tvStatus;
    private TextView tvPosition;
    private TextView tvDuration;
    
    private boolean isPaused = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcm_player);
        
        initViews();
        initPcmPlayer();
        setupListeners();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        etStreamUrl = findViewById(R.id.et_stream_url);
        btnStartStream = findViewById(R.id.btn_start_stream);
        btnStopStream = findViewById(R.id.btn_stop_stream);
        btnPauseResume = findViewById(R.id.btn_pause_resume);
        seekBarVolume = findViewById(R.id.seekbar_volume);
        tvStatus = findViewById(R.id.tv_status);
        tvPosition = findViewById(R.id.tv_position);
        tvDuration = findViewById(R.id.tv_duration);
        
        // 设置默认流URL
        etStreamUrl.setText("http://example.com/pcm_stream");
        
        // 设置音量滑块初始值
        seekBarVolume.setMax(100);
        seekBarVolume.setProgress(100);
    }
    
    /**
     * 初始化PCM播放器
     */
    private void initPcmPlayer() {
        pcmPlayerManager = new PcmPlayerManager(this);
        
        // 设置PCM参数（44.1kHz, 2声道, 16位）
        pcmPlayerManager.setPcmParameters(44100, 2, 16);
        
        // 设置初始音量
        pcmPlayerManager.setVolume(1.0f);
        
        updateStatus("播放器已初始化");
    }
    
    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 开始流媒体播放
        btnStartStream.setOnClickListener(v -> startPcmStream());
        
        // 停止播放
        btnStopStream.setOnClickListener(v -> stopPcmStream());
        
        // 暂停/恢复播放
        btnPauseResume.setOnClickListener(v -> togglePauseResume());
        
        // 音量控制
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float volume = progress / 100.0f;
                    pcmPlayerManager.setVolume(volume);
                    Log.d(TAG, "Volume set to: " + volume);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    /**
     * 开始PCM流媒体播放
     */
    private void startPcmStream() {
        String streamUrl = etStreamUrl.getText().toString().trim();
        if (streamUrl.isEmpty()) {
            Toast.makeText(this, "请输入流媒体URL", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查网络权限
        if (!checkNetworkPermission()) {
            return;
        }
        
        updateStatus("正在连接流媒体...");
        
        // 在后台线程中启动流媒体
        new Thread(() -> {
            boolean success = pcmPlayerManager.startPcmStream(streamUrl);
            
            runOnUiThread(() -> {
                if (success) {
                    updateStatus("正在播放PCM流");
                    btnStartStream.setEnabled(false);
                    btnStopStream.setEnabled(true);
                    btnPauseResume.setEnabled(true);
                    startPositionUpdates();
                } else {
                    updateStatus("连接流媒体失败");
                    Toast.makeText(PcmPlayerActivity.this, 
                            "无法连接到流媒体服务器", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    
    /**
     * 停止PCM流媒体播放
     */
    private void stopPcmStream() {
        pcmPlayerManager.stopPlayback();
        
        updateStatus("播放已停止");
        btnStartStream.setEnabled(true);
        btnStopStream.setEnabled(false);
        btnPauseResume.setEnabled(false);
        btnPauseResume.setText("暂停");
        isPaused = false;
        
        stopPositionUpdates();
        updatePositionDisplay(0, 0);
    }
    
    /**
     * 切换暂停/恢复状态
     */
    private void togglePauseResume() {
        if (isPaused) {
            pcmPlayerManager.resumePlayback();
            btnPauseResume.setText("暂停");
            updateStatus("播放已恢复");
            isPaused = false;
        } else {
            pcmPlayerManager.pausePlayback();
            btnPauseResume.setText("恢复");
            updateStatus("播放已暂停");
            isPaused = true;
        }
    }
    
    /**
     * 开始位置更新
     */
    private void startPositionUpdates() {
        new Thread(() -> {
            while (pcmPlayerManager.isPlaying() || pcmPlayerManager.isConnected()) {
                try {
                    Thread.sleep(1000); // 每秒更新一次
                    
                    runOnUiThread(() -> {
                        long position = pcmPlayerManager.getCurrentPosition();
                        long duration = pcmPlayerManager.getDuration();
                        updatePositionDisplay(position, duration);
                    });
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    /**
     * 停止位置更新
     */
    private void stopPositionUpdates() {
        // 这里可以通过设置标志位来停止位置更新线程
    }
    
    /**
     * 更新位置显示
     */
    private void updatePositionDisplay(long position, long duration) {
        String positionStr = formatTime(position);
        String durationStr = formatTime(duration);
        tvPosition.setText("位置: " + positionStr);
        tvDuration.setText("时长: " + durationStr);
    }
    
    /**
     * 格式化时间显示
     */
    private String formatTime(long timeMs) {
        if (timeMs <= 0) {
            return "00:00";
        }
        
        long seconds = timeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * 更新状态显示
     */
    private void updateStatus(String status) {
        tvStatus.setText("状态: " + status);
        Log.d(TAG, "Status: " + status);
    }
    
    /**
     * 检查网络权限
     */
    private boolean checkNetworkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.INTERNET}, 
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "网络权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要网络权限才能播放流媒体", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pcmPlayerManager != null) {
            pcmPlayerManager.release();
        }
    }
}