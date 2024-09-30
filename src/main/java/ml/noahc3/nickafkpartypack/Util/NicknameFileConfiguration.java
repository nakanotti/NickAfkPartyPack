package ml.noahc3.nickafkpartypack.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

public class NicknameFileConfiguration {
    static private YamlConfiguration s_memory;

    public NicknameFileConfiguration()
    {
        load();
    }

    public void load()
    {
        s_memory = YamlConfiguration.loadConfiguration(new File(Constants.plugin.getDataFolder(), "nicknames.yml"));
    }

    public void save()
    {
        try {
            s_memory.save(new File(Constants.plugin.getDataFolder(), "nicknames.yml"));
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
    }

    public boolean setNickname(String player, String nick, boolean online)
    {
        try {
            String key = "players." + Base64.getEncoder().encodeToString(player.getBytes());
            ConfigurationSection section = s_memory.getConfigurationSection(key);
            Map<String, Object> map = section != null ? section.getValues(false) : new HashMap<String, Object>(); 
            int flag = online ? 1 : 0;  // 0=未設定/1=設定済み
            map.put("player", player);
            map.put("nickname", nick);
            map.put("flag", flag);
            s_memory.createSection(key, map);
            save();
            return true;
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
        return false;
    }

    public boolean removeNickname(String player)
    {
        try {
            ConfigurationSection section = s_memory.getConfigurationSection("players");
            if (section == null) return false;
            Map<String, Object> map = section.getValues(false); 
            if (map == null || map.isEmpty()) return false;
            String key = Base64.getEncoder().encodeToString(player.getBytes());
            map.remove(key);
            s_memory.createSection("players", map);
            save();
            return true;
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
        return false;
    }

    public String findOfflineNickname(String player)
    {
        try {
            ConfigurationSection section = s_memory.getConfigurationSection("players");
            if (section == null) return null;
            Map<String, Object> map = section.getValues(false); 
            if (map == null || map.isEmpty()) return null;
            String key = Base64.getEncoder().encodeToString(player.getBytes());
            section = s_memory.getConfigurationSection("players." + key);
            if (section == null) return null;
            Map<String, Object> entries = section.getValues(false); ;
            if (entries != null) {
                int flag = (int) entries.get("flag");
                if (flag == 0) {
                    String nick = (String) entries.get("nickname");
                    Constants.plugin.getLogger().info("New nickname: " + nick);
                    return nick;
                }
            }
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
        return null;
    }
}
