package com.rempler.jaiea.client;

import blusunrize.immersiveengineering.client.DynamicModelLoader;
import com.rempler.jaiea.common.block.SpeedyConveyor;
import com.rempler.jaiea.common.block.SuperSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSuperSpeedyConveyor;
import net.minecraft.client.Minecraft;

public class ClientEvent {
    public static void createMod() {
        if (Minecraft.getInstance() != null) {
            requestModelsAndTextures();
        }
    }



    private static void requestModelsAndTextures() {
        DynamicModelLoader.requestTexture(SpeedyConveyor.texture_off);
        DynamicModelLoader.requestTexture(SpeedyConveyor.texture_on);
        DynamicModelLoader.requestTexture(SuperSpeedyConveyor.texture_off);
        DynamicModelLoader.requestTexture(SuperSpeedyConveyor.texture_on);
        DynamicModelLoader.requestTexture(VerticalSpeedyConveyor.texture_off);
        DynamicModelLoader.requestTexture(VerticalSpeedyConveyor.texture_on);
        DynamicModelLoader.requestTexture(VerticalSuperSpeedyConveyor.texture_off);
        DynamicModelLoader.requestTexture(VerticalSuperSpeedyConveyor.texture_on);
    }
}
