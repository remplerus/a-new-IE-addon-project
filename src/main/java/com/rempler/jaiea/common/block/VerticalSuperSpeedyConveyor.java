package com.rempler.jaiea.common.block;

import blusunrize.immersiveengineering.api.tool.conveyor.BasicConveyorType;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import com.rempler.jaiea.client.BasicVerticalSpeedyConveyorRender;
import com.rempler.jaiea.utils.JAIEAConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class VerticalSuperSpeedyConveyor extends BasicVerticalSpeedyConveyors {
    public static final ResourceLocation NAME = new ResourceLocation(JAIEAConstants.MODID, "super_speedy_vertical");
    public static ResourceLocation texture_on = new ResourceLocation("jaiea:block/conveyor/super_speedy_vertical");
    public static ResourceLocation texture_off = new ResourceLocation("jaiea:block/conveyor/super_speedy_vertical_off");
    public static final IConveyorType<BasicVerticalSpeedyConveyors> TYPE = new BasicConveyorType<>(
            NAME, false, true, VerticalSuperSpeedyConveyor::new, () -> new BasicVerticalSpeedyConveyorRender(texture_on, texture_off)
    );
    public VerticalSuperSpeedyConveyor(BlockEntity tile) {
        super(tile);
    }

    @Override
    public double getMove() {
        return 2;
    }

    @Override
    public IConveyorType<BasicVerticalSpeedyConveyors> getType() {
        return TYPE;
    }
}
