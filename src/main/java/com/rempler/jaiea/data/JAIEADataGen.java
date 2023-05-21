package com.rempler.jaiea.data;

import com.rempler.jaiea.utils.JAIEAConstants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JAIEAConstants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class JAIEADataGen {
    @SubscribeEvent
    public static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new JAIEARecipes(gen));
    }
}
