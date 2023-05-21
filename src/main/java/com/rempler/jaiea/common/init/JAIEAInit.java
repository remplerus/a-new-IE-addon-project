package com.rempler.jaiea.common.init;

import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import com.rempler.jaiea.common.block.SpeedyConveyor;
import com.rempler.jaiea.common.block.SuperSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSuperSpeedyConveyor;
import net.minecraftforge.eventbus.api.IEventBus;

public class JAIEAInit {
    public static void init(IEventBus eventBus) {
        ConveyorHandler.registerConveyorType(SpeedyConveyor.TYPE);
        ConveyorHandler.registerConveyorType(SuperSpeedyConveyor.TYPE);
        ConveyorHandler.registerConveyorType(VerticalSpeedyConveyor.TYPE);
        ConveyorHandler.registerConveyorType(VerticalSuperSpeedyConveyor.TYPE);
    }
}
