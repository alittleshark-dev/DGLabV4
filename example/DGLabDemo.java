/**
 *               File: ./example/DGLabDemo.java
 * 
 * =================================================================
 *               Copyright © 2026 alittleshark-dev
 *                      Licensed under the 
 *                 GNU General Public License v3.0
 *                   See for details /LICENSE
 *       
 *             Imports:
 *               com.alittleshark.dglabv4.DGLabV4
 * ===============================================================
 */
import com.alittleshark.dglabv4.DGLabV4;

public class DGLabDemo {
    public static void main(String[] args) throws InterruptedException {
        DGLabV4 client = new DGLabV4();

        client.setDebug(true); // 开启调试模式
        client.setMaxIntensity(0, 30); // 设置 A 通道最大 30
        client.setMaxIntensity(1, 30); // 设置 B 通道最大 30

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
        if (!client.connect()) {
            System.err.println("[DGLab] 连接发起失败，请检查网络！");
            return; // 如果没连上就直接结束程序
        }

        // 阻塞主线程防止退出（在实际游戏/UI中不需要这一步）
        Thread.sleep(60000);

        // 断开连接
        System.out.println("[DGLab] Demo 运行结束，正在断开连接...");
        client.disconnect();
    }
}