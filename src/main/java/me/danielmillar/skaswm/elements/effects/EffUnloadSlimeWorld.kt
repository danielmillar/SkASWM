package me.danielmillar.skaswm.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.findValidDefaultSpawn
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture

@Name("Unload Slime World")
@Description("Unload a Slime World with a name")
@Examples("unload slime world named \"Test\"")
@Since("1.0.0")
class EffUnloadSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffUnloadSlimeWorld::class.java,
				"unload (slimeworld|slime world) named %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world unload"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		worldName = expressions[0] as Expression<String>
		return true
	}

	override fun execute(event: Event) {
		val setupResult = setupEvent(event) ?: return

		val (player) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld == null) {
			player?.sendMessage("World $worldName isn't loaded, can't unload!")
			Skript.error("World $worldName isn't loaded, can't unload!")
			return
		}

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return
		}

		val playersInWorld = bukkitWorld.players
		if (playersInWorld.isEmpty()) {
			Bukkit.unloadWorld(bukkitWorld, true);
			player?.sendMessage("World $worldName unloaded successfully")
			Skript.info("World $worldName unloaded successfully")
			return
		}

		val spawnLocation = findValidDefaultSpawn()
		val futures = playersInWorld.map { it.teleportAsync(spawnLocation) }
		val allOfFuture = CompletableFuture.allOf(*futures.toTypedArray())

		allOfFuture.thenRun {
			val success: Boolean =
				if (worldData.readOnly) Bukkit.unloadWorld(bukkitWorld, false) else Bukkit.unloadWorld(
					bukkitWorld,
					true
				)

			if (!success) {
				player?.sendMessage("World $worldName failed to unload")
				Skript.info("World $worldName failed to unload")
			} else {
				if (!worldData.readOnly) {
					SkASWM.getInstance().getConfigManager().getWorldConfig()
						.updateWorldProperties(worldName, worldData, bukkitWorld)
					SkASWM.getInstance().getConfigManager().saveWorldConfig()
				}
				player?.sendMessage("World $worldName successfully to unload")
				Skript.info("World $worldName successfully to unload")
			}
		}
	}
}