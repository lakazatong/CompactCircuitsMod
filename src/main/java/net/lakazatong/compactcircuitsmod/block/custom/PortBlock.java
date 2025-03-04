package net.lakazatong.compactcircuitsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.utils.ClientHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.CIRCUITS;

public class PortBlock extends Block implements EntityBlock {
    private static final MapCodec<PortBlock> CODEC = simpleCodec(PortBlock::new);
    @Override public MapCodec<PortBlock> codec() {
        return CODEC;
    }

    // Input / Output
    public static final BooleanProperty IS_INPUT = BooleanProperty.create("is_input");
    // deduced from its port number (0 means closed)
    public static final BooleanProperty IS_OPEN = BooleanProperty.create("is_open");
    // on which side of its circuit block this port is on (6 means none <=> closed)
    public static final IntegerProperty SIDE = IntegerProperty.create("side", 0, 6);

    public PortBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(IS_INPUT, PortBlockEntity.DEFAULT_IS_INPUT)
                .setValue(IS_OPEN, PortBlockEntity.DEFAULT_IS_OPEN)
                .setValue(SIDE, PortBlockEntity.DEFAULT_SIDE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(IS_INPUT);
        pBuilder.add(IS_OPEN);
        pBuilder.add(SIDE);
    }

@Override
public @NotNull InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos,
                                                 Player pPlayer, BlockHitResult pHitResult) {
    if(pPlayer.getUsedItemHand() != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

    if (pPlayer.isCreative() && pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND) {
        ItemStack stack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);

        if (stack.getItem() == Items.NAME_TAG && !stack.getHoverName().getString().equals("Name Tag")) {
            String newCircuitId = stack.getHoverName().getString();

            if (pLevel.getBlockEntity(pPos) instanceof PortBlockEntity portBlockEntity) {
                String oldCircuitId = portBlockEntity.getCircuitId();
                portBlockEntity.setCircuitId(newCircuitId);
                int portNumber = portBlockEntity.getPortNumber();
                Circuit.migratePort(portBlockEntity, oldCircuitId, newCircuitId, portNumber, portNumber);
            }

            return InteractionResult.SUCCESS;
        }
    }

    if (pLevel.isClientSide())
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openPortScreen(pPos));

    return InteractionResult.SUCCESS;
}

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof PortBlockEntity be) {
            String circuitId =  be.getCircuitId();
            if (!circuitId.isEmpty()) {
                Circuit circuit = CIRCUITS.get(be.getCircuitId());
                if (circuit != null) {
                    circuit.removePort(be);
                    Circuit.removeIfEmpty(circuitId);
                }
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(IS_INPUT);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.core.Direction mDirection) {
        if (world.getBlockEntity(pos) instanceof PortBlockEntity be) {
            if (!be.getIsInput()) return 0;

            Circuit circuit = CIRCUITS.get(be.getCircuitId());
            if (circuit == null) return 0;
            int portNumber = be.getPortNumber();
            BlockPos hubBlockPos = circuit.hubBlocks.get(portNumber);

            if (hubBlockPos != null && world.getBlockEntity(hubBlockPos) instanceof HubBlockEntity hubBlockEntity)
                return hubBlockEntity.getSignalStrength(portNumber);
        }
        return 0;
    }

    private void updateSignal(Level pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof PortBlockEntity be) {
            be.updateSignalStrength(pLevel);
            HubBlockEntity hubBlockEntity = be.getHub();
            if (hubBlockEntity != null)
                pLevel.updateNeighborsAt(hubBlockEntity.getBlockPos(), ModBlocks.HUB_BLOCK.get());
        }
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        updateSignal(pLevel, pPos);
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, neighborBlock, neighborPos, movedByPiston);
        updateSignal(pLevel, pPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.PORT_BLOCK.get().create(pPos, pState);
    }
}