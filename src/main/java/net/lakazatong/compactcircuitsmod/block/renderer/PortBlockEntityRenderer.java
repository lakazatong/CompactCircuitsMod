package net.lakazatong.compactcircuitsmod.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PortBlockEntityRenderer extends BaseBlockEntityRenderer<PortBlockEntity> implements BlockEntityRenderer<PortBlockEntity> {
    public PortBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PortBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        if (level == null) {
            return;
        }

        String circuitId = pBlockEntity.getCircuitId();
        int portNumber = pBlockEntity.getPortNumber();

        StringBuilder text = new StringBuilder(circuitId);

        if (circuitId.isEmpty()) {
            if (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER) {
                text.append(portNumber);
            }
        } else if (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER) {
            text.append("\n").append(portNumber);
        }

        renderTextAbove(text.toString());
    }

}
