package kirsan122.assemblyexpansion.network.packet;

import kirsan122.assemblyexpansion.api.RedstoneModes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateRedstone {
    private final BlockPos pos;

    public PacketUpdateRedstone(BlockPos pos) {
        this.pos = pos;
    }

    public PacketUpdateRedstone(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Level level = player.level();
                if (level.isLoaded(pos)) {
                    BlockEntity be = level.getBlockEntity(pos);


                    if (be instanceof IRedstoneConfigurable machine) {
                        machine.setRedstoneMode(machine.getRedstoneMode().next());
                        be.setChanged();

                        level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
                    }
                }
            }
        });
        return true;
    }


    public interface IRedstoneConfigurable {

        RedstoneModes getRedstoneMode();

        void setRedstoneMode(RedstoneModes mode);






    }
}

