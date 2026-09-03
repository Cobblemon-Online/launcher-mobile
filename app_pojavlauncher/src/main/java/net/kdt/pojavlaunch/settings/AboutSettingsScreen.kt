package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kdt.pojavlaunch.BuildConfig
import net.kdt.pojavlaunch.R

@Composable
public fun AboutSettingsScreen(
    updateState: PlayUpdateUiState,
    onCheckForUpdate: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {

    LaunchedEffect(Unit) {
        if (updateState.status == PlayUpdateStatus.IDLE) {
            onCheckForUpdate()
        }
    }

    val statusText = when (updateState.status) {
        PlayUpdateStatus.IDLE -> "Verificação pendente"
        PlayUpdateStatus.CHECKING -> "Verificando atualização"
        PlayUpdateStatus.UP_TO_DATE -> "Versão atualizada"
        PlayUpdateStatus.AVAILABLE -> "Atualização disponível"
        PlayUpdateStatus.DOWNLOADING -> "Baixando atualização"
        PlayUpdateStatus.READY_TO_INSTALL -> "Pronta para instalar"
        PlayUpdateStatus.ERROR -> "Não foi possível verificar"
    }

    val statusColor = when (updateState.status) {
        PlayUpdateStatus.AVAILABLE,
        PlayUpdateStatus.READY_TO_INSTALL -> Color(0xFFFFC107)
        PlayUpdateStatus.ERROR -> Color(0xFFE57373)
        else -> Color(0xFF79D800)
    }

    val buttonText = when (updateState.status) {
        PlayUpdateStatus.CHECKING -> "VERIFICANDO..."
        PlayUpdateStatus.AVAILABLE -> "ATUALIZAR AGORA"
        PlayUpdateStatus.DOWNLOADING -> updateState.progressPercent?.let { "BAIXANDO $it%" }
            ?: "BAIXANDO..."
        PlayUpdateStatus.READY_TO_INSTALL -> "REINICIAR E INSTALAR"
        else -> "CHECAR ATUALIZAÇÃO"
    }

    val onUpdateClick = when (updateState.status) {
        PlayUpdateStatus.AVAILABLE -> onStartUpdate
        PlayUpdateStatus.READY_TO_INSTALL -> onCompleteUpdate
        PlayUpdateStatus.CHECKING,
        PlayUpdateStatus.DOWNLOADING -> ({})
        else -> onCheckForUpdate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        /*
         * =========================================================
         * CABEÇALHO
         * =========================================================
         */

        Text(
            text = "Sobre",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Sobre o Launcher",
            color = Color(0xFF777777),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * =========================================================
         * CARD DO LAUNCHER
         * =========================================================
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(98.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color(0xFF202020)
                )
                .padding(
                    horizontal = 30.dp,
                    vertical = 18.dp
                )
        ) {

            /*
             * Nome do launcher
             */
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "COBBLEMON ONLINE LAUNCHER",
                    color = Color.White,
                    fontFamily =
                        MinecraftFont,
                    fontSize = 9.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            /*
             * Versão + botão
             */
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                /*
                 * Status da versão
                 */
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * Círculo verde
                     */
                    Image(
                        painter = painterResource(
                            R.drawable.ic_check
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .width(27.dp)
                            .height(27.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            statusText,
                        color =
                            statusColor,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 9.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text = BuildConfig.VERSION_NAME,
                        color = Color.White,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 9.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                /*
                 * Botão atualizar
                 */
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(25.dp)
                        .background(
                            Color(0xFF858585)
                        )
                        .border(
                            width = 1.dp,
                            color =
                                Color(0xFFA7A7A7)
                        )
                        .clickable(onClick = onUpdateClick),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            buttonText,
                        color = Color.White,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 7.sp
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * =========================================================
         * RELEASE NOTES
         * =========================================================
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color(0xFF1D1D1D)
                )
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 12.dp,
                    bottom = 20.dp
                )
        ) {

            Text(
                text =
                    "Notas de atualização",
                color = Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize = 19.sp
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            /*
             * Tag da versão
             */
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF853AC7)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    )
            ) {

                Text(
                    text =
                        "ATUALIZAÇÕES PELA GOOGLE PLAY",
                    color =
                        Color(0xFFE4D5F2),
                    fontFamily =
                        MinecraftFont,
                    fontSize = 6.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )

            /*
             * Desempenho
             */
            AboutReleaseSection(
                title = "Status",
                description = updateState.message
                    ?: "A Google Play mantém o launcher atualizado automaticamente."
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            /*
             * Segurança
             */
            AboutReleaseSection(
                title = "Novidades",
                description =
                    "Consulte as novidades e o histórico da versão na página do launcher na Google Play."
            )
        }
    }
}
@Composable
private fun AboutReleaseSection(
    title: String,
    description: String
) {

    Column {

        Text(
            text = title,
            color = Color.White,
            fontFamily =
                MinecraftFont,
            fontSize = 11.sp,
            lineHeight = 13.sp
        )

        Text(
            text = description,
            color = Color(0xFF8A8A8A),
            fontFamily =
                MinecraftFont,
            fontSize = 9.sp,
            lineHeight = 11.sp
        )
    }
}