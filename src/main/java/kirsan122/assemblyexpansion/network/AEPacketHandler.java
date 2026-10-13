package kirsan122.assemblyexpansion.network;

import kirsan122.assemblyexpansion.network.packet.PacketUpdateConfig;
import kirsan122.assemblyexpansion.network.packet.PacketUpdateRedstone;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static kirsan122.assemblyexpansion.AssemblyExpansion.MOD_ID;

public class AEPacketHandler {
    public static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;


        net.messageBuilder(PacketUpdateRedstone.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketUpdateRedstone::new)
                .encoder(PacketUpdateRedstone::toBytes)
                .consumerMainThread(PacketUpdateRedstone::handle)
                .add();
        net.messageBuilder(PacketUpdateConfig.class, id())
                .encoder(PacketUpdateConfig::toBytes)
                .decoder(PacketUpdateConfig::new)
                .consumerMainThread(PacketUpdateConfig::handle)
                .add();
    }




    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}

