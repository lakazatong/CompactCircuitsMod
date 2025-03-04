package net.lakazatong.compactcircuitsmod.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Arrays;

public class BaseBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected final BlockEntityRendererProvider.Context ctx;
    protected final Minecraft minecraft;
    protected final Camera camera;
    protected final Font font;
    protected Level level;
    protected T be;
    protected PoseStack ps;
    protected MultiBufferSource buffer;
    protected int light;
    protected int overlay;

    protected final float MIN_SCALE = 0.01f;
    protected final float MAX_SCALE = 0.03f;

    public BaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.ctx = context;
        this.minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
        this.camera = minecraft.gameRenderer.getMainCamera();
    }

    private Quaternionf createRotationFromCamera(float cameraX, float cameraY) {
        float yaw = 2f * (float) Math.PI * cameraX;
        float pitch = 2f * (float) Math.PI * cameraY;
        Quaternionf rotation = new Quaternionf();
        return rotation.rotateY(-yaw).rotateX(-pitch).normalize();
    }

    protected void drawTextUpward(
            String pText,
            float pX,
            float pY,
            int pColor,
            boolean pDropShadow,
            Matrix4f pMatrix,
            MultiBufferSource pBuffer,
            Font.DisplayMode pDisplayMode,
            int pBackgroundColor,
            int pPackedLightCoords) {
        String[] lines = pText.split("\n");

        int maxWidth = Arrays.stream(lines).mapToInt(font::width).reduce(0, Integer::max);

        float centeredX = pX - (maxWidth / 2f);

        for (int i = 0; i < lines.length; i++) {
            int j = lines.length - 1 - i;
            font.drawInBatch(
                    lines[j],
                    centeredX + (maxWidth - font.width(lines[j])) / 2f,
                    pY - i * (font.lineHeight + 2),
                    pColor,
                    pDropShadow,
                    pMatrix,
                    pBuffer,
                    pDisplayMode,
                    pBackgroundColor,
                    pPackedLightCoords
            );
        }
    }

    protected void drawTextDownward(
            String pText,
            float pX,
            float pY,
            int pColor,
            boolean pDropShadow,
            Matrix4f pMatrix,
            MultiBufferSource pBuffer,
            Font.DisplayMode pDisplayMode,
            int pBackgroundColor,
            int pPackedLightCoords) {
        String[] lines = pText.split("\n");

        int maxWidth = Arrays.stream(lines).mapToInt(font::width).reduce(0, Integer::max);

        float centeredX = pX - (maxWidth / 2f);

        for (int i = 0; i < lines.length; i++) {
            font.drawInBatch(
                    lines[i],
                    centeredX + (maxWidth - font.width(lines[i])) / 2f,
                    pY + i * (font.lineHeight + 2),
                    pColor,
                    pDropShadow,
                    pMatrix,
                    pBuffer,
                    pDisplayMode,
                    pBackgroundColor,
                    pPackedLightCoords
            );
        }
    }

    protected void renderTextAbove(String text) {
        BlockPos pos = be.getBlockPos().above();

        float distance = (float) camera.getPosition().distanceTo(pos.getCenter());
        float width = font.width(text);
        float scale = Math.clamp(distance / width, MIN_SCALE, MAX_SCALE);

        float cameraX = (camera.getYRot() % 360 + 180) / 360f; // 0.5 -> 1.5, +z -> +z
        float cameraY = camera.getXRot() / 360f; // -0.25 -> 0.25, up -> down

        // n = a * MAX_SCALE + b
        // m = a * MIN_SCALE + b
        float n = 0.66f;
        float m = 0.66f;
        float a = (n - m) / (MAX_SCALE - MIN_SCALE);
        float b = n - a * MAX_SCALE;

        int linesCount = text.chars().reduce(1, (acc, c) -> c == '\n' ? acc + 1 : acc);
        float amp = (a * scale + b) * (((linesCount - 1) * font.lineHeight) / 3f + font.lineHeight) * (cameraY / 0.25f);
        float translateX = 0.5f + amp / 16f * ((float) Math.cos(2 * Math.PI * (cameraX + 0.25f)));
        float translateZ = 0.5f + amp / 16f * ((float) Math.cos(2 * Math.PI * cameraX));

        ps.pushPose();

        ps.translate(translateX, 0.75f + Math.abs(cameraY), translateZ);
        ps.mulPose(createRotationFromCamera(cameraX, cameraY));
        ps.scale(scale, -scale, scale);

        drawTextUpward(
                text,
                0,
                -0.5f / scale,
                0xFFFFFF,
                true,
                ps.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0x000000,
                LightTexture.pack(10, level.getBrightness(LightLayer.SKY, pos))
        );

        ps.popPose();
    }

//    protected void renderTextBelow(String text) {
//        BlockPos pos = be.getBlockPos().below();
//
//        float distance = (float) camera.getPosition().distanceTo(pos.getCenter());
//        float width = font.width(text);
//        float scale = Math.clamp(distance / width, MIN_SCALE, MAX_SCALE);
//
//        float cameraX = (camera.getYRot() % 360 + 180) / 360f; // 0.5 -> 1.5, +z -> +z
//        float cameraY = camera.getXRot() / 360f; // -0.25 -> 0.25, up -> down
//
//        float n = 1.1f;
//        float m = 0.98f;
//        float a = (n - m) / (MAX_SCALE - MIN_SCALE);
//        float b = n - a * MAX_SCALE;
//
//        int linesCount = text.chars().reduce(1, (acc, c) -> c == '\n' ? acc + 1 : acc);
//        float amp = (a * scale + b) * (font.lineHeight + ((linesCount - 1) * font.lineHeight) / 4f) * (cameraY / 0.25f);
//        float translateX = 0.5f - amp / 16f * ((float) Math.cos(2 * Math.PI * (cameraX + 0.25f)));
//        float translateZ = 0.5f - amp / 16f * ((float) Math.cos(2 * Math.PI * cameraX));
//
//        ps.pushPose();
//
//        ps.translate(translateX, 0.25 - Math.abs(cameraY), translateZ);
//        ps.mulPose(createRotationFromCamera(cameraX, cameraY));
//        ps.scale(scale, -scale, scale);
//
//        drawTextDownward(
//                text,
//                0,
//                0.5f / scale,
//                0xFFFFFF,
//                true,
//                ps.last().pose(),
//                buffer,
//                Font.DisplayMode.NORMAL,
//                0x000000,
//                LightTexture.pack(10, level.getBrightness(LightLayer.SKY, pos))
//        );
//
//        ps.popPose();
//    }
//
//    protected void renderTextAside(String text, Direction direction) {
//        BlockPos pos = be.getBlockPos().relative(direction);
//
//        float distance = (float) camera.getPosition().distanceTo(pos.getCenter());
//        float width = font.width(text);
//        float scale = Math.clamp(distance / width, MIN_SCALE, MAX_SCALE);
//
//        float cameraX = (camera.getYRot() % 360 + 180) / 360f; // 0.5 -> 1.5, +z -> +z
//        float cameraY = camera.getXRot() / 360f; // -0.25 -> 0.25, up -> down
//
//        float n = 0.5f;
//        float m = 0.5f;
//        float a = (n - m) / (MAX_SCALE - MIN_SCALE);
//        float b = n - a * MAX_SCALE;
//
//        ps.pushPose();
//
//        int linesCount = text.chars().reduce(1, (acc, c) -> c == '\n' ? acc + 1 : acc);
//        float amp = (a * cameraY + b) * ((font.lineHeight + ((linesCount - 1) * font.lineHeight) / 4f)) / 16f;
//        float translateX = direction.getStepX() / 1.5f + 0.5f;
//        float translateY = 0.5f + amp;
//        float translateZ = direction.getStepZ() / 1.5f + 0.5f;
//
//        ps.translate(translateX, translateY, translateZ);
//        ps.mulPose(createRotationFromCamera(cameraX, cameraY));
//        ps.scale(scale, -scale, scale);
//
//        drawTextDownward(
//                text,
//                0f,
//                0.5f / scale,
//                0xFFFFFF,
//                true,
//                ps.last().pose(),
//                buffer,
//                Font.DisplayMode.NORMAL,
//                0x000000,
//                LightTexture.pack(10, level.getBrightness(LightLayer.SKY, pos))
//        );
//
//        ps.popPose();
//    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        level = pBlockEntity.getLevel();
        be = pBlockEntity;
        ps = pPoseStack;
        buffer = pBufferSource;
        light = pPackedLight;
        overlay = pPackedOverlay;
    }
}
