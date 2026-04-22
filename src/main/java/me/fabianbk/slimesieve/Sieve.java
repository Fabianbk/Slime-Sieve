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

    // ตัวแปรไอเทมแร่ (เชื่อมจากคลาสที่นาย Register ไว้)
    private final ItemStack goldPiece, ironPiece, copperPiece, aluminiumPiece, leadPiece, silverPiece;

    public Sieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                 ItemStack goldPiece, ItemStack ironPiece, ItemStack copperPiece,
                 ItemStack aluminiumPiece, ItemStack leadPiece, ItemStack silverPiece) {
        super(itemGroup, item, recipeType, recipe);
        this.goldPiece = goldPiece;
        this.ironPiece = ironPiece;
        this.copperPiece = copperPiece;
        this.aluminiumPiece = aluminiumPiece;
        this.leadPiece = leadPiece;
        this.silverPiece = silverPiece;
    }

    @Override
    public void preRegister() {
        // 1. ดักจับตอนคลิกขวา (ของเดิมของนาย)
        addItemHandler((BlockUseHandler) this::onBlockRightClick);

        // 2. เพิ่มตัวดักจับตอนทุบบล็อก (เพื่อคืน Mesh)
        addItemHandler(new io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(org.bukkit.event.block.BlockBreakEvent event, ItemStack item, List<ItemStack> drops) {
                Block b = event.getBlock();
                String meshTier = BlockStorage.getLocationInfo(b.getLocation(), "mesh_tier");

                // ถ้ามี Mesh เสียบอยู่ ให้เอา Mesh ของ Slimefun ใส่รวมในกองไอเทมที่จะดรอป
                if (meshTier != null) {
                    SlimefunItem meshItem = null;

                    // ดึงไอเทมจาก ID ของ Slimefun ตามระดับของ Mesh
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

                    // ถ้าหาไอเทมเจอ ให้ทำการดรอปไอเทมนั้นคืนมา
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

        // 1. เช็คว่ามี Mesh หรือยัง (ถ้ายังไม่มี ถึงจะยอมให้ใส่ตาข่าย)
        if (meshTier == null) {

            // ดึงข้อมูล SlimefunItem จากของที่ถือในมือ
            SlimefunItem sfItemInHand = SlimefunItem.getByItem(itemInHand);

            // เช็คว่าเป็นไอเทม Slimefun ไหม และ ID ตรงกับ Mesh ของเราหรือเปล่า
            if (sfItemInHand != null) {
                String itemId = sfItemInHand.getId();

                if (itemId.equals("STRING_MESH") || itemId.equals("FLINT_MESH") ||
                        itemId.equals("IRON_MESH") || itemId.equals("DIAMOND_MESH")) {

                    if (p.getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }

                    // แปลงชื่อจาก STRING_MESH ให้เหลือแค่ STRING เพื่อเซฟลงบล็อก
                    String tierToSave = itemId.replace("_MESH", "");

                    BlockStorage.addBlockInfo(clickedBlock.getLocation(), "mesh_tier", tierToSave);
                    p.playSound(clickedBlock.getLocation(), Sound.BLOCK_WOOL_PLACE, 1f, 1f);
                    p.sendMessage("§aInstalled " + tierToSave + " Mesh!");
                }
            }
            return; // ถ้าไม่มี Mesh หรือถือของผิด ก็จบการทำงานตรงนี้เลย
        }

        // 2. ลอจิกร่อนแร่ (ของเดิม)
        if (meshTier.equals("STRING")) {
            boolean isGravel = itemInHand.getType() == Material.GRAVEL;
            boolean isSand = itemInHand.getType() == Material.SAND;

            if (isGravel || isSand) {
                if (p.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                startSiftingProcess(p, clickedBlock, isGravel);
            }
        }
        // เดี๋ยวอนาคตถ้านายเพิ่มเรตของ FLINT, IRON ก็มาเพิ่ม else if (meshTier.equals("FLINT")) ต่อตรงนี้ได้เลย
    }

    private void startSiftingProcess(Player p, Block clickedBlock, boolean isGravel) {
        // เล่น Effect ตอนกำลังร่อน (Repeating Task)
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Slimefun.instance(),
                () -> clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, clickedBlock.getType()),
                0L, 10L // ปรับให้ถี่ขึ้นนิดนึง ทุก 0.5 วินาที
        );

        // รอ 5 วินาทีแล้วดรอปของ
        Bukkit.getScheduler().scheduleSyncDelayedTask(Slimefun.instance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);

            // สร้างรายการไอเทมที่จะดรอป (Independent Rolls)
            List<ItemStack> outputs = generateOutputs(isGravel);
            Location dropLoc = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

            if (!outputs.isEmpty()) {
                for (ItemStack out : outputs) {
                    // ระบบ OutputChest เดิมของนาย
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

    private List<ItemStack> generateOutputs(boolean isGravel) {
        List<ItemStack> results = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // ใส่เรตตามที่นายบอกมาเป๊ะๆ (Independent Rolls)
        if (random.nextDouble() <= 0.03) results.add(goldPiece);
        if (random.nextDouble() <= 0.20) results.add(ironPiece);
        if (random.nextDouble() <= 0.08) results.add(copperPiece);
        if (random.nextDouble() <= 0.05) results.add(aluminiumPiece);
        if (random.nextDouble() <= 0.03) results.add(leadPiece);
        if (random.nextDouble() <= 0.04) results.add(silverPiece);

        // ถ้าเป็นกรวด มีโอกาสได้ Flint เพิ่ม
        if (isGravel && random.nextDouble() <= 0.18) {
            results.add(new ItemStack(Material.FLINT));
        }

        return results;
    }
}