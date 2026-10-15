package com.alpha.ghostac.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired right before a ban, kick or command is applied. Cancelling it only stops the punishment,
 * the flag and the violation level stay. It can fire again on the next flag above the threshold.
 * Check isAsynchronous() before touching Bukkit.
 */
public class GhostPunishEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String checkName;
    private final String check;
    private final String category;
    private final String punishmentType;
    private final int violationLevel;
    private final int threshold;
    private final int attempt;
    private boolean cancelled;

    public GhostPunishEvent(Player player, String checkName, String check, String category, String punishmentType,
                            int violationLevel, int threshold, int attempt) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.checkName = checkName;
        this.check = check;
        this.category = category;
        this.punishmentType = punishmentType;
        this.violationLevel = violationLevel;
        this.threshold = threshold;
        this.attempt = attempt;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getCheck() {
        return check;
    }

    public String getCategory() {
        return category;
    }

    public String getPunishmentType() {
        return punishmentType;
    }

    public int getViolationLevel() {
        return violationLevel;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getAttempt() {
        return attempt;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
