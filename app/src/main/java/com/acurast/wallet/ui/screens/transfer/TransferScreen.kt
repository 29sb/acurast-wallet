package com.acurast.wallet.ui.screens.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.acurast.wallet.data.model.*
import com.acurast.wallet.data.repository.WalletRepository
import kotlinx.coroutines.launch

/**
 * 转账屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    
    // 状态
    var recipientAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    
    // 当前钱包地址（模拟）
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("acurast_wallet", Context.MODE_PRIVATE)
    val currentAddress = prefs.getString("wallet_address", "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") ?: "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("转账") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 发送地址
            Text(
                "从",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    currentAddress,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 接收地址
            Text(
                "到",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = recipientAddress,
                onValueChange = { recipientAddress = it },
                label = { Text("接收地址") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("输入 SS58 地址") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 金额
            Text(
                "金额",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("ACU 数量") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("0.0") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 手续费提示
            Text(
                "手续费: ~0.001 ACU",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 错误消息
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        errorMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 成功消息
            if (successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        successMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 确认对话框
            if (showConfirmation) {
                AlertDialog(
                    onDismissRequest = { showConfirmation = false },
                    title = { Text("确认转账") },
                    text = {
                        Column {
                            Text("确定要发送 $amount ACU 到以下地址吗？")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                recipientAddress,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmation = false
                                isLoading = true
                                scope.launch {
                                    try {
                                        // 实际应该使用 Nova SDK 发送交易
                                        // 这里模拟发送
                                        kotlinx.coroutines.delay(2000)
                                        successMessage = "转账成功！交易哈希: 0x1234567890abcdef"
                                        recipientAddress = ""
                                        amount = ""
                                    } catch (e: Exception) {
                                        errorMessage = "转账失败: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            Text("确认")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showConfirmation = false }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
            
            // 发送按钮
            Button(
                onClick = {
                    // 验证输入
                    if (recipientAddress.isBlank()) {
                        errorMessage = "请输入接收地址"
                        return@Button
                    }
                    if (amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                        errorMessage = "请输入有效金额"
                        return@Button
                    }
                    
                    errorMessage = null
                    showConfirmation = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 最大金额按钮
            OutlinedButton(
                onClick = { amount = "100" }, // 模拟最大金额
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("最大金额")
            }
        }
    }
}
