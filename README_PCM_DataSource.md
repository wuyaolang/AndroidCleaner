# ExoPlayer 自定义 PCM DataSource 实现

这个项目展示了如何自定义 ExoPlayer 的 DataSource 来实现 PCM 格式的视频推流播放。

## 功能特性

- 🎵 支持 PCM 格式音频流播放
- 🌐 支持网络流媒体推流
- 📱 完整的播放控制（播放、暂停、停止、音量调节）
- ⚙️ 可配置的音频参数（采样率、声道数、位深度）
- 🔄 自动重连和错误处理
- 📊 实时播放状态监控

## 架构设计

### 核心组件

1. **PcmDataSource** - 自定义的 DataSource 实现
2. **PcmDataSourceFactory** - DataSource 工厂类
3. **NetworkPcmDataProvider** - 网络 PCM 数据提供者
4. **PcmMediaSourceFactory** - PCM 媒体源工厂
5. **PcmPlayerManager** - 播放器管理器
6. **PcmPlayerActivity** - 示例 UI 界面

### 类关系图

```
PcmPlayerActivity
    ↓
PcmPlayerManager
    ↓
PcmMediaSourceFactory → PcmDataSourceFactory → PcmDataSource
    ↓
NetworkPcmDataProvider
```

## 使用方法

### 1. 基本播放

```java
// 创建播放器管理器
PcmPlayerManager playerManager = new PcmPlayerManager(context);

// 设置 PCM 参数（44.1kHz, 2声道, 16位）
playerManager.setPcmParameters(44100, 2, 16);

// 开始播放网络流
boolean success = playerManager.startPcmStream("http://example.com/pcm_stream");

if (success) {
    // 播放成功
    Log.d("PCM", "开始播放PCM流");
} else {
    // 播放失败
    Log.e("PCM", "无法连接到PCM流");
}
```

### 2. 播放控制

```java
// 暂停播放
playerManager.pausePlayback();

// 恢复播放
playerManager.resumePlayback();

// 停止播放
playerManager.stopPlayback();

// 设置音量（0.0 - 1.0）
playerManager.setVolume(0.8f);

// 检查播放状态
if (playerManager.isPlaying()) {
    Log.d("PCM", "正在播放");
}

// 获取播放位置
long position = playerManager.getCurrentPosition();
```

### 3. 自定义 PCM 参数

```java
// 高保真音频（48kHz, 2声道, 24位）
playerManager.setPcmParameters(48000, 2, 24);

// 单声道音频（44.1kHz, 1声道, 16位）
playerManager.setPcmParameters(44100, 1, 16);

// 低质量音频（22.05kHz, 1声道, 8位）
playerManager.setPcmParameters(22050, 1, 8);
```

### 4. 创建自定义数据提供者

```java
public class CustomPcmDataProvider implements PcmDataSource.PcmDataProvider {
    
    @Override
    public int readPcmData(byte[] buffer, int offset, int length) {
        // 实现从自定义数据源读取PCM数据的逻辑
        // 返回实际读取的字节数，-1表示没有更多数据
        return bytesRead;
    }
    
    @Override
    public long getPcmDataLength() {
        // 返回PCM数据的总长度（字节）
        // 对于流媒体，可以返回-1表示未知长度
        return dataLength;
    }
    
    @Override
    public boolean hasMoreData() {
        // 返回是否还有更多数据可读
        return hasMoreData;
    }
    
    @Override
    public void reset() {
        // 重置数据源到初始状态
    }
}

// 使用自定义数据提供者
CustomPcmDataProvider customProvider = new CustomPcmDataProvider();
PcmDataSourceFactory factory = new PcmDataSourceFactory(customProvider);
```

## 技术实现细节

### PCM 格式支持

- **采样率**: 支持 8kHz 到 192kHz
- **声道数**: 支持单声道、立体声、多声道
- **位深度**: 支持 8位、16位、24位、32位
- **字节序**: 小端序（Little Endian）

### 网络流处理

- 自动重连机制
- 超时处理
- 错误恢复
- 缓冲区管理

### 性能优化

- 异步数据读取
- 内存缓冲区管理
- 线程安全设计
- 资源自动释放

## 配置要求

### 依赖库

```gradle
dependencies {
    implementation 'com.google.android.exoplayer:exoplayer-core:2.18.7'
    implementation 'com.google.android.exoplayer:exoplayer-ui:2.18.7'
    implementation 'com.google.android.exoplayer:exoplayer-dash:2.18.7'
    implementation 'com.google.android.exoplayer:exoplayer-hls:2.18.7'
    implementation 'com.google.android.exoplayer:exoplayer-smoothstreaming:2.18.7'
}
```

### 权限要求

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 最低 SDK 版本

- `minSdkVersion: 21` (Android 5.0)

## 示例应用

项目包含一个完整的示例应用 `PcmPlayerActivity`，展示了：

- PCM 流媒体播放
- 播放控制界面
- 音量调节
- 播放状态显示
- 错误处理

## 常见问题

### Q: 如何支持其他音频格式？

A: 可以通过修改 `PcmMediaSourceFactory` 来支持其他格式，或者使用 ExoPlayer 内置的格式支持。

### Q: 如何处理网络中断？

A: `NetworkPcmDataProvider` 包含自动重连机制，网络恢复后会自动重新连接。

### Q: 如何优化音频质量？

A: 可以调整 PCM 参数（提高采样率、增加位深度）来提升音频质量。

### Q: 支持实时推流吗？

A: 是的，这个实现专门为实时推流设计，支持流式数据传输。

## 扩展功能

### 1. 本地文件支持

可以创建 `LocalPcmDataProvider` 来支持本地 PCM 文件播放。

### 2. 音频效果处理

可以在 `PcmDataSource` 中添加音频处理链，支持均衡器、混响等效果。

### 3. 多流同步

可以扩展支持多个 PCM 流的同步播放。

### 4. 录制功能

可以添加 PCM 数据录制功能，将播放的音频保存到文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 许可证

本项目采用 MIT 许可证。