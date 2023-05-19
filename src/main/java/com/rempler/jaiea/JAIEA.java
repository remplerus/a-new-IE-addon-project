package com.rempler.jaiea;

import com.mojang.logging.LogUtils;
import com.rempler.jaiea.client.ClientEvent;
import com.rempler.jaiea.common.init.JAIEAInit;
import com.rempler.jaiea.utils.JAIEAConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static blusunrize.immersiveengineering.ImmersiveEngineering.bootstrapErrorToXCPInDev;

@Mod(JAIEAConstants.MODID)
public class JAIEA {
    private static final Logger LOGGER = LogUtils.getLogger();

    public JAIEA() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        JAIEAInit.init(modEventBus);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, bootstrapErrorToXCPInDev(() -> ClientEvent::createMod));
    }
}
