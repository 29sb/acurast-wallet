package com.acurast.wallet.data.repository

import com.acurast.wallet.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 钱包仓库 - 处理链上交互
 */
class WalletRepository {
    
    private var currentNetwork = NetworkType.MAINNET
    
    /**
     * 切换网络
     */
    fun switchNetwork(network: NetworkType) {
        currentNetwork = network
    }
    
    /**
     * 查询账户余额
     */
    suspend fun getBalance(address: String): AccountBalance = withContext(Dispatchers.IO) {
        try {
            // 使用 HTTP RPC 查询余额
            val rpcUrl = currentNetwork.rpcUrl.replace("wss://", "https://").replace("ws://", "http://")
            
            val request = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "state_getStorage")
                put("params", listOf(
                    "0x26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9" + 
                    address.toByteArray().toHex() + 
                    "0000000000000000"
                ))
            }
            
            val response = makeHttpRequest("$rpcUrl/rpc", request.toString())
            val jsonResponse = JSONObject(response)
            
            if (jsonResponse.has("result")) {
                val hex = jsonResponse.getString("result")
                if (hex != "0x" && hex.length > 2) {
                    parseBalance(hex)
                } else {
                    AccountBalance("", 0L, 0L, 0L, 0L)
                }
            } else {
                AccountBalance("", 0L, 0L, 0L, 0L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AccountBalance("", 0L, 0L, 0L, 0L)
        }
    }
    
    /**
     * 获取当前区块高度
     */
    suspend fun getBlockNumber(): Long = withContext(Dispatchers.IO) {
        try {
            val rpcUrl = currentNetwork.rpcUrl.replace("wss://", "https://").replace("ws://", "http://")
            
            val request = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "chain_getHeader")
                put("params", emptyList<String>())
            }
            
            val response = makeHttpRequest("$rpcUrl/rpc", request.toString())
            val jsonResponse = JSONObject(response)
            
            if (jsonResponse.has("result")) {
                val result = jsonResponse.getJSONObject("result")
                val numberHex = result.getString("number")
                numberHex.removePrefix("0x").toLong(16)
            } else {
                0L
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    
    /**
     * 发送转账交易
     */
    suspend fun transfer(
        fromAddress: String,
        toAddress: String,
        amount: Long,
        privateKey: ByteArray
    ): String = withContext(Dispatchers.IO) {
        // TODO: 实现真正的转账逻辑
        // 需要使用 Nova SDK 构造 extrinsic
        throw NotImplementedError("转账功能需要集成 Nova Substrate SDK")
    }
    
    /**
     * 获取交易历史
     */
    suspend fun getTransactionHistory(address: String): List<Transaction> = withContext(Dispatchers.IO) {
        // TODO: 实现交易历史查询
        // 需要扫描链上事件
        emptyList()
    }
    
    /**
     * 解析余额数据
     */
    private fun parseBalance(hex: String): AccountBalance {
        return try {
            val bytes = hex.removePrefix("0x").chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            
            // Substrate AccountData 结构: free(16) + reserved(16) + miscFrozen(16) + feeFrozen(16)
            if (bytes.size >= 64) {
                val free = bytes.sliceArray(0..15).toLongLE()
                val reserved = bytes.sliceArray(16..31).toLongLE()
                val frozen = bytes.sliceArray(32..47).toLongLE()
                AccountBalance("", free, reserved, frozen, 0L)
            } else {
                AccountBalance("", 0L, 0L, 0L, 0L)
            }
        } catch (e: Exception) {
            AccountBalance("", 0L, 0L, 0L, 0L)
        }
    }
    
    /**
     * 发送 HTTP 请求
     */
    private fun makeHttpRequest(url: String, body: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 10000
        connection.readTimeout = 30000
        
        connection.outputStream.use { os ->
            os.write(body.toByteArray())
        }
        
        return connection.inputStream.bufferedReader().readText()
    }
    
    /**
     * 字节数组转十六进制
     */
    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 小端序字节数组转 Long
     */
    private fun ByteArray.toLongLE(): Long {
        var result = 0L
        for (i in indices.reversed()) {
            result = result shl 8 or (this[i].toLong() and 0xFF)
        }
        return result
    }
}