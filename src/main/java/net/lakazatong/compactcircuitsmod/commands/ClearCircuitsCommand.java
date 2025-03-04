package net.lakazatong.compactcircuitsmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ClearCircuitsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("clearcircuits").executes(ClearCircuitsCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        for (Circuit circuit : CompactCircuitsMod.CIRCUITS.values())
            circuit.clear(context.getSource().getLevel());
        return 0;
    }
}
