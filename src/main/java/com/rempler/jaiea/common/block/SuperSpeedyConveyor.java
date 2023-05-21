package com.rempler.jaiea.common.block;

import blusunrize.immersiveengineering.api.tool.conveyor.BasicConveyorType;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import com.rempler.jaiea.client.BasicSpeedyConveyorRender;
import com.rempler.jaiea.utils.JAIEAConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SuperSpeedyConveyor extends BasicSpeedyConveyors {
    public static final ResourceLocation NAME = new ResourceLocation(JAIEAConstants.MODID, "super_speedy");
    public static ResourceLocation texture_on = new ResourceLocation("jaiea:block/conveyor/super_speedy");
    public static ResourceLocation texture_off = new ResourceLocation("jaiea:block/conveyor/super_speedy_off");
    public static final IConveyorType<SuperSpeedyConveyor> TYPE = new BasicConveyorType<>(
            NAME, false, true, SuperSpeedyConveyor::new, () -> new BasicSpeedyConveyorRender<>(texture_on, texture_off)
    );
    public SuperSpeedyConveyor(BlockEntity tile) {
        super(tile);
    }

    @Override
    public double getMove() {
        return 2;
    }

    @Override
    public IConveyorType<?> getType() {
        return TYPE;
    }
}
