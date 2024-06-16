package me.danielmillar.skaswm

import ch.njol.skript.Skript
import ch.njol.skript.SkriptAddon
import me.danielmillar.skaswm.elements.Types
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.util.logging.Level

class SkASWM : JavaPlugin() {

    companion object {
        private lateinit var instance: SkASWM
        fun getInstance(): SkASWM {
            return instance
        }
    }

    private lateinit var addon: SkriptAddon

    override fun onEnable() {
        instance = this
        addon = Skript.registerAddon(this).setLanguageFileDirectory("lang")
        try {
            Types()
            addon.loadClasses("me.danielmillar.skaswm")
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        }
    }

    override fun onDisable() {}
}
