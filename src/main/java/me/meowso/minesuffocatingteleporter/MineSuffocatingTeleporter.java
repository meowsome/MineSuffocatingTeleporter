package me.meowso.minesuffocatingteleporter;

import org.bukkit.plugin.java.JavaPlugin;

public final class MineSuffocatingTeleporter extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }
}
