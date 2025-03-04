package net.lakazatong.compactcircuitsmod.screen;

import net.lakazatong.compactcircuitsmod.block.custom.PortBlock;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class PortScreen extends SafeScreen<PortBlockEntity> {
    private static Component ctranslate(String key) { return Utils.ctranslate("gui", "port_config_screen." + key); }

    private static final Component TITLE = ctranslate("title");

    private EditBox circuitIdField;
    private EditBox portNumberField;
    private EditBox busSizeField;

    private Boolean initialIsInput;

    private Button inputOutputToggle;

    public PortScreen(BlockPos pos) {
        super(TITLE, pos);
    }

    @Override
    protected void init() {
        super.init();
        if (unsafe()) return;

        initialIsInput = be.getIsInput();

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

        portNumberField = new EditBox(font, centerX - maxWidth / 2, centerY - verticalTotalSpacingWidth, maxWidth / 2, fieldHeight, Component.empty());
        portNumberField.setMaxLength(maxLength / 2);
        portNumberField.setFilter(s -> Utils.isValidNumber(s, maxLength / 2));
        portNumberField.insertText(be.getPortNumber() > 0 ? String.valueOf(be.getPortNumber()) : "");
        addRenderableWidget(portNumberField);

        busSizeField = new EditBox(font, centerX - maxWidth / 2, centerY, maxWidth / 4, fieldHeight, Component.empty());
        busSizeField.setMaxLength(maxLength);
        busSizeField.setFilter(s -> Utils.isValidNumber(s, maxLength / 4));
        busSizeField.insertText(String.valueOf(be.getBusSize()));
        addRenderableWidget(busSizeField);

        inputOutputToggle = Button.builder(
                Component.literal(be.getIsInput() ? Utils.translate("word", "input") : Utils.translate("word", "output")),
                        button -> toggleInputOutput())
                .bounds(centerX - maxWidth / 2, centerY + verticalTotalSpacingWidth, maxWidth, fieldHeight)
                .build();
        addRenderableWidget(inputOutputToggle);

        int buttonsWidth = (maxWidth - horizontalSpacingWidth) / 2;

        String[] buttonTexts = {
                Utils.translate("word", "done"),
                Utils.translate("word", "cancel")
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

    private void toggleInputOutput() {
        if (unsafe()) return;

        boolean newIsInput = !be.getIsInput();;

        be.setIsInput(newIsInput);

        inputOutputToggle.setMessage(Component.literal(newIsInput ? Utils.translate("word", "input") : Utils.translate("word", "output")));

        clientLevel.setBlockAndUpdate(be.getBlockPos(), be.getBlockState().setValue(PortBlock.IS_INPUT, newIsInput));
    }

    @Override
    protected void onDone() {
        if (unsafe()) return;

        String oldCircuitId = be.getCircuitId();
        String newCircuitId = circuitIdField.getValue();
        Integer oldPortNumber = be.getPortNumber();
        Integer newPortNumber = this.portNumberField.getValue().isEmpty() ? PortBlockEntity.DEFAULT_PORT_NUMBER
                : Integer.parseInt(this.portNumberField.getValue());

        be.setCircuitId(newCircuitId);
        be.setBusSize(this.busSizeField.getValue().isEmpty() ? PortBlockEntity.DEFAULT_BUS_SIZE
                : Integer.parseInt(this.busSizeField.getValue()));

        Circuit.migratePort(be, oldCircuitId, newCircuitId, oldPortNumber, newPortNumber);

        onClose();
    }

    @Override
    protected void onCancel() {
        if (unsafe()) return;

        if (be.getIsInput() != initialIsInput)
            toggleInputOutput();

        onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (unsafe()) return;

        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawString(font, TITLE, titleLabelX, titleLabelY, 0xFFFFFF);

        guiGraphics.drawString(font, Utils.ctranslate("common", "circuit_id"), circuitIdField.getX(), circuitIdField.getY() - 10, 0xA0A0A0);
        guiGraphics.drawString(font, ctranslate("port_number"), portNumberField.getX(), portNumberField.getY() - 10, 0xA0A0A0);
        guiGraphics.drawString(font, ctranslate("bus_size"), busSizeField.getX(), busSizeField.getY() - 10, 0xA0A0A0);

        circuitIdField.render(guiGraphics, mouseX, mouseY, delta);
        portNumberField.render(guiGraphics, mouseX, mouseY, delta);
        busSizeField.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (unsafe()) return false;

        if (circuitIdField.keyPressed(keyCode, scanCode, modifiers) && circuitIdField.canConsumeInput()) return true;
        if (portNumberField.keyPressed(keyCode, scanCode, modifiers) && portNumberField.canConsumeInput()) return true;
        if (busSizeField.keyPressed(keyCode, scanCode, modifiers) && busSizeField.canConsumeInput()) return true;

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
        } else if (portNumberField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(portNumberField);
            anyClicked = true;
        } else if (busSizeField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(busSizeField);
            anyClicked = true;
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