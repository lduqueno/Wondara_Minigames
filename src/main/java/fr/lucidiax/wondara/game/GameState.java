package fr.lucidiax.wondara.game;

public enum GameState {

    WAITING("§6En attente"), INGAME("§cEn jeu"), ENDING("§4Fin du jeu");

    private String name;

    GameState(String name) {
        this.name = name;
    }

    public boolean canJoin() {
        return this == WAITING;
    }

    public String getName() {
        return name;
    }
}
