package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.triggers.TriggerType;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "genericsFtw", at = @At("HEAD"), cancellable = true)
    private static void onPacketReceived(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ClientPacketListener) {
            TriggerType.PACKET_RECEIVED.triggerAll(packet, ci);
        }
    }
}
