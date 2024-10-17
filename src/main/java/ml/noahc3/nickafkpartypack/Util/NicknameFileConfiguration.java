package ml.noahc3.nickafkpartypack.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
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

    String getPlayerKey(String player)
    {
        return player.replaceFirst("^\\.", "__bedrock__"); 
    }

    public boolean setNickname(String player, String nick, int flag)
    {
        try {
            String key = "players." + getPlayerKey(player);
            ConfigurationSection section = s_memory.getConfigurationSection(key);
            Map<String, Object> map = section != null ? section.getValues(false) : new HashMap<String, Object>(); 
            map.put("player", player);
            map.put("nickname", nick);
            map.put("flag", flag);  // 0=未設定/1=設定済み
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
            String key = getPlayerKey(player);
            map.remove(key);
            s_memory.createSection("players", map);
            save();
            return true;
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
        return false;
    }

    public String findNickname(String player, int flagMatch)
    {
        try {
            ConfigurationSection section = s_memory.getConfigurationSection("players");
            if (section == null) return null;
            Map<String, Object> map = section.getValues(false); 
            if (map == null || map.isEmpty()) return null;
            String key = "players." + getPlayerKey(player);
            section = s_memory.getConfigurationSection(key);
            if (section == null) return null;
            Map<String, Object> entries = section.getValues(false); ;
            if (entries != null) {
                int flag = (int) entries.get("flag");
                if (flagMatch == -1 || flagMatch == flag) {
                    String nick = (String) entries.get("nickname");
                    return nick;
                }
            }
        } catch (Exception e) {
            Constants.plugin.getLogger().warning(e.toString());
        }
        return null;
    }
}
