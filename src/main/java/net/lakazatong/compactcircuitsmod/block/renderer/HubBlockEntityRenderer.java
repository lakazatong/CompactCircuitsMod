package net.lakazatong.compactcircuitsmod.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HubBlockEntityRenderer extends BaseBlockEntityRenderer<HubBlockEntity> implements BlockEntityRenderer<HubBlockEntity> {
    public HubBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HubBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        if (level == null)
            return;
//        BlockState state = level.getBlockState(pBlockEntity.getBlockPos());

        int portNumber = be.getPortNumberAt(HubBlockEntity.UP);
        renderTextAbove(be.getCircuitId());
//        renderTextAbove(be.getCircuitId() + (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER ? "\n" + portNumber : ""));

//        portNumber = be.getPortNumberAt(HubBlockEntity.DOWN);
//        if (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER) renderTextBelow("" + portNumber);
//
//        for (int i = 0; i < 4; i++) {
//            Direction direction = Direction.fromSide(i).toAbsolute(state.getValue(HubBlock.FACING));
//            portNumber = be.getPortNumberAt(direction.side);
//            if (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER) renderTextAside("" + portNumber, direction.toMinecraftDirection());
//        }
    }
}
