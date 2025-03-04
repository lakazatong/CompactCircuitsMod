package net.lakazatong.compactcircuitsmod.screen;

import net.lakazatong.compactcircuitsmod.block.entity.LampBlockEntity;
import net.lakazatong.compactcircuitsmod.server.PacketHandler;
import net.lakazatong.compactcircuitsmod.server.UpdateLampPacket;
import net.lakazatong.compactcircuitsmod.utils.Utils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import static net.lakazatong.compactcircuitsmod.utils.Utils.translate;

public class LampScreen extends SafeScreen<LampBlockEntity> {
    private static Component ctranslate(String key) { return Utils.ctranslate("gui", "lamp_screen." + key); }

    private static final Component TITLE = ctranslate("title");

    public LampScreen(BlockPos pos) {
        super(TITLE, pos);
    }

    @Override
    protected void init() {
        super.init();
        if (unsafe()) return;

        int maxLength = 32;
        int maxWidth = getFieldWidth(maxLength);
        int fieldHeight = 20;
        int verticalSpacingWidth = 16;
        int verticalTotalSpacingWidth = fieldHeight + verticalSpacingWidth;
        int horizontalSpacing = 2;
        int horizontalSpacingWidth = getFieldWidth(horizontalSpacing);

        int centerX = width / 2;
        int centerY = height / 2;

        titleLabelX = centerX - font.width(TITLE) / 2;
        titleLabelY = centerY - 3 * verticalTotalSpacingWidth;

        int buttonsWidth = (maxWidth - horizontalSpacingWidth) / 2;

        String[] buttonTexts = {
                translate("word", "done"),
                translate("word", "cancel")
        };
        Button.OnPress[] buttonCallbacks = {button -> onDone(), button -> onCancel()};
        for (int k = 0; k < 2; k++) {
            int x = centerX - maxWidth / 2 + k * (buttonsWidth + horizontalSpacingWidth);
            int y = centerY + 2 * verticalTotalSpacingWidth;

            addRenderableWidget(Button.builder(Component.literal(buttonTexts[k]), buttonCallbacks[k])
                    .bounds(x, y, buttonsWidth, fieldHeight)
                    .build());
        }
    }

    @Override
    protected void onDone() {
        if (unsafe()) return;

        be.setClicked(!be.getClicked());

        PacketHandler.sendToServer(new UpdateLampPacket(be));

        onClose();
    }

    @Override
    protected void onCancel() {
        if (unsafe()) return;

        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
