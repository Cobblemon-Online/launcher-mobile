package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
public fun LauncherSettingsScreen(
    onOpenVideoSettings: () -> Unit,
    onOpenControlSettings: () -> Unit,
    onOpenJavaSettings: () -> Unit,
    onOpenMiscSettings: () -> Unit,
    onOpenExperimentalSettings: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "Launcher",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Gerencie seu launcher",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LauncherSettingsCard(
            title = "Vídeo e Renderizador",
            description = "Resolução e desempenho",
            onClick = onOpenVideoSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Controles",
            description = "Gestos, botões e escala",
            onClick = onOpenControlSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Ajustes do Java",
            description =
                "Java Runtimes, argumentos JVM, quantidade de RAM e sandbox",
            onClick = onOpenJavaSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Configurações diversas",
            description = "Organize seu jogo",
            onClick = onOpenMiscSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Opções experimentais",
            description =
                "Não fornecemos suporte a essas opções",
            onClick =
                onOpenExperimentalSettings
        )
    }
}

@Composable
private fun LauncherSettingsCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 10.sp,
            lineHeight = 12.sp
        )

//        Spacer(
//            modifier = Modifier.height(2.dp)
//        )

        Text(
            text = description,
            color = Color(0xFF858585),
            fontFamily = MinecraftFont,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            maxLines = 2
        )
    }
}