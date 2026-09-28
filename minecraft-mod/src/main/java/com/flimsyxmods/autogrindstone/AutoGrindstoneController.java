package com.flimsyxmods.autogrindstone;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.enchantment.EnchantmentHelper;

public final class AutoGrindstoneController {
    private static final int FIRST_INPUT_SLOT = 0;
    private static final int SECOND_INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int FIRST_PLAYER_SLOT = 3;

    private AutoGrindstoneConfig config = new AutoGrindstoneConfig();
    private ButtonWidget button;
    private boolean running;
    private boolean waitingForOutput;
    private int cooldown;

    public Text buttonText() {
        return Text.literal(running ? "STOP" : "AUTO");
    }

    public void setButton(ButtonWidget button) {
        this.button = button;
        refreshButton();
    }

    public void toggle(MinecraftClient client) {
        if (!(client.currentScreen instanceof GrindstoneScreen)) {
            sendStatus(client, "text.auto_grindstone.open_grindstone");
            return;
        }

        if (running) {
            stop(client, false);
        } else {
            config = AutoGrindstoneConfig.load();
            running = true;
            waitingForOutput = false;
            cooldown = 0;
            sendStatus(client, "text.auto_grindstone.started");
            refreshButton();
        }
    }

    public void tick(MinecraftClient client) {
        if (!running) {
            return;
        }

        if (!(client.currentScreen instanceof GrindstoneScreen)
                || client.player == null
                || client.interactionManager == null) {
            stop(client, false);
            return;
        }

        ScreenHandler handler = client.player.currentScreenHandler;
        if (handler == null || handler.slots.size() < FIRST_PLAYER_SLOT) {
            return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        ItemStack output = handler.getSlot(OUTPUT_SLOT).getStack();
        if (!output.isEmpty()) {
            click(client, OUTPUT_SLOT, SlotActionType.QUICK_MOVE);
            waitingForOutput = false;
            cooldown = actionDelay();
            return;
        }

        if (waitingForOutput) {
            if (config.syncToServerTps) {
                // Do not issue a second action until the server has acknowledged the first.
                return;
            }
            waitingForOutput = false;
        }

        if (!handler.getSlot(FIRST_INPUT_SLOT).getStack().isEmpty()
                || !handler.getSlot(SECOND_INPUT_SLOT).getStack().isEmpty()) {
            waitingForOutput = true;
            cooldown = config.syncToServerTps ? 0 : actionDelay();
            return;
        }

        int inventorySlot = findNextEnchantedSlot(handler);
        if (inventorySlot < 0) {
            stop(client, true);
            return;
        }

        click(client, inventorySlot, SlotActionType.QUICK_MOVE);
        waitingForOutput = true;
        cooldown = actionDelay();
    }

    private int findNextEnchantedSlot(ScreenHandler handler) {
        for (int slot = FIRST_PLAYER_SLOT; slot < handler.slots.size(); slot++) {
            ItemStack stack = handler.getSlot(slot).getStack();
            if (!stack.isEmpty()
                    && EnchantmentHelper.hasEnchantments(stack)
                    && (config.includeEnchantedBooks || !stack.isOf(net.minecraft.item.Items.ENCHANTED_BOOK))) {
                return slot;
            }
        }
        return -1;
    }

    private void click(MinecraftClient client, int slot, SlotActionType action) {
        ClientPlayerEntity player = client.player;
        if (player != null && client.interactionManager != null) {
            client.interactionManager.clickSlot(
                    player.currentScreenHandler.syncId,
                    slot,
                    0,
                    action,
                    player
            );
        }
    }

    private int actionDelay() {
        return Math.max(1, Math.round(20.0f / config.actionsPerSecond));
    }

    private void stop(MinecraftClient client, boolean finished) {
        running = false;
        waitingForOutput = false;
        cooldown = 0;
        refreshButton();
        if (finished) {
            sendStatus(client, "text.auto_grindstone.finished");
        } else {
            sendStatus(client, "text.auto_grindstone.stopped");
        }
    }

    private void refreshButton() {
        if (button != null) {
            button.setMessage(buttonText());
        }
    }

    private void sendStatus(MinecraftClient client, String translationKey) {
        if (client.player != null) {
            client.player.sendMessage(Text.translatable(translationKey), true);
        }
    }
}