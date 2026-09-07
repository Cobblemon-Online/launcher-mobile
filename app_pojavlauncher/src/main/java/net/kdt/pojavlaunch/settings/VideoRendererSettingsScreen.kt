package net.kdt.pojavlaunch.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import kotlin.math.roundToInt

private data class RendererOption(
    val id: String?,
    val name: String
)

@Composable
fun VideoRendererSettingsScreen() {

    val context = LocalContext.current
    val prefs = LauncherPreferences.DEFAULT_PREF

    /*
     * =========================
     * Renderizadores do Pojav
     * =========================
     */

    val compatibleRenderers = remember {
        Tools.getCompatibleRenderers(context)
    }

    val rendererOptions = remember {
        buildList {

            // null = deixa o próprio launcher escolher
            add(
                RendererOption(
                    id = null,
                    name = "Automático"
                )
            )

            compatibleRenderers.rendererIds.forEachIndexed { index, id ->
                add(
                    RendererOption(
                        id = id,
                        name = compatibleRenderers.rendererDisplayNames[index]
                    )
                )
            }
        }
    }

    /*
     * =========================
     * Perfil atual
     * =========================
     */

    val currentProfile = remember {
        LauncherProfiles.load()
        LauncherProfiles.getCurrentProfile()
    }

    var selectedRenderer by remember {
        mutableStateOf(
            currentProfile.pojavRendererName
                ?.takeIf {
                    it in compatibleRenderers.rendererIds
                }
        )
    }

    /*
     * =========================
     * Resolução
     * =========================
     */

    val minimumResolution = remember {
        context.resources.getInteger(
            R.integer.resolution_seekbar_min
        )
    }

    val resolutionIncrement = remember {
        context.resources.getInteger(
            R.integer.resolution_seekbar_increment
        )
    }

    var resolution by remember {
        mutableFloatStateOf(
            (LauncherPreferences.PREF_SCALE_FACTOR * 100f)
                .coerceIn(
                    minimumResolution.toFloat(),
                    100f
                )
        )
    }

    /*
     * =========================
     * Switches
     * =========================
     */

    var sustainedPerformance by remember {
        mutableStateOf(
            LauncherPreferences.PREF_SUSTAINED_PERFORMANCE
        )
    }

    var alternateSurface by remember {
        mutableStateOf(
            LauncherPreferences.PREF_USE_ALTERNATE_SURFACE
        )
    }

    var forceVsync by remember {
        mutableStateOf(
            LauncherPreferences.PREF_FORCE_VSYNC
        )
    }

    var vsyncInZink by remember {
        mutableStateOf(
            LauncherPreferences.PREF_VSYNC_IN_ZINK
        )
    }

    var ignoreNotch by remember {
        mutableStateOf(
            LauncherPreferences.PREF_IGNORE_NOTCH
        )
    }

    /*
     * Faz a mesma atualização que o
     * LauncherPreferenceFragment original fazia.
     */
    fun reloadLauncherPreferences() {
        LauncherPreferences.loadPreferences(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        /*
         * Título
         */

        Text(
            text = "Vídeo e Renderizador",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Resolução e desempenho",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        /*
         * =========================
         * Renderizador
         * =========================
         */

        SettingsContainer {

            Text(
                text = "Renderizador",
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 10.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            rendererOptions.forEach { renderer ->

                val selected =
                    renderer.id == selectedRenderer

                RendererItem(
                    name = renderer.name,
                    selected = selected,
                    onClick = {

                        selectedRenderer =
                            renderer.id

                        currentProfile.pojavRendererName =
                            renderer.id

                        LauncherProfiles.write()
                    }
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        /*
         * =========================
         * Escala de resolução
         * =========================
         */

        SettingsContainer {

            Text(
                text = "Escala de resolução",
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 10.sp
            )

            Text(
                text = "Escolha a escala de resolução do jogo",
                color = Color(0xFF858585),
                fontFamily = MinecraftFont,
                fontSize = 7.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Slider(
                    value = resolution,
                    onValueChange = { rawValue ->

                        val rounded =
                            (
                                    (
                                            rawValue -
                                                    minimumResolution
                                            ) /
                                            resolutionIncrement
                                    )
                                .roundToInt() *
                                    resolutionIncrement +
                                    minimumResolution

                        resolution =
                            rounded.coerceIn(
                                minimumResolution,
                                100
                            ).toFloat()
                    },
                    onValueChangeFinished = {

                        prefs.edit()
                            .putInt(
                                "resolutionRatio",
                                resolution.toInt()
                            )
                            .apply()

                        reloadLauncherPreferences()
                    },
                    valueRange =
                        minimumResolution.toFloat()..100f,
                    steps = (
                            (100 - minimumResolution) /
                                    resolutionIncrement - 1
                            ).coerceAtLeast(0),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${resolution.toInt()}%",
                    color = Color.White,
                    fontFamily = MinecraftFont,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(
                        start = 8.dp
                    )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        /*
         * =========================
         * Sustained performance
         * =========================
         */

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.N
        ) {

            SettingSwitchCard(
                title =
                    "Ativar o modo de desempenho sustentado",
                description =
                    "Evita aquecimento e limita o pico de desempenho",
                checked = sustainedPerformance,
                onCheckedChange = {

                    sustainedPerformance = it

                    prefs.edit()
                        .putBoolean(
                            "sustainedPerformance",
                            it
                        )
                        .apply()

                    reloadLauncherPreferences()
                }
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )
        }

        /*
         * =========================
         * Alternate Surface
         * =========================
         */

        SettingSwitchCard(
            title =
                "Usar renderização de superfície alternativa",
            description =
                "Utiliza uma superfície alternativa para renderizar o jogo",
            checked = alternateSurface,
            onCheckedChange = {

                alternateSurface = it

                prefs.edit()
                    .putBoolean(
                        "alternate_surface",
                        it
                    )
                    .apply()

                reloadLauncherPreferences()
            }
        )

        /*
         * O Pojav original só mostra Force VSync
         * quando alternate_surface está ativo.
         */

        if (alternateSurface) {

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            SettingSwitchCard(
                title = "Forçar VSync",
                description =
                    "Força a sincronização vertical durante a renderização",
                checked = forceVsync,
                onCheckedChange = {

                    forceVsync = it

                    prefs.edit()
                        .putBoolean(
                            "force_vsync",
                            it
                        )
                        .apply()

                    reloadLauncherPreferences()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        /*
         * =========================
         * VSync Zink
         * =========================
         */

        SettingSwitchCard(
            title = "VSync no Zink",
            description =
                "Ativa sincronização vertical ao utilizar Zink",
            checked = vsyncInZink,
            onCheckedChange = {

                vsyncInZink = it

                prefs.edit()
                    .putBoolean(
                        "vsync_in_zink",
                        it
                    )
                    .apply()

                reloadLauncherPreferences()
            }
        )

        /*
         * =========================
         * Notch
         * =========================
         */

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.P &&
            LauncherPreferences.PREF_NOTCH_SIZE > 0
        ) {

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            SettingSwitchCard(
                title = "Ignorar recorte da tela",
                description =
                    "Permite que o jogo utilize a área do notch",
                checked = ignoreNotch,
                onCheckedChange = {

                    ignoreNotch = it

                    prefs.edit()
                        .putBoolean(
                            "ignoreNotch",
                            it
                        )
                        .apply()

                    reloadLauncherPreferences()
                }
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
private fun SettingsContainer(
    content: @Composable () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 9.dp
            )
    ) {
        content()
    }
}

@Composable
private fun RendererItem(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background =
        if (selected) {
            Color(0xFF3A3A3A)
        } else {
            Color(0xFF2C2C2C)
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 30.dp
            )
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(background)
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = name,
            color =
                if (selected) {
                    Color.White
                } else {
                    Color(0xFFD0D0D0)
                },
            fontFamily = MinecraftFont,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun SettingSwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = title,
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 9.sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = description,
                color = Color(0xFF858585),
                fontFamily = MinecraftFont,
                fontSize = 7.sp,
                lineHeight = 9.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange =
                onCheckedChange
        )
    }
}