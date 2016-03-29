package com.jdlogic.ranchablefluidcows.handler;

import com.jdlogic.ranchablefluidcows.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler
{
    public static Configuration config;

    public static float penaltyMultiplier;
    public static boolean fakePlayerFix;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    public static void loadConfig()
    {
        penaltyMultiplier = config.getFloat("penaltyMultiplier", Configuration.CATEGORY_GENERAL, 1.0F, 0.0F, 100.0F,
                "This value is used when setting the fluid cow cooldown timer. EG: 1 = normal time, 2 = double the time, 0.5 = half the time.");

        fakePlayerFix = config.getBoolean("fakePlayerFix", Configuration.CATEGORY_GENERAL, true,
                "Sends the updated current use cooldown to the client when a fake Player interacts with a fluid cow. Set to false to disable.");

        if (config.hasChanged())
        {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(Reference.MOD_ID))
        {
            loadConfig();
        }
    }
}