package me.danielmillar.skaswm.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.command.EffectCommandEvent
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException
import com.infernalsuite.aswm.api.exceptions.NewerFormatException
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException
import com.infernalsuite.aswm.api.exceptions.WorldLockedException
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimeLoader
import me.danielmillar.skaswm.elements.effects.EffInitializeSlime.Companion.getSlimePlugin
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.io.IOException
import kotlin.system.measureTimeMillis

@Name("Load Slime World")
@Description("Load a new Slime World with a name")
@Examples("load slime world with name \"Test\"")
@Since("1.0.0")
class EffLoadSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffLoadSlimeWorld::class.java,
				"load slime world with name %string%"
			)
		}
	}

	private lateinit var worldName: Expression<String>

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world load"
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

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld != null) {
			player?.sendMessage("World $worldName is already loaded!")
			Skript.error("World $worldName is already loaded!")
			return
		}

		val worldData = SkASWM.getInstance().getConfigManager().getWorldConfig().getWorldConfig(worldName)
		if(worldData == null){
			player?.sendMessage("World $worldName cannot be found in config")
			Skript.error("World $worldName cannot be found in config")
			return
		}

		Bukkit.getScheduler().runTaskAsynchronously(SkASWM.getInstance(), Runnable AsyncTask@{
			try {
				val timeTaken = measureTimeMillis {
					val slimeWorld = slimePlugin.loadWorld(
						slimeLoader,
						worldName,
						worldData.readOnly,
						worldData.toPropertyMap()
					)

					Bukkit.getScheduler().runTask(SkASWM.getInstance(), Runnable SyncTask@{
						try {
							slimePlugin.loadWorld(slimeWorld, true)
						} catch (ex: Exception) {
							when (ex) {
								is IllegalArgumentException, is WorldLockedException, is UnknownWorldException, is IOException -> {
									player?.sendMessage("Failed to create/load world $worldName. Check console for more information!")
									Skript.error("Failed to create/load world $worldName: ${ex.message}")
								}
								else -> throw ex
							}
						}
					})
				}

				player?.sendMessage("World $worldName loaded within $timeTaken ms!")
				Skript.info("World $worldName loaded within $timeTaken ms!")
			} catch (ex: Exception) {
				when (ex) {
					is CorruptedWorldException -> {
						player?.sendMessage("Failed to load world $worldName. World seems to be corrupted")
						Skript.error("Failed to load world $worldName. World seems to be corrupted")
						ex.printStackTrace()
					}

					is NewerFormatException -> {
						player?.sendMessage("Failed to load world $worldName. This world was serialized with a newer version of Slime Format that SWM can't understand")
						Skript.error("Failed to load world $worldName. This world was serialized with a newer version of Slime Format that SWM can't understand")
						ex.printStackTrace()
					}

					is UnknownWorldException -> {
						player?.sendMessage("Failed to load world $worldName. World cannot be found")
						Skript.error("Failed to load world $worldName. World cannot be found")
						ex.printStackTrace()
					}

					is WorldLockedException -> {
						player?.sendMessage("Failed to load world $worldName. World is already in use!")
						Skript.error("Failed to load world $worldName. World is already in use!")
						ex.printStackTrace()
					}

					is IOException, is IllegalArgumentException -> {
						player?.sendMessage("Failed to create world $worldName. Check console for more information!")
						Skript.error("Failed to create world $worldName. Check logger for more information")
						ex.printStackTrace()
					}
				}
			}
		})
	}
}