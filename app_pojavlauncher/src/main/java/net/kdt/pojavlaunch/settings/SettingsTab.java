package net.kdt.pojavlaunch.settings;

public enum SettingsTab {

    ACCOUNT(
            "Conta",
            "Gerencie suas contas"
    ),

    MODPACK(
            "Modpack",
            "Configurações relacionadas ao modpack"
    ),

    LAUNCHER(
            "Launcher",
            "Configurações do launcher"
    ),

    ABOUT(
            "Sobre",
            "Informações sobre o launcher"
    );

    private final String title;
    private final String subtitle;

    SettingsTab(
            String title,
            String subtitle
    ) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}