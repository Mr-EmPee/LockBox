package ml.empee.lockbox.events.handlers;

import lombok.RequiredArgsConstructor;
import ml.empee.ioc.Bean;
import ml.empee.ioc.RegisteredListener;
import ml.empee.lockbox.model.Vault;
import ml.empee.lockbox.services.VaultService;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

@RequiredArgsConstructor
public class VaultProtectionListener implements RegisteredListener, Bean {

  private final VaultService vaultService;

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultExplode(EntityExplodeEvent event) {
    event.blockList().removeIf(block -> vaultService.getVaultAt(block).isPresent());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultExplode(BlockExplodeEvent event) {
    event.blockList().removeIf(block -> vaultService.getVaultAt(block).isPresent());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultMove(BlockPistonExtendEvent event) {
    boolean isMovingVault = event.getBlocks().stream().anyMatch(block -> vaultService.getVaultAt(block).isPresent());
    if(isMovingVault) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultMove(BlockPistonRetractEvent event) {
    boolean isMovingVault = event.getBlocks().stream().anyMatch(block -> vaultService.getVaultAt(block).isPresent());
    if(isMovingVault) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultRemove(EntityChangeBlockEvent event) {
    if(vaultService.getVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultRemove(BlockFadeEvent event) {
    if(vaultService.getVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVaultRemove(BlockBurnEvent event) {
    if(vaultService.getVaultAt(event.getBlock()).isPresent()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDecorationBreak(HangingBreakEvent event) {
    Hanging entity = event.getEntity();
    BlockFace attachedFace = entity.getAttachedFace();
    Location vaultLoc = entity.getLocation().getBlock().getLocation();
    vaultLoc.add(attachedFace.getModX(), attachedFace.getModY(), attachedFace.getModZ());
    Vault vault = vaultService.getVaultAt(vaultLoc.getBlock()).orElse(null);
    if(vault == null) {
      return;
    }

    if(vault.isDecoration(entity)) {
      event.setCancelled(true);
    }
  }

}
