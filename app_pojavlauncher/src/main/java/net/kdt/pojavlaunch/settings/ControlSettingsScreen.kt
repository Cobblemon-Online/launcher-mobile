package net.kdt.pojavlaunch.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.kdt.pojavlaunch.CustomControlsActivity
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import kotlin.math.roundToInt

@Composable
fun ControlSettingsScreen() {

    val context = LocalContext.current
    val prefs = LauncherPreferences.DEFAULT_PREF

    /*
     * =========================================================
     * GESTOS
     * =========================================================
     */

    /*
     * Essa opção é específica da nossa tela.
     *
     * Ela fica persistida para podermos ligar posteriormente
     * ao comportamento dos gestos.
     */
    var swapGestures by remember {
        mutableStateOf(
            prefs.getBoolean(
                "swapGestures",
                false
            )
        )
    }

    var disableGestures by remember {
        mutableStateOf(
            LauncherPreferences.PREF_DISABLE_GESTURES
        )
    }

    var disableDoubleTap by remember {
        mutableStateOf(
            LauncherPreferences.PREF_DISABLE_SWAP_HAND
        )
    }

    var longPressTrigger by remember {
        mutableIntStateOf(
            LauncherPreferences.PREF_LONGPRESS_TRIGGER
        )
    }

    /*
     * =========================================================
     * BOTÕES
     * =========================================================
     */

    var buttonScale by remember {
        mutableIntStateOf(
            LauncherPreferences.PREF_BUTTONSIZE
                .roundToInt()
        )
    }

    var buttonAllCaps by remember {
        mutableStateOf(
            LauncherPreferences.PREF_BUTTON_ALL_CAPS
        )
    }

    /*
     * =========================================================
     * MOUSE
     * =========================================================
     */

    var mouseScale by remember {
        mutableIntStateOf(
            (
                    LauncherPreferences.PREF_MOUSESCALE *
                            100f
                    ).roundToInt()
        )
    }

    var mouseSpeed by remember {
        mutableIntStateOf(
            (
                    LauncherPreferences.PREF_MOUSESPEED *
                            100f
                    ).roundToInt()
        )
    }

    var mouseStart by remember {
        mutableStateOf(
            LauncherPreferences.PREF_VIRTUAL_MOUSE_START
        )
    }

    /*
     * =========================================================
     * GIROSCÓPIO
     * =========================================================
     */

    val gyroAvailable = remember {
        Tools.deviceSupportsGyro(context)
    }

    var enableGyro by remember {
        mutableStateOf(
            LauncherPreferences.PREF_ENABLE_GYRO
        )
    }

    var gyroSensitivity by remember {
        mutableIntStateOf(
            (
                    LauncherPreferences.PREF_GYRO_SENSITIVITY *
                            100f
                    ).roundToInt()
        )
    }

    var gyroSampleRate by remember {
        mutableIntStateOf(
            LauncherPreferences.PREF_GYRO_SAMPLE_RATE
        )
    }

    var gyroSmoothing by remember {
        mutableStateOf(
            LauncherPreferences.PREF_GYRO_SMOOTHING
        )
    }

    var gyroInvertX by remember {
        mutableStateOf(
            LauncherPreferences.PREF_GYRO_INVERT_X
        )
    }

    var gyroInvertY by remember {
        mutableStateOf(
            LauncherPreferences.PREF_GYRO_INVERT_Y
        )
    }

    /*
     * =========================================================
     * GAMEPAD
     * =========================================================
     */

    var gamepadDeadzone by remember {
        mutableIntStateOf(
            (
                    LauncherPreferences.PREF_DEADZONE_SCALE *
                            100f
                    ).roundToInt()
        )
    }

    var showResetDialog by remember {
        mutableStateOf(false)
    }

    fun reloadPreferences() {
        LauncherPreferences.loadPreferences(
            context
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        /*
         * =========================================================
         * CABEÇALHO
         * =========================================================
         */

        SettingsPageHeader(
            title = "Controles",
            description = "Gestos, botões e escala"
        )

        /*
         * =========================================================
         * PERSONALIZAR CONTROLES
         * =========================================================
         */

        SettingsActionCard(
            title = "Personalizar controles",
            description =
                "Ajuste o esquema de controles de acordo com suas necessidades.",
            onClick = {

                context.startActivity(
                    Intent(
                        context,
                        CustomControlsActivity::class.java
                    )
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * RESETAR CONTROLES
         * =========================================================
         */

        SettingsActionCard(
            title = "Resetar controles",
            description =
                "Restaura o esquema de controles padrão.",
            onClick = {
                showResetDialog = true
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * TROCAR GESTOS
         * =========================================================
         */

        SettingsSwitchCard(
            title = "Trocar Gestos",
            description =
                "Troque os gestos, toque para bater, segure para colocar blocos ou usar um arco.",
            checked = swapGestures,
            onCheckedChange = {

                swapGestures = it

                prefs.edit()
                    .putBoolean(
                        "swapGestures",
                        it
                    )
                    .apply()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * DESATIVAR GESTOS
         * =========================================================
         */

        SettingsSwitchCard(
            title = "Desativar Gestos",
            description =
                "Desativa gestos, como segurar para quebrar blocos e tocar para colocar um bloco.",
            checked = disableGestures,
            onCheckedChange = {

                disableGestures = it

                prefs.edit()
                    .putBoolean(
                        "disableGestures",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * TOQUE DUPLO
         * =========================================================
         */

        SettingsSwitchCard(
            title =
                "Desativar toque duplo para trocar as mãos",
            description =
                "Desativa o toque duplo em linha na hotbar para trocar a mão secundária.",
            checked = disableDoubleTap,
            onCheckedChange = {

                disableDoubleTap = it

                prefs.edit()
                    .putBoolean(
                        "disableDoubleTap",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        /*
         * O Pojav original esconde essa opção
         * quando todos os gestos estão desativados.
         */
        if (!disableGestures) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SettingsSliderCard(
                title = "Atraso do toque longo",
                description =
                    "Altera o atraso do gatilho de toque longo.",
                value = longPressTrigger,
                valueSuffix = " ms",

                min = context.resources.getInteger(
                    R.integer.gesture_delay_seekbar_min
                ),

                max = context.resources.getInteger(
                    R.integer.gesture_delay_seekbar_max
                ),

                increment = context.resources.getInteger(
                    R.integer.gesture_delay_seekbar_increment
                ),

                onValueChange = {
                    longPressTrigger = it
                },

                onValueChangeFinished = {

                    prefs.edit()
                        .putInt(
                            "timeLongPressTrigger",
                            longPressTrigger
                        )
                        .apply()

                    reloadPreferences()
                }
            )
        }


        SettingsSectionTitle(
            text = "Botões"
        )

        SettingsSliderCard(
            title = "Escala dos botões",
            description =
                "Altera o tamanho dos botões de controle.",
            value = buttonScale,
            valueSuffix = " %",

            min = context.resources.getInteger(
                R.integer.button_scale_seekbar_min
            ),

            max = context.resources.getInteger(
                R.integer.button_scale_seekbar_max
            ),

            increment = context.resources.getInteger(
                R.integer.button_scale_seekbar_increment
            ),

            onValueChange = {
                buttonScale = it
            },

            onValueChangeFinished = {

                prefs.edit()
                    .putInt(
                        "buttonscale",
                        buttonScale
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsSwitchCard(
            title = "Botões em maiúsculo",
            description =
                "Exibe o texto dos botões usando letras maiúsculas.",
            checked = buttonAllCaps,
            onCheckedChange = {

                buttonAllCaps = it

                prefs.edit()
                    .putBoolean(
                        "buttonAllCaps",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        /*
         * =========================================================
         * MOUSE VIRTUAL
         * =========================================================
         */

        SettingsSectionTitle(
            text = "Mouse virtual"
        )

        SettingsSliderCard(
            title = "Escala do mouse",
            description =
                "Altera o tamanho do ponteiro do mouse virtual.",
            value = mouseScale,
            valueSuffix = " %",

            min = context.resources.getInteger(
                R.integer.mouse_scale_seekbar_min
            ),

            max = context.resources.getInteger(
                R.integer.mouse_scale_seekbar_max
            ),

            increment = context.resources.getInteger(
                R.integer.mouse_scale_seekbar_increment
            ),

            onValueChange = {
                mouseScale = it
            },

            onValueChangeFinished = {

                prefs.edit()
                    .putInt(
                        "mousescale",
                        mouseScale
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsSliderCard(
            title = "Velocidade do mouse",
            description =
                "Altera a velocidade do mouse virtual.",
            value = mouseSpeed,
            valueSuffix = " %",

            min = context.resources.getInteger(
                R.integer.mouse_speed_seekbar_min
            ),

            max = context.resources.getInteger(
                R.integer.mouse_speed_seekbar_max
            ),

            increment = context.resources.getInteger(
                R.integer.mouse_speed_seekbar_increment
            ),

            onValueChange = {
                mouseSpeed = it
            },

            onValueChangeFinished = {

                prefs.edit()
                    .putInt(
                        "mousespeed",
                        mouseSpeed
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsSwitchCard(
            title = "Iniciar com mouse virtual",
            description =
                "Ativa automaticamente o mouse virtual ao iniciar o jogo.",
            checked = mouseStart,
            onCheckedChange = {

                mouseStart = it

                prefs.edit()
                    .putBoolean(
                        "mouse_start",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        /*
         * =========================================================
         * GIROSCÓPIO
         * =========================================================
         */

        if (gyroAvailable) {

            SettingsSectionTitle(
                text = "Giroscópio"
            )

            SettingsSwitchCard(
                title = "Ativar giroscópio",
                description =
                    "Permite controlar a câmera movimentando o aparelho.",
                checked = enableGyro,
                onCheckedChange = {

                    enableGyro = it

                    prefs.edit()
                        .putBoolean(
                            "enableGyro",
                            it
                        )
                        .apply()

                    reloadPreferences()
                }
            )

            if (enableGyro) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SettingsSliderCard(
                    title = "Sensibilidade do giroscópio",
                    description =
                        "Altera a sensibilidade dos movimentos.",
                    value = gyroSensitivity,
                    valueSuffix = " %",

                    min = context.resources.getInteger(
                        R.integer.gyro_speed_seekbar_min
                    ),

                    max = context.resources.getInteger(
                        R.integer.gyro_speed_seekbar_max
                    ),

                    increment = context.resources.getInteger(
                        R.integer.gyro_speed_seekbar_increment
                    ),

                    onValueChange = {
                        gyroSensitivity = it
                    },

                    onValueChangeFinished = {

                        prefs.edit()
                            .putInt(
                                "gyroSensitivity",
                                gyroSensitivity
                            )
                            .apply()

                        reloadPreferences()
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SettingsSliderCard(
                    title = "Taxa de amostragem",
                    description =
                        "Intervalo entre as leituras do giroscópio.",
                    value = gyroSampleRate,
                    valueSuffix = " ms",

                    min = context.resources.getInteger(
                        R.integer.gyro_rate_seekbar_min
                    ),

                    max = context.resources.getInteger(
                        R.integer.gyro_rate_seekbar_max
                    ),

                    increment = 1,

                    onValueChange = {
                        gyroSampleRate = it
                    },

                    onValueChangeFinished = {

                        prefs.edit()
                            .putInt(
                                "gyroSampleRate",
                                gyroSampleRate
                            )
                            .apply()

                        reloadPreferences()
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SettingsSwitchCard(
                    title = "Suavização",
                    description =
                        "Suaviza os movimentos capturados pelo giroscópio.",
                    checked = gyroSmoothing,
                    onCheckedChange = {

                        gyroSmoothing = it

                        prefs.edit()
                            .putBoolean(
                                "gyroSmoothing",
                                it
                            )
                            .apply()

                        reloadPreferences()
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SettingsSwitchCard(
                    title = "Inverter eixo X",
                    description =
                        "Inverte o movimento horizontal do giroscópio.",
                    checked = gyroInvertX,
                    onCheckedChange = {

                        gyroInvertX = it

                        prefs.edit()
                            .putBoolean(
                                "gyroInvertX",
                                it
                            )
                            .apply()

                        reloadPreferences()
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                SettingsSwitchCard(
                    title = "Inverter eixo Y",
                    description =
                        "Inverte o movimento vertical do giroscópio.",
                    checked = gyroInvertY,
                    onCheckedChange = {

                        gyroInvertY = it

                        prefs.edit()
                            .putBoolean(
                                "gyroInvertY",
                                it
                            )
                            .apply()

                        reloadPreferences()
                    }
                )
            }
        }

        /*
         * =========================================================
         * GAMEPAD
         * =========================================================
         */

        SettingsSectionTitle(
            text = "Controle físico"
        )

        SettingsSliderCard(
            title = "Zona morta",
            description =
                "Ajusta a zona morta dos analógicos do controle.",
            value = gamepadDeadzone,
            valueSuffix = " %",

            min = context.resources.getInteger(
                R.integer.gamepad_deadzone_seekbar_min
            ),

            max = context.resources.getInteger(
                R.integer.gamepad_deadzone_seekbar_max
            ),

            increment = context.resources.getInteger(
                R.integer.gamepad_deadzone_seekbar_increment
            ),

            onValueChange = {
                gamepadDeadzone = it
            },

            onValueChangeFinished = {

                prefs.edit()
                    .putInt(
                        "gamepad_deadzone_scale",
                        gamepadDeadzone
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }

    /*
     * =========================================================
     * DIALOG RESET
     * =========================================================
     */

    if (showResetDialog) {

        AlertDialog(
            onDismissRequest = {
                showResetDialog = false
            },

            title = {
                Text(
                    text = "Resetar controles"
                )
            },

            text = {
                Text(
                    text =
                        "Deseja restaurar o esquema de controles padrão?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        prefs.edit()
                            .putString(
                                "defaultCtrl",
                                Tools.CTRLDEF_FILE
                            )
                            .apply()

                        reloadPreferences()

                        showResetDialog = false
                    }
                ) {
                    Text(
                        text = "Resetar"
                    )
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        showResetDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar"
                    )
                }
            }
        )
    }
}