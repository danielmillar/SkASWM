package me.danielmillar.skaswm

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import me.danielmillar.skaswm.config.ConfigManager
import me.danielmillar.skaswm.elements.Types
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Path
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

		if (!hasClass("com.destroystokyo.paper.PaperConfig") || !hasClass("com.infernalsuite.aswm.level.SlimeLevelInstance")) {
			logger.severe("You're server isn't running SlimeWorldManager fork of Paper, verify that you're using AdvancedSlimeWorldManager")
			server.pluginManager.disablePlugin(this)
			return
		}

		if (server.pluginManager.getPlugin("SlimeWorldManager") == null) {
			logger.severe("Seems like you're missing the SlimeWorldManager plugin, please install it!")
			server.pluginManager.disablePlugin(this)
			return
		}

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

	private fun hasClass(className: String): Boolean {
		try {
			Class.forName(className)
			return true
		} catch (e: ClassNotFoundException) {
			return false
		}
	}
}
