package papper.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

class CommandListener implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            SneakListener.uuids.add(((Player) sender).getUniqueId());
            SneakListener.counter = 0;

            return true;
        }
        sender.sendMessage("This command can be used only by player");

        return false;
    }
}

class SneakListener implements Listener {
    public static Set<UUID> uuids = new HashSet<>();
    public static int counter = 0;

    private int shiftMaxValue = 5;

    private int soundsStandardVolume = 10;
    private int soundsStandardPitch = 0;
    private int soundsMutedVolume = 5;

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event){

        Player player = event.getPlayer();

        if(uuids.contains(player.getUniqueId())){
            if(counter==shiftMaxValue){
                player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, soundsStandardVolume, soundsStandardPitch);

                Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(SexPlugin.class), () -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, soundsMutedVolume, soundsStandardPitch);

                    Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    FireworkEffect.Type type = FireworkEffect.Type.STAR;
                    FireworkEffect effect = FireworkEffect.builder().with(type).withColor(Color.WHITE).build();
                    fireworkMeta.addEffect(effect);

                    fireworkMeta.setPower(0);
                    firework.setFireworkMeta(fireworkMeta);

                    firework.detonate();

                    uuids.remove(player.getUniqueId());
                }, 30);
                return;
            }
            if(player.isSneaking()){
                player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, soundsStandardVolume, soundsStandardPitch);
            }else{
                counter++;
                player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, soundsStandardVolume, soundsStandardPitch);
            }
        }else{
            return;
        }
    }
}

public final class SexPlugin extends JavaPlugin implements CommandExecutor{
    @Override
    public void onEnable(){
        Objects.requireNonNull(this.getCommand("sex")).setExecutor(new CommandListener());
        getServer().getPluginManager().registerEvents(new SneakListener(), this);
    }
}

