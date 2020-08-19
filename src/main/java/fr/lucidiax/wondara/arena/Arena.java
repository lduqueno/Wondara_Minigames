package fr.lucidiax.wondara.arena;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.lucidiax.wondara.MinigamesPlugin;
import fr.lucidiax.wondara.countdown.Countdown;
import fr.lucidiax.wondara.countdown.CountdownHandler;
import fr.lucidiax.wondara.game.Game;
import fr.lucidiax.wondara.game.GameState;
import fr.lucidiax.wondara.game.Games;
import fr.lucidiax.wondara.listener.ArenaListener;
import fr.lucidiax.wondara.util.Area;
import fr.lucidiax.wondara.util.PlayerRestorer;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class Arena {

    private static final Color[] COLORS = new Color[]{Color.AQUA, Color.RED, Color.GREEN, Color.YELLOW, Color.BLACK, Color.ORANGE, Color.WHITE, Color.PURPLE};
    private static final int MAX_REGEN_TICKS = 6 * 20;

    private Area area;
    private Location spawn;
    private Location signLocation;
    private List<Player> players = Lists.newArrayList();
    private GameState state = GameState.WAITING;
    private Game game;
    private Countdown countdown;
    private Listener listener;
    private boolean gameProtected = true;
    private Map<String, Object> properties = Maps.newHashMap();

    public Arena(Area area, Location spawn, Location signLocation, Map<String, Object> properties, Games game) {
        this.area = area;
        this.spawn = spawn;
        this.signLocation = signLocation;
        this.game = game.get();
        this.properties = properties;
        listener = new ArenaListener(this);
    }

    public void startGame() {
        setState(GameState.INGAME);
        gameProtected = true;
        sendMessage("§bLa partie commence !");
        players.forEach(player -> {
            PlayerRestorer.get(player).clearAndSave();
            player.teleport(spawn);
            player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
        });
        if (game.shouldGiveColoredArmors()) {
            int iteration = 0;
            for (Player player : players) {
                Color color = COLORS[iteration++];
                player.getInventory().setHelmet(createArmor(Material.LEATHER_HELMET, color));
                player.getInventory().setChestplate(createArmor(Material.LEATHER_CHESTPLATE, color));
                player.getInventory().setLeggings(createArmor(Material.LEATHER_LEGGINGS, color));
                player.getInventory().setBoots(createArmor(Material.LEATHER_BOOTS, color));
            }
        }
        game.onGameStart(this);
        Bukkit.getPluginManager().registerEvents(listener, MinigamesPlugin.getInstance());
        countdown = new Countdown(3, Countdown.CountdownType.SECOND);
        countdown.setCountdownHandler(new CountdownHandler() {
            @Override
            public void onTime(int seconds) {
                players.forEach(player -> {
                    player.sendTitle("§e" + seconds + "..", "");
                    player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
                });
            }

            @Override
            public void onFinish() {
                gameProtected = false;
                players.forEach(player -> {
                    player.sendTitle("§bGO !", "");
                    player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 0.5f);
                });
                countdown = new Countdown(300, Countdown.CountdownType.SECOND);
                countdown.setCountdownHandler(new CountdownHandler() {
                    @Override
                    public void onTime(int seconds) {
                        if (seconds % 60 == 0 && seconds != 300)
                            sendMessage("§bLa partie se termine dans §6" + (seconds / 60) + " minute" + (seconds > 60 ? "s" : "") + " §b!");
                        else if (seconds <= 10)
                            sendMessage("§bLa partie se termine dans §6" + seconds + " seconde" + (seconds > 1 ? "s" : "") + " §b!");
                        game.onSecond(Arena.this, seconds);
                    }

                    @Override
                    public void onFinish() {
                        sendMessage("§bLa partie est terminée, personne n'a gagné !");
                        countdown = null;
                        stopGame();
                    }
                });
                countdown.start();
            }
        });
        countdown.start();
    }

    public void stopGame() {
        setState(GameState.ENDING);
        game.onGameFinish(this);
        new ArrayList<>(players).forEach(this::removePlayer);
        HandlerList.unregisterAll(listener);
        if (countdown != null && countdown.getTimeLeft() > 0)
            countdown.cancel();
        if (game.getRegenHandler() != null) {
            countdown = new Countdown(MAX_REGEN_TICKS, Countdown.CountdownType.TICK);
            countdown.setCountdownHandler(new CountdownHandler() {
                Map<Location, Material> blocksLeft = game.getRegenHandler().getBlocksToRegen(Arena.this);
                int blocksPerTick = blocksLeft.size() / MAX_REGEN_TICKS;

                @Override
                public void onTime(int time) {
                    int iteration = 0;
                    for (Map.Entry<Location, Material> entry : new HashSet<>(blocksLeft.entrySet())) {
                        entry.getKey().getBlock().setType(entry.getValue());
                        blocksLeft.remove(entry.getKey());
                        if (++iteration > blocksPerTick)
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    countdown = null;
                    setState(GameState.WAITING);
                }
            });
            countdown.start();
        } else
            setState(GameState.WAITING);
    }

    public void addPlayer(Player player) {
        if (hasPlayer(player) || !state.canJoin())
            return;
        players.add(player);
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
        sendMessage("§6" + player.getName() + " §ba rejoint la file d'attente (§e" + players.size() + "/" + game.getMaxPlayers() + " joueurs§b) !");
        if (players.size() == game.getMaxPlayers()) {
            startGame();
        } else if (players.size() >= game.getMinPlayers() && countdown == null) {
            countdown = new Countdown(20, Countdown.CountdownType.SECOND);
            countdown.setCountdownHandler(new CountdownHandler() {
                @Override
                public void onTime(int seconds) {
                    if (seconds % 10 == 0 || seconds <= 5) {
                        sendMessage("§bLa partie commence dans §6" + seconds + " seconde" + (seconds > 1 ? "s" : "") + " §b!");
                        players.forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f));
                    }
                    updateSign();
                }

                @Override
                public void onFinish() {
                    countdown = null;
                    startGame();
                }
            });
            countdown.start();
        }
        updateSign();
    }

    public void removePlayer(Player player) {
        if (!hasPlayer(player))
            return;
        players.remove(player);
        switch (state) {
            case WAITING:
                sendMessage("§6" + player.getName() + " §ca quitté la file d'attente (§e" + players.size() + "/" + game.getMaxPlayers() + " joueurs§c) !");
                player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
                if (countdown != null && players.size() < game.getMinPlayers() && countdown.getTimeLeft() > 0) {
                    sendMessage("§cAnnulation du compte à rebours, il n'y a pas assez de joueurs inscrits !");
                    countdown.cancel();
                    countdown = null;
                }
                break;
            case INGAME:
                sendMessage("§6" + player.getName() + " §best éliminé !");
                player.sendMessage("§cVous êtes éliminé !");
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                if (players.size() == 1) {
                    Player winner = players.get(0);
                    Firework fw = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    Random random = new Random();
                    fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255),
                            random.nextInt(255))).flicker(true).build());
                    fw.setFireworkMeta(fwm);
                    fw.detonate();
                    winner.sendMessage("§bTu as gagné la partie ! §dFélicitations !");
                    winner.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    game.onWin(winner);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MinigamesPlugin.getInstance().getCommandToExecuteOnWinning().replaceAll("%player%", winner.getName()));
                    if (countdown != null && countdown.getTimeLeft() > 0)
                        countdown.cancel();
                    countdown = new Countdown(3, Countdown.CountdownType.SECOND);
                    countdown.setCountdownHandler(new CountdownHandler() {
                        @Override
                        public void onTime(int seconds) {
                        }

                        @Override
                        public void onFinish() {
                            countdown = null;
                            stopGame();
                        }
                    });
                    countdown.start();
                } else if (players.size() == 0)
                    stopGame();
            case ENDING:
                PlayerRestorer restorer = PlayerRestorer.get(player);
                restorer.restore();
                PlayerRestorer.removePlayer(restorer);
                break;
        }
        updateSign();
    }

    public void updateSign() {
        if (!signLocation.getBlock().getType().name().contains("SIGN"))
            return;
        if(!signLocation.getChunk().isLoaded())
            signLocation.getChunk().load();
        Sign sign = (Sign) this.signLocation.getBlock().getState();
        sign.setLine(0, "§4§l" + game.getName());
        sign.setLine(1, state.getName() + (state.canJoin() && countdown != null ? " §0(" + countdown.getTimeLeft() + " s)" : ""));
        sign.setLine(2, "§f" + players.size() + " /" + game.getMaxPlayers() + " joueurs");
        sign.setLine(3, "> Clic droit <");
        sign.update();
    }

    public void sendMessage(String message) {
        players.forEach(p -> p.sendMessage("§e[" + game.getName() + "] " + message));
    }

    public Area getArea() {
        return area;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        updateSign();
    }

    public Game getGame() {
        return game;
    }

    public boolean isGameProtected() {
        return gameProtected;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    private ItemStack createArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
        lam.setColor(color);
        item.setItemMeta(lam);
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arena arena = (Arena) o;

        if (!area.getMinLoc().equals(arena.getArea().getMinLoc())) return false;
        return area.getMaxLoc().equals(arena.getArea().getMaxLoc());
    }

}
