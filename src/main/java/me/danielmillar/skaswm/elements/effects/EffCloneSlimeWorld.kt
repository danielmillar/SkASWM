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
import com.infernalsuite.aswm.api.exceptions.*
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.io.IOException
import kotlin.system.measureTimeMillis

@Name("Clone Slime World")
@Description("Clone a Slime World based on a current world with a new name. Clones inherit all properties from the base world.")
@Examples(
	value = [
		"clone slime world based on \"Test\" named \"TestClone\""
	]
)
@Since("1.0.0")
class EffCloneSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffCloneSlimeWorld::class.java,
				"clone (slimeworld|slime world) based on %string% named %string%"
			)
		}
	}

	private lateinit var baseWorldName: Expression<String>
	private lateinit var clonedWorldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Clone slime world ${baseWorldName.toString(event, debug)} to ${clonedWorldName.toString(event, debug)}"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		baseWorldName = expressions[0] as Expression<String>
		clonedWorldName = expressions[1] as Expression<String>
		return true
	}

	override fun execute(event: Event) {
		val setupResult = setupEvent(event) ?: return

		val (player, slimeData) = setupResult
		val (slimeInstance, slimeLoader) = slimeData

		val baseWorldName = checkWorldName(event, baseWorldName, player) ?: return
		val cloneWorldName = checkWorldName(event, clonedWorldName, player) ?: return

		if (baseWorldName == cloneWorldName) {
			player?.sendMessage("Template world name cannot be the same one as the cloned world name!")
			Skript.error("Template world name cannot be the same one as the cloned world name!")
			return
		}

		val baseWorldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(baseWorldName)
		if (baseWorldData == null) {
			player?.sendMessage("World $baseWorldName cannot be found in config")
			Skript.error("World $baseWorldName cannot be found in config")
			return
		}

		val cloneWorldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(cloneWorldName)
		if (cloneWorldData != null) {
			player?.sendMessage("World $cloneWorldName already exists in config!")
			Skript.error("World $cloneWorldName already exists in config!")
			return
		}

		Bukkit.getScheduler().runTaskAsynchronously(SkASWM.getInstance(), Runnable AsyncTask@{
			try {
				val timeTaken = measureTimeMillis {
					val slimeWorld = slimeInstance.readWorld(
						slimeLoader,
						baseWorldName,
						baseWorldData.readOnly,
						baseWorldData.toPropertyMap()
					).clone(cloneWorldName, slimeLoader)

					Bukkit.getScheduler().runTask(SkASWM.getInstance(), Runnable SyncTask@{
						try {
							slimeInstance.loadWorld(slimeWorld, true)
							val bukkitWorld = Bukkit.getWorld(cloneWorldName)

							bukkitWorld?.let {
								SkASWM.getInstance().getConfigManager().getWorldConfig().setWorldProperties(
									baseWorldData,
									it
								)
							}

							SkASWM.getInstance().getConfigManager().getWorldConfig()
								.setWorldConfig(cloneWorldName, baseWorldData)
							SkASWM.getInstance().getConfigManager().saveWorldConfig()
						} catch (ex: Exception) {
							when (ex) {
								is UnknownWorldException, is IOException, is CorruptedWorldException, is NewerFormatException -> {
									player?.sendMessage("Failed to clone world $baseWorldName as $cloneWorldName. Check console for more information!")
									Skript.error("Failed to clone world $baseWorldName as $cloneWorldName: ${ex.message}")
								}

								else -> throw ex
							}
						}
					})
				}

				player?.sendMessage("World $cloneWorldName cloned based on $baseWorldName took $timeTaken ms!")
				Skript.info("World $cloneWorldName cloned based on $baseWorldName took $timeTaken ms!")
			} catch (ex: Exception) {
				when (ex) {
					is WorldAlreadyExistsException -> {
						player?.sendMessage("There is already a world called $cloneWorldName")
						Skript.error("There is already a world called $cloneWorldName")
						ex.printStackTrace()
					}

					is CorruptedWorldException -> {
						player?.sendMessage("Failed to load world $baseWorldName. World seems to be corrupted")
						Skript.error("Failed to load world $baseWorldName. World seems to be corrupted")
						ex.printStackTrace()
					}

					is NewerFormatException -> {
						player?.sendMessage("Failed to load world $baseWorldName. This world was serialized with a newer version of Slime Format that SWM can't understand")
						Skript.error("Failed to load world $baseWorldName. This world was serialized with a newer version of Slime Format that SWM can't understand")
						ex.printStackTrace()
					}

					is UnknownWorldException -> {
						player?.sendMessage("Failed to load world $baseWorldName. World cannot be found")
						Skript.error("Failed to load world $baseWorldName. World cannot be found")
						ex.printStackTrace()
					}

					is IOException, is IllegalArgumentException -> {
						player?.sendMessage("Failed to create world $baseWorldName. Check console for more information!")
						Skript.error("Failed to create world $baseWorldName. Check logger for more information")
						ex.printStackTrace()
					}
				}
			}
		})
	}
}