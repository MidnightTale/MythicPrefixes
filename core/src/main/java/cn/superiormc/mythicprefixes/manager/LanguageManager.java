package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LanguageManager {

    public static LanguageManager languageManager;

    private YamlConfiguration messageFile;

    private YamlConfiguration tempMessageFile;

    private File file;

    private File tempFile;

    public LanguageManager() {
        languageManager = this;
        initLanguage();
    }

    private void initLanguage() {
        this.file = new File(MythicPrefixes.instance.getDataFolder() + "/languages/" + ConfigManager.configManager.getString("config-files.language", "en_US") + ".yml");
        if (!file.exists()){
            this.file = new File(MythicPrefixes.instance.getDataFolder(), "message.yml");
            if (!file.exists()) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cWe can not found your message file, " +
                        "please try restart your server!");
            }
        }
        else {
            this.messageFile = YamlConfiguration.loadConfiguration(file);
        }
        InputStream is = MythicPrefixes.instance.getResource("languages/en_US.yml");
        if (is == null) {
            return;
        }
        this.tempFile = new File(MythicPrefixes.instance.getDataFolder(), "tempMessage.yml");
        try {
            Files.copy(is, tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tempMessageFile = YamlConfiguration.loadConfiguration(tempFile);
        this.tempFile.delete();
    }

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            sendStringText((Player) sender, args);
        }
        else {
            sendStringText(args);
        }
    }

    public void sendStringText(String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cCan not found language key: " + args[0] + "!");
                return;
            }
            else {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        if (text.length() != 0) {
            MythicPrefixes.methodUtil.sendMessage(null, text);
        }
    }

    public void sendStringText(Player player, String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                player.sendMessage(TextUtil.pluginPrefix() + " §cCan not found language key: " + args[0] + "!");
                return;
            }
            else {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        if (text.length() != 0) {
            MythicPrefixes.methodUtil.sendMessage(player, text);
        }
    }

    public String getStringText(String path) {
        if (this.messageFile.getString(path) == null) {
            if (this.tempMessageFile.getString(path) == null) {
                return "§cCan not found language key: " + path + "!";
            }
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                    "new language key and it's default value: " + path + "!");
            messageFile.set(path, this.tempMessageFile.getString(path));
            try {
                messageFile.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this.tempMessageFile.getString(path);
        }
        return this.messageFile.getString(path);
    }

}
