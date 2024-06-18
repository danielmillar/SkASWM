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
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException
import com.infernalsuite.aswm.api.exceptions.WorldLockedException
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.config.WorldConfig
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.io.IOException
import kotlin.system.measureTimeMillis

@Name("Create Slime World")
@Description("Create a new Slime World with a name, slime properties and whether it's readOnly")
@Examples(
	"create slime world named \"Test\" with props {globalProps}",
	"new slime world named \"Test\" with props {globalProps} as readOnly"
)
@Since("1.0.0")
class EffCreateSlimeWorld : Effect() {

	companion object {
		init {
			Skript.registerEffect(
				EffCreateSlimeWorld::class.java,
				"(create|new) (slimeworld|slime world) named %string% with props %slimepropertymap% [readonly:as ReadOnly]"
			)
		}
	}

	private lateinit var worldName: Expression<String>
	private lateinit var slimeProperties: Expression<SlimePropertyMap>
	private var isReadOnly = false

	override fun toString(event: Event?, debug: Boolean): String {
		return "Slime world creation"
	}

	@Suppress("unchecked_cast")
	override fun init(
		expressions: Array<out Expression<*>>,
		matchedPattern: Int,
		isDelayed: Kleenean,
		parser: SkriptParser.ParseResult
	): Boolean {
		worldName = expressions[0] as Expression<String>
		slimeProperties = expressions[1] as Expression<SlimePropertyMap>
		isReadOnly = parser.hasTag("readonly")
		return true
	}

	override fun execute(event: Event) {
		val setupResult = setupEvent(event) ?: return

		val (player, slimeData) = setupResult
		val (slimePlugin, slimeLoader) = slimeData

		val worldName = checkWorldName(event, worldName, player) ?: return

		val properties = slimeProperties.getSingle(event)
		if (properties == null) {
			player?.sendMessage("Slime properties cannot be null.")
			Skript.error("Slime properties cannot be null.")
			return
		}

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld != null) {
			player?.sendMessage("A world with that name already exists.")
			Skript.error("A world with that name already exists!")
			return
		}

		val worldDataExists = SkASWM.getInstance().getConfigManager().getWorldConfig().hasWorldConfig(worldName)
		if (worldDataExists) {
			player?.sendMessage("World $worldName already exists in config")
			Skript.error("World $worldName already exists in config")
			return
		}

		Bukkit.getScheduler().runTaskAsynchronously(SkASWM.getInstance(), Runnable AsyncTask@{
			try {
				val timeTaken = measureTimeMillis {
					val slimeWorld = slimePlugin.createEmptyWorld(
						slimeLoader,
						worldName,
						isReadOnly,
						properties
					)

					Bukkit.getScheduler().runTask(SkASWM.getInstance(), Runnable SyncTask@{
						try {
							slimePlugin.loadWorld(slimeWorld, true)

							val worldData = WorldConfig.fromPropertyMap(properties, isReadOnly)
							SkASWM.getInstance().getConfigManager().getWorldConfig()
								.setWorldConfig(worldName, worldData)
							SkASWM.getInstance().getConfigManager().saveWorldConfig()
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

				player?.sendMessage("Successfully created world $worldName within $timeTaken ms!")
				Skript.info("Successfully created world $worldName within $timeTaken ms!")
			} catch (ex: Exception) {
				when (ex) {
					is WorldAlreadyExistsException -> {
						player?.sendMessage("Failed to create world $worldName: world already exists")
						Skript.error("Failed to create world $worldName: world already exists")
					}

					is IOException -> {
						player?.sendMessage("Failed to create world $worldName. Check console for more information!")
						Skript.error("Failed to create world $worldName. Check logger for more information")
						ex.printStackTrace()
					}
				}
			}
		})
	}
}