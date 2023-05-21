package com.rempler.jaiea.data;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import blusunrize.immersiveengineering.common.blocks.metal.ConveyorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.BasicConveyor;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.rempler.jaiea.common.block.SpeedyConveyor;
import com.rempler.jaiea.common.block.SuperSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSpeedyConveyor;
import com.rempler.jaiea.common.block.VerticalSuperSpeedyConveyor;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class JAIEARecipes extends RecipeProvider {
    public JAIEARecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ItemLike speedy = ConveyorHandler.getBlock(SpeedyConveyor.TYPE);
        ItemLike super_speedy = ConveyorHandler.getBlock(SuperSpeedyConveyor.TYPE);
        ItemLike vertical_speedy = ConveyorHandler.getBlock(VerticalSpeedyConveyor.TYPE);
        ItemLike vertical_super_speedy = ConveyorHandler.getBlock(VerticalSuperSpeedyConveyor.TYPE);
        ItemLike basic = ConveyorHandler.getBlock(BasicConveyor.TYPE);
        addConveyorCoveringRecipe(speedy, consumer);
        addConveyorCoveringRecipe(super_speedy, consumer);
        CompoundTag longswift = new CompoundTag();
        longswift.putString("Potion", getPotionString(Potions.LONG_SWIFTNESS));
        CompoundTag strongswift = new CompoundTag();
        strongswift.putString("Potion", getPotionString(Potions.STRONG_SWIFTNESS));
        ShapedRecipeBuilder.shaped(speedy, 4)
                .pattern(" c ")
                .pattern("cpc")
                .pattern(" c ")
                .define('c', basic)
                .define('p', getPotion(longswift))
                .unlockedBy("has_speedy_conveyor", has(basic))
                .save(consumer, new ResourceLocation(toPath(speedy)));
        ShapedRecipeBuilder.shaped(super_speedy, 4)
                .pattern(" c ")
                .pattern("cpc")
                .pattern(" c ")
                .define('c', speedy)
                .define('p', getPotion(strongswift))
                .unlockedBy("has_speedy_conveyor", has(speedy))
                .save(consumer, new ResourceLocation(toPath(super_speedy)));
        ShapedRecipeBuilder.shaped(vertical_speedy, 3)
                .pattern("ci")
                .pattern("c ")
                .pattern("ci")
                .define('c', speedy)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_conveyor", has(speedy))
                .save(consumer, new ResourceLocation(toPath(vertical_speedy)));
        ShapedRecipeBuilder.shaped(vertical_super_speedy, 3)
                .pattern("ci")
                .pattern("c ")
                .pattern("ci")
                .define('c', super_speedy)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_conveyor", has(super_speedy))
                .save(consumer, new ResourceLocation(toPath(vertical_super_speedy)));
    }

    private void addConveyorCoveringRecipe(ItemLike basic, Consumer<FinishedRecipe> out) {
        new ShapedNBTBuilder(ConveyorBlock.makeCovered(basic, IEBlocks.MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD).get()))
                .pattern("s")
                .pattern("c")
                .define('s', IETags.getItemTag(IETags.scaffoldingSteel))
                .define('c', basic)
                .unlockedBy("has_vertical_conveyor", has(basic))
                .save(out, new ResourceLocation(toPath(basic)+"_covered"));
    }

    private String toPath(ItemLike src) {
        return Registry.ITEM.getKey(src.asItem()).getPath();
    }

    private Ingredient getPotion(CompoundTag tag) {
        return PartialNBTIngredient.of(Items.POTION, tag);
    }

    private String getPotionString(Potion potion) {
        return ForgeRegistries.POTIONS.getKey(potion).toString();
    }
}
