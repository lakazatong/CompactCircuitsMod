package net.lakazatong.compactcircuitsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.utils.ClientHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LampBlock extends Block implements EntityBlock {
    private static final MapCodec<LampBlock> CODEC = simpleCodec(LampBlock::new);
    @Override public MapCodec<LampBlock> codec() {
        return CODEC;
    }

    public static final BooleanProperty CLICKED = BooleanProperty.create("clicked");

    public LampBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CLICKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(CLICKED);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos,
                                                     Player pPlayer, BlockHitResult pHitResult) {
        if(pPlayer.getUsedItemHand() != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (pLevel.isClientSide())
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openLampScreen(pPos));

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.LAMP_BLOCK.get().create(pPos, pState);
    }
}
