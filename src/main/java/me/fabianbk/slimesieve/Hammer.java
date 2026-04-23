package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Hammer extends SlimefunItem implements Listener, NotPlaceable {

    private final Material pickaxeForm;
    private final Material shovelForm;

    public Hammer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                  Material pickaxeForm, Material shovelForm, Plugin plugin) {
        super(itemGroup, item, recipeType, recipe);
        this.pickaxeForm = pickaxeForm;
        this.shovelForm = shovelForm;

        // Register the listener to handle dynamic item switching and block breaking
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void preRegister() {
        // Removed BlockBreakHandler because that is meant for when a Slimefun block is broken,
        // not for when a Slimefun tool is used to break something.
    }

    // Triggered when a player starts breaking a block (handles the Paxel switching)
    @EventHandler(ignoreCancelled = true)
    public void onMine(BlockDamageEvent e) {
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        SlimefunItem sfItem = SlimefunItem.getByItem(itemInHand);

        // Verify if the item is exactly this specific Hammer tier
        if (sfItem != null && sfItem.getId().equals(this.getId())) {
            Block b = e.getBlock();
            Material blockType = b.getType();

            // Swap to shovel form for loose blocks
            if (blockType == Material.GRAVEL || blockType == Material.SAND || blockType == Material.DIRT) {
                if (itemInHand.getType() != shovelForm) {
                    itemInHand.setType(shovelForm);
                }
            } else {
                // Default back to pickaxe form for stone-like blocks
                if (itemInHand.getType() != pickaxeForm) {
                    itemInHand.setType(pickaxeForm);
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

        // Verify if the player is using THIS specific Hammer tier
        if (sfItem != null && sfItem.getId().equals(this.getId())) {
            Block b = e.getBlock();
            Material type = b.getType();
            Location loc = b.getLocation().add(0.5, 0.5, 0.5);

            // Convert Stone or Cobblestone into Gravel
            if (type == Material.STONE || type == Material.COBBLESTONE) {
                e.setDropItems(false); // Disable vanilla cobblestone drop
                b.getWorld().dropItemNaturally(loc, new ItemStack(Material.GRAVEL));
            }
            // Convert Gravel into Sand
            else if (type == Material.GRAVEL) {
                e.setDropItems(false); // Disable vanilla gravel/flint drop
                b.getWorld().dropItemNaturally(loc, new ItemStack(Material.SAND));
            }
        }
    }
}