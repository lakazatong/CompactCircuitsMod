package net.lakazatong.compactcircuitsmod.screen;

import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.ClientModEvents.getColorForSide;
import static net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity.DEFAULT_PORT_NUMBER;

public class HubScreen extends SafeScreen<HubBlockEntity> {
    private static Component ctranslate(String key) { return Utils.ctranslate("gui", "hub_config_screen." + key); }

    private static final Component TITLE = ctranslate("title");

    private EditBox circuitIdField;

    private final EditBox[] PORT_NUMBER_FIELDS = new EditBox[6];

    public HubScreen(BlockPos pos) {
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

        circuitIdField = new EditBox(font, centerX - maxWidth / 2, centerY - 2 * verticalTotalSpacingWidth, maxWidth, fieldHeight, Component.empty());
        circuitIdField.setMaxLength(maxLength);
        circuitIdField.setFilter(s -> s.matches("\\w*"));
        circuitIdField.insertText(be.getCircuitId());
        addRenderableWidget(circuitIdField);

        int nbCols = 3;
        int nbRows = 2;
        int portNumberFieldsWidth = (maxWidth - horizontalSpacingWidth * (nbCols - 1)) / nbCols;
        int portNumberFieldsLength = getFieldLength(portNumberFieldsWidth);

        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                int fieldIndex = j * nbRows + i;
                int x = centerX - maxWidth / 2 + j * (portNumberFieldsWidth + horizontalSpacingWidth);
                int y = centerY + (i - 1) * verticalTotalSpacingWidth;

                EditBox portField = new EditBox(font, x, y, portNumberFieldsWidth, fieldHeight, Component.empty());
                portField.setMaxLength(portNumberFieldsLength);
                portField.setFilter(s -> Utils.isValidNumber(s, portNumberFieldsLength));
                portField.insertText(be.getPortNumberAt(fieldIndex) > 0 ? String.valueOf(be.getPortNumberAt(fieldIndex)) : "");
                addRenderableWidget(portField);

                PORT_NUMBER_FIELDS[fieldIndex] = portField;
            }
        }

        int buttonsWidth = (maxWidth - horizontalSpacingWidth) / 2;

        String[] buttonTexts = {
                Utils.translate("word", "done"),
                Utils.translate("word", "cancel")
        };
        Button.OnPress[] buttonCallbacks = {button -> onDone(), button -> onCancel()};
        for (int k = 0; k < 2; k++) {
            int x = centerX - maxWidth / 2 + k * (buttonsWidth + horizontalSpacingWidth);
            int y = centerY + verticalTotalSpacingWidth;

            addRenderableWidget(Button.builder(Component.literal(buttonTexts[k]), buttonCallbacks[k])
                    .bounds(x, y, buttonsWidth, fieldHeight)
                    .build());
        }
    }

    @Override
    protected void onDone() {
        if (unsafe()) return;

        String oldCircuitId = be.getCircuitId();
        String newCircuitId = circuitIdField.getValue();
        int[] oldPortNumbers = be.getPortNumbers();
        int[] newPortNumbers = Arrays.stream(PORT_NUMBER_FIELDS).mapToInt(box ->
            box.getValue().isEmpty() ? DEFAULT_PORT_NUMBER : Integer.parseInt(box.getValue())
        ).toArray();

        be.setCircuitId(newCircuitId);

        Circuit.migrateHub(be, oldCircuitId, newCircuitId, oldPortNumbers, newPortNumbers);

        onClose();
    }

    @Override
    protected void onCancel() {
        if (unsafe()) return;

        onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (unsafe()) return;

        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawString(font, TITLE, titleLabelX, titleLabelY, 0xFFFFFF);
        guiGraphics.drawString(font, Utils.ctranslate("common", "circuit_id"), circuitIdField.getX(), circuitIdField.getY() - 10, 0xA0A0A0);

        Component[] sides = {
                Utils.ctranslate("word", "front"),
                Utils.ctranslate("word", "back"),
                Utils.ctranslate("word", "right"),
                Utils.ctranslate("word", "left"),
                Utils.ctranslate("word", "up"),
                Utils.ctranslate("word", "down"),
        };
        for (int i = 0; i < 6; i++) {
            guiGraphics.drawString(font, sides[i], PORT_NUMBER_FIELDS[i].getX(), PORT_NUMBER_FIELDS[i].getY() - 10, getColorForSide(i));
        }

        circuitIdField.render(guiGraphics, mouseX, mouseY, delta);
        for (int i = 0; i < 6; i++) {
            PORT_NUMBER_FIELDS[i].render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (unsafe()) return false;

        if (circuitIdField.keyPressed(keyCode, scanCode, modifiers) && circuitIdField.canConsumeInput()) return true;
        for (int i = 0; i < 6; i++)
            if (PORT_NUMBER_FIELDS[i].keyPressed(keyCode, scanCode, modifiers) && PORT_NUMBER_FIELDS[i].canConsumeInput()) return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (unsafe()) return false;

        boolean anyClicked = false;
        setFocused(null);
        if (circuitIdField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(circuitIdField);
            anyClicked = true;
        } else {
            for (int i = 0; i < 6; i++) {
                if (PORT_NUMBER_FIELDS[i].mouseClicked(mouseX, mouseY, button)) {
                    setFocused(PORT_NUMBER_FIELDS[i]);
                    anyClicked = true;
                }
            }
        }
        return anyClicked || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void setInitialFocus() {
        if (unsafe()) return;

        this.setInitialFocus(this.circuitIdField);
    }
}
