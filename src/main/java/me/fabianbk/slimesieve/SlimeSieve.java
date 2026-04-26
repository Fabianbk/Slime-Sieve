package me.fabianbk.slimesieve;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
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

    // ประกาศตัวแปรระดับคลาสไว้ตรงนี้ เพื่อให้ทุกเมธอดมองเห็น
    private SlimefunItemStack ironOrePiece;
    private SlimefunItemStack goldOrePiece;
    private SlimefunItemStack copperOrePiece;
    private SlimefunItemStack aluminumOrePiece;
    private SlimefunItemStack leadOrePiece;
    private SlimefunItemStack silverOrePiece;
    private SlimefunItemStack stonePebble;

    @Override
    public void onEnable() {
        Config cfg = new Config(this);

        ItemStack sieveGroupItem = new CustomItemStack(Material.SCAFFOLDING, "Slime Sieve", "", "&a> Click to open");
        NamespacedKey sieveGroupId = new NamespacedKey(this, "sieve_category");
        ItemGroup sieveGroup = new ItemGroup(sieveGroupId, sieveGroupItem);

        SlimefunItemStack sieveItem = new SlimefunItemStack("SIEVE", Material.SCAFFOLDING, "Sieve", "&7Used for sifting dirt, sand, and gravel.");

        ItemStack[] sieveRecipe = {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.STRING), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.STRING), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.STICK),      null                          , new ItemStack(Material.STICK)
        };

        // สำคัญ: ต้องสร้างไอเทมแร่ "ก่อน" ที่จะเอาไปโยนใส่คลาส Sieve
        registerSieveItems(this, sieveGroup);

        // ตอนนี้เรามีตัวแปรแร่ครบแล้ว ก็โยนใส่ Constructor ได้เลย! (ครบ 10 ตัว)
        Sieve sieve = new Sieve(sieveGroup, sieveItem, RecipeType.ENHANCED_CRAFTING_TABLE, sieveRecipe,
                goldOrePiece, ironOrePiece, copperOrePiece, aluminumOrePiece, leadOrePiece, silverOrePiece,stonePebble);
        sieve.register(this);
    }

    @Override
    public void onDisable() {}

    @Override
    public String getBugTrackerURL() { return null; }

    @Override
    public JavaPlugin getJavaPlugin() { return this; }


    // เปลี่ยนจาก static เป็นเมธอดธรรมดา จะได้เข้าถึงตัวแปรระดับคลาสได้
    public void registerSieveItems(SlimefunAddon plugin, ItemGroup itemGroup) {

        // ==========================================
        // ===== MESH ITEMS =====
        // ==========================================

        // String Mesh - Basic tier
        SlimefunItemStack stringMesh = new SlimefunItemStack(
                "STRING_MESH",
                Material.WHITE_CARPET,
                "&fString Mesh",
                "&7Basic mesh for sieving materials"
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
                "&7Improved mesh for better sieving"
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
                "&7Durable mesh for advanced sieving"
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
                "&7Ultimate mesh for maximum sieving efficiency"
        );
        ItemStack[] diamondMeshRecipe = {
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND)
        };
        new SlimefunItem(itemGroup, diamondMesh, RecipeType.ENHANCED_CRAFTING_TABLE, diamondMeshRecipe).register(plugin);

        // ==========================================
        // ===== STONE PEBBLE (for dirt sifting) =====
        // ==========================================
        stonePebble = new SlimefunItemStack("STONE_PEBBLE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU1NGFiYmM2NWIxM2E0MmMyOTU5MGEwY2Y5ZDNlMDA3MDJkMWU2MGQ5NzRmOTI4NmE3YzE3MjY3ZjIyODJjOSJ9fX0=", "&7Stone Pebble", "&7Small stone fragment from dirt", "&7Combine 4 to make Cobblestone");
        new SlimefunItem(itemGroup, stonePebble, RecipeType.NULL, null).register(plugin);

        // Recipe: 4 Stone Pebbles → 1 Cobblestone
        ItemStack[] pebbleToCobbleRecipe = { stonePebble, stonePebble, null, stonePebble, stonePebble, null, null, null, null };
        new SlimefunItem(itemGroup, new SlimefunItemStack("COBBLESTONE_FROM_PEBBLES", new ItemStack(Material.COBBLESTONE)), RecipeType.ENHANCED_CRAFTING_TABLE, pebbleToCobbleRecipe).register(plugin);

        // ==========================================
        // ===== ORE PROCESSING ITEMS =====
        // ==========================================

        // --- Iron ---
        ironOrePiece = new SlimefunItemStack("IRON_ORE_PIECE", Material.GUNPOWDER, "&fIron Ore Piece", "&7Small piece of iron ore", "&7Combine 4 to make an Iron Ore Chunk");
        new SlimefunItem(itemGroup, ironOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack ironOreChunk = new SlimefunItemStack("IRON_ORE_CHUNK", Material.RAW_IRON, "&fIron Ore Chunk", "&7Compressed iron ore ready for smelting");
        ItemStack[] ironOreChunkRecipe = { ironOrePiece, ironOrePiece, null, ironOrePiece, ironOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, ironOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, ironOreChunkRecipe).register(plugin);

        ItemStack[] ironSmeltRecipe = {ironOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("IRON_INGOT_FROM_CHUNK", new ItemStack(Material.IRON_INGOT)), RecipeType.SMELTERY, ironSmeltRecipe).register(plugin);

        // --- Gold ---
        goldOrePiece = new SlimefunItemStack("GOLD_ORE_PIECE", Material.GLOWSTONE_DUST, "&eGold Ore Piece", "&7Small piece of gold ore", "&7Combine 4 to make a Gold Ore Chunk");
        new SlimefunItem(itemGroup, goldOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack goldOreChunk = new SlimefunItemStack("GOLD_ORE_CHUNK", Material.RAW_GOLD, "&eGold Ore Chunk", "&7Compressed gold ore ready for smelting");
        ItemStack[] goldOreChunkRecipe = { goldOrePiece, goldOrePiece, null, goldOrePiece, goldOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, goldOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, goldOreChunkRecipe).register(plugin);

        ItemStack[] goldSmeltRecipe = {goldOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("GOLD_INGOT_FROM_CHUNK", new ItemStack(Material.GOLD_INGOT)), RecipeType.SMELTERY, goldSmeltRecipe).register(plugin);

        // --- Copper ---
        copperOrePiece = new SlimefunItemStack("COPPER_ORE_PIECE", Material.GLOWSTONE_DUST, "&6Copper Ore Piece", "&7Small piece of copper ore", "&7Combine 4 to make a Copper Ore Chunk");
        new SlimefunItem(itemGroup, copperOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack copperOreChunk = new SlimefunItemStack("COPPER_ORE_CHUNK", Material.RAW_COPPER, "&6Copper Ore Chunk", "&7Compressed copper ore ready for smelting");
        ItemStack[] copperOreChunkRecipe = { copperOrePiece, copperOrePiece, null, copperOrePiece, copperOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, copperOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, copperOreChunkRecipe).register(plugin);

        ItemStack[] copperSmeltRecipe = {copperOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("COPPER_INGOT_FROM_CHUNK", SlimefunItems.COPPER_INGOT), RecipeType.SMELTERY, copperSmeltRecipe).register(plugin);

        // --- Aluminum ---
        aluminumOrePiece = new SlimefunItemStack("ALUMINUM_ORE_PIECE", Material.SUGAR, "&fAluminum Ore Piece", "&7Small piece of aluminum ore", "&7Combine 4 to make an Aluminum Ore Chunk");
        new SlimefunItem(itemGroup, aluminumOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack aluminumOreChunk = new SlimefunItemStack("ALUMINUM_ORE_CHUNK", Material.FIREWORK_STAR, "&fAluminum Ore Chunk", "&7Compressed aluminum ore ready for smelting");
        ItemStack[] aluminumOreChunkRecipe = { aluminumOrePiece, aluminumOrePiece, null, aluminumOrePiece, aluminumOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, aluminumOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, aluminumOreChunkRecipe).register(plugin);

        ItemStack[] aluminumSmeltRecipe = {aluminumOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("ALUMINUM_INGOT_FROM_CHUNK", SlimefunItems.ALUMINUM_INGOT), RecipeType.SMELTERY, aluminumSmeltRecipe).register(plugin);

        // --- Lead ---
        leadOrePiece = new SlimefunItemStack("LEAD_ORE_PIECE", Material.GUNPOWDER, "&8Lead Ore Piece", "&7Small piece of lead ore", "&7Combine 4 to make a Lead Ore Chunk");
        new SlimefunItem(itemGroup, leadOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack leadOreChunk = new SlimefunItemStack("LEAD_ORE_CHUNK", Material.FIREWORK_STAR, "&8Lead Ore Chunk", "&7Compressed lead ore ready for smelting");
        ItemStack[] leadOreChunkRecipe = { leadOrePiece, leadOrePiece, null, leadOrePiece, leadOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, leadOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, leadOreChunkRecipe).register(plugin);

        ItemStack[] leadSmeltRecipe = {leadOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("LEAD_INGOT_FROM_CHUNK", SlimefunItems.LEAD_INGOT), RecipeType.SMELTERY, leadSmeltRecipe).register(plugin);

        // --- Silver ---
        silverOrePiece = new SlimefunItemStack("SILVER_ORE_PIECE", Material.SUGAR, "&7Silver Ore Piece", "&7Small piece of silver ore", "&7Combine 4 to make a Silver Ore Chunk");
        new SlimefunItem(itemGroup, silverOrePiece, RecipeType.NULL, null).register(plugin);

        SlimefunItemStack silverOreChunk = new SlimefunItemStack("SILVER_ORE_CHUNK", Material.FIREWORK_STAR, "&7Silver Ore Chunk", "&7Compressed silver ore ready for smelting");
        ItemStack[] silverOreChunkRecipe = { silverOrePiece, silverOrePiece, null, silverOrePiece, silverOrePiece, null, null, null, null };
        new SlimefunItem(itemGroup, silverOreChunk, RecipeType.ENHANCED_CRAFTING_TABLE, silverOreChunkRecipe).register(plugin);

        ItemStack[] silverSmeltRecipe = {silverOreChunk, null, null, null, null, null, null, null, null};
        new SlimefunItem(itemGroup, new SlimefunItemStack("SILVER_INGOT_FROM_CHUNK", SlimefunItems.SILVER_INGOT), RecipeType.SMELTERY, silverSmeltRecipe).register(plugin);

        // 1. Wooden Hammer
        SlimefunItemStack woodenHammer = new SlimefunItemStack("WOODEN_HAMMER", Material.WOODEN_PICKAXE, "&fWooden Hammer", "&7Basic hammer");
        ItemStack[] woodenRecipe = {
                null, new ItemStack(Material.OAK_PLANKS), null,
                null, new ItemStack(Material.STICK), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.STICK), null, null
        };
        new Hammer(itemGroup, woodenHammer, RecipeType.ENHANCED_CRAFTING_TABLE, woodenRecipe,
                Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, plugin.getJavaPlugin()).register(plugin);

        // 2. Stone Hammer
        SlimefunItemStack stoneHammer = new SlimefunItemStack("STONE_HAMMER", Material.STONE_PICKAXE, "&7Stone Hammer", "&7Faster hammer");
        ItemStack[] stoneRecipe = {
                null, new ItemStack(Material.COBBLESTONE), null,
                null, new ItemStack(Material.STICK), new ItemStack(Material.COBBLESTONE),
                new ItemStack(Material.STICK), null, null
        };
        new Hammer(itemGroup, stoneHammer, RecipeType.ENHANCED_CRAFTING_TABLE, stoneRecipe,
                Material.STONE_PICKAXE, Material.STONE_SHOVEL, plugin.getJavaPlugin()).register(plugin);

        // 3. Iron Hammer
        SlimefunItemStack ironHammer = new SlimefunItemStack("IRON_HAMMER", Material.IRON_PICKAXE, "&fIron Hammer", "&7Efficient hammer");
        ItemStack[] ironRecipe = {
                null, new ItemStack(Material.IRON_INGOT), null,
                null, new ItemStack(Material.STICK), new ItemStack(Material.IRON_INGOT),
                new ItemStack(Material.STICK), null, null
        };
        new Hammer(itemGroup, ironHammer, RecipeType.ENHANCED_CRAFTING_TABLE, ironRecipe,
                Material.IRON_PICKAXE, Material.IRON_SHOVEL, plugin.getJavaPlugin()).register(plugin);

        // 3. Diamond Hammer
        SlimefunItemStack diamondHammer = new SlimefunItemStack("DIAMOND_HAMMER", Material.DIAMOND_PICKAXE, "&bDiamond Hammer", "&7Fastest hammer");
        ItemStack[] diamondRecipe = {
                null, new ItemStack(Material.DIAMOND), null,
                null, new ItemStack(Material.STICK), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.STICK), null, null
        };
        new Hammer(itemGroup, diamondHammer, RecipeType.ENHANCED_CRAFTING_TABLE, diamondRecipe,
                Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, plugin.getJavaPlugin()).register(plugin);

        // ==========================================
        // ===== CROOK & SILKWORM =====
        // ==========================================

        // 1. Silkworm
        SlimefunItemStack silkwormItem = new SlimefunItemStack(
                "SILKWORM",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4YTY0ZmE2YmQ2ZjZhYmJhNzA1MzNjMTkxNmM0Y2FkMmZhMTJjNzMwM2Q4OTM0YTc5OWE5MTUxNTRiODBmYSJ9fX0=",
                "&fSilkworm",
                "&7Right-click on leaves to infest them",
                "&7Break infested leaves with a Crook for string"
        );
        new Silkworm(itemGroup, silkwormItem, RecipeType.NULL, null).register(plugin);

        // 2. Wooden Crook
        SlimefunItemStack woodenCrook = new SlimefunItemStack(
                "WOODEN_CROOK",
                Material.WOODEN_HOE,
                "&fWooden Crook",
                "&7Breaks leaves to find Silkworms",
                "&7Harvests string from infested leaves"
        );
        // Recipe: A hook shape made of sticks
        ItemStack[] crookRecipe = {
                new ItemStack(Material.STICK), new ItemStack(Material.STICK), null,
                null,                          new ItemStack(Material.STICK), null,
                null,                          new ItemStack(Material.STICK), null
        };

        // We pass the silkwormItem, WOODEN_HOE, WOODEN_SWORD, and the plugin instance into the Crook constructor
        new Crook(itemGroup, woodenCrook, RecipeType.ENHANCED_CRAFTING_TABLE, crookRecipe,
                silkwormItem, Material.WOODEN_HOE, Material.WOODEN_SWORD, plugin.getJavaPlugin()).register(plugin);

        // ==========================================
        // ===== COMPOST BARREL =====
        // ==========================================

        SlimefunItemStack compostBarrelItem = new SlimefunItemStack(
                "COMPOST_BARREL",
                Material.COMPOSTER,
                "&fCompost Barrel",
                "&7Turns organic matter into Dirt",
                "&7Right-click with 8x Leaves, Saplings, Wheat, etc."
        );

        // Super cheap early game recipe (Wood Planks and Slabs)
        ItemStack[] compostBarrelRecipe = {
                new ItemStack(Material.OAK_SLAB), null, new ItemStack(Material.OAK_SLAB),
                new ItemStack(Material.OAK_PLANKS), null, new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_PLANKS)
        };

        new CompostBarrel(itemGroup, compostBarrelItem, RecipeType.ENHANCED_CRAFTING_TABLE, compostBarrelRecipe).register(plugin);
    }
}