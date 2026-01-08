package com.tardislabs.plaguesky.events;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.PlagueSky;
import com.tardislabs.plaguesky.blocks.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;

@Mod.EventBusSubscriber(modid = PlagueSky.MODID)
public class RespawnEvent {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
        Level level = player.level();
        
        // Skip if the event already has a respawn position (bed/anchor/etc)
        if (serverPlayer.getRespawnPosition() != null) { return; }

        // Only do server-side logic
        if (level.isClientSide) return;

        BlockPos pos = player.blockPosition();
        BlockState block = level.getBlockState(pos);

        // Pick a safe fallback pos (adjust to your liking)
        BlockPos safe = level.getSharedSpawnPos(); // World spawn

        // start at plague height and go down until we find a non air or non dragon skin block
        int y = Config.COMMON.plagueHeight.get();
        while (y > 0) {
            y--;
            if (
                !level
                    .getBlockState(new BlockPos(safe.getX(), y, safe.getZ()))
                    .is(BlockRegistry.DRAGONSKIN.get()) &&
                !level
                    .getBlockState(new BlockPos(safe.getX(), y, safe.getZ()))
                    .isAir()
            ) {
                break;
            }
        }

        // teleport player to safe position
        player.teleportTo(safe.getX() + 0.5, y + 1, safe.getZ() + 0.5);
    }
}
