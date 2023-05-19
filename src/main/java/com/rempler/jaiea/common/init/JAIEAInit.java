package com.rempler.jaiea.common.init;

import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import com.rempler.jaiea.common.block.SpeedyConveyor;
import com.rempler.jaiea.common.block.SuperSpeedyConveyor;
import net.minecraftforge.eventbus.api.IEventBus;

public class JAIEAInit {
    public static void init(IEventBus eventBus) {
        ConveyorHandler.registerConveyorType(SpeedyConveyor.TYPE);
        ConveyorHandler.registerConveyorType(SuperSpeedyConveyor.TYPE);
    }
}
