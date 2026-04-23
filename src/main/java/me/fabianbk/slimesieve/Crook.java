package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ThreadLocalRandom;

public class Crook extends SlimefunItem implements Listener {

    private final SlimefunItemStack silkworm;
    private final Material hoeForm;
    private final Material swordForm;

    public Crook(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                 SlimefunItemStack silkworm, Material hoeForm, Material swordForm, Plugin plugin) {
        super(itemGroup, item, recipeType, recipe);
        this.silkworm = silkworm;
        this.hoeForm = hoeForm;
        this.swordForm = swordForm;

        // Register as a Bukkit listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void preRegister() {
    }

    // Triggered when a player starts breaking a block (dynamic item swapping)
    @EventHandler(ignoreCancelled = true)
    public void onMine(BlockDamageEvent e) {
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        SlimefunItem sfItem = SlimefunItem.getByItem(itemInHand);

        // Verify if the item is exactly this specific Crook
        if (sfItem != null && sfItem.getId().equals(this.getId())) {
            Block b = e.getBlock();
            Material blockType = b.getType();

            // Swap to sword form for cobwebs (breaks cobwebs much faster)
            if (blockType == Material.COBWEB) {
                if (itemInHand.getType() != swordForm) {
                    itemInHand.setType(swordForm);
                }
            }
            // Default back to hoe form for everything else (including leaves)
            else {
                if (itemInHand.getType() != hoeForm) {
                    itemInHand.setType(hoeForm);
                }
            }
        }
    }

    // Triggered when a block is successfully broken
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        SlimefunItem sfItem = SlimefunItem.getByItem(itemInHand);

        if (sfItem != null && sfItem.getId().equals(this.getId())) {
            Block b = e.getBlock();
            Material type = b.getType();
            Location loc = b.getLocation().add(0.5, 0.5, 0.5);

            // 1. Breaking normal leaves
            if (Tag.LEAVES.isTagged(type)) {
                // 10% chance to drop a Silkworm
                if (ThreadLocalRandom.current().nextDouble() <= 0.10) {
                    b.getWorld().dropItemNaturally(loc, silkworm.clone());
                }
            }
            // 2. Breaking infested leaves (represented by Cobweb)
            else if (type == Material.COBWEB) {
                e.setDropItems(false); // Clear default vanilla drops

                // Guaranteed 1-2 strings from the custom drop
                int stringAmount = ThreadLocalRandom.current().nextInt(1, 3);
                b.getWorld().dropItemNaturally(loc, new ItemStack(Material.STRING, stringAmount));

                // 20% chance to get a Silkworm back
                if (ThreadLocalRandom.current().nextDouble() <= 0.20) {
                    b.getWorld().dropItemNaturally(loc, silkworm.clone());
                }
            }
        }
    }
}