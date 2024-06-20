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
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event

@Name("Save Slime World")
@Description("Save a Slime World with a specified name.")
@Examples(
	value = [
		"save slime world named \"Test\""
	]
)
@Since("1.0.0")
class EffSaveSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffSaveSlimeWorld::class.java,
				"save (slimeworld|slime world) named %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Save slime world ${worldName.toString(event, debug)}"
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

		val (player, _) = setupResult

		val worldName = checkWorldName(event, worldName, player) ?: return

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld == null) {
			player?.sendMessage("World $worldName is not loaded!")
			Skript.error("World $worldName is not loaded!")
			return
		}

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if (worldData == null) {
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return
		}

		if (worldData.readOnly) {
			player?.sendMessage("World $worldName readOnly property is true, can't save")
			Skript.warning("World $worldName readOnly property is true, can't save")
			return
		}

		bukkitWorld.save()
		SkASWM.getInstance().getConfigManager().getWorldConfig()
			.updateWorldProperties(worldName, worldData, bukkitWorld)
	}
}