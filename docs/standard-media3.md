# Android 标准版播放策略

标准版使用 AndroidX Media3 1.8.0 作为唯一视频播放引擎。WebView 仅承载页面、焦点、弹幕和控制，不再通过 HTML5 video 加载或解码媒体。标准版不包含 NextLib Media3 Extensions、FFmpeg 或 libmpv。

## Media3 原生播放

每次播放、换源和连续切集均直接创建/复用 Media3 ExoPlayer，HTML5 video 不再接收媒体地址，也不作为 Media3 失败后的回退引擎。Media3 失败时向播放界面报告错误，保留当前集和进度供用户重试或切换清晰度。

解码器排序始终优先设备硬件解码器；仅当选中的 HEVC 硬件轨道在首帧前明确报告 `FORMAT_EXCEEDS_CAPABILITIES`，且设备确实提供系统 HEVC 软件解码器时，才允许一次受限的软件解码恢复。不会因为 Android 版本较旧就默认优先软件解码。

从 Media3 换源交回 HTML5 前，同步当前播放器的音量和静音状态，避免隐藏 HTML5 元素遗留的过渡静音被下一次播放当成用户偏好。正在进行的过渡仍保持临时静音，待原有首帧/绘制条件满足后恢复；用户主动静音和零音量不被强制解除。遥控器系统音量与播放器静音仍独立，不通过调大系统音量绕过过渡保护。

## Media3 解码

使用 `DefaultRenderersFactory` 和设备提供的解码器，恢复 0.2.136 的解码器排序、兼容模式、Android 6 PCM 音频处理及错误分类。保留初始化失败时切换设备候选解码器的能力。

仅当选中的 HEVC 轨道报告 `FORMAT_EXCEEDS_CAPABILITIES`、尚未输出首帧、该源未重试且设备确实提供系统 HEVC 软件解码器时，才重建一次并限定使用该系统软解。不会加入 FFmpeg renderer，也不会把 AVC 或所有解码错误泛化为强制软解。重试保留位置，并隔离上一轮解码尝试的回调与诊断状态。

## 范围与验证

- 保留新版登录、界面、焦点、弹幕、预下载、切集过渡和竖屏背景功能，不将整个应用回退到旧版本。
- MPV 通过独立的 `MpvPlayerBridge` 播放，Media3 依赖仍为 1.11.1；MPV 库及播放代码不变。桌面和 webOS 播放路径不变。
- compileSdk 36、targetSdk 35、minSdk 23、应用 ID、版本号、签名及更新通道不变。
- CI 核对两版实际解析的 Media3 版本、单元测试、构建、lint，以及 APK 三种 ABI 和 native library 隔离；标准版不得重新混入 NextLib/FFmpeg 库。
- CI 检查不能替代问题设备实测。该回退针对引入 FFmpeg 后的持续掉帧与包体增长，不代表已经验证修复切清晰度后无声等其它问题。
