# GhostAC API

Public API for GhostAC. It contains only interfaces, a static facade and events, no detection code.
GhostAC itself must be installed on the server for the API to work.

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
It exposes the same getters as `GhostFlagEvent`, with the violation level already updated.

## Exemptions

```java
if (GhostAPI.isAvailable()) {
    GhostAPI.exemptPlayer(player, 3000L, "MyPlugin", GhostAPI.ExemptionType.MOVEMENT);
    GhostAPI.exemptPlayer(player, 3000L, "MyPlugin", GhostAPI.ExemptionType.COMBAT);
    GhostAPI.exemptChecks(player, 3000L, "speed", "reach");
    boolean exempt = GhostAPI.isPlayerExempt(player, GhostAPI.ExemptionType.COMBAT);
    boolean checkExempt = GhostAPI.isCheckExempt(player, "speed");
    GhostAPI.removeExemption(player);
}
```

- `ExemptionType` can be `MOVEMENT`, `COMBAT`, `BLOCK` or `ALL`.
- `COMBAT` suppresses combat flags, so no violation level, alert or punishment is produced.
- `exemptChecks` suppresses flags of single checks by name. Names are case-insensitive. `removeExemption` also removes check exemptions.

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
```
