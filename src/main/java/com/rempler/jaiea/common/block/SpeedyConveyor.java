package com.rempler.jaiea.common.block;

import blusunrize.immersiveengineering.api.tool.conveyor.BasicConveyorType;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import blusunrize.immersiveengineering.client.render.conveyor.BasicConveyorRender;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.ConveyorBase;
import com.rempler.jaiea.utils.JAIEAConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpeedyConveyor extends ConveyorBase {
    public static final ResourceLocation NAME = new ResourceLocation(JAIEAConstants.MODID, "speedy");
    public static ResourceLocation texture_on = new ResourceLocation("jaiea:block/conveyor/speedy");
    public static ResourceLocation texture_off = new ResourceLocation("jaiea:block/conveyor/speedy_off");
    public static final IConveyorType<SpeedyConveyor> TYPE = new BasicConveyorType<>(
            NAME, false, true, SpeedyConveyor::new, () -> new BasicConveyorRender<>(texture_on, texture_off)
    );
    public SpeedyConveyor(BlockEntity tile) {
        super(tile);
    }

    @Override
    public IConveyorType<?> getType() {
        return TYPE;
    }
}
