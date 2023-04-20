package ml.noahc3.nickafkpartypack.Util;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import ml.noahc3.nickafkpartypack.Packets.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.EnumSet;

public class Tasks {
    public static void refreshPlayer(Player player) {
        PlayerInfoData pid = new PlayerInfoData(WrappedGameProfile.fromPlayer(player),
            player.getPing(), EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
            WrappedChatComponent.fromText(player.getName()));

        WrapperPlayServerPlayerInfo updatePacket = new WrapperPlayServerPlayerInfo();
        EnumSet<EnumWrappers.PlayerInfoAction> actions = EnumSet.of(
            EnumWrappers.PlayerInfoAction.ADD_PLAYER,
            EnumWrappers.PlayerInfoAction.UPDATE_LISTED,
            EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        updatePacket.setAction(actions);
        updatePacket.setData(Collections.singletonList(pid));

        for(Player p : Bukkit.getOnlinePlayers())
        {
            p.hidePlayer(Constants.plugin, player);
            updatePacket.sendPacket(p);
            p.showPlayer(Constants.plugin, player);
        }
    }

    public static String getPlayerDisplayName(Player player) {
        String displayName;

        if (player == null) return "";

        PersistentDataContainer data = player.getPersistentDataContainer();
        String nick = data.get(Constants.nickKey, PersistentDataType.STRING);

        if (nick == null) displayName = WrappedGameProfile.fromPlayer(player).getName();
        else displayName = nick;

        return displayName;
    }

    public static boolean setPlayerNick(CommandSender sender, Player player, String nick) {
        if (nick.length() > 16) {
            sender.sendMessage("ニックネームは 16 文字以下にしてください。");
            return false;
        }

        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(Constants.nickKey, PersistentDataType.STRING, nick);

        Tasks.refreshPlayer(player);

        sender.sendMessage("ニックネームを設定しました。 '" + nick + "'");

        return true;
    }

    public static void removePlayerNick(CommandSender sender, Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.remove(Constants.nickKey);

        Tasks.refreshPlayer(player);

        sender.sendMessage("ニックネームを削除しました。");
    }

    public static boolean isPlayerNicked(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.has(Constants.nickKey, PersistentDataType.STRING);
    }

    public static boolean isPlayerAfk(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        Byte isAfk = data.get(Constants.afkKey, PersistentDataType.BYTE);

        return isAfk != null && isAfk == 1;
    }

    public static String getPlayerPrefix(Player player) {
        if (player == null || !isPlayerAfk(player)) return "";
        else return "[AFK]";
    }

    public static void setAfk(Player player, boolean isAfk) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        Byte wasAfk = data.get(Constants.afkKey, PersistentDataType.BYTE);
        if (wasAfk == null) wasAfk = 0;

        byte target = 0;
        if (isAfk) target = 1;

        if (!wasAfk.equals(target)) {
            data.set(Constants.afkKey, PersistentDataType.BYTE, target);

            Tasks.refreshPlayer(player);

            if (isAfk) Bukkit.broadcastMessage(Tasks.getPlayerDisplayName(player) + " 離席中…");
            else Bukkit.broadcastMessage(Tasks.getPlayerDisplayName(player) + " 復帰。");
        }
    }

    public static void toggleAfk(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        Byte isAfk = data.get(Constants.afkKey, PersistentDataType.BYTE);

        setAfk(player, isAfk == null || isAfk == 0);
    }

    public static String cropString(String str, int limit) {
        if (str.length() > limit) return str.substring(0, limit - 2) + "…";
        else return str;
    }
}
