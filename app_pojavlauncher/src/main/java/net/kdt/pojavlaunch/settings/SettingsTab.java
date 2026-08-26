package net.kdt.pojavlaunch.settings;

public enum SettingsTab {

    ACCOUNT(
            "Conta",
            "Gerencie suas contas"
    ),

    MINECRAFT(
            "Minecraft",
            "Configurações relacionadas ao jogo"
    ),

    JAVA(
            "Java",
            "Gerencie memória e ambiente Java"
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