package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope

/*
 * =========================================================
 * TOKENS VISUAIS
 * =========================================================
 *
 * Se quiser alterar o visual de TODAS as configurações,
 * muda apenas aqui.
 */

private val SettingsCardBackground =
    Color(0xFF1C1C1C)

private val SettingsPrimaryText =
    Color.White

private val SettingsSecondaryText =
    Color(0xFF858585)

private val SettingsSectionText =
    Color(0xFFAAAAAA)

private val SettingsCardShape =
    RoundedCornerShape(7.dp)

val SettingsCardSelectedBorder =
    Color(0xFF54E2B0)

val SettingsCardPadding =
    PaddingValues(
        horizontal = 12.dp,
        vertical = 8.dp
    )

/*
 * =========================================================
 * CABEÇALHO DE SUBTELA
 * =========================================================
 */

@Composable
fun SettingsPageHeader(
    title: String,
    description: String
) {

    Text(
        text = title,
        color = SettingsPrimaryText,
        fontFamily = MinecraftFont,
        fontSize = 15.sp
    )

    Text(
        text = description,
        color = Color(0xFF8D8D8D),
        fontFamily = MinecraftFont,
        fontSize = 9.sp,
        lineHeight = 11.sp
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )
}

/*
 * =========================================================
 * CARD BASE
 * =========================================================
 */

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    backgroundColor: Color = SettingsCardBackground,
    contentPadding: PaddingValues = SettingsCardPadding,
    content: @Composable ColumnScope.() -> Unit
) {

    var cardModifier =
        modifier
            .fillMaxWidth()
            .clip(SettingsCardShape)
            .background(backgroundColor)

    if (selected) {
        cardModifier =
            cardModifier.border(
                width = 1.dp,
                color = SettingsCardSelectedBorder,
                shape = SettingsCardShape
            )
    }

    if (onClick != null) {
        cardModifier =
            cardModifier.clickable(
                onClick = onClick
            )
    }

    Column(
        modifier =
            cardModifier.padding(
                contentPadding
            ),
        content = content
    )
}
/*
 * =========================================================
 * CARD DE AÇÃO
 * =========================================================
 *
 * Ex:
 * - Vídeo e Renderizador
 * - Personalizar controles
 * - Java Runtimes
 */

@Composable
fun SettingsActionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    SettingsCard(
        modifier = modifier,
        onClick = onClick
    ) {

        SettingsCardTexts(
            title = title,
            description = description
        )
    }
}

/*
 * =========================================================
 * CARD COM SWITCH
 * =========================================================
 */

@Composable
fun SettingsSwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    SettingsCard(
        modifier = modifier,
        onClick = if (enabled) {
            {
                onCheckedChange(!checked)
            }
        } else {
            null
        }
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            SettingsCardTexts(
                title = title,
                description = description,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = checked,
                onCheckedChange =
                    if (enabled) {
                        onCheckedChange
                    } else {
                        null
                    },
                enabled = enabled
            )
        }
    }
}

/*
 * =========================================================
 * CARD COM SLIDER
 * =========================================================
 */

@Composable
fun SettingsSliderCard(
    title: String,
    description: String,
    value: Int,
    min: Int,
    max: Int,
    increment: Int = 1,
    valueSuffix: String = "",
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {

    val safeMin =
        min.coerceAtMost(max)

    val safeMax =
        max.coerceAtLeast(min)

    val safeIncrement =
        increment.coerceAtLeast(1)

    val safeValue =
        value.coerceIn(
            safeMin,
            safeMax
        )

    SettingsCard(
        modifier = modifier
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            SettingsCardTexts(
                title = title,
                description = description,
                modifier =
                    Modifier.weight(1f)
            )

            Text(
                text =
                    "$safeValue$valueSuffix",
                color =
                    SettingsPrimaryText,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    8.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Slider(
            value =
                safeValue.toFloat(),

            valueRange =
                safeMin.toFloat()..
                        safeMax.toFloat(),

            onValueChange = { rawValue ->

                val snapped =
                    (
                            rawValue /
                                    safeIncrement
                            )
                        .roundToInt()
                        .times(
                            safeIncrement
                        )
                        .coerceIn(
                            safeMin,
                            safeMax
                        )

                onValueChange(
                    snapped
                )
            },

            onValueChangeFinished =
                onValueChangeFinished
        )
    }
}

/*
 * =========================================================
 * TÍTULO DE SEÇÃO
 * =========================================================
 *
 * Ex:
 * Gestos
 * Mouse
 * Giroscópio
 * Gamepad
 */

@Composable
fun SettingsSectionTitle(
    text: String
) {

    Spacer(
        modifier =
            Modifier.height(14.dp)
    )

    Text(
        text = text,
        color = SettingsSectionText,
        fontFamily = MinecraftFont,
        fontSize = 8.sp
    )

    Spacer(
        modifier =
            Modifier.height(6.dp)
    )
}

/*
 * =========================================================
 * TEXTOS PADRÃO DO CARD
 * =========================================================
 */

@Composable
private fun SettingsCardTexts(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = title,
            color = SettingsPrimaryText,
            fontFamily = MinecraftFont,
            fontSize = 9.6.sp,
            lineHeight = 11.sp
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(
            text = description,
            color = SettingsSecondaryText,
            fontFamily = MinecraftFont,
            fontSize = 7.sp,
            lineHeight = 9.sp
        )
    }
}