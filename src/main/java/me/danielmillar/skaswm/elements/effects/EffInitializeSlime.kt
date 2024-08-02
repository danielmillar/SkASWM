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
import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI
import com.infernalsuite.aswm.api.loaders.SlimeLoader
import com.infernalsuite.aswm.loaders.file.FileLoader
import me.danielmillar.skaswm.SkASWM
import me.danielmillar.skaswm.elements.SlimeLoaderTypeEnum
import org.bukkit.event.Event
import java.io.File

@Name("Initialize Slime")
@Description("Initializes the AdvancedSlimeWorld API and sets up a SlimeLoader based on the specified type.")
@Examples(
    value = [
        "initialize slime instance with loader file",
        "initialize slime instance with loader type mongodb",
    ]
)
@Since("1.0.0")
class EffInitializeSlime : Effect() {

    companion object {
        private var slimeInstance: AdvancedSlimePaperAPI = AdvancedSlimePaperAPI.instance()
        private lateinit var slimeLoader: SlimeLoader

        fun getSlimeLoader(): SlimeLoader? {
            if (::slimeLoader.isInitialized) return slimeLoader
            return null
        }

        fun getSlimeInstance(): AdvancedSlimePaperAPI {
            return slimeInstance
        }

        init {
            Skript.registerEffect(
                EffInitializeSlime::class.java,
                "initialize slime instance with loader [type] %slimeloader%"
            )
        }
    }

    private lateinit var loaderType: Expression<SlimeLoaderTypeEnum>

    override fun toString(event: Event?, debug: Boolean): String {
        return "Initialize slime instance with loader type ${loaderType.toString(event, debug)}"
    }

    @Suppress("unchecked_cast")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean,
        parser: SkriptParser.ParseResult
    ): Boolean {
        loaderType = expressions[0] as Expression<SlimeLoaderTypeEnum>
        return true
    }

    override fun execute(event: Event) {
        try {
            if (loaderType.getSingle(event).toString().lowercase() == "file") {
                slimeLoader = FileLoader(File("slime_worlds"))
            }

            if (getSlimeLoader() == null) {
                Skript.error(
                    "An error occurred while trying to setup Slime Instance/Loader. Loader type: ${
                        loaderType.getSingle(
                            event
                        ).toString()
                    }"
                )
            }
        } catch (ex: Exception) {
            Skript.error("Failed to initialize Slime Instance/Loader: ${ex.message}")
            ex.printStackTrace()
        }
    }
}