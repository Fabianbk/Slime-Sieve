package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems; // นำเข้าไอเทมของ Slimefun
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.concurrent.ThreadLocalRandom;

public class Sieve extends SlimefunItem {
    public Sieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        BlockUseHandler blockUseHandler = this::onBlockRightClick;
        addItemHandler(blockUseHandler);
    }

    private void onBlockRightClick(PlayerRightClickEvent event) {
        event.cancel();

        Player p = event.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() == Material.GRAVEL) {

            itemInHand.setAmount(itemInHand.getAmount() - 1);

            p.playSound(p.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1.0f, 1.0f);

            Location dropLocation = event.getClickedBlock().get().getLocation().add(0.5, 1.0, 0.5);

            double randomChance = ThreadLocalRandom.current().nextDouble(); // Random number 0.00 to 0.99

            if (randomChance <= 0.25) {
                ItemStack ironDrop = SlimefunItems.IRON_DUST.clone();

                dropLocation.getWorld().dropItemNaturally(dropLocation, ironDrop);
            }
        }
    }
}