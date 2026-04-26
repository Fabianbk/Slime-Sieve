package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompostBarrel extends SlimefunItem implements RecipeDisplayItem {

    private final List<ItemStack> recipes = new ArrayList<>();

    public CompostBarrel(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        setupRecipes();
    }

    /**
     * Define the inputs and outputs for the Compost Barrel.
     * Format: add(Input), then add(Output)
     */
    private void setupRecipes() {
        // All types of leaves (8x) -> 1x Dirt
        for (Material leaf : Tag.LEAVES.getValues()) {
            recipes.add(new ItemStack(leaf, 8));
            recipes.add(new ItemStack(Material.DIRT));
        }

        // All types of saplings (8x) -> 1x Dirt
        for (Material sapling : Tag.SAPLINGS.getValues()) {
            recipes.add(new ItemStack(sapling, 8));
            recipes.add(new ItemStack(Material.DIRT));
        }

        // Other organic materials
        recipes.add(new ItemStack(Material.WHEAT, 8));
        recipes.add(new ItemStack(Material.DIRT));

        recipes.add(new ItemStack(Material.ROTTEN_FLESH, 8));
        recipes.add(new ItemStack(Material.DIRT));

        recipes.add(new ItemStack(Material.STRING, 8)); // String from Silkworms
        recipes.add(new ItemStack(Material.DIRT));
    }

    // This makes the recipes show up correctly in the Slimefun Guide
    @Override
    public List<ItemStack> getDisplayRecipes() {
        return recipes;
    }

    @Override
    public void preRegister() {
        addItemHandler((BlockUseHandler) this::onBlockRightClick);
    }

    private void onBlockRightClick(PlayerRightClickEvent event) {
        Block b = event.getClickedBlock().orElse(null);
        if (b == null) return;

        event.cancel();
        Player p = event.getPlayer();
        ItemStack input = p.getInventory().getItemInMainHand();
        ItemStack output = getOutput(p, input);

        if (output != null) {
            // Process the composting with Bukkit Scheduler
            startComposting(b, p, input, output);
        } else if (input.getType() != Material.AIR) {
            // Only send error if they are actually holding an invalid item
            p.sendMessage("§cThis item cannot be composted or you don't have enough (Need 8)!");
        }
    }

    private ItemStack getOutput(Player p, ItemStack input) {
        for (int i = 0; i < recipes.size(); i += 2) {
            ItemStack requiredInput = recipes.get(i);

            // Check if the item matches and the player has enough amount
            if (requiredInput != null && SlimefunUtils.isItemSimilar(input, requiredInput, true)) {
                if (input.getAmount() >= requiredInput.getAmount()) {
                    ItemStack output = recipes.get(i + 1);

                    // Consume items from player's hand
                    ItemStack removing = input.clone();
                    removing.setAmount(requiredInput.getAmount());
                    p.getInventory().removeItem(removing);

                    return output.clone();
                }
            }
        }
        return null;
    }

    private void startComposting(Block b, Player p, ItemStack input, ItemStack output) {
        Material effectMaterial = input.getType().isBlock() ? input.getType() : Material.DIRT;

        // Visual effects loop (plays particles 3 times with delays)
        for (int i = 0; i < 3; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
                b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, effectMaterial);
            }, i * 10L);
        }

        // Final task to drop the item and play sound (after 30 ticks = 1.5 seconds)
        Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
            p.playSound(b.getLocation(), Sound.BLOCK_COMPOSTER_READY, 1f, 1f);
            pushOutput(b, output);
        }, 30L);
    }

    private void pushOutput(Block b, ItemStack output) {
        // OutputChest compatibility (Drops item into a chest below if it exists)
        Optional<Inventory> outputChest = OutputChest.findOutputChestFor(b.getRelative(BlockFace.DOWN), output);

        if (outputChest.isPresent()) {
            outputChest.get().addItem(output);
        } else {
            Location loc = b.getLocation().add(0.5, 1.0, 0.5);
            b.getWorld().dropItemNaturally(loc, output);
        }
    }
}