package me.fabianbk.slimesieve;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;

public class SlimeSieve extends JavaPlugin implements SlimefunAddon {

    @Override
    public void onEnable() {
        // Read something from your config.yml
        Config cfg = new Config(this);

        if (cfg.getBoolean("options.auto-update")) {
            // You could start an Auto-Updater for example
        }

        /*
         * 1. Creating a new Category
         * This Category will use the following ItemStack
         */
        ItemStack sieveGroupItem = new CustomItemStack(Material.SCAFFOLDING, "Slime Sieve", "", "&a> Click to open");

        // Give your Category a unique id.
        NamespacedKey sieveGroupId = new NamespacedKey(this, "sieve_category");
        ItemGroup sieveGroup = new ItemGroup(sieveGroupId, sieveGroupItem);

        /*
         * 2. Create a new SlimefunItemStack
         * This class has many constructors, it is very important
         * that you give each item a unique id.
         */
        SlimefunItemStack sieveItem = new SlimefunItemStack("SIEVE", Material.SCAFFOLDING, "Sieve", "&7Used for sifting dirt, sand, and gravel.");

        /*
         * 3. Creating a Recipe
         * The Recipe is an ItemStack Array with a length of 9.
         * It represents a Shaped Recipe in a 3x3 crafting grid.
         * The machine in which this recipe is crafted in is specified
         * further down as the RecipeType.
         */
        ItemStack[] sieveRecipe = {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.STRING), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.STRING), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.STICK),      null                          , new ItemStack(Material.STICK)
        };

        /*
         * 4. Registering the Item
         * Now you just have to register the item.
         * RecipeType.ENHANCED_CRAFTING_TABLE refers to the machine in
         * which this item is crafted in.
         * Recipe Types from Slimefun itself will automatically add the recipe to that machine.
         */
        Sieve sieve = new Sieve(sieveGroup, sieveItem, RecipeType.ENHANCED_CRAFTING_TABLE, sieveRecipe);
        sieve.register(this);

        registerSieveItems(this, sieveGroup);
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public String getBugTrackerURL() {
        // You can return a link to your Bug Tracker instead of null here
        return null;
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        /*
         * You will need to return a reference to your Plugin here.
         * If you are using your main class for this, simply return "this".
         */
        return this;
    }

    /**
     * Registers all sieve-related items including meshes and ore processing items.
     * @param plugin The SlimefunAddon instance
     * @param itemGroup The ItemGroup to register items under
     */
    public static void registerSieveItems(SlimefunAddon plugin, ItemGroup itemGroup) {
        // ===== MESH ITEMS =====

        // String Mesh - Basic tier
        SlimefunItemStack stringMesh = new SlimefunItemStack(
            "STRING_MESH",
            Material.WHITE_CARPET,
            "&fString Mesh",
            "&7Basic mesh for sieving materials",
            "&7Chance: +0% (Base)"
        );
        ItemStack[] stringMeshRecipe = {
            new ItemStack(Material.STRING), new ItemStack(Material.STRING), new ItemStack(Material.STRING),
            new ItemStack(Material.STRING), null, new ItemStack(Material.STRING),
            new ItemStack(Material.STRING), new ItemStack(Material.STRING), new ItemStack(Material.STRING)
        };
        new SlimefunItem(itemGroup, stringMesh, RecipeType.ENHANCED_CRAFTING_TABLE, stringMeshRecipe).register(plugin);

        // Flint Mesh - Tier 2
        SlimefunItemStack flintMesh = new SlimefunItemStack(
            "FLINT_MESH",
            Material.LIGHT_GRAY_CARPET,
            "&7Flint Mesh",
            "&7Improved mesh for better sieving",
            "&7Chance: +5%"
        );
        ItemStack[] flintMeshRecipe = {
            new ItemStack(Material.FLINT), new ItemStack(Material.FLINT), new ItemStack(Material.FLINT),
            new ItemStack(Material.FLINT), new ItemStack(Material.STRING), new ItemStack(Material.FLINT),
            new ItemStack(Material.FLINT), new ItemStack(Material.FLINT), new ItemStack(Material.FLINT)
        };
        new SlimefunItem(itemGroup, flintMesh, RecipeType.ENHANCED_CRAFTING_TABLE, flintMeshRecipe).register(plugin);

        // Iron Mesh - Tier 3
        SlimefunItemStack ironMesh = new SlimefunItemStack(
            "IRON_MESH",
            Material.GRAY_CARPET,
            "&fIron Mesh",
            "&7Durable mesh for advanced sieving",
            "&7Chance: +10%"
        );
        ItemStack[] ironMeshRecipe = {
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.FLINT), new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT)
        };
        new SlimefunItem(itemGroup, ironMesh, RecipeType.ENHANCED_CRAFTING_TABLE, ironMeshRecipe).register(plugin);

        // Diamond Mesh - Tier 4
        SlimefunItemStack diamondMesh = new SlimefunItemStack(
            "DIAMOND_MESH",
            Material.LIGHT_BLUE_CARPET,
            "&bDiamond Mesh",
            "&7Ultimate mesh for maximum sieving efficiency",
            "&7Chance: +15%"
        );
        ItemStack[] diamondMeshRecipe = {
            new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
            new ItemStack(Material.DIAMOND), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.DIAMOND),
            new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND)
        };
        new SlimefunItem(itemGroup, diamondMesh, RecipeType.ENHANCED_CRAFTING_TABLE, diamondMeshRecipe).register(plugin);

        // ===== ORE PROCESSING ITEMS =====

        // Iron Ore Processing
        SlimefunItemStack ironOrePiece = new SlimefunItemStack(
            "IRON_ORE_PIECE",
            Material.RAW_IRON,
            "&fIron Ore Piece",
            "&7Small piece of iron ore",
            "&7Combine 4 to make an Iron Ore Chunk"
        );
        new SlimefunItem(itemGroup, ironOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack ironOreChunk = new SlimefunItemStack(
            "IRON_ORE_CHUNK",
            Material.RAW_IRON,
            "&fIron Ore Chunk",
            "&7Compressed iron ore ready for smelting"
        );
        ItemStack[] ironOreChunkRecipe = {
            ironOrePiece, ironOrePiece, null,
            ironOrePiece, ironOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, ironOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, ironOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            new SlimefunItemStack("IRON_INGOT_FROM_CHUNK", new ItemStack(Material.IRON_INGOT)),
            RecipeType.NULL,
            new ItemStack[]{ironOreChunk}
        ).register(plugin);

        // Gold Ore Processing
        SlimefunItemStack goldOrePiece = new SlimefunItemStack(
            "GOLD_ORE_PIECE",
            Material.RAW_GOLD,
            "&eGold Ore Piece",
            "&7Small piece of gold ore",
            "&7Combine 4 to make a Gold Ore Chunk"
        );
        new SlimefunItem(itemGroup, goldOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack goldOreChunk = new SlimefunItemStack(
            "GOLD_ORE_CHUNK",
            Material.RAW_GOLD,
            "&eGold Ore Chunk",
            "&7Compressed gold ore ready for smelting"
        );
        ItemStack[] goldOreChunkRecipe = {
            goldOrePiece, goldOrePiece, null,
            goldOrePiece, goldOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, goldOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, goldOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            new SlimefunItemStack("GOLD_INGOT_FROM_CHUNK", new ItemStack(Material.GOLD_INGOT)),
            RecipeType.NULL,
            new ItemStack[]{goldOreChunk}
        ).register(plugin);

        // Copper Ore Processing
        SlimefunItemStack copperOrePiece = new SlimefunItemStack(
            "COPPER_ORE_PIECE",
            Material.RAW_COPPER,
            "&6Copper Ore Piece",
            "&7Small piece of copper ore",
            "&7Combine 4 to make a Copper Ore Chunk"
        );
        new SlimefunItem(itemGroup, copperOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack copperOreChunk = new SlimefunItemStack(
            "COPPER_ORE_CHUNK",
            Material.RAW_COPPER,
            "&6Copper Ore Chunk",
            "&7Compressed copper ore ready for smelting"
        );
        ItemStack[] copperOreChunkRecipe = {
            copperOrePiece, copperOrePiece, null,
            copperOrePiece, copperOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, copperOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, copperOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            SlimefunItems.COPPER_INGOT,
            RecipeType.NULL,
            new ItemStack[]{copperOreChunk}
        ).register(plugin);

        // Aluminum Ore Processing
        SlimefunItemStack aluminumOrePiece = new SlimefunItemStack(
            "ALUMINUM_ORE_PIECE",
            Material.QUARTZ,
            "&fAluminum Ore Piece",
            "&7Small piece of aluminum ore",
            "&7Combine 4 to make an Aluminum Ore Chunk"
        );
        new SlimefunItem(itemGroup, aluminumOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack aluminumOreChunk = new SlimefunItemStack(
            "ALUMINUM_ORE_CHUNK",
            Material.QUARTZ,
            "&fAluminum Ore Chunk",
            "&7Compressed aluminum ore ready for smelting"
        );
        ItemStack[] aluminumOreChunkRecipe = {
            aluminumOrePiece, aluminumOrePiece, null,
            aluminumOrePiece, aluminumOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, aluminumOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, aluminumOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            SlimefunItems.ALUMINUM_INGOT,
            RecipeType.NULL,
            new ItemStack[]{aluminumOreChunk}
        ).register(plugin);

        // Lead Ore Processing
        SlimefunItemStack leadOrePiece = new SlimefunItemStack(
            "LEAD_ORE_PIECE",
            Material.COAL,
            "&8Lead Ore Piece",
            "&7Small piece of lead ore",
            "&7Combine 4 to make a Lead Ore Chunk"
        );
        new SlimefunItem(itemGroup, leadOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack leadOreChunk = new SlimefunItemStack(
            "LEAD_ORE_CHUNK",
            Material.COAL,
            "&8Lead Ore Chunk",
            "&7Compressed lead ore ready for smelting"
        );
        ItemStack[] leadOreChunkRecipe = {
            leadOrePiece, leadOrePiece, null,
            leadOrePiece, leadOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, leadOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, leadOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            SlimefunItems.LEAD_INGOT,
            RecipeType.NULL,
            new ItemStack[]{leadOreChunk}
        ).register(plugin);

        // Silver Ore Processing
        SlimefunItemStack silverOrePiece = new SlimefunItemStack(
            "SILVER_ORE_PIECE",
            Material.IRON_NUGGET,
            "&7Silver Ore Piece",
            "&7Small piece of silver ore",
            "&7Combine 4 to make a Silver Ore Chunk"
        );
        new SlimefunItem(itemGroup, silverOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack silverOreChunk = new SlimefunItemStack(
            "SILVER_ORE_CHUNK",
            Material.IRON_NUGGET,
            "&7Silver Ore Chunk",
            "&7Compressed silver ore ready for smelting"
        );
        ItemStack[] silverOreChunkRecipe = {
            silverOrePiece, silverOrePiece, null,
            silverOrePiece, silverOrePiece, null,
            null, null, null
        };
        new SlimefunItem(itemGroup, silverOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, silverOreChunkRecipe).register(plugin);

        new SlimefunItem(itemGroup,
            SlimefunItems.SILVER_INGOT,
            RecipeType.NULL,
            new ItemStack[]{silverOreChunk}
        ).register(plugin);
    }

}
