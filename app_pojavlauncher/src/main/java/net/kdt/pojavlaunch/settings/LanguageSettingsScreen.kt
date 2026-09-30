package net.kdt.pojavlaunch.settings

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import net.kdt.pojavlaunch.R
import androidx.preference.PreferenceManager

@Composable
fun LanguageSettingsScreen() {

    val context = LocalContext.current

    val preferences = remember {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    var selectedLanguage by remember {
        mutableStateOf(
            preferences.getString(
                "launcher_language",
                "pt-BR"
            ) ?: "pt-BR"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        SettingsPageHeader(
            title = stringResource(R.string.settings_language),
            description = stringResource(R.string.settings_select_language)
        )

        LanguageOption(
            name = "Português (Brasil)",
            selected = selectedLanguage == "pt-BR",
            onClick = {
                selectedLanguage = "pt-BR"

                setLauncherLanguage(
                    context = context,
                    languageTag = "pt-BR"
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LanguageOption(
            name = "English",
            selected = selectedLanguage == "en",
            onClick = {
                selectedLanguage = "en"

                setLauncherLanguage(
                    context = context,
                    languageTag = "en"
                )
            }
        )
    }
}

@Composable
private fun LanguageOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    SettingsActionCard(
        title = name,
        description =
            if (selected) {
                "Selecionado"
            } else {
                ""
            },
        onClick = onClick
    )
}

private fun setLauncherLanguage(
    context: Context,
    languageTag: String
) {

    PreferenceManager
        .getDefaultSharedPreferences(context)
        .edit()
        .putString(
            "launcher_language",
            languageTag
        )
        .apply()

    /*
     * A Activity será recriada e o attachBaseContext()
     * passará novamente pelo LocaleUtils.setLocale().
     */
    (context as? Activity)?.recreate()
}
