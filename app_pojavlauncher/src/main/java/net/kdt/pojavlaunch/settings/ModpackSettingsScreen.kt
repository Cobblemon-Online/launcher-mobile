package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.modpacks.ManagedModpack
import net.kdt.pojavlaunch.modpacks.ManagedModpackCatalog

@Composable
public fun ModpackSettingsScreen() {

    val context = LocalContext.current

    val activity =
        context as? LauncherActivity

    val modpacks =
        remember {
            ManagedModpackCatalog.getPacks()
        }

    var selectedModpackId by remember {
        mutableStateOf(
            activity?.selectedManagedModpackId
                ?: ManagedModpackCatalog.DEFAULT_PACK_ID
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "Modpack",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Selecione o modpack que deseja jogar",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        modpacks.forEachIndexed { index, modpack ->

            ModpackOptionCard(
                modpack = modpack,
                selected =
                    selectedModpackId == modpack.id,
                onClick = {

                    if (
                        selectedModpackId != modpack.id
                    ) {

                        selectedModpackId =
                            modpack.id

                        activity
                            ?.selectManagedModpackFromCompose(
                                modpack.id
                            )
                    }
                }
            )


            if (
                index < modpacks.lastIndex
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
            }
        }
    }
}
@Composable
private fun ModpackOptionCard(
    modpack: ManagedModpack,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .then(
                if (selected) {

                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFF54E2B0),
                        shape = RoundedCornerShape(8.dp)
                    )

                } else {

                    Modifier
                }
            )
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    modpack.name,
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    9.sp
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "MINECRAFT ${modpack.minecraftVersion}",
                color =
                    Color(0xFF8A8A8A),
                fontFamily =
                    MinecraftFont,
                fontSize =
                    7.sp
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    "VERSÃO DO MODPACK: ${modpack.modpackVersion}",
                color =
                    Color(0xFF8A8A8A),
                fontFamily =
                    MinecraftFont,
                fontSize =
                    7.sp
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    modpack.description,
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    8.sp
            )
        }


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        Box(
            modifier = Modifier
                .width(124.dp)
                .height(36.dp)
                .clip(
                    RoundedCornerShape(6.dp)
                )
                .background(
                    if (selected) {

                        Color(0xFF8B8B8B)

                    } else {

                        Color(0xFF7B2FD3)
                    }
                )
                .clickable(
                    enabled = !selected
                ) {

                    onClick()
                },
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    if (selected) {
                        "Selecionado"
                    } else {
                        "Selecionar"
                    },
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    9.sp
            )
        }
    }
}