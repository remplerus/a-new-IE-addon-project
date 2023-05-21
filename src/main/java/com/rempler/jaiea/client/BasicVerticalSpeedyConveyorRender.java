package com.rempler.jaiea.client;

import blusunrize.immersiveengineering.api.tool.conveyor.BasicConveyorCacheData;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler.ConveyorDirection;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorWall;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorModelRender;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.models.ModelConveyor;
import blusunrize.immersiveengineering.client.utils.ModelUtils;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.ConveyorBase;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.VerticalConveyor;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.rempler.jaiea.common.block.BasicVerticalSpeedyConveyors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class BasicVerticalSpeedyConveyorRender extends BasicSpeedyConveyorRender<BasicVerticalSpeedyConveyors>
{
    public BasicVerticalSpeedyConveyorRender(ResourceLocation active, ResourceLocation inactive)
    {
        super(active, inactive);
    }

    @Override
    public boolean shouldRenderWall(Direction facing, ConveyorWall wall, RenderContext<BasicVerticalSpeedyConveyors> context)
    {
        return true;
    }

    @Override
    public Object getModelCacheKey(RenderContext<BasicVerticalSpeedyConveyors> context)
    {
        BasicConveyorCacheData basic = IConveyorModelRender.getDefaultData(this, context);
        BasicVerticalSpeedyConveyors instance = context.instance();
        if(instance==null)
            return basic;
        BlockEntity blockEntity = instance.getBlockEntity();
        Direction facing = context.getFacing();
        if(!VerticalConveyor.renderBottomBelt(blockEntity, facing))
            return basic;
        record Key(BasicConveyorCacheData base, boolean inward, boolean bottomWall0, boolean bottomWall1)
        {
        }
        return new Key(
                basic,
                VerticalConveyor.isInwardConveyor(blockEntity, facing.getOpposite()),
                renderBottomWall(facing, ConveyorWall.LEFT, context),
                renderBottomWall(facing, ConveyorWall.RIGHT, context)
        );
    }

    @Override
    public Transformation modifyBaseRotationMatrix(Transformation matrix)
    {
        return matrix.compose(new Transformation(
                new Vector3f(0, 1, 0), new Quaternion((float)Math.PI/2, 0, 0, false), null, null
        ));
    }

    public boolean renderBottomWall(Direction facing, ConveyorWall wall, RenderContext<BasicVerticalSpeedyConveyors> context)
    {
        return super.shouldRenderWall(facing, wall, context);
    }

    @Override
    public List<BakedQuad> modifyQuads(List<BakedQuad> baseModel, RenderContext<BasicVerticalSpeedyConveyors> context, @Nullable RenderType renderType)
    {
        BasicVerticalSpeedyConveyors instance = context.instance();
        boolean[] walls = {true, true};
        if(instance!=null)
        {
            BlockEntity blockEntity = instance.getBlockEntity();
            Direction facing = context.getFacing();

            if(VerticalConveyor.renderBottomBelt(blockEntity, facing))
            {
                TextureAtlasSprite sprite = ClientUtils.getSprite(
                        instance.isActive()?ConveyorBase.texture_on: ConveyorBase.texture_off
                );
                DyeColor dyeColour = instance.getDyeColour();
                TextureAtlasSprite spriteColour = dyeColour!=null?ClientUtils.getSprite(getColouredStripesTexture()): null;
                walls = new boolean[]{
                        renderBottomWall(facing, ConveyorWall.LEFT, context),
                        renderBottomWall(facing, ConveyorWall.RIGHT, context)
                };
                if(renderType==null||renderType==RenderType.cutout())
                    baseModel.addAll(ModelConveyor.getBaseConveyor(
                            facing, .875f, ClientUtils.rotateTo(facing), ConveyorDirection.HORIZONTAL, sprite, walls,
                            new boolean[]{true, false}, spriteColour, dyeColour
                    ));
            }
        }
        if(renderType==null||renderType==RenderType.translucent())
            addCoverQuads(baseModel, context, walls);
        return baseModel;
    }

    private void addCoverQuads(List<BakedQuad> baseModel, RenderContext<BasicVerticalSpeedyConveyors> context, boolean[] walls)
    {
        Block b = context.getCover();
        if(b==Blocks.AIR)
            return;
        Direction facing = context.getFacing();
        BasicVerticalSpeedyConveyors conveyor = context.instance();
        BlockEntity blockEntity = conveyor!=null?conveyor.getBlockEntity(): null;
        boolean renderBottom = conveyor!=null&&BasicVerticalSpeedyConveyors.renderBottomBelt(blockEntity, facing);
        Function<Direction, TextureAtlasSprite> getSprite = makeTextureGetter(b);
        float[] colour = {1, 1, 1, 1};
        Matrix4 matrix = new Matrix4(facing);

        if(!renderBottom)//just vertical
        {
            baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, 0, .75f), new Vec3(1, 1, 1), matrix, facing, getSprite, colour));
            baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, 0, .1875f), new Vec3(.0625f, 1, .75f), matrix, facing, getSprite, colour));
            baseModel.addAll(ModelUtils.createBakedBox(new Vec3(.9375f, 0, .1875f), new Vec3(1, 1, .75f), matrix, facing, getSprite, colour));
        }
        else
        {
            boolean straightInput = VerticalConveyor.isInwardConveyor(blockEntity, facing.getOpposite());
            baseModel.addAll(ModelUtils.createBakedBox(new Vec3(.0625, .9375f, .75f), new Vec3(.9375, 1, .9375), matrix, facing, getSprite, colour));
            if(!straightInput)
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, .1875f, .9375f), new Vec3(1, 1f, 1), matrix, facing, getSprite, colour));
            else//has direct input, needs a cutout
            {
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, .75f, .9375f), new Vec3(1, 1, 1), matrix, facing, getSprite, colour));
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, .1875f, .9375f), new Vec3(.0625f, .75f, 1), matrix, facing, getSprite, colour));
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(.9375f, .1875f, .9375f), new Vec3(1, .75f, 1), matrix, facing, getSprite, colour));
            }

            if(walls[0])//wall to the left
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, .1875f, .1875f), new Vec3(.0625f, 1, .9375f), matrix, facing, getSprite, colour));
            else//cutout to the left
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(0, .75f, .1875f), new Vec3(.0625f, 1, .9375f), matrix, facing, getSprite, colour));

            if(walls[1])//wall to the right
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(.9375f, .1875f, .1875f), new Vec3(1, 1, .9375f), matrix, facing, getSprite, colour));
            else//cutout to the right
                baseModel.addAll(ModelUtils.createBakedBox(new Vec3(.9375f, .75f, .1875f), new Vec3(1, 1, .9375f), matrix, facing, getSprite, colour));
        }
    }
}
