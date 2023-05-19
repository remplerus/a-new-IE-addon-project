package com.rempler.jaiea.common.block;

import blusunrize.immersiveengineering.api.tool.conveyor.BasicConveyorType;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import blusunrize.immersiveengineering.client.render.conveyor.BasicConveyorRender;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.ConveyorBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import static blusunrize.immersiveengineering.ImmersiveEngineering.MODID;

public class SuperSpeedyConveyor extends ConveyorBase {
    public static final ResourceLocation NAME = new ResourceLocation(MODID, "dropper");
    public static ResourceLocation texture_on = new ResourceLocation("jaiea:block/conveyor/super_speedy");
    public static ResourceLocation texture_off = new ResourceLocation("jaiea:block/conveyor/super_speedy_off");
    public static final IConveyorType<SuperSpeedyConveyor> TYPE = new BasicConveyorType<>(
            NAME, false, true, SuperSpeedyConveyor::new, () -> new BasicConveyorRender<>(texture_on, texture_off)
    );
    public SuperSpeedyConveyor(BlockEntity tile) {
        super(tile);
    }

    @Override
    public IConveyorType<?> getType() {
        return TYPE;
    }
}
