package com.gmail.favorlock.bonesqlib;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 1:09 AM
 */
public class BoneSQLib extends JavaPlugin {

	public static final Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		BoneSQLib.logger.log(Level.INFO, "BoneSQLib enabled!");
	}

	public void onDisable() {
		BoneSQLib.logger.log(Level.INFO, "BoneSQLib disabled!");
	}

}
