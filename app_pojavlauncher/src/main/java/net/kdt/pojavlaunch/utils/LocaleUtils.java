package net.kdt.pojavlaunch.utils;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_FORCE_ENGLISH;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LocaleUtils extends ContextWrapper {

    public LocaleUtils(Context base) {
        super(base);
    }

    public static ContextWrapper setLocale(Context context) {

        if (DEFAULT_PREF == null) {

            DEFAULT_PREF =
                    PreferenceManager.getDefaultSharedPreferences(
                            context
                    );

            PREF_FORCE_ENGLISH =
                    DEFAULT_PREF.getBoolean(
                            "force_english",
                            false
                    );
        }

        /*
         * Novo seletor de idioma.
         *
         * Valores:
         * pt-BR
         * en
         */
        String languageTag =
                DEFAULT_PREF.getString(
                        "launcher_language",
                        null
                );

        Locale locale;

        if (languageTag != null) {

            if ("pt-BR".equals(languageTag)) {

                locale =
                        new Locale(
                                "pt",
                                "BR"
                        );

            } else if ("en".equals(languageTag)) {

                locale =
                        Locale.ENGLISH;

            } else {

                locale =
                        Locale.getDefault();
            }

        } else if (PREF_FORCE_ENGLISH) {

            /*
             * Compatibilidade com a configuração
             * antiga do Amethyst/Pojav.
             */
            locale =
                    Locale.ENGLISH;

        } else {

            /*
             * Nenhuma preferência salva:
             * mantém idioma do sistema.
             */
            return new LocaleUtils(context);
        }

        Resources resources =
                context.getResources();

        Configuration configuration =
                new Configuration(
                        resources.getConfiguration()
                );

        Locale.setDefault(locale);

        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            LocaleList localeList =
                    new LocaleList(locale);

            LocaleList.setDefault(
                    localeList
            );

            configuration.setLocales(
                    localeList
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

            context =
                    context.createConfigurationContext(
                            configuration
                    );

        } else {

            resources.updateConfiguration(
                    configuration,
                    resources.getDisplayMetrics()
            );
        }

        return new LocaleUtils(context);
    }
}