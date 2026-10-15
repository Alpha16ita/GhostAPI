package com.alpha.ghostac.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GhostFlagEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String checkName;
    private final String check;
    private final String category;
    private final String info;
    private final int violationLevel;
    private final double confidence;
    private boolean cancelled;

    public GhostFlagEvent(Player player, String checkName, String check, String category, String info,
                          int violationLevel, double confidence) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.checkName = checkName;
        this.check = check;
        this.category = category;
        this.info = info;
        this.violationLevel = violationLevel;
        this.confidence = confidence;
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

    public String getInfo() {
        return info;
    }

    public int getViolationLevel() {
        return violationLevel;
    }

    public double getConfidence() {
        return confidence;
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
