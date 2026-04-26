package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Sieve extends SlimefunItem {

    private final ItemStack goldPiece, ironPiece, copperPiece, aluminiumPiece, leadPiece, silverPiece, stonePebble;

    public Sieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                 ItemStack goldPiece, ItemStack ironPiece, ItemStack copperPiece,
                 ItemStack aluminiumPiece, ItemStack leadPiece, ItemStack silverPiece, ItemStack stonePebble) {
        super(itemGroup, item, recipeType, recipe);
        this.goldPiece = goldPiece;
        this.ironPiece = ironPiece;
        this.copperPiece = copperPiece;
        this.aluminiumPiece = aluminiumPiece;
        this.leadPiece = leadPiece;
        this.silverPiece = silverPiece;
        this.stonePebble = stonePebble;
    }

    @Override
    public void preRegister() {
        // Handle right-click for sifting
        addItemHandler((BlockUseHandler) this::onBlockRightClick);

        // Handle block breaking to return the installed mesh
        addItemHandler(new io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(org.bukkit.event.block.BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                Block b = event.getBlock();
                String meshTier = BlockStorage.getLocationInfo(b.getLocation(), "mesh_tier");

                if (meshTier != null) {
                    SlimefunItem meshItem = null;

                    switch (meshTier) {
                        case "STRING":
                            meshItem = SlimefunItem.getById("STRING_MESH");
                            break;
                        case "FLINT":
                            meshItem = SlimefunItem.getById("FLINT_MESH");
                            break;
                        case "IRON":
                            meshItem = SlimefunItem.getById("IRON_MESH");
                            break;
                        case "DIAMOND":
                            meshItem = SlimefunItem.getById("DIAMOND_MESH");
                            break;
                    }

                    if (meshItem != null) {
                        drops.add(meshItem.getItem().clone());
                    }
                }
            }
        });
    }

    private void onBlockRightClick(PlayerRightClickEvent event) {
        event.cancel();

        Player p = event.getPlayer();
        Block clickedBlock = event.getClickedBlock().orElse(null);
        if (clickedBlock == null) return;

        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        String meshTier = BlockStorage.getLocationInfo(clickedBlock.getLocation(), "mesh_tier");

        // Mesh Installation Logic
        if (meshTier == null) {
            SlimefunItem sfItemInHand = SlimefunItem.getByItem(itemInHand);

            if (sfItemInHand != null) {
                String itemId = sfItemInHand.getId();

                if (itemId.equals("STRING_MESH") || itemId.equals("FLINT_MESH") ||
                        itemId.equals("IRON_MESH") || itemId.equals("DIAMOND_MESH")) {

                    if (p.getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }

                    String tierToSave = itemId.replace("_MESH", "");
                    BlockStorage.addBlockInfo(clickedBlock.getLocation(), "mesh_tier", tierToSave);
                    p.playSound(clickedBlock.getLocation(), Sound.BLOCK_WOOL_PLACE, 1f, 1f);
                    p.sendMessage("§aInstalled " + tierToSave + " Mesh!");
                }
            }
            return;
        }

        // Sifting Logic
        boolean isGravel = itemInHand.getType() == Material.GRAVEL;
        boolean isSand = itemInHand.getType() == Material.SAND;
        boolean isDirt = itemInHand.getType() == Material.DIRT;

        if (isGravel || isSand || isDirt) {

            // Validation check: Prevent sifting dirt with advanced meshes
            if (isDirt && !meshTier.equals("STRING")) {
                p.sendMessage("§cThis mesh tier cannot sift Dirt!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            // FIX: Store the material type BEFORE we consume the item
            Material siftedMaterial = itemInHand.getType();

            // Consume the item
            if (p.getGameMode() != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }

            // Pass the safely stored material to the animation process
            startSiftingProcess(p, clickedBlock, meshTier, isGravel, isSand, isDirt, siftedMaterial);
        }
    }

    // Notice the new parameter: Material siftedMaterial
    private void startSiftingProcess(Player p, Block clickedBlock, String meshTier, boolean isGravel, boolean isSand, boolean isDirt, Material siftedMaterial) {

        Location particleLoc = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

        // Visual effects while sifting - Now uses siftedMaterial instead of clickedBlock.getType()
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Slimefun.instance(),
                () -> clickedBlock.getWorld().playEffect(particleLoc, Effect.STEP_SOUND, siftedMaterial),
                0L, 10L
        );

        // Delayed task for actual drop logic (5 seconds)
        Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);

            List<ItemStack> outputs = generateOutputs(meshTier, isGravel, isSand, isDirt);
            Location dropLoc = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

            if (!outputs.isEmpty()) {
                for (ItemStack out : outputs) {
                    Optional<Inventory> outputChest = OutputChest.findOutputChestFor(
                            clickedBlock.getRelative(BlockFace.DOWN), out
                    );

                    if (outputChest.isPresent()) {
                        outputChest.get().addItem(out.clone());
                    } else {
                        clickedBlock.getWorld().dropItemNaturally(dropLoc, out.clone());
                    }
                }
                p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1f, 1.5f);
            } else {
                p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1f, 0.5f);
            }
        }, 100L);
    }

    private void startSiftingProcess(Player p, Block clickedBlock, String meshTier, boolean isGravel, boolean isSand, boolean isDirt) {
        // Visual effects while sifting
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Slimefun.instance(),
                () -> clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, clickedBlock.getType()),
                0L, 10L
        );

        // Delayed task for actual drop logic (5 seconds)
        Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);

            List<ItemStack> outputs = generateOutputs(meshTier, isGravel, isSand, isDirt);
            Location dropLoc = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

            if (!outputs.isEmpty()) {
                for (ItemStack out : outputs) {
                    Optional<Inventory> outputChest = OutputChest.findOutputChestFor(
                            clickedBlock.getRelative(BlockFace.DOWN), out
                    );

                    if (outputChest.isPresent()) {
                        outputChest.get().addItem(out.clone());
                    } else {
                        clickedBlock.getWorld().dropItemNaturally(dropLoc, out.clone());
                    }
                }
                p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1f, 1.5f);
            } else {
                p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1f, 0.5f);
            }
        }, 100L);
    }

    /**
     * Helper method to process multiple drop chances for a single item.
     * This perfectly mimics the Ex Nihilo independent roll system.
     * Example: addDrop(results, item, 1.0, 0.5) = Guaranteed 1, 50% chance for a 2nd.
     */
    private void addDrop(List<ItemStack> results, ItemStack baseItem, double... chances) {
        int amount = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Roll for each probability provided
        for (double chance : chances) {
            if (random.nextDouble() <= chance) {
                amount++;
            }
        }

        // If any rolls were successful, add the combined amount to the results
        if (amount > 0) {
            ItemStack drop = baseItem.clone();
            drop.setAmount(amount);
            results.add(drop);
        }
    }

    private List<ItemStack> generateOutputs(String meshTier, boolean isGravel, boolean isSand, boolean isDirt) {
        List<ItemStack> results = new ArrayList<>();

        switch (meshTier) {
            case "STRING":
                if (isDirt) {
                    // DIRT DROPS (Stone pebbles: 2x 100%, 2x 50%, 2x 10%)
                    addDrop(results, stonePebble, 1.0, 1.0, 0.50, 0.50, 0.10, 0.10);

                    // Seeds
                    addDrop(results, new ItemStack(Material.POTATO), 0.04);
                    addDrop(results, new ItemStack(Material.CARROT), 0.04);
                    addDrop(results, new ItemStack(Material.SUGAR_CANE), 0.04);
                    addDrop(results, new ItemStack(Material.WHEAT_SEEDS), 0.10);
                    addDrop(results, new ItemStack(Material.MELON_SEEDS), 0.02);
                    addDrop(results, new ItemStack(Material.PUMPKIN_SEEDS), 0.02);
                } else if (isGravel || isSand) {
                    // ORE DROPS (Base chance + a small chance to get a 2nd piece)
                    addDrop(results, goldPiece, 0.03);
                    addDrop(results, ironPiece, 0.20);
                    addDrop(results, copperPiece, 0.08);
                    addDrop(results, aluminiumPiece, 0.05);
                    addDrop(results, leadPiece, 0.03);
                    addDrop(results, silverPiece, 0.04);

                    if (isGravel) {
                        addDrop(results, new ItemStack(Material.FLINT), 0.18);
                    }
                }
                break;

            case "FLINT":
                if (isGravel) {
                    addDrop(results, goldPiece, 0.06);
                    addDrop(results, ironPiece, 0.22);
                    addDrop(results, copperPiece, 0.16);
                    addDrop(results, aluminiumPiece, 0.11);
                    addDrop(results, leadPiece, 0.06);
                    addDrop(results, silverPiece, 0.08);
                    addDrop(results, new ItemStack(Material.COAL), 0.18);
                    addDrop(results, new ItemStack(Material.LAPIS_LAZULI), 0.05);
                } else if (isSand) {
                    addDrop(results, goldPiece, 0.06);
                    addDrop(results, ironPiece, 0.22);
                    addDrop(results, copperPiece, 0.16);
                    addDrop(results, aluminiumPiece, 0.11);
                    addDrop(results, leadPiece, 0.06);
                    addDrop(results, silverPiece, 0.08);
                }
                break;

            case "IRON":
                if (isGravel) {
                    addDrop(results, goldPiece, 0.10);
                    addDrop(results, ironPiece, 0.24);
                    addDrop(results, leadPiece, 0.10);
                    addDrop(results, new ItemStack(Material.DIAMOND), 0.01);
                    addDrop(results, new ItemStack(Material.LAPIS_LAZULI), 0.10);
                    addDrop(results, new ItemStack(Material.COAL), 0.18);
                } else if (isSand) {
                    addDrop(results, goldPiece, 0.10);
                    addDrop(results, ironPiece, 0.24);
                    addDrop(results, leadPiece, 0.10);
                    addDrop(results, new ItemStack(Material.REDSTONE), 0.05);
                    addDrop(results, new ItemStack(Material.GUNPOWDER), 0.16);
                    addDrop(results, new ItemStack(Material.BLAZE_POWDER), 0.05);
                }
                break;

            case "DIAMOND":
                if (isGravel) {
                    addDrop(results, goldPiece, 0.15);
                    addDrop(results, new ItemStack(Material.DIAMOND), 0.03);
                    addDrop(results, new ItemStack(Material.EMERALD), 0.01);
                    addDrop(results, new ItemStack(Material.LAPIS_LAZULI), 0.12);
                    addDrop(results, new ItemStack(Material.COAL), 0.22);
                } else if (isSand) {
                    addDrop(results, goldPiece, 0.15);
                    addDrop(results, new ItemStack(Material.REDSTONE), 0.10);
                    addDrop(results, new ItemStack(Material.GUNPOWDER), 0.20);
                    addDrop(results, new ItemStack(Material.BLAZE_POWDER), 0.10);
                }
                break;
        }

        return results;
    }
}