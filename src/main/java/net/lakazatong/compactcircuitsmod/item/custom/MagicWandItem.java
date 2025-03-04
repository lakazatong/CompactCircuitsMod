package net.lakazatong.compactcircuitsmod.item.custom;

import net.lakazatong.compactcircuitsmod.datacomponents.ModDataComponents;
import net.lakazatong.compactcircuitsmod.datacomponents.custom.LastPosDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicWandItem extends Item {
    public MagicWandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand.compareTo(InteractionHand.MAIN_HAND) == 0 && !pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = pLevel.getServer();
            assert server != null;
            ItemStack stack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            LastPosDataComponent component = stack.get(ModDataComponents.LAST_POS.get());
            assert component != null;
            ServerLevel lastLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, component.rl()));
            assert lastLevel != null;
            Vec3 lastPos = component.pos();
            stack.set(ModDataComponents.LAST_POS.get(), new LastPosDataComponent(pLevel.dimension().location(), pPlayer.position()));
            serverPlayer.teleportTo(lastLevel, lastPos.x, lastPos.y, lastPos.z, 0, 0);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
