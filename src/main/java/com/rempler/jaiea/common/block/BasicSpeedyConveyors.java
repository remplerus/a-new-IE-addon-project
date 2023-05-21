/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 * optimized to add speed to conveyors
 */

package com.rempler.jaiea.common.block;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorBelt;
import blusunrize.immersiveengineering.api.utils.EntityCollisionTracker;
import blusunrize.immersiveengineering.api.utils.PlayerUtils;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.api.utils.shapes.CachedVoxelShapes;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.metal.ConveyorBeltBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class BasicSpeedyConveyors implements IConveyorBelt {
    double itemMove = 1;
    private Block cover = Blocks.AIR;

    ConveyorHandler.ConveyorDirection direction = ConveyorHandler.ConveyorDirection.HORIZONTAL;
    @javax.annotation.Nullable
    DyeColor dyeColour = null;
    private final BlockEntity tile;
    protected final EntityCollisionTracker collisionTracker = new EntityCollisionTracker(10);

    public BasicSpeedyConveyors(BlockEntity tile)
    {
        this.tile = tile;
    }

    @Override
    public BlockEntity getBlockEntity()
    {
        return tile;
    }

    @Override
    public ConveyorHandler.ConveyorDirection getConveyorDirection()
    {
        return direction;
    }

    @Override
    public boolean changeConveyorDirection()
    {
        if(!tile.getLevel().isClientSide)
            direction = direction== ConveyorHandler.ConveyorDirection.HORIZONTAL? ConveyorHandler.ConveyorDirection.UP: direction== ConveyorHandler.ConveyorDirection.UP? ConveyorHandler.ConveyorDirection.DOWN: ConveyorHandler.ConveyorDirection.HORIZONTAL;
        return true;
    }

    @Override
    public boolean setConveyorDirection(ConveyorHandler.ConveyorDirection dir)
    {
        direction = dir;
        return true;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public void onEntityCollision(@Nonnull Entity entity)
    {
        collisionTracker.onEntityCollided(entity);
        if(!isActive()||!entity.isAlive())
            return;
        if(entity instanceof Player&&entity.isShiftKeyDown())
            return;
        PlayerUtils.resetFloatingState(entity);
        ConveyorHandler.ConveyorDirection conveyorDirection = getConveyorDirection();
        float heightLimit = conveyorDirection== ConveyorHandler.ConveyorDirection.HORIZONTAL?.25f: 1f;
        BlockPos pos = getBlockEntity().getBlockPos();
        final double relativeHeight = entity.getY()-pos.getY();
        if(relativeHeight >= 0&&relativeHeight < heightLimit)
        {
            boolean hasBeenHandled = !ConveyorHandler.markEntityAsHandled(entity);
            final boolean outputBlocked = isOutputBlocked();
            Vec3 vec = this.getDirection(entity, outputBlocked);
            Vec3 newVec;
            if (vec.z == 0 && (vec.x < 0 || vec.x > 0)) {
                newVec = new Vec3(vec.x * getMove(), 0, 0);
            } else if (vec.x == 0 && (vec.z > 0 || vec.z < 0)) {
                newVec = new Vec3(0, 0, vec.z*getMove());
            } else {
                newVec = new Vec3(vec.x*getMove(), 0, vec.z*getMove());
            }
            if(entity.fallDistance < 3)
                entity.fallDistance = 0;
            if(outputBlocked)
            {
                double replacementX;
                double replacementZ;
                if(hasBeenHandled)
                {
                    replacementX = entity.getDeltaMovement().x;
                    replacementZ = entity.getDeltaMovement().z;
                }
                else
                {
                    replacementX = 0;
                    replacementZ = 0;
                }
                newVec = new Vec3(
                        replacementX,
                        vec.y,
                        replacementZ
                );
            }
            entity.setDeltaMovement(newVec);
            double distX = Math.abs(pos.relative(getFacing()).getX()+.5-entity.getX());
            double distZ = Math.abs(pos.relative(getFacing()).getZ()+.5-entity.getZ());
            double threshold = .9;
            boolean contact = getFacing().getAxis()== Direction.Axis.Z?distZ < threshold: distX < threshold;
            Level w = Preconditions.checkNotNull(getBlockEntity().getLevel());
            BlockPos upPos = pos.relative(getFacing()).above();
            if(contact&&conveyorDirection== ConveyorHandler.ConveyorDirection.UP&&
                    !Block.isFaceFull(w.getBlockState(upPos).getShape(w, upPos), Direction.DOWN))
            {
                double move = 0.4;
                entity.setPos(entity.getX()+move*getFacing().getStepX(), entity.getY()+1*move, entity.getZ()+move*getFacing().getStepZ());
            }
            if(!contact)
                ConveyorHandler.applyMagnetSuppression(entity, (ConveyorHandler.IConveyorBlockEntity<?>)getBlockEntity());
            else
            {
                BlockPos nextPos = getBlockEntity().getBlockPos().relative(getFacing());
                if(!(SafeChunkUtils.getSafeBE(getBlockEntity().getLevel(), nextPos) instanceof ConveyorHandler.IConveyorBlockEntity))
                    ConveyorHandler.revertMagnetSuppression(entity, (ConveyorHandler.IConveyorBlockEntity<?>)getBlockEntity());
            }

            // In the first tick this could be an entity the conveyor belt just dropped, causing #3023
            if(entity instanceof ItemEntity item&&entity.tickCount > 1)
            {
                if(!contact)
                {
                    ConveyorHandler.ItemAgeAccessor access = ConveyorHandler.ITEM_AGE_ACCESS.getValue();
                    if(item.getAge() > item.lifespan-60*20&&!outputBlocked)
                        access.setAge(item, item.lifespan-60*20);
                }
                else if(!w.isClientSide)
                    handleInsertion(item, conveyorDirection, distX, distZ);
            }
        }
        if(isCovered()&&entity instanceof ItemEntity)
            ((ItemEntity)entity).setPickUpDelay(10);
    }

    @Override
    public boolean isBlocked()
    {
        return collisionTracker.getCollidedInRange(getBlockEntity().getLevel().getGameTime()) > 2;
    }

    @Override
    public void onItemDeployed(ItemEntity entity)
    {
        IConveyorBelt.super.onItemDeployed(entity);
        if(isCovered())
            entity.setPickUpDelay(10);
    }

    @Override
    public boolean playerInteraction(Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ, Direction side)
    {
        return handleCoverInteraction(player, hand, heldItem);
    }

    /* ============ NBT ============ */

    @Override
    public CompoundTag writeConveyorNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("direction", direction.ordinal());
        if(dyeColour!=null)
            nbt.putInt("dyeColour", dyeColour.getId());
        if(cover!=Blocks.AIR)
            nbt.putString("cover", ForgeRegistries.BLOCKS.getKey(cover).toString());
        return nbt;
    }

    @Override
    public void readConveyorNBT(CompoundTag nbt)
    {
        direction = ConveyorHandler.ConveyorDirection.values()[nbt.getInt("direction")];
        if(nbt.contains("dyeColour", Tag.TAG_INT))
            dyeColour = DyeColor.byId(nbt.getInt("dyeColour"));
        else
            dyeColour = null;
        if(nbt.contains("cover", Tag.TAG_STRING))
            cover = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("cover")));
    }

    /* ============ RENDERING ============ */

    @Override
    public boolean setDyeColour(DyeColor colour)
    {
        if(colour==this.dyeColour)
            return false;
        this.dyeColour = colour;
        return true;
    }

    @Override
    public DyeColor getDyeColour()
    {
        return this.dyeColour;
    }

    /* ============ AABB ============ */

    private static final AABB topBox = new AABB(0, .75, 0, 1, 1, 1);

    private static final CachedVoxelShapes<BasicSpeedyConveyors.ShapeKey> SHAPES = new CachedVoxelShapes<>(BasicSpeedyConveyors::getBoxes);

    @Override
    public VoxelShape getCollisionShape()
    {
        VoxelShape baseShape = IConveyorBelt.super.getCollisionShape();
        if(isCovered())
            return SHAPES.get(new BasicSpeedyConveyors.ShapeKey(this, true, baseShape));
        return baseShape;
    }

    @Override
    public VoxelShape getSelectionShape()
    {
        if(isCovered())
            return SHAPES.get(new BasicSpeedyConveyors.ShapeKey(this, false, null));
        return IConveyorBelt.super.getSelectionShape();
    }

    protected final boolean isCovered()
    {
        return IConveyorBelt.isCovered(this, Blocks.AIR);
    }

    private static List<AABB> getBoxes(BasicSpeedyConveyors.ShapeKey key)
    {
        List<AABB> ret = new ArrayList<>();
        if(key.superShape!=null)
            ret.addAll(key.superShape.toAabbs());
        if(key.direction== ConveyorHandler.ConveyorDirection.HORIZONTAL)
        {
            if(!key.collision)
                return ImmutableList.of(FULL_BLOCK.bounds());
            else
                ret.add(topBox);
        }
        else
        {
            boolean up = key.direction== ConveyorHandler.ConveyorDirection.UP;
            boolean collision = key.collision;
            Direction facing = key.facing;
            ret.add(new AABB(
                    (facing==Direction.WEST&&!up)||(facing==Direction.EAST&&up)?.5: 0,
                    collision?1.75: .5,
                    (facing==Direction.NORTH&&!up)||(facing==Direction.SOUTH&&up)?.5: 0,
                    (facing==Direction.WEST&&up)||(facing==Direction.EAST&&!up)?.5: 1,
                    2,
                    (facing==Direction.NORTH&&up)||(facing==Direction.SOUTH&&!up)?.5: 1
            ));
            ret.add(new AABB(
                    (facing==Direction.WEST&&up)||(facing==Direction.EAST&&!up)?.5: 0,
                    collision?1.25: 0,
                    (facing==Direction.NORTH&&up)||(facing==Direction.SOUTH&&!up)?.5: 0,
                    (facing==Direction.WEST&&!up)||(facing==Direction.EAST&&up)?.5: 1,
                    1.5,
                    (facing==Direction.NORTH&&!up)||(facing==Direction.SOUTH&&up)?.5: 1
            ));
        }
        return ret;
    }


    /* ============ COVER UTILITY METHODS ============ */

    public static ArrayList<Predicate<Block>> validCoveyorCovers = new ArrayList<>();

    static
    {
        validCoveyorCovers.add(b -> b.defaultBlockState().is(IETags.scaffoldingAlu));
        validCoveyorCovers.add(b -> b.defaultBlockState().is(IETags.scaffoldingSteel));
        validCoveyorCovers.add(input -> input== IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get());
        validCoveyorCovers.add(b -> b.defaultBlockState().is(Tags.Blocks.GLASS));
    }

    public void dropCover(Player player)
    {
        if(tile!=null&&!tile.getLevel().isClientSide&&cover!=Blocks.AIR&&tile.getLevel().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
        {
            ItemEntity entityitem = player.drop(new ItemStack(cover), false);
            if(entityitem!=null)
                entityitem.setNoPickUpDelay();
        }
    }

    protected boolean handleCoverInteraction(Player player, InteractionHand hand, ItemStack heldItem)
    {
        if(heldItem.isEmpty()&&player.isShiftKeyDown()&&cover!=Blocks.AIR)
        {
            dropCover(player);
            this.cover = Blocks.AIR;
            return true;
        }
        else if(!heldItem.isEmpty()&&!player.isShiftKeyDown())
        {
            Block heldBlock = Block.byItem(heldItem.getItem());
            if(heldBlock!=Blocks.AIR)
                for(Predicate<Block> func : validCoveyorCovers)
                    if(func.test(heldBlock))
                    {
                        if(heldBlock!=cover)
                        {
                            dropCover(player);
                            this.cover = heldBlock;
                            heldItem.shrink(1);
                            if(heldItem.getCount() <= 0)
                                player.setItemInHand(hand, heldItem);
                            return true;
                        }
                    }
        }
        return false;
    }

    protected final boolean isPowered()
    {
        BlockEntity te = getBlockEntity();
        if(te instanceof ConveyorBeltBlockEntity<?> conveBE)
            return conveBE.isRSPowered();
        else
            return te.getLevel().getBestNeighborSignal(te.getBlockPos()) > 0;
    }

    @Override
    public Direction getFacing()
    {
        BlockEntity te = getBlockEntity();
        if(te instanceof IEBlockInterfaces.IDirectionalBE)
            return ((IEBlockInterfaces.IDirectionalBE)te).getFacing();
        return Direction.NORTH;
    }

    @Override
    public Block getCover()
    {
        return cover;
    }

    @Override
    public void setCover(Block cover)
    {
        this.cover = cover;
    }

    private static class ShapeKey
    {
        private final ConveyorHandler.ConveyorDirection direction;
        private final boolean collision;
        private final Direction facing;
        @javax.annotation.Nullable
        private final VoxelShape superShape;

        public ShapeKey(BasicSpeedyConveyors conveyor, boolean collision, @Nullable VoxelShape superShape)
        {
            this.direction = conveyor.getConveyorDirection();
            this.collision = collision;
            this.facing = conveyor.getFacing();
            this.superShape = superShape;
        }

        @Override
        public boolean equals(Object o)
        {
            if(this==o) return true;
            if(o==null||getClass()!=o.getClass()) return false;
            BasicSpeedyConveyors.ShapeKey shapeKey = (BasicSpeedyConveyors.ShapeKey)o;
            return collision==shapeKey.collision&&
                    direction==shapeKey.direction&&
                    facing==shapeKey.facing&&
                    Objects.equals(superShape, shapeKey.superShape);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(direction, collision, facing, superShape);
        }
    }

    public double getMove() {
        return itemMove;
    }
}
