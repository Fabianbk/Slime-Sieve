package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler; // Changed to ItemUseHandler
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Implement NotPlaceable to prevent players from placing the string on the ground
public class Silkworm extends SlimefunItem implements NotPlaceable {

    public Silkworm(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        // Use ItemUseHandler instead of BlockUseHandler for NotPlaceable items
        ItemUseHandler itemUseHandler = this::onItemRightClick;
        addItemHandler(itemUseHandler);
    }

    private void onItemRightClick(PlayerRightClickEvent event) {
        // Cancel the event so it doesn't place the string or trigger vanilla stuff
        event.cancel();

        Block b = event.getClickedBlock().orElse(null);
        if (b == null) return;

        // Check if the clicked block is any type of leaf
        if (Tag.LEAVES.isTagged(b.getType())) {
            Player p = event.getPlayer();
            ItemStack itemInHand = p.getInventory().getItemInMainHand();

            // Consume the Silkworm item
            if (p.getGameMode() != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }

            // Play sound and visual effect
            p.playSound(b.getLocation(), Sound.ENTITY_SILVERFISH_STEP, 1f, 1f);
            b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.COBWEB);

            // Delay the transformation to simulate spreading
            Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
                // Verify it is still a leaf block before transforming it
                if (Tag.LEAVES.isTagged(b.getType())) {
                    b.setType(Material.COBWEB);
                    b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.COBWEB);
                }
            }, 60L); // 3 seconds delay
        }
    }
}