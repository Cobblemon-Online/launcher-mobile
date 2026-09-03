package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kdt.pojavlaunch.R

@Composable
public fun SettingsSidebar(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .fillMaxHeight()
    ) {

        Box(
            modifier = Modifier
                .width(116.dp)
                .wrapContentHeight()
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(
                    R.drawable.btn_voltar
                ),
                contentDescription = "Voltar",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Text(
                text = "Voltar",
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 9.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        SettingsSidebarItem(
            text = "Conta",
            selected = selectedTab == SettingsTab.ACCOUNT,
            onClick = {
                onTabSelected(SettingsTab.ACCOUNT)
            }
        )

        SettingsSidebarItem(
            text = "Launcher",
            selected = selectedTab == SettingsTab.LAUNCHER,
            onClick = {
                onTabSelected(SettingsTab.LAUNCHER)
            }
        )

        SettingsSidebarItem(
            text = "Modpack",
            selected = selectedTab == SettingsTab.MODPACK,
            onClick = {
                onTabSelected(SettingsTab.MODPACK)
            }
        )

        SettingsSidebarItem(
            text = "Sobre",
            selected = selectedTab == SettingsTab.ABOUT,
            onClick = {
                onTabSelected(SettingsTab.ABOUT)
            }
        )
    }
}
@Composable
private fun SettingsSidebarItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                start = 8.dp
            ),
            color = if (selected) {
                Color.White
            } else {
                Color(0xFF747474)
            },
            fontFamily = MinecraftFont,
            fontSize = 10.sp
        )
    }
}