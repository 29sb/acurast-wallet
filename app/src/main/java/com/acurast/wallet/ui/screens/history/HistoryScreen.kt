package com.acurast.wallet.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acurast.wallet.data.model.*
import com.acurast.wallet.data.repository.WalletRepository
import kotlinx.coroutines.launch

/**
 * 交易历史屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    
    // 状态
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 模拟交易数据
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // 实际应该从链上查询交易历史
            // 这里模拟数据
            kotlinx.coroutines.delay(1000)
            transactions = listOf(
                Transaction(
                    hash = "0x1234567890abcdef",
                    from = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY",
                    to = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty",
                    amount = 1000000000,
                    fee = 1000000,
                    timestamp = System.currentTimeMillis() - 3600000,
                    status = TransactionStatus.CONFIRMED
                ),
                Transaction(
                    hash = "0xabcdef1234567890",
                    from = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty",
                    to = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY",
                    amount = 500000000,
                    fee = 500000,
                    timestamp = System.currentTimeMillis() - 7200000,
                    status = TransactionStatus.CONFIRMED
                ),
                Transaction(
                    hash = "0x9876543210fedcba",
                    from = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY",
                    to = "5DAAnrj7VHTznn2AWBemMuyBwZWs6Fzfj7ib7jdKSsEby2jV",
                    amount = 200000000,
                    fee = 750000,
                    timestamp = System.currentTimeMillis() - 86400000,
                    status = TransactionStatus.CONFIRMED
                )
            )
        } catch (e: Exception) {
            errorMessage = "加载交易历史失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("交易历史") },
                actions = {
                    IconButton(onClick = { /* 刷新 */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
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
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
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
            } else if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "暂无交易记录",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(transactions) { transaction ->
                        TransactionHistoryItem(transaction = transaction)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryItem(transaction: Transaction) {
    val isOutgoing = transaction.from == "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isOutgoing) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isOutgoing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isOutgoing) "发送" else "接收",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isOutgoing) "到: ${transaction.to.take(8)}...${transaction.to.takeLast(8)}" 
                        else "来自: ${transaction.from.take(8)}...${transaction.from.takeLast(8)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${if (isOutgoing) "-" else "+"}${transaction.amount / 1_000_000_000} ACU",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isOutgoing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${transaction.status.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 交易详情
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "哈希: ${transaction.hash.take(8)}...${transaction.hash.takeLast(8)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "手续费: ${transaction.fee / 1_000_000_000} ACU",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Text(
                "时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(transaction.timestamp))}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
