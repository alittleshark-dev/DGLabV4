# DGLab v4

> 这是一个 **DGLab v4 版本协议** 的 Java 控制库
>
> **警告**：请务必在确保绝对安全的前提下使用本库
> **!!!在使用前请务必 明确个人承受极限 !!!**
> 切勿将强度（Intensity）设置**超过个人承受极限**
> 因使用不当导致的任何身体不适或设备损坏**本项目及开发者不承担任何责任**

[![Java Version](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

## 特性

- 使用 Java 11+ 自带的 `HttpClient` 和 `WebSocket`
- **安全限制**: 添加了输出最大上限
- **内置官方波形库**：收录了 **40+ 款** 官方郊狼 (COYOTE) 与负鼠 (OVC) 波形数据 [官方文件](https://github.com/dungeonlab-open/dglab-kit/blob/main/src/waveform/ovc.ts)
- **异步非阻塞**：网络请求异步，不会卡死主线程

## 快速开始

### 引入

纯源码结构。将 `src/main/java/com/alittleshark/dglabv4/DGLabV4.java` 复制到你项目对应的包路径下即可。

```bash
git clone https://github.com/alittleshark-dev/DGLabV4.git
cd DGLabV4
cp -r ./src/main/java/com/alittleshark/dglabv4 /path/to/your/project/src/main/java/com/alittleshark/
```

### 用法示例

```java
import com.alittleshark.dglabv4.DGLabV4;

public class DGLabDemo {
    public static void main(String[] args) throws InterruptedException {
        DGLabV4 client = new DGLabV4();

        client.setDebug(true); // 开启调试模式

        // 1. 设置事件回调
        client.setCallback(new DGLabV4.DGLabCallback() {
            @Override
            public void onTargetUrlReceived(String url) {
                // 拿到配对链接，通常可以生成二维码让手机 APP 扫描
                System.out.println("[DGLab] 请使用手机 APP 扫码或输入以下链接:");
                System.out.println(url);
            }

            @Override
            public void onAppConnected() {
                System.out.println("[DGLab] APP 已连接！");

                // 调节 A 通道基础强度 (+5)
                client.addIntensity(5, 0);

                // 播放官方「气泡」波形 (A通道，持续 3 秒)
                client.playWave(DGLabV4.WAVE_BUBBLE, 0, 3000);

                // 播放官方「心跳」波形 (B通道，持续 5 秒)
                client.playWave(DGLabV4.WAVE_HEARTBEAT, 1, 5000);
            }

            @Override
            public void onAppDisconnected() {
                System.out.println("[DGLab] 手机 APP 已断开连接。");
            }
        });

        // 发起连接
        System.out.println("[DGLab] 正在连接中继服务器...");
        client.connect();

        // 阻塞主线程防止退出（在实际游戏/UI中不需要这一步）
        Thread.sleep(60000);

        // 断开连接
        client.disconnect();
    }
}
```

## 函数

### 最大上限 (安全限制)

**安全机制说明：**

为了保障使用者的人身安全，SDK在底层实现了拦截：

1. **单次调整拦截**：`addIntensity` 内部会记录当前通道的强度，若目标值超过最大上限，会自动截断至上限值。
2. **波形时长拦截**：`playWave` 方法中限制了单次波形持续时间不得超过 10000 毫秒（10秒），防止因意外导致长时间持续输出。

| 方法 | 说明 |
| :--- | :--- |
| `setMaxIntensity(int channel, int maxValue)` | 设置指定通道的强度安全上限。`channel`: 0=A通道, 1=B通道。`maxValue` 建议不超过 100。默认 A、B 通道上限均为 50。 |
| `stopShock(int channel)` | 将指定通道的强度强制归零。`channel`: 0=A通道, 1=B通道。同时同步本地记录的强度为 0。 |

### 事件回调 (`DGLabCallback`)

此接口用来接收来自底层服务器的状态通知。

| 回调方法 | 说明 |
| :--- | :--- |
| `onTargetUrlReceived(String url)` | 成功连接中继服务器，返回配对链接（可生成二维码） |
| `onAppConnected()` | 手机 APP 已经成功连接，此时可以开始下发指令 |
| `onAppDisconnected()` | 手机 APP 断开连接 |

### 基础连接

| 方法 | 说明 |
| :--- | :--- |
| `connect()` | 连接 DGLab V4 中继服务器，返回 `boolean` 表示是否成功发起连接 |
| `disconnect()` | 主动断开服务器连接，清理缓存 |
| `isAppConnected()` | 返回 `true` 代表手机 APP 已成功连接并准备就绪，**发指令前必须检查** |
| `setCallback(DGLabCallback callback)` | 注册事件监听器 |

### 设置强度

| 方法 | 说明 |
| :--- | :--- |
| `addIntensity(int value, int channel)` | 相对增加强度。`channel`: 0=A通道, 1=B通道，`value` 为正数则增加，负数减少 |
| `stopShock(int channel)` | 将指定通道的强度强制归零。`channel`: 0=A通道, 1=B通道 |

### 波形下发

| 方法 | 说明 |
| :--- | :--- |
| `playWave(String[] frames, int channel, int durationMs)` | 下发官方波形 `frames` 为波形数组, `channel` 为通道, `durationMs` 为持续时间(毫秒) |

### 波形字典工具

| 方法 / 字段 | 说明 |
| :--- | :--- |
| `ALL_WAVES` | 静态字典 `Map<String, String[]>`，包含了所有官方波形（键为波形名称，值为波形数据） |
| `printAllWaveNames()` | 在控制台打印所有可用的波形名称列表 |
| `getWaveByName(String name)` | 根据名称从字典中获取波形数组，例如 `getWaveByName("气泡 (Bubble)")` |

### 调试

| 方法 | 说明 |
| :--- | :--- |
| `setDebug()` | 接收一个布尔值, `true` 开启调试模式，控制台会输出详细的底层 JSON 收发数据, `false` 则关闭 |

## 内置波形库

你可以在 `DGLabV4.ALL_WAVES` 中找到所有波形的名称和数组，或者直接调用 `DGLabV4.WAVE_xxx`。

## 内置脉冲

- `WAVE_TAP_1` 单次
- `WAVE_TAP_2` 双次

### 郊狼 [官方文件](https://github.com/dungeonlab-open/dglab-kit/blob/main/src/waveform/ovc.ts)

- `WAVE_EXTRUSTION` (挤压)
- `WAVE_BUBBLE` (气泡)
- `WAVE_RHYTHM` (律动)
- `WAVE_AIR_WAVES` (电波)
- `WAVE_DANCE` (舞步)
- `WAVE_CLIMB` (攀登)
- `WAVE_SHADE` (树荫)
- `WAVE_PULSE` (脉冲)
- `WAVE_BREATHING` (呼吸)
- `WAVE_TIDE` (潮汐)
- `WAVE_PULSATING` (连击)
- `WAVE_QUICK_RUB` (快速按捏)
- `WAVE_GRADUALRUB` (按捏渐强)
- `WAVE_HEARTBEAT` (心跳)
- `WAVE_COMPRESS` (压缩)
- `WAVE_RHYTHMIC` (节奏步伐)
- `WAVE_GRAINY` (颗粒摩擦)
- `WAVE_BOUNCY` (渐变弹跳)
- `WAVE_RIPPLE` (波浪涟漪)
- `WAVE_RAINFALL` (雨水冲刷)
- `WAVE_TEMPO_TAP` (变速敲击)
- `WAVE_SIGNAL` (信号灯)
- `WAVE_TEASE_1` (挑逗1)
- `WAVE_TEASE_2` (挑逗2)

### 负鼠 [官方文件](https://github.com/dungeonlab-open/dglab-kit/blob/main/src/waveform/ovc.ts)

- `WAVE_OVC_ALARM` (闹钟)
- `WAVE_OVC_PINPOINT` (精准打击)
- `WAVE_OVC_UNDERTOW` (暗涌)
- `WAVE_OVC_SOOTHE` (舒脉)
- `WAVE_OVC_TRAPEZOID` (梯形波动)
- `WAVE_OVC_ENGINE_WAVE` (机车声浪)
- `WAVE_OVC_AFTERSHOCK` (余震)
- `WAVE_OVC_STEADY_HIT` (持续冲击)
- `WAVE_OVC_CURVE_PRESS` (曲线压缩)
- `WAVE_OVC_BEAT` (脉搏)
- `WAVE_OVC_LOOP_SPREAD` (回环扩散)
- `WAVE_OVC_INTERMITTENT` (间歇节律)
- `WAVE_OVC_SURF_WAVE` (踏浪起伏)
- `WAVE_OVC_LOOP_PUSH` (循环推进)
- `WAVE_OVC_CANOPY` (树冠脉冲)
- `WAVE_OVC_JOY_CLIMB` (愉悦攀登)
- `WAVE_OVC_DECEL` (持续降速)
- `WAVE_OVC_RANDOM` (随机节奏)
- `WAVE_OVC_POP_CANDY` (跳跳糖)
- `WAVE_OVC_PHASE_SHIFT` (相位偏移)

## 开源协议

本项目遵循 [GNU General Public License v3.0{GPLv3}](LICENSE) 开源
