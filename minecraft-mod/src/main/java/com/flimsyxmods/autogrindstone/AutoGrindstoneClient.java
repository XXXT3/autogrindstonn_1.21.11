package com.flimsyxmods.autogrindstone;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import com.flimsyxmods.autogrindstone.mixin.ScreenAccessor;

public final class AutoGrindstoneClient implements ClientModInitializer {
    private static final AutoGrindstoneController CONTROLLER = new AutoGrindstoneController();
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        AutoGrindstoneConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auto_grindstone.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.create(Identifier.of("auto_grindstone", "keybindings"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                CONTROLLER.toggle(client);
            }
            CONTROLLER.tick(client);
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof GrindstoneScreen)) {
                return;
            }

            ButtonWidget button = ButtonWidget.builder(CONTROLLER.buttonText(), ignored -> CONTROLLER.toggle(client))
                    .dimensions(screen.width / 2 + 92, screen.height / 2 - 58, 58, 20)
                    .build();
            ((ScreenAccessor) screen).autoGrindstone$addDrawableChild(button);
            CONTROLLER.setButton(button);
        });
    }

    private AutoGrindstoneClient() {
    }
}