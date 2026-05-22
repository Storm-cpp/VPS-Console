package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.VpsOrder
import com.example.ui.components.GooglePromoBanner
import com.example.ui.theme.*
import com.example.viewmodel.VpsViewModel
import kotlinx.coroutines.launch

data class PricingPlan(
    val name: String,
    val cpuCores: Int,
    val ramGb: Int,
    val diskGb: Int,
    val priceMonthly: Double,
    val labelBadge: String? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderScreen(
    viewModel: VpsViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val scope = rememberCoroutineScope()

    val plans = remember {
        listOf(
            PricingPlan("Micro Sandbox", 1, 2, 40, 3.99),
            PricingPlan("Balanced Compute", 2, 4, 80, 7.99, "RECOMMENDED"),
            PricingPlan("Production Cluster", 4, 8, 160, 15.99),
            PricingPlan("High-Performance Node", 8, 16, 320, 29.99)
        )
    }

    val locations = remember {
        listOf(
            "Europe Central (Frankfurt)",
            "North America West (Oregon)",
            "Asia Pacific East (Tokyo)",
            "UK South (London)"
        )
    }

    val osDistros = remember {
        listOf(
            "Ubuntu 24.04 LTS",
            "Debian 12 Bookworm",
            "CentOS Stream 9",
            "Rocky Linux 9.4"
        )
    }

    var selectedPlanIndex by remember { mutableStateOf(1) } // Default to Balanced
    var selectedLocation by remember { mutableStateOf(locations[0]) }
    var selectedOs by remember { mutableStateOf(osDistros[0]) }
    var contractMonths by remember { mutableStateOf(1) }

    val activePlan = plans[selectedPlanIndex]
    val discountMultiplier = when (contractMonths) {
        6 -> 0.90 // 10% discount
        12 -> 0.85 // 15% discount
        else -> 1.0
    }
    val finalPrice = activePlan.priceMonthly * discountMultiplier * contractMonths

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            GooglePromoBanner()
        }

        item {
            Text(
                text = "1. Select Hosting Plan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plans.forEachIndexed { index, plan ->
                    val isSelected = selectedPlanIndex == index
                    PlanRow(
                        plan = plan,
                        isSelected = isSelected,
                        onClick = { selectedPlanIndex = index }
                    )
                }
            }
        }

        item {
            Text(
                text = "2. OS Distribution & Cloud Zone",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text("Select Location Region", fontSize = 12.sp, color = TextSecondary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    locations.forEach { loc ->
                        val isSelected = selectedLocation == loc
                        ChoiceChip(
                            text = loc.substringBefore(" "),
                            isSelected = isSelected,
                            onClick = { selectedLocation = loc },
                            modifier = Modifier.testTag("location_chip_$loc")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(4.dp))

                Text("Operating System Image", fontSize = 12.sp, color = TextSecondary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    osDistros.forEach { os ->
                        val isSelected = selectedOs == os
                        ChoiceChip(
                            text = os.substringBefore(" "),
                            isSelected = isSelected,
                            onClick = { selectedOs = os },
                            modifier = Modifier.testTag("os_chip_$os")
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "3. Billing Period",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 6, 12).forEach { months ->
                    val isSelected = contractMonths == months
                    val pct = if (months == 1) "Daily" else if (months == 6) "Save 10%" else "Save 15%"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoogleBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isSelected) GoogleBlue else BorderColor, RoundedCornerShape(8.dp))
                            .clickable { contractMonths = months }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$months Mon",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GoogleBlue else TextPrimary
                            )
                            Text(text = pct, fontSize = 10.sp, color = if (isSelected) GoogleBlue else TextSecondary)
                        }
                    }
                }
            }
        }

        item {
            // Checkout Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "CONSOLIDATED ORDER ESTIMATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = activePlan.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "OS: $selectedOs | Region: $selectedLocation", fontSize = 12.sp, color = TextSecondary)
                        }
                        Text(
                            text = "\$${String.format("%.2f", finalPrice)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoogleGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.submitOrder(
                                planName = activePlan.name,
                                vCpu = activePlan.cpuCores,
                                ramGb = activePlan.ramGb,
                                diskGb = activePlan.diskGb,
                                duration = contractMonths,
                                region = selectedLocation,
                                os = selectedOs,
                                price = finalPrice
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("checkout_order_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Checkout", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.background)
                            Text("Initiate Cloud Deployment", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Order Deployment Status",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No prior orders. Trigger deployment to start simulation.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(orders, key = { it.id }) { ord ->
                val ordColor = when (ord.orderStatus) {
                    "ACTIVE" -> GoogleGreen
                    "DEPLOYING" -> GoogleYellow
                    "PENDING" -> GoogleBlue
                    else -> TextSecondary
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(ord.planName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("OS: ${ord.osDistribution} | Reg: ${ord.region}", fontSize = 11.sp, color = TextSecondary)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ordColor.copy(alpha = 0.12f),
                            contentColor = ordColor
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (ord.orderStatus == "DEPLOYING" || ord.orderStatus == "PENDING") {
                                    CircularProgressIndicator(
                                        color = ordColor,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    ord.orderStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanRow(
    plan: PricingPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GoogleBlue.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isSelected) GoogleBlue else BorderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = GoogleBlue)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(plan.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (plan.labelBadge != null) {
                    Surface(
                        color = GoogleYellow.copy(alpha = 0.2f),
                        contentColor = GoogleYellow,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            plan.labelBadge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                "${plan.cpuCores} vCPU | ${plan.ramGb} GB RAM | ${plan.diskGb} GB SSD",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("\$${plan.priceMonthly}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoogleGreen)
            Text("/month", fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun ChoiceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) GoogleBlue.copy(alpha = 0.16f) else GoogleDarkBg)
            .border(1.dp, if (isSelected) GoogleBlue else BorderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) GoogleBlue else TextSecondary
        )
    }
}
