package me.danielmillar.skaswm

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import de.exlll.configlib.YamlConfigurations
import me.danielmillar.skaswm.config.ConfigManager
import me.danielmillar.skaswm.config.WorldConfig
import me.danielmillar.skaswm.config.Worlds
import me.danielmillar.skaswm.elements.Types
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Level


class SkASWM : JavaPlugin() {

    companion object {
        private lateinit var instance: SkASWM
        fun getInstance(): SkASWM {
            return instance
        }
    }

    private lateinit var addon: SkriptAddon
    private lateinit var worldsConfig: ConfigManager

    private val configPath: Path = File(dataFolder, "worlds.yml").toPath()

    override fun onEnable() {
        instance = this

        worldsConfig = ConfigManager(configPath)

        addon = Skript.registerAddon(this).setLanguageFileDirectory("lang")
        try {
            Types()
            addon.loadClasses("me.danielmillar.skaswm")
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        }
    }

    override fun onDisable() {
        worldsConfig.saveWorldConfig()
    }

    fun getConfigManager(): ConfigManager {
        return worldsConfig
    }
}
