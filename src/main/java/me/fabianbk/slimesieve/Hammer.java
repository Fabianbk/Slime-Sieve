package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Hammer extends SlimefunItem implements Listener, NotPlaceable {

    private final Material pickaxeForm;
    private final Material shovelForm;

    public Hammer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                  Material pickaxeForm, Material shovelForm, Plugin plugin) {
        super(itemGroup, item, recipeType, recipe);
        this.pickaxeForm = pickaxeForm;
        this.shovelForm = shovelForm;

        // Register the listener to handle dynamic item switching
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void preRegister() {
        // Handle custom block drops for the hammer progression
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                Block b = event.getBlock();
                Material type = b.getType();

                // Convert Stone or Cobblestone into Gravel
                if (type == Material.STONE || type == Material.COBBLESTONE) {
                    drops.clear();
                    drops.add(new ItemStack(Material.GRAVEL));
                }
                // Convert Gravel into Sand
                else if (type == Material.GRAVEL) {
                    drops.clear();
                    drops.add(new ItemStack(Material.SAND));
                }
                // Convert Sand into Dirt
                else if (type == Material.SAND) {
                    drops.clear();
                    drops.add(new ItemStack(Material.DIRT));
                }
            }
        });
    }

    // Triggered when a player starts breaking a block
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
}