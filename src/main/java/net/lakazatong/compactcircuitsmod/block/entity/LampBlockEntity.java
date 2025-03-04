package net.lakazatong.compactcircuitsmod.block.entity;

import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.custom.LampBlock;
import net.lakazatong.compactcircuitsmod.server.UpdateLampPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LampBlockEntity extends UpdatableBlockEntity<UpdateLampPacket> {
    // dynamic block states

    public static final Boolean DEFAULT_CLICKED = false;

    // block properties

    // -----------------------------------------------------------------------------------------------------------------

    private Boolean clicked = DEFAULT_CLICKED;

    public LampBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(UpdateLampPacket.class, pType, pPos, pBlockState);
        if (!(pBlockState.getBlock() instanceof LampBlock)) return;
        clicked = pBlockState.getValue(LampBlock.CLICKED);
    }

    public LampBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(ModBlockEntities.LAMP_BLOCK.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putBoolean("clicked", clicked);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        clicked = pTag.getBoolean("clicked");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag pTag = new CompoundTag();
        saveAdditional(pTag, pRegistries);
        return pTag;
    }

    @Override
    public void handleUpdatePacket(ServerLevel serverLevel, UpdateLampPacket packet) {
        clicked = packet.clicked;

        serverLevel.setBlockAndUpdate(packet.pos, serverLevel.getBlockState(packet.pos)
                .setValue(LampBlock.CLICKED, packet.clicked)
        );
    }

    public Boolean getClicked() { return clicked; }
    public void setClicked(Boolean v) { clicked = v; }
}
