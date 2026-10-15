package com.alpha.ghostac.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class GhostAPI {

    private GhostAPI() {
    }

    private static GhostAPIProvider provider() {
        RegisteredServiceProvider<GhostAPIProvider> registration =
                Bukkit.getServicesManager().getRegistration(GhostAPIProvider.class);
        return registration == null ? null : registration.getProvider();
    }

    @Deprecated
    public static void init(Plugin plugin) {
    }

    public static boolean isAvailable() {
        return provider() != null;
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("GhostAC");
    }

    public static String getVersion() {
        GhostAPIProvider provider = provider();
        return provider == null ? null : provider.getVersion();
    }

    public static void exemptPlayer(Player player, long durationMs, String reason, ExemptionType... types) {
        GhostAPIProvider provider = provider();
        if (provider != null) {
            provider.exemptPlayer(player, durationMs, reason, normalize(types));
        }
    }

    public static void exemptPlayer(Player player, long durationMs, ExemptionType... types) {
        exemptPlayer(player, durationMs, "External Plugin", types);
    }

    public static boolean isPlayerExempt(Player player, ExemptionType type) {
        return player != null && isPlayerExempt(player.getUniqueId(), type);
    }

    public static boolean isPlayerExempt(UUID uuid, ExemptionType type) {
        GhostAPIProvider provider = provider();
        return provider != null && uuid != null
                && provider.isPlayerExempt(uuid, type == null ? ExemptionType.ALL : type);
    }

    public static ExemptionData getExemptionData(Player player) {
        return player == null ? null : getExemptionData(player.getUniqueId());
    }

    public static ExemptionData getExemptionData(UUID uuid) {
        GhostAPIProvider provider = provider();
        return provider == null || uuid == null ? null : provider.getExemptionData(uuid);
    }

    public static void removeExemption(Player player) {
        if (player != null) {
            removeExemption(player.getUniqueId());
        }
    }

    public static void removeExemption(UUID uuid) {
        GhostAPIProvider provider = provider();
        if (provider != null && uuid != null) {
            provider.removeExemption(uuid);
        }
    }

    public static void clearAllExemptions() {
        GhostAPIProvider provider = provider();
        if (provider != null) {
            provider.clearAllExemptions();
        }
    }

    public static void cleanupExpired() {
        GhostAPIProvider provider = provider();
        if (provider != null) {
            provider.cleanupExpired();
        }
    }

    public static int getViolationLevel(Player player, String checkName) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null || checkName == null ? 0
                : provider.getViolationLevel(player, checkName);
    }

    public static int getTotalViolationLevel(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? 0 : provider.getTotalViolationLevel(player);
    }

    public static Map<String, Integer> getViolationLevels(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? Collections.emptyMap() : provider.getViolationLevels(player);
    }

    public static Map<String, Integer> getCategoryViolationLevels(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? Collections.emptyMap()
                : provider.getCategoryViolationLevels(player);
    }

    public static Set<String> getCheckNames() {
        GhostAPIProvider provider = provider();
        return provider == null ? Collections.emptySet() : provider.getCheckNames();
    }

    public static void exemptChecks(Player player, long durationMs, String... checkNames) {
        GhostAPIProvider provider = provider();
        if (provider != null && player != null && checkNames != null && checkNames.length > 0) {
            provider.exemptChecks(player, durationMs, checkNames);
        }
    }

    public static boolean isCheckExempt(Player player, String checkName) {
        return player != null && isCheckExempt(player.getUniqueId(), checkName);
    }

    public static boolean isCheckExempt(UUID uuid, String checkName) {
        GhostAPIProvider provider = provider();
        return provider != null && uuid != null && checkName != null && provider.isCheckExempt(uuid, checkName);
    }

    public static void removeCheckExemptions(Player player) {
        if (player != null) {
            removeCheckExemptions(player.getUniqueId());
        }
    }

    public static void removeCheckExemptions(UUID uuid) {
        GhostAPIProvider provider = provider();
        if (provider != null && uuid != null) {
            provider.removeCheckExemptions(uuid);
        }
    }

    public static double getTrustScore(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? 0.0 : provider.getTrustScore(player);
    }

    public static int getPing(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? 0 : provider.getPing(player);
    }

    public static String getClientType(Player player) {
        GhostAPIProvider provider = provider();
        return provider == null || player == null ? null : provider.getClientType(player);
    }

    private static ExemptionType[] normalize(ExemptionType... types) {
        if (types == null || types.length == 0) {
            return new ExemptionType[] {ExemptionType.MOVEMENT};
        }
        return types;
    }

    public static class ExemptionData {
        public final long expiry;
        public final String reason;
        public final ExemptionType[] types;

        public ExemptionData(long expiry, String reason, ExemptionType[] types) {
            this.expiry = expiry;
            this.reason = reason;
            this.types = types;
        }

        public long getRemainingMs() {
            return Math.max(0, expiry - System.currentTimeMillis());
        }

        public boolean isExpired() {
            return expiry < System.currentTimeMillis();
        }
    }

    public enum ExemptionType {
        MOVEMENT,
        COMBAT,
        BLOCK,
        ALL
    }
}
