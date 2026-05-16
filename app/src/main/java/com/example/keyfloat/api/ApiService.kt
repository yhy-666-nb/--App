package com.example.keyfloat.api

// 暂未使用 Retrofit，保留文件作为占位
object ApiService {
    fun activate(key: String, deviceId: String): Boolean {
        // 测试卡密：123456
        return key == "123456"
    }
}
