package com.acurast.wallet.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acurast.wallet.data.model.*
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.acurast.wallet.data.repository.WalletRepository
import kotlinx.coroutines.launch

/**
 * 创建钱包屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(
    onNavigateBack: () -> Unit,
    onWalletCreated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    
    // 状态
    var currentStep by remember { mutableStateOf(0) }
    var mnemonicWords by remember { mutableStateOf<List<String>>(emptyList()) }
    var walletName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var createdAddress by remember { mutableStateOf<String?>(null) }
    
    // 生成助记词
    LaunchedEffect(Unit) {
        try {
            // 使用 Nova SDK 生成真实助记词
            val result = repository.generateWallet()
            mnemonicWords = result.mnemonic.split(" ")
            createdAddress = result.address
        } catch (e: Exception) {
            errorMessage = "生成助记词失败: ${e.message}"
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建钱包") },
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
            when (currentStep) {
                0 -> {
                    // 步骤1：显示助记词
                    Text(
                        "备份助记词",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "请按顺序抄写以下12个单词，并妥善保管。这是恢复钱包的唯一方式。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 助记词网格
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            for (i in 0 until 3) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (j in 0 until 4) {
                                        val index = i * 4 + j
                                        if (index < mnemonicWords.size) {
                                            Text(
                                                "${index + 1}. ${mnemonicWords[index]}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                                if (i < 2) Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 警告信息
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "请勿截图或分享助记词，否则可能导致资产丢失！",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { currentStep = 1 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("我已备份")
                    }
                }
                
                1 -> {
                    // 步骤2：设置钱包名称
                    Text(
                        "设置钱包名称",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "为您的钱包设置一个名称，方便识别。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = walletName,
                        onValueChange = { walletName = it },
                        label = { Text("钱包名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { currentStep = 0 }) {
                            Text("上一步")
                        }
                        Button(
                            onClick = {
                                if (walletName.isBlank()) {
                                    errorMessage = "请输入钱包名称"
                                    return@Button
                                }
                                currentStep = 2
                            }
                        ) {
                            Text("下一步")
                        }
                    }
                }
                
                2 -> {
                    // 步骤3：确认助记词
                    Text(
                        "确认助记词",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "请按顺序选择以下助记词，以确认您已正确备份。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 助记词选择（简化版）
                    Text(
                        "请确认您已正确备份助记词",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { currentStep = 1 }) {
                            Text("上一步")
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    try {
                                        // 钱包已经在 LaunchedEffect 中创建了，这里保存到本地存储
                                        if (createdAddress != null) {
                                            val prefs = context.getSharedPreferences("acurast_wallet", Context.MODE_PRIVATE)
                                            val editor = prefs.edit()
                                            editor.putString("wallet_address", createdAddress)
                                            editor.putString("wallet_name", walletName.ifEmpty { "我的钱包" })
                                            editor.putString("wallet_mnemonic", mnemonicWords.joinToString(" "))
                                            editor.apply()
                                        }
                                        currentStep = 3
                                    } catch (e: Exception) {
                                        errorMessage = "创建钱包失败: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("创建钱包")
                            }
                        }
                    }
                }
                
                3 -> {
                    // 步骤4：创建成功
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "钱包创建成功！",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "钱包名称: $walletName",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "地址: ${createdAddress?.take(8)}...${createdAddress?.takeLast(8)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = onWalletCreated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("完成")
                        }
                    }
                }
            }
            
            // 错误消息
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
