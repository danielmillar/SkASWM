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
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.Event
import java.io.IOException
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

@Name("Delete Slime World")
@Description("Delete a Slime World with a name")
@Examples("delete slime world named \"Test\"")
@Since("1.0.0")
class EffDeleteSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffDeleteSlimeWorld::class.java,
				"delete (slimeworld|slime world) named %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world delete"
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

		val (player, slimeData) = setupResult
		val (slimePlugin, slimeLoader) = slimeData

		val worldName = checkWorldName(event, worldName, player) ?: return

		if(!SkASWM.getInstance().getConfigManager().getWorldConfig().hasWorldConfig(worldName)){
			player?.sendMessage("Can't find world $worldName is config!")
			player?.sendMessage("Can't find world $worldName is config!")
			return
		}

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld != null) {
			player?.sendMessage("World $worldName is loaded, can't delete. Try unload world first")
			Skript.error("World $worldName is loaded, can't delete. Try unload world first")
			return
		}

		Bukkit.getScheduler().runTaskAsynchronously(SkASWM.getInstance(), Runnable {
			try {
				val timeTaken = measureTimeMillis {
					slimeLoader.deleteWorld(worldName)

					SkASWM.getInstance().getConfigManager().getWorldConfig().removeWorldConfig(worldName)
					SkASWM.getInstance().getConfigManager().saveWorldConfig()
				}

				player?.sendMessage("Successfully deleted world $worldName within $timeTaken ms!")
				Skript.info("Successfully deleted world $worldName within $timeTaken ms!")
			} catch (ex: Exception) {
				when(ex){
					is IOException -> {
						player?.sendMessage("Failed to delete world $worldName. Check console for more information!")
						Skript.error("Failed to delete world $worldName. Check logger for more information")
					}

					is UnknownWorldException -> {
						player?.sendMessage("Datasource doesn't contain any world called $worldName")
						player?.sendMessage("Datasource doesn't contain any world called $worldName")
					}
				}
			}

		})
	}
}