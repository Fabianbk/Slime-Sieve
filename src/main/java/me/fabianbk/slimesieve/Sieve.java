package me.fabianbk.slimesieve;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
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
import java.util.Optional;
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
        Optional<Block> clickedBlockOpt = event.getClickedBlock();

        if (!clickedBlockOpt.isPresent()) {
            return;
        }

        Block clickedBlock = clickedBlockOpt.get();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        // Validate input material (only GRAVEL for now)
        if (itemInHand.getType() != Material.GRAVEL) {
            return;
        }

        // Determine output based on random chance
        ItemStack output = generateOutput();

        // Fire MultiBlockCraftEvent for cancellation/modification by other plugins
        MultiBlockCraftEvent craftEvent = new MultiBlockCraftEvent(p, null, itemInHand, output);
        Bukkit.getPluginManager().callEvent(craftEvent);

        if (craftEvent.isCancelled()) {
            return;
        }

        ItemStack finalOutput = craftEvent.getOutput();

        // Consume the input item (decrease stack by 1)
        if (p.getGameMode() != GameMode.CREATIVE) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }

        // Schedule animation and output using Bukkit scheduler
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
            Slimefun.instance(),
            () -> clickedBlock.getWorld().playEffect(
                clickedBlock.getLocation(),
                Effect.STEP_SOUND,
                Material.GRAVEL
            ),
            0L,           // Initial delay in ticks
            20L           // Repeat every 20 ticks (1 second)
        );

        // After 5 seconds (100 ticks), stop animation and deliver output
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            Slimefun.instance(),
            () -> {
                // Cancel the repeating animation task
                Bukkit.getScheduler().cancelTask(taskId);

                Location dropLocation = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

                if (finalOutput.getType() != Material.AIR) {
                    // Try to find an output chest below the sieve block
                    Optional<Inventory> outputChest = OutputChest.findOutputChestFor(
                        clickedBlock.getRelative(BlockFace.DOWN),
                        finalOutput
                    );

                    if (outputChest.isPresent()) {
                        // Place output in chest
                        outputChest.get().addItem(finalOutput.clone());
                    } else {
                        // Drop naturally if no chest found
                        clickedBlock.getWorld().dropItemNaturally(dropLocation, finalOutput.clone());
                    }

                    // Success sound
                    p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1.0f, 1.5f);
                } else {
                    // Fail sound if no output
                    p.playSound(clickedBlock.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1.0f, 0.5f);
                }
            },
            100L          // Delay 100 ticks (5 seconds) before executing
        );
    }

    /**
     * Generate random output based on probability.
     * Currently gives IRON_DUST with 25% chance, otherwise AIR.
     */
    private ItemStack generateOutput() {
        double randomChance = ThreadLocalRandom.current().nextDouble();

        if (randomChance <= 0.25) {
            return SlimefunItems.IRON_DUST.clone();
        }

        return new ItemStack(Material.AIR);
    }
}