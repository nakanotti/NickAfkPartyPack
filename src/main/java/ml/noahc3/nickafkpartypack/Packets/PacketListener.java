package ml.noahc3.nickafkpartypack.Packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedProfilePublicKey;
import com.comphenix.protocol.wrappers.WrappedProfilePublicKey.WrappedProfileKeyData;
import ml.noahc3.nickafkpartypack.Util.Constants;
import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PacketListener {
    private static PacketAdapter playServerPlayerInfo;
    public static void init() {
        playServerPlayerInfo = new PacketAdapter(Constants.plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Set<EnumWrappers.PlayerInfoAction> actions = event.getPacket().getPlayerInfoActions().read(0);
                if (!actions.contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) return;
                final boolean bShowAfkTagOverHeads = Constants.config.getBoolean("show-afk-tag-over-heads");
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(1);
                if (actions.contains(EnumWrappers.PlayerInfoAction.INITIALIZE_CHAT)) {
                    // INITIALIZE_CHAT のアクションが含まれる際に、新しい newPlayerInfoDataList を write(1) してしまうと、
                    // チャットした瞬間に「チャットメッセージの検証に失敗しました。」と、クライアント側が落ちる問題が、1.19.4 から発生。
                    // 可能性としては、INITIALIZE_CHAT に対応する署名付きデータが失われているか、正しく送れていない可能性がある。
                    //Bukkit.getLogger().info(actions.toString());
                    //Bukkit.getLogger().info(playerInfoDataList.toString());
                    //EventListener.postRequestRefresh();
                    return; // チャットの初期化時は緊急回避
                }
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                for (PlayerInfoData pid : playerInfoDataList) {
                    if (pid == null) continue;  // ないはず
                    WrappedGameProfile profile = pid.getProfile();
                    PlayerInfoData newPid = pid;
                    if (profile != null) {
                        UUID uuid = profile.getUUID();
                        Player player = Bukkit.getPlayer(uuid);
                        WrappedProfileKeyData key;
                        try {
                            key = WrappedProfilePublicKey.ofPlayer(player).getKeyData();
                        } catch (Exception e) {
                            //Bukkit.getLogger().info("key = WrappedProfilePublicKey.ofPlayer(player).getKeyData() ... failed.");
                            //Bukkit.getLogger().info(e.toString());
                            key = pid.getProfileKeyData();
                        }
                        String displayName = Tasks.getPlayerDisplayName(player);
                        String prefix = Tasks.getPlayerPrefix(player);
                        String fullName = prefix + displayName;
                        String headName = bShowAfkTagOverHeads ? fullName : displayName;

                        player.setDisplayName(displayName);
                        WrappedGameProfile newProfile = profile.withName(Tasks.cropString(headName, 16));
                        newProfile.getProperties().putAll(profile.getProperties());
                        newPid = new PlayerInfoData(newProfile, pid.getLatency(), pid.getGameMode(), WrappedChatComponent.fromText(fullName), key);
                     }
                     newPlayerInfoDataList.add(newPid);
                }
                //Bukkit.getLogger().info(newPlayerInfoDataList.toString());
                event.getPacket().getPlayerInfoDataLists().write(1, newPlayerInfoDataList);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(playServerPlayerInfo);
    }

    public static void deinit
    () {
        ProtocolLibrary.getProtocolManager().removePacketListener(playServerPlayerInfo);
    }
}
