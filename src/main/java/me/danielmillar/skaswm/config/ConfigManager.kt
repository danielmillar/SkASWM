package me.danielmillar.skaswm.config

import de.exlll.configlib.YamlConfigurationProperties
import de.exlll.configlib.YamlConfigurationStore
import java.nio.file.Path
import kotlin.io.path.exists

class ConfigManager(private val configPath: Path) {

	private var configStore: YamlConfigurationStore<Worlds>
	private var worldConfig: Worlds

	init {
		val properties = YamlConfigurationProperties.newBuilder().build()
		configStore = YamlConfigurationStore(Worlds::class.java, properties)
		if (!configPath.exists()) configStore.save(Worlds(), configPath)
		worldConfig = configStore.load(configPath)
	}

	fun getStore(): YamlConfigurationStore<Worlds> {
		return configStore
	}

	fun getWorldConfig(): Worlds {
		return worldConfig
	}

	fun saveWorldConfig() {
		configStore.save(
			worldConfig,
			configPath
		)
	}
}