package net.lakazatong.compactcircuitsmod.screen;

import net.lakazatong.compactcircuitsmod.utils.Utils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SafeScreen<E extends BlockEntity> extends Screen {
    protected boolean isInit = false;

    protected ClientLevel clientLevel;

    protected BlockPos pos;
    protected BlockState state;
    protected E be;

    int titleLabelX = 0;
    int titleLabelY = 0;

    // considering length as the number of characters
    // the + 2 is to compensate for the different paddings and margins
    protected int getFieldWidth(int length) { return Utils.getFieldWidth(font, length); }

    // inverse operation
    protected int getFieldLength(int width) {
        return Utils.getFieldLength(font, width);
    }

    protected SafeScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        this.pos = pos;
    }

    protected boolean unsafe() {
        if (isInit) return false;
        onClose();
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void init() {
        super.init();
        if (minecraft == null) return;
        if (minecraft.level == null) {
            onClose();
            return;
        }
        clientLevel = minecraft.level;
        state = clientLevel.getBlockState(pos);
        be = (E) clientLevel.getBlockEntity(pos);
        isInit = true;
    }

    protected abstract void onDone();
    protected abstract void onCancel();

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return Utils.keyPressed(pKeyCode, this::onDone, this::onCancel) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(null);
    }
}
