package kirsan122.assemblyexpansion.network.packet;
import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.IConfigurableBE;
import kirsan122.assemblyexpansion.api.IUniversalMenu;
import kirsan122.assemblyexpansion.api.ConfigModes;
import kirsan122.assemblyexpansion.common.block.transportation.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class PacketUpdateConfig {
    private final BlockPos pos;
    private final ConfigModes.ConfigType type;
    private final Direction side;

    public PacketUpdateConfig(BlockPos pos, ConfigModes.ConfigType type, Direction side) {
        this.pos = pos;
        this.type = type;
        this.side = side;
    }


    public PacketUpdateConfig(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.type = buf.readEnum(ConfigModes.ConfigType.class);
        this.side = buf.readEnum(Direction.class);
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.type);
        buf.writeEnum(this.side);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            net.minecraft.world.level.Level level = player.level();
            BlockEntity be = level.getBlockEntity(this.pos);
            if (be == null) return;

            if (be instanceof IConfigurableBE machine) {

                Direction facing = be.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);


                Direction relativeSide = GuiConfigHelper.getRelativeSide(facing, this.side);
                int sideId = relativeSide.get3DDataValue();


                int currentMode = machine.getDeviceSideMode(sideId);
                int nextMode = (currentMode + 1) % 3;


                if (player.containerMenu instanceof IUniversalMenu configMenu) {
                    configMenu.setSideMode(sideId, nextMode);
                }


                machine.setDeviceSideMode(sideId, nextMode);


                machine.refreshCaps();
                be.setChanged();



                level.sendBlockUpdated(this.pos, be.getBlockState(), be.getBlockState(), 2);


                for (Direction dir : Direction.values()) {
                    BlockPos neighborPos = this.pos.relative(dir);
                    BlockState neighborState = level.getBlockState(neighborPos);


                    if (neighborState.getBlock() instanceof CableBlock) {


                        BlockState updatedState = neighborState.updateShape(dir.getOpposite(), be.getBlockState(), level, neighborPos, this.pos);


                        if (updatedState != neighborState) {

                            level.setBlock(neighborPos, updatedState, 3);
                        }


                        level.blockUpdated(neighborPos, neighborState.getBlock());
                    } else {

                        level.neighborChanged(neighborPos, be.getBlockState().getBlock(), this.pos);
                    }
                }

            }
        });
        ctx.setPacketHandled(true);
        return true;
    }

}

