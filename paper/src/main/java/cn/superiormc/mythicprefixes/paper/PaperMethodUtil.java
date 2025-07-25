package cn.superiormc.mythicprefixes.paper;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.paper.utils.PaperTextUtil;
import cn.superiormc.mythicprefixes.utils.SpecialMethodUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaperMethodUtil implements SpecialMethodUtil {

    @Override
    public String methodID() {
        return "paper";
    }

    @Override
    public void dispatchCommand(String command) {
        if (MythicPrefixes.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(MythicPrefixes.instance, task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void dispatchCommand(Player player, String command) {
        if (MythicPrefixes.isFolia) {
            player.getScheduler().run(MythicPrefixes.instance, task -> Bukkit.dispatchCommand(player, command), () -> {
            });
            return;
        }
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public void dispatchOpCommand(Player player, String command) {
        if (MythicPrefixes.isFolia) {
            player.getScheduler().run(MythicPrefixes.instance, task -> {
                boolean playerIsOp = player.isOp();
                try {
                    player.setOp(true);
                    Bukkit.dispatchCommand(player, command);
                } finally {
                    player.setOp(playerIsOp);
                }
            }, () -> {
            });
            return;
        }
        boolean playerIsOp = player.isOp();
        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command);
        } finally {
            player.setOp(playerIsOp);
        }
    }

    @Override
    public void spawnEntity(Location location, EntityType entity) {
        if (MythicPrefixes.isFolia) {
            Bukkit.getRegionScheduler().run(MythicPrefixes.instance, location, task -> location.getWorld().spawnEntity(location, entity));
            return;
        }
        location.getWorld().spawnEntity(location, entity);
    }

    @Override
    public void playerTeleport(Player player, Location location) {
        if (MythicPrefixes.isFolia) {
            player.teleportAsync(location);
        } else {
            player.teleport(location);
        }
    }

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", skull));
        meta.setPlayerProfile(profile);
        return meta;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        if (PaperTextUtil.containsLegacyCodes(name)) {
            name = "<!i>" + name;
        }
        meta.displayName(PaperTextUtil.modernParse(name, player));
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<Component> veryNewLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\n")) {
                if (PaperTextUtil.containsLegacyCodes(singleLore)) {
                    singleLore = "<!i>" + singleLore;
                }
                veryNewLore.add(PaperTextUtil.modernParse(singleLore, player));
            }
        }
        if (!veryNewLore.isEmpty()) {
            meta.lore(veryNewLore);
        }
    }

    @Override
    public void sendMessage(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(PaperTextUtil.modernParse(text));
        } else {
            player.sendMessage(PaperTextUtil.modernParse(text, player));
        }
    }

    @Override
    public Inventory createNewInv(Player player, int size, String text) {
        return Bukkit.createInventory(player, size, PaperTextUtil.modernParse(text, player));
    }

    @Override
    public String legacyParse(String text) {
        if (text == null) {
            return "";
        }
        if (!ConfigManager.configManager.getBoolean("config-files.force-parse-mini-message")) {
            return TextUtil.colorize(text);
        }
        return LegacyComponentSerializer.legacySection().serialize(PaperTextUtil.modernParse(text));
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.displayName());
    }

}
