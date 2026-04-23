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
        addItemHandler((BlockUseHandler) this::onBlockRightClick);

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

        // Handle sifting for all mesh tiers
        boolean isGravel = itemInHand.getType() == Material.GRAVEL;
        boolean isSand = itemInHand.getType() == Material.SAND;
        boolean isDirt = itemInHand.getType() == Material.DIRT;

        if (isGravel || isSand || isDirt) {
            if (p.getGameMode() != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
            startSiftingProcess(p, clickedBlock, meshTier, isGravel, isSand, isDirt);
        }
    }

    private void startSiftingProcess(Player p, Block clickedBlock, String meshTier, boolean isGravel, boolean isSand, boolean isDirt) {
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Slimefun.instance(),
                () -> clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, clickedBlock.getType()),
                0L, 10L
        );

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
        }, 100L); // 5 Seconds
    }

    private List<ItemStack> generateOutputs(String meshTier, boolean isGravel, boolean isSand, boolean isDirt) {
        List<ItemStack> results = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        switch (meshTier) {
            case "STRING":
                // STRING MESH - Works on gravel and dirt
                if (isDirt) {
                    // DIRT DROPS - Stone pebbles and seeds
                    // Guaranteed 2 pebbles
                    ItemStack twoPebbles = stonePebble.clone();
                    twoPebbles.setAmount(2);
                    results.add(twoPebbles);

                    // 5 independent rolls for more pebbles
                    if (random.nextDouble() <= 0.50) results.add(stonePebble.clone()); // 50%
                    if (random.nextDouble() <= 0.50) results.add(stonePebble.clone()); // 50%
                    if (random.nextDouble() <= 0.10) results.add(stonePebble.clone()); // 10%
                    if (random.nextDouble() <= 0.10) results.add(stonePebble.clone()); // 10%

                    // Seeds and crops
                    if (random.nextDouble() <= 0.04) results.add(new ItemStack(Material.POTATO));
                    if (random.nextDouble() <= 0.04) results.add(new ItemStack(Material.CARROT));
                    if (random.nextDouble() <= 0.04) results.add(new ItemStack(Material.SUGAR_CANE));
                    if (random.nextDouble() <= 0.10) results.add(new ItemStack(Material.WHEAT_SEEDS));
                    if (random.nextDouble() <= 0.02) results.add(new ItemStack(Material.MELON_SEEDS));
                    if (random.nextDouble() <= 0.02) results.add(new ItemStack(Material.PUMPKIN_SEEDS));
                } else if (isGravel || isSand) {
                    // GRAVEL SAND DROPS - Ore pieces
                    if (random.nextDouble() <= 0.03) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.20) results.add(ironPiece.clone());
                    if (random.nextDouble() <= 0.08) results.add(copperPiece.clone());
                    if (random.nextDouble() <= 0.05) results.add(aluminiumPiece.clone());
                    if (random.nextDouble() <= 0.03) results.add(leadPiece.clone());
                    if (random.nextDouble() <= 0.04) results.add(silverPiece.clone());
                    if (isGravel) {
                        if (random.nextDouble() <= 0.18) results.add(new ItemStack(Material.FLINT));
                    }
                }
                break;

            case "FLINT":
                // FLINT MESH - Works on both gravel and sand
                if (isGravel) {
                    // Gravel drops
                    if (random.nextDouble() <= 0.06) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.22) results.add(ironPiece.clone());
                    if (random.nextDouble() <= 0.16) results.add(copperPiece.clone());
                    if (random.nextDouble() <= 0.11) results.add(aluminiumPiece.clone());
                    if (random.nextDouble() <= 0.06) results.add(leadPiece.clone());
                    if (random.nextDouble() <= 0.08) results.add(silverPiece.clone());
                    if (random.nextDouble() <= 0.18) results.add(new ItemStack(Material.COAL));
                    if (random.nextDouble() <= 0.05) results.add(new ItemStack(Material.LAPIS_LAZULI));
                } else {
                    // Sand drops
                    if (random.nextDouble() <= 0.06) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.22) results.add(ironPiece.clone());
                    if (random.nextDouble() <= 0.16) results.add(copperPiece.clone());
                    if (random.nextDouble() <= 0.11) results.add(aluminiumPiece.clone());
                    if (random.nextDouble() <= 0.06) results.add(leadPiece.clone());
                    if (random.nextDouble() <= 0.08) results.add(silverPiece.clone());
                }
                break;

            case "IRON":
                // IRON MESH - Works on both gravel and sand
                if (isGravel) {
                    // Gravel drops
                    if (random.nextDouble() <= 0.10) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.24) results.add(ironPiece.clone());
                    if (random.nextDouble() <= 0.10) results.add(leadPiece.clone());
                    if (random.nextDouble() <= 0.01) results.add(new ItemStack(Material.DIAMOND));
                    if (random.nextDouble() <= 0.10) results.add(new ItemStack(Material.LAPIS_LAZULI));
                    if (random.nextDouble() <= 0.18) results.add(new ItemStack(Material.COAL));
                } else {
                    // Sand drops
                    if (random.nextDouble() <= 0.10) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.24) results.add(ironPiece.clone());
                    if (random.nextDouble() <= 0.10) results.add(leadPiece.clone());
                    if (random.nextDouble() <= 0.05) results.add(new ItemStack(Material.REDSTONE));
                    if (random.nextDouble() <= 0.16) results.add(new ItemStack(Material.GUNPOWDER));
                    if (random.nextDouble() <= 0.05) results.add(new ItemStack(Material.BLAZE_POWDER));
                }
                break;

            case "DIAMOND":
                // DIAMOND MESH - Works on both gravel and sand
                if (isGravel) {
                    // Gravel drops
                    if (random.nextDouble() <= 0.15) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.03) results.add(new ItemStack(Material.DIAMOND));
                    if (random.nextDouble() <= 0.01) results.add(new ItemStack(Material.EMERALD));
                    if (random.nextDouble() <= 0.12) results.add(new ItemStack(Material.LAPIS_LAZULI));
                    if (random.nextDouble() <= 0.22) results.add(new ItemStack(Material.COAL));
                } else {
                    // Sand drops
                    if (random.nextDouble() <= 0.15) results.add(goldPiece.clone());
                    if (random.nextDouble() <= 0.10) results.add(new ItemStack(Material.REDSTONE));
                    if (random.nextDouble() <= 0.20) results.add(new ItemStack(Material.GUNPOWDER));
                    if (random.nextDouble() <= 0.10) results.add(new ItemStack(Material.BLAZE_POWDER));
                }
                break;
        }

        return results;
    }
}