package me.f64.playtime;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.f64.playtime.commands.Playtime;
import me.f64.playtime.placeholderapi.Expansion;
import me.f64.playtime.utils.Chat;
//import me.f64.playtime.utils.UpdateChecker;

public class Main extends JavaPlugin implements Listener {
    public static Plugin plugin;
    public String storagePath = getDataFolder() + "/userdata.json";
    public boolean playerJoinForceSave;

    @Override
    public void onEnable() {
        plugin = this;
        getCommand("playtime").setExecutor(new Playtime(this));
        checkStorage();
        placeholderAPI();
        updateChecker();
        if (plugin.getConfig().getBoolean("playerJoin.forceSave")) playerJoinForceSave = true;
        else playerJoinForceSave = false;
    }

    private void updateChecker() {
        Chat.console("&7[PlayTime] &dYou got a fork of a fork installed!");
        Chat.console("&7[PlayTime] &dmichael's 3.7 --> 852DuartePls' 3.7 --> tbm00's 3.8");
        Chat.console("&7[PlayTime] &dhttps://github.com/tbm00/PlayTime");
        /*new UpdateChecker(this, 26016).getVersion(version -> {
            if (getDescription().getVersion().equalsIgnoreCase(version)) {
                Chat.console("&7[PlayTime] Latest version is &ainstalled&7! - v" + getDescription().getVersion());
            } else {
                Chat.console("&7[PlayTime] Latest version is &cnot installed&7! - v" + version);
            }
        });*/
    }

    private void placeholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Chat.console("&7[PlayTime] &bPlaceholderAPI &awas found&7! Registering Placeholders.");
            new Expansion(this).register();
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            Chat.console("&7[PlayTime] &bPlaceholderAPI &cwas not found&7! Disabling Plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(this::savePlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        savePlayer(e.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        JSONObject target = new JSONObject();

        Chat chat = new Chat(this);
        if (!(player.hasPlayedBefore()) || playerJoinForceSave) {
            target.put("uuid", player.getUniqueId().toString());
            target.put("lastName", player.getName());
            target.put("time", chat.ticksPlayed(player) + 1);
            target.put("joins", player.getStatistic(Statistic.LEAVE_GAME) + 1);
            target.put("session", chat.ticksPlayed(player));
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> writePlayer(target));
        }
    }

    public int getPlayerSession(final String name) {
        try (FileReader reader = new FileReader(storagePath)) {
            JSONArray players = (JSONArray) new JSONParser().parse(reader);
            for (final Object o : players) {
                final JSONObject player = (JSONObject) o;
                if (player.get("lastName").equals(name)) {
                    Chat chat = new Chat(this);
                    final Player p = Main.plugin.getServer().getPlayer(name);
                    final int session = Integer.parseInt(player.get("session").toString());
                    final int current = chat.ticksPlayed(p);
                    return current - session;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*@EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (e.getPlayer().getName().equals("itemnames")) {
            new UpdateChecker(this, 26016).getVersion(version -> {
                if (getDescription().getVersion().equalsIgnoreCase(version)) {
                    Chat.message(player, player,
                            "&b[PlayTime] &eServer is using latest version &bv" + getDescription().getVersion());
                } else {
                    Chat.message(player, player, "&b[PlayTime] &eServer is using &bv" + getDescription().getVersion()
                            + " &eLatest version is &bv" + version);
                }
            });
        }
    }*/

    private void checkStorage() {
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        File userdataFile = new File(storagePath);
        if (!userdataFile.exists()) {
            try (FileWriter writer = new FileWriter(userdataFile.getAbsoluteFile())) {
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void savePlayer(Player player) {
        JSONObject target = new JSONObject();
        Chat chat = new Chat(this);
        target.put("uuid", player.getUniqueId().toString());
        target.put("lastName", player.getName());
        target.put("time", chat.ticksPlayed(player));
        target.put("joins", player.getStatistic(Statistic.LEAVE_GAME) + 1);
        target.put("session", chat.ticksPlayed(player));
        if (!Bukkit.getPluginManager().isPluginEnabled(this))
            writePlayer(target);
        else
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> writePlayer(target));
    }

    @SuppressWarnings("unchecked")
    private void writePlayer(JSONObject target) {
        if (Bukkit.getPluginManager().isPluginEnabled(this) && Bukkit.isPrimaryThread()) {
            final JSONObject finalTarget = target;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> writePlayer(finalTarget));
            return;
        }

        JSONParser jsonParser = new JSONParser();
        JSONArray players = new JSONArray();;
        List<JSONObject> list = new ArrayList<>();

        try (FileReader reader = new FileReader(storagePath)) {
            Object obj = jsonParser.parse(reader);
            
            if (obj instanceof JSONArray) {
                players = (JSONArray) obj;
            } else if (obj instanceof JSONObject) {
                JSONObject singleObject = (JSONObject) obj;
                players.add(singleObject);
            }

            for (Object player : players) {
                JSONObject player_JSON = (JSONObject) player;
                if (!player_JSON.get("uuid").equals(target.get("uuid")))
                    list.add(player_JSON);
            }
            for (int i = 0; i < list.size(); i++) {
                if (Integer.parseInt(target.get("time").toString()) > Integer
                        .parseInt(list.get(i).get("time").toString())) {
                    JSONObject temp = list.get(i);
                    list.set(i, target);
                    target = temp;
                }
            }
            list.add(target);
            JSONArray sortedPlayers = new JSONArray();
            sortedPlayers.addAll(list);
            try (FileWriter writer = new FileWriter(storagePath)) {
                writer.write(sortedPlayers.toJSONString().replace(",", ",\n").replace("{", "{\n").replace("}", "\n}"));
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            players = new JSONArray();
        }
    }
}