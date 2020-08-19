package fr.lucidiax.wondara.countdown;

import fr.lucidiax.wondara.MinigamesPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private int timeInTicks;
    private CountdownType type;
    private CountdownHandler countdownHandler;

    public Countdown(int time, CountdownType type) {
        this.timeInTicks = time * type.getFactor();
        this.type = type;
    }

    public void start() {
        runTaskTimer(MinigamesPlugin.getInstance(), 0L, 1);
    }

    @Override
    public void run() {
        if (timeInTicks <= 0) {
            cancel();
            if (countdownHandler != null)
                countdownHandler.onFinish();
            return;
        }

        if(countdownHandler != null)
            switch (type) {
                case SECOND:
                    if(timeInTicks % 20 != 0)
                        break;
                case TICK:
                    countdownHandler.onTime(timeInTicks / type.getFactor());
                    break;
            }

        --timeInTicks;
    }

    public int getTimeLeft() {
        return timeInTicks / type.getFactor();
    }

    public CountdownType getType() {
        return type;
    }

    public void setCountdownHandler(CountdownHandler countdownHandler) {
        this.countdownHandler = countdownHandler;
    }

    public enum CountdownType {

        SECOND(20), TICK(1);

        private int factor;

        CountdownType(int factor) {
            this.factor = factor;
        }

        public int getFactor() {
            return factor;
        }
    }

}
