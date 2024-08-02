package me.danielmillar.skaswm.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException
import com.infernalsuite.aswm.api.world.SlimeWorld
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.config.WorldConfig
import me.danielmillar.skaswm.util.Util.checkWorldName
import me.danielmillar.skaswm.util.Util.setupEvent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event
import java.io.IOException
import java.util.concurrent.*

@Name("Create Slime World")
@Description("Create a new Slime World with a name, slime properties, and whether it's readOnly.")
@Examples(
	value = [
		"set {_world} to create slime world named \"Test\" with props {globalProps}",
		"to {_world} to new slime world named \"Test\" with props {globalProps} as readOnly"
	]
)
@Since("1.0.0")
class ExprCreateSlimeWorld : SimpleExpression<World>() {

	companion object {
		init {
			Skript.registerExpression(
				ExprCreateSlimeWorld::class.java,
				World::class.java,
				ExpressionType.SIMPLE,
				"(create|new) (slimeworld|slime world) named %string% with props %slimepropertymap% [readonly:as ReadOnly]"
			)
		}
	}

	private lateinit var worldName: Expression<String>
	private lateinit var slimeProperties: Expression<SlimePropertyMap>
	private var isReadOnly = false

	override fun toString(event: Event?, debug: Boolean): String {
		return "Create slime world ${worldName.toString(event, debug)} with properties ${
			slimeProperties.toString(
				event,
				debug
			)
		} ${if (isReadOnly) "as readOnly" else ""}"
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

	override fun isSingle(): Boolean {
		return true
	}

	override fun getReturnType(): Class<World> {
		return World::class.java
	}

	override fun get(event: Event): Array<World> {
		val setupResult = setupEvent(event) ?: return emptyArray()

		val (player, slimeData) = setupResult
		val (slimePlugin, slimeLoader) = slimeData

		val worldName = checkWorldName(event, worldName, player) ?: return emptyArray()

		val properties = slimeProperties.getSingle(event)
		if (properties == null) {
			player?.sendMessage("Slime properties cannot be null.")
			Skript.error("Slime properties cannot be null.")
			return emptyArray()
		}

		val bukkitWorld = Bukkit.getWorld(worldName)
		if (bukkitWorld != null) {
			player?.sendMessage("A world with that name already exists.")
			Skript.error("A world with that name already exists!")
			return emptyArray()
		}

		val worldDataExists = SkASWM.getInstance().getConfigManager().getWorldConfig().hasWorldConfig(worldName)
		if (worldDataExists) {
			player?.sendMessage("World $worldName already exists in config")
			Skript.error("World $worldName already exists in config")
			return emptyArray()
		}

		val slimeWorldFuture: CompletableFuture<SlimeWorld> = CompletableFuture.supplyAsync {
			try {
				return@supplyAsync slimePlugin.createEmptyWorld(worldName, isReadOnly, properties, slimeLoader)
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

					else -> throw ex
				}
			}

			return@supplyAsync null
		}

		var slimeWorld: SlimeWorld? = null
		try {
			slimeWorld = slimeWorldFuture.get(5L, TimeUnit.SECONDS)
		} catch (ex: Exception) {
			when (ex) {
				is CancellationException -> {
					player?.sendMessage("Failed to create world $worldName: future was cancelled")
					Skript.error("Failed to create world $worldName: future was cancelled")
					return emptyArray()
				}

				is ExecutionException -> {
					player?.sendMessage("Failed to create world $worldName: future completed with exception")
					Skript.error("Failed to create world $worldName: future completed with exception")
					return emptyArray()
				}

				is InterruptedException -> {
					player?.sendMessage("Failed to create world $worldName: future interrupted")
					Skript.error("Failed to create world $worldName: future interrupted")
					return emptyArray()
				}

				is TimeoutException -> {
					player?.sendMessage("Failed to create world $worldName: future timeout")
					Skript.error("Failed to create world $worldName: future timeout")
					return emptyArray()
				}
			}
		}

		if (slimeWorld == null) {
			player?.sendMessage("Failed to create world $worldName: Future returned null")
			Skript.error("Failed to create world $worldName: Future returned null")
			return emptyArray()
		}

		try {
			slimePlugin.loadWorld(slimeWorld, true)

			val worldData = WorldConfig.fromPropertyMap(properties, isReadOnly)
			SkASWM.getInstance().getConfigManager().getWorldConfig()
				.setWorldConfig(worldName, worldData)
			SkASWM.getInstance().getConfigManager().saveWorldConfig()

			val world = Bukkit.getWorld(worldName)
			if (world != null) {
				return arrayOf(world)
			}
		} catch (ex: Exception) {
			when (ex) {
				is IllegalArgumentException, is UnknownWorldException, is IOException -> {
					player?.sendMessage("Failed to create/load world $worldName. Check console for more information!")
					Skript.error("Failed to create/load world $worldName: ${ex.message}")
				}

				else -> throw ex
			}
		}

		return emptyArray()
	}
}