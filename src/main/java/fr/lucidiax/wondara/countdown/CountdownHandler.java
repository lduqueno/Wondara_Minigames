package fr.lucidiax.wondara.countdown;

public interface CountdownHandler {

    void onTime(int seconds);
    void onFinish();
}
