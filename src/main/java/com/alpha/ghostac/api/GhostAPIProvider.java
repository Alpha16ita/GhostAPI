package com.alpha.ghostac.api;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GhostAPIProvider {

    String getVersion();

    void exemptPlayer(Player player, long durationMs, String reason, GhostAPI.ExemptionType... types);

    boolean isPlayerExempt(UUID uuid, GhostAPI.ExemptionType type);

    GhostAPI.ExemptionData getExemptionData(UUID uuid);

    void removeExemption(UUID uuid);

    void clearAllExemptions();

    void cleanupExpired();

    int getViolationLevel(Player player, String checkName);

    int getTotalViolationLevel(Player player);

    Map<String, Integer> getViolationLevels(Player player);

    Map<String, Integer> getCategoryViolationLevels(Player player);

    Set<String> getCheckNames();

    void exemptChecks(Player player, long durationMs, String... checkNames);

    boolean isCheckExempt(UUID uuid, String checkName);

    void removeCheckExemptions(UUID uuid);

    double getTrustScore(Player player);

    int getPing(Player player);

    String getClientType(Player player);
}
