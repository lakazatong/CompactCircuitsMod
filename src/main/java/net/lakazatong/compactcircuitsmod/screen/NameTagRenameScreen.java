package net.lakazatong.compactcircuitsmod.screen;

import net.lakazatong.compactcircuitsmod.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static net.lakazatong.compactcircuitsmod.utils.Utils.translate;

public class NameTagRenameScreen extends Screen {
    private static Component ctranslate(String key) { return Utils.ctranslate("gui", "name_tag_rename_screen." + key); }

    private static final Component TITLE = ctranslate("title");

    int titleLabelX = 0;
    int titleLabelY = 0;

    private final ItemStack itemStack;
    private EditBox nameField;

    public NameTagRenameScreen(ItemStack itemStack) {
        super(TITLE);
        this.itemStack = itemStack;
    }

    @Override
    protected void init() {
        int maxLength = 32;
        int maxWidth = Utils.getFieldWidth(font, maxLength);
        int fieldHeight = 20;
        int verticalSpacingWidth = 16;
        int verticalTotalSpacingWidth = fieldHeight + verticalSpacingWidth;
        int horizontalSpacing = 2;
        int horizontalSpacingWidth = Utils.getFieldWidth(font, horizontalSpacing);

        int centerX = width / 2;
        int centerY = height / 2;

        titleLabelX = centerX - font.width(TITLE) / 2;
        titleLabelY = centerY - 3 * verticalTotalSpacingWidth;

        nameField = new EditBox(this.font, centerX - maxWidth / 2, centerY - verticalTotalSpacingWidth, maxWidth, fieldHeight, Component.empty());
        nameField.setMaxLength(maxLength);
        nameField.setFilter(s -> s.matches("\\w*"));
        nameField.setValue(itemStack.getHoverName().getString());
        addWidget(this.nameField);

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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawString(font, TITLE, titleLabelX, titleLabelY, 0xFFFFFF);

        guiGraphics.drawString(font, ctranslate("name_field"), nameField.getX(), nameField.getY() - 10, 0xA0A0A0);

        nameField.render(guiGraphics, mouseX, mouseY, delta);
    }

    private void onDone() {
        itemStack.set(DataComponents.CUSTOM_NAME, Component.literal(nameField.getValue()));
        onClose();
    }

    private void onCancel() {
        onClose();
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return Utils.keyPressed(keyCode, this::onDone, this::onCancel) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        setFocused(null);
        if (nameField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(nameField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameField);
    }
}
