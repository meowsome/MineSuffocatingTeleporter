package me.meowso.minesuffocatingteleporter;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Arrays;
import java.util.List;

public class Listeners implements Listener {
    private final List<String> validRegionNames = Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","wyrm","wyvern","hydra","prestige_i","prestige_ii","prestige_iii","pvp1","pvp2","pvp2","pvp3");
    private final Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws InvalidWorldException, WarpNotFoundException {
        handleCheck(event.getPlayer());
    }

    public void handleCheck(Player player) throws InvalidWorldException, WarpNotFoundException {
        //Check if player is suffocating (block at and 1 block above head is not air)
        if (isPlayerSuffocating(player.getEyeLocation())) {
            //Teleport to specified location
            player.teleport(getLocationToTeleportTo(getMineRegionAtLocation(getRegions(BukkitAdapter.adapt(player.getLocation())))));
        }
    }

    public ApplicableRegionSet getRegions(com.sk89q.worldedit.util.Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(location);
    }

    public boolean isPlayerSuffocating(Location eyeLocation) {
        Location aboveEyeLocation = eyeLocation.clone();
        aboveEyeLocation.setY(eyeLocation.getY() + 1);
        
        return checkBlockIsNotAir(eyeLocation) || checkBlockIsNotAir(aboveEyeLocation);
    }

    public boolean checkBlockIsNotAir(Location location) {
        return !location.getBlock().getType().equals(Material.AIR);
    }

    public ProtectedRegion getMineRegionAtLocation(ApplicableRegionSet regions) {
        ProtectedRegion mineRegion = null;
        if (regions.size() > 0) {
            for (ProtectedRegion region : regions) {
                if (validRegionNames.contains(region.getId())) {
                    mineRegion = region;
                    break;
                }
            }
        }

        return mineRegion;
    }

    public Location getLocationToTeleportTo(ProtectedRegion mineRegion) throws InvalidWorldException, WarpNotFoundException {
        if (mineRegion != null) {
            //Get the location of the warp to teleport to. Take special cases, like pvp and prestige, into mind
            if (mineRegion.getId().contains("pvp")) return ess.getWarps().getWarp("pvp");
            if (mineRegion.getId().equals("prestige_i")) return ess.getWarps().getWarp("grunt");
            if (mineRegion.getId().equals("prestige_ii")) return ess.getWarps().getWarp("executive");
            if (mineRegion.getId().equals("prestige_iii")) return ess.getWarps().getWarp("boss");
            else return ess.getWarps().getWarp(mineRegion.getId().toLowerCase());
        } else {
            //Get the location of the spawn
            return Bukkit.getWorld("Prison").getSpawnLocation();
        }
    }
}