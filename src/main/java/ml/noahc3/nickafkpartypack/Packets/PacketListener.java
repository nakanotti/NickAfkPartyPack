package ml.noahc3.nickafkpartypack.Packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedRemoteChatSessionData;
import ml.noahc3.nickafkpartypack.Util.Constants;
import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PacketListener {
    private static PacketAdapter playServerPlayerInfo;
    public static void init() {
        playServerPlayerInfo = new PacketAdapter(Constants.plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int idx = 1;
                if (MinecraftVersion.FEATURE_PREVIEW_UPDATE.atOrAbove()) { // 1.19.3 以降
                    Set<EnumWrappers.PlayerInfoAction> actions = event.getPacket().getPlayerInfoActions().read(0);
                    if (!actions.contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) return;
                } else {    // 1.19.2 以前
                    if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER) return;
                    idx = 0;
                }
                final boolean bShowDisplayNameOverHeads = true;
                final boolean bShowAfkTagOverHeads = Constants.config.getBoolean("show-afk-tag-over-heads");
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(idx);
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                for (PlayerInfoData pid : playerInfoDataList) {
                    if (pid == null) continue;
                    WrappedGameProfile profile = pid.getProfile();
                    PlayerInfoData newPid = pid;
                    if (profile != null) {
                        Player player = Bukkit.getPlayer(profile.getUUID());
                        String displayName = Tasks.getPlayerDisplayName(player);
                        player.setDisplayName(displayName);
                        String prefix = Tasks.getPlayerPrefix(player);
                        String fullName = prefix + displayName;
                        if (bShowDisplayNameOverHeads) {
                            String headName = bShowAfkTagOverHeads ? fullName : displayName;
                            WrappedGameProfile newProfile = profile.withName(Tasks.cropString(headName, 16));
                            newProfile.getProperties().putAll(profile.getProperties());
                            profile = newProfile;
                        }
                        WrappedRemoteChatSessionData rcsd = pid.getRemoteChatSessionData();
                        newPid = new PlayerInfoData(pid.getProfileId(), pid.getLatency(), pid.isListed(), pid.getGameMode(), profile, WrappedChatComponent.fromText(fullName), rcsd);
                    }
                    newPlayerInfoDataList.add(newPid);
                }
                //Bukkit.getLogger().info(newPlayerInfoDataList.toString());
                event.getPacket().getPlayerInfoDataLists().write(idx, newPlayerInfoDataList);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(playServerPlayerInfo);
    }

    public static void deinit
    () {
        ProtocolLibrary.getProtocolManager().removePacketListener(playServerPlayerInfo);
    }
}
