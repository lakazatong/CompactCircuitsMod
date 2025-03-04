package net.lakazatong.compactcircuitsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.utils.ClientHooks;
import net.lakazatong.compactcircuitsmod.utils.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.CIRCUITS;

public class HubBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final MapCodec<HubBlock> CODEC = simpleCodec(HubBlock::new);
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public static final IntegerProperty FRONT_PORT_STATE = IntegerProperty.create("front_port_state", 0, 3);
    public static final IntegerProperty BACK_PORT_STATE = IntegerProperty.create("back_port_state", 0, 3);
    public static final IntegerProperty RIGHT_PORT_STATE = IntegerProperty.create("right_port_state", 0, 3);
    public static final IntegerProperty LEFT_PORT_STATE = IntegerProperty.create("left_port_state", 0, 3);
    public static final IntegerProperty UP_PORT_STATE = IntegerProperty.create("up_port_state", 0, 3);
    public static final IntegerProperty DOWN_PORT_STATE = IntegerProperty.create("down_port_state", 0, 3);

    public static final IntegerProperty[] SIDE_PROPERTIES = {
        FRONT_PORT_STATE, BACK_PORT_STATE,
        RIGHT_PORT_STATE, LEFT_PORT_STATE,
        UP_PORT_STATE, DOWN_PORT_STATE,
    };

    public HubBlock(Properties properties) {
        super(properties);
        BlockState state = this.defaultBlockState();
        for (int i = 0; i < 6; i++) state = state.setValue(SIDE_PROPERTIES[i], HubBlockEntity.DEFAULT_PORT_STATE);
        this.registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
        for (int i = 0; i < 6; i++) pBuilder.add(SIDE_PROPERTIES[i]);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos,
                                                     Player pPlayer, BlockHitResult pHitResult) {
        if(pPlayer.getUsedItemHand() != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (pPlayer.isCreative() && pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            ItemStack stack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);

            if (stack.getItem() == Items.NAME_TAG && !stack.getHoverName().getString().equals("Name Tag")) {
                String newCircuitId = stack.getHoverName().getString();

                if (pLevel.getBlockEntity(pPos) instanceof HubBlockEntity hubBlockEntity) {
                    String oldCircuitId = hubBlockEntity.getCircuitId();
                    hubBlockEntity.setCircuitId(newCircuitId);
                    int[] portNumbers = hubBlockEntity.getPortNumbers();
                    Circuit.migrateHub(hubBlockEntity, oldCircuitId, newCircuitId, portNumbers, portNumbers);
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (pLevel.isClientSide())
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openHubScreen(pPos));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof HubBlockEntity be) {
            String circuitId =  be.getCircuitId();
            if (!circuitId.isEmpty()) {
                Circuit circuit = CIRCUITS.get(be.getCircuitId());
                if (circuit != null) {
                    circuit.removeHub(be);
                    Circuit.removeIfEmpty(circuitId);
                }
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.core.Direction mDirection) {
        if (world.getBlockEntity(pos) instanceof HubBlockEntity be) {
            Circuit circuit = CIRCUITS.get(be.getCircuitId());
            if (circuit == null) return 0;
            int portNumber = be.getPortNumberAt(Direction.fromMinecraftDirection(mDirection.getOpposite())
                    .toRelative(state.getValue(HubBlock.FACING)).side);
            BlockPos portBlockPos = circuit.portBlocks.get(portNumber);
            if (portBlockPos != null && world.getBlockEntity(portBlockPos) instanceof PortBlockEntity portBlockEntity)
                return portBlockEntity.getIsInput() ? 0 : portBlockEntity.getSignalStrength();
        }
        return 0;
    }

    private void updateSignal(Level pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof HubBlockEntity be) {
            be.updateSignalStrengths(pLevel);
            for (int i = 0; i < 6; i++) {
                PortBlockEntity portBlockEntity = be.getPort(i);
                if (portBlockEntity != null)
                    pLevel.updateNeighborsAt(portBlockEntity.getBlockPos(), ModBlocks.PORT_BLOCK.get());
            }
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

    @Nullable
    private Direction getNeighborDirection(BlockPos pPos, BlockPos pNeighborPos) {
        int dx = pNeighborPos.getX() - pPos.getX();
        int dy = pNeighborPos.getY() - pPos.getY();
        int dz = pNeighborPos.getZ() - pPos.getZ();

        if (dx == 1 && dy == 0 && dz == 0) {
            return Direction.EAST;
        } else if (dx == -1 && dy == 0 && dz == 0) {
            return Direction.WEST;
        } else if (dx == 0 && dy == 1 && dz == 0) {
            return Direction.UP;
        } else if (dx == 0 && dy == -1 && dz == 0) {
            return Direction.DOWN;
        } else if (dx == 0 && dy == 0 && dz == 1) {
            return Direction.SOUTH;
        } else if (dx == 0 && dy == 0 && dz == -1) {
            return Direction.NORTH;
        }

        // Not adjacent
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.HUB_BLOCK.get().create(pPos, pState);
    }
}
