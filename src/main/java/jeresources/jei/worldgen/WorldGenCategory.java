package jeresources.jei.worldgen;

import jeresources.api.render.ColourHelper;
import jeresources.config.Settings;
import jeresources.entry.WorldGenEntry;
import jeresources.jei.BlankJEIRecipeCategory;
import jeresources.jei.JEIConfig;
import jeresources.json.WorldGenAdapter;
import jeresources.reference.Resources;
import jeresources.registry.WorldGenRegistry;
import jeresources.util.LogHelper;
import jeresources.util.RenderHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldGenCategory extends BlankJEIRecipeCategory<WorldGenWrapper> {
    protected static final int X_ITEM = 5;
    protected static final int Y_ITEM = 21;
    protected static final int X_DROP_ITEM = 5;
    protected static final int Y_DROP_ITEM = 66;
    private static final int DROP_ITEM_COUNT = 8;

    private IDrawable icon;

    public WorldGenCategory() {
        icon = JEIConfig.getJeiHelpers().getGuiHelper().createDrawable(Resources.Gui.Jei.TABS, 32, 16, 16, 16);
    }

    @Nonnull
    @Override
    public String getUid() {
        return JEIConfig.WORLD_GEN;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return TranslationHelper.translateToLocal("jer.worldgen.title");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return Resources.Gui.Jei.WORLD_GEN;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        RenderHelper.drawLine(WorldGenWrapper.X_OFFSET, WorldGenWrapper.Y_OFFSET, WorldGenWrapper.X_OFFSET + WorldGenWrapper.X_AXIS_SIZE, WorldGenWrapper.Y_OFFSET, ColourHelper.GRAY);
        RenderHelper.drawLine(WorldGenWrapper.X_OFFSET, WorldGenWrapper.Y_OFFSET, WorldGenWrapper.X_OFFSET, WorldGenWrapper.Y_OFFSET - WorldGenWrapper.Y_AXIS_SIZE, ColourHelper.GRAY);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull WorldGenWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, X_ITEM, Y_ITEM);

        for (int i = 0; i < DROP_ITEM_COUNT; i++)
            recipeLayout.getItemStacks().init(i + 1, false, X_DROP_ITEM + i * 18, Y_DROP_ITEM);

        recipeLayout.getItemStacks().addTooltipCallback(recipeWrapper);
        recipeLayout.getItemStacks().set(0, recipeWrapper.getBlock());
        for (int i = 0; i < Math.min(DROP_ITEM_COUNT, recipeWrapper.getDrops().size()); i++)
            recipeLayout.getItemStacks().set(i + 1, recipeWrapper.getDrops().get(i));
    }

    public static void reload() {
        WorldGenRegistry.getInstance().clear();
        JEIConfig.purgeCategories(JEIConfig.WORLD_GEN);
        try {
            if (Settings.useDIYdata) {
                if (WorldGenAdapter.hasWorldGenDIYData()) {
                    WorldGenAdapter.readDIYData();
                }
            }
        } catch (Exception e) {
            LogHelper.warn("Error during reloading of DIY data", e);
        }
        IJeiRuntime jeiRuntime = JEIConfig.getJeiRuntime();
        if (jeiRuntime != null) {
            WorldGenWrapperFactory factory = new WorldGenWrapperFactory();
            for (WorldGenEntry entry : WorldGenRegistry.getInstance().getWorldGen()) {
                jeiRuntime.getRecipeRegistry().addRecipe(factory.getRecipeWrapper(entry), JEIConfig.WORLD_GEN);
            }
        }
    }
}