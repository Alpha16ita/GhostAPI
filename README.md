# GhostAC API

[![Download on SpigotMC](https://img.shields.io/badge/Download-SpigotMC-ED8106?style=for-the-badge&logo=spigotmc&logoColor=white)](https://www.spigotmc.org/resources/nova-anticheat-java-bedrock-geyser-support.134592/)
[![Join the Discord](https://img.shields.io/badge/Discord-Join-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/zdvaKxjShz)

Public API for GhostAC. It contains only interfaces, a static facade and events, no detection code.
GhostAC itself must be installed on the server for the API to work.

## Requirements

Java 21 or higher.

API 1.1.0 needs GhostAC 1.104.00 or newer on the server. With an older GhostAC the new events and methods are not available and calling them throws a `NoSuchMethodError`. Check `GhostAPI.getVersion()` if your plugin has to support both.

## Add the dependency (JitPack)

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Alpha16ita</groupId>
        <artifactId>GhostAPI</artifactId>
        <version>1.1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle (Kotlin DSL):

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Alpha16ita:GhostAPI:1.1.0")
}
```

Add `depend: [GhostAC]` (or `softdepend`) to your `plugin.yml`.

## Listening to detections

```java
import com.alpha.ghostac.api.event.GhostFlagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlagListener implements Listener {

    @EventHandler
    public void onFlag(GhostFlagEvent event) {
        String player = event.getPlayer().getName();
        String check = event.getCheckName();
        int vl = event.getViolationLevel();
        String info = event.getInfo();
    }
}
```

- The event can run asynchronously. `event.isAsynchronous()` tells you which thread you are on.
  Do not touch the world or Bukkit entities from an async handler. Schedule that work on the main thread.
- `event.setCancelled(true)` suppresses this flag completely: no violation level, no alert and no punishment.
  If you only want to observe detections, do not cancel.
- `getViolationLevel()` on this event is the level the flag would reach, the current one plus one. It has not been recorded yet.

## Punishment event

```java
@EventHandler
public void onPunish(GhostPunishEvent event) {
    String type = event.getPunishmentType();
    int vl = event.getViolationLevel();
    int threshold = event.getThreshold();
    int attempt = event.getAttempt();
}
```

- Fired right before GhostAC applies a ban, kick or command.
- `event.setCancelled(true)` stops that punishment. The event can fire again on the next flag above the threshold.

## Post-flag event

`GhostFlagPostEvent` is fired after the violation has been recorded. It cannot be cancelled and is meant for observers.
It exposes the same getters as `GhostFlagEvent`, with the violation level already updated to the recorded value.

## Exemptions

```java
if (GhostAPI.isAvailable()) {
    GhostAPI.exemptPlayer(player, 3000L, "MyPlugin", GhostAPI.ExemptionType.MOVEMENT);
    GhostAPI.exemptPlayer(player, 10000L, "MyPlugin", GhostAPI.ExemptionType.COMBAT);
    GhostAPI.exemptChecks(player, 3000L, "speed", "reach");
    boolean exempt = GhostAPI.isPlayerExempt(player, GhostAPI.ExemptionType.COMBAT);
    boolean checkExempt = GhostAPI.isCheckExempt(player, "speed");
    GhostAPI.ExemptionData data = GhostAPI.getExemptionData(player);
    GhostAPI.removeExemption(player);
}
```

- `ExemptionType` can be `MOVEMENT`, `COMBAT`, `BLOCK` or `ALL`. Duration is in milliseconds.
- Each type is tracked on its own. Calling `exemptPlayer` again adds the new types to the existing ones
  instead of replacing them, and for a type that is already exempt the longest expiry is kept.
  The example above leaves the player MOVEMENT-exempt for 3 seconds and COMBAT-exempt for 10 seconds.
- `MOVEMENT` skips movement processing for the player.
- `COMBAT` suppresses combat flags, so no violation level, alert or punishment is produced.
- `BLOCK` suppresses block flags in the same way.
- `ALL` covers every type and every category.
- `exemptChecks` suppresses flags of single checks by name. Names are case-insensitive.
- `removeExemption` removes the player's type exemptions and check exemptions. `removeCheckExemptions` removes only the check ones.
- `getExemptionData` returns the active types, the longest remaining expiry and the reason of that exemption, or `null` if there is none.
- `clearAllExemptions` drops every exemption for every player. `cleanupExpired` removes expired entries, GhostAC also ignores them on its own.

Exemption merging and the `BLOCK` type need GhostAC 1.107.40 or newer. Older versions replace the previous exemption
and only apply `BLOCK` to some block checks.

## Violation data

```java
Set<String> checks = GhostAPI.getCheckNames();
int speedVl = GhostAPI.getViolationLevel(player, "Speed");
int totalVl = GhostAPI.getTotalViolationLevel(player);
Map<String, Integer> perCheck = GhostAPI.getViolationLevels(player);
Map<String, Integer> perCategory = GhostAPI.getCategoryViolationLevels(player);
```

## Player data

```java
double trust = GhostAPI.getTrustScore(player);
int ping = GhostAPI.getPing(player);
String client = GhostAPI.getClientType(player);
String version = GhostAPI.getVersion();
Plugin ghost = GhostAPI.getPlugin();
```

`GhostAPI.init(Plugin)` is deprecated and does nothing, you do not need to call it.
