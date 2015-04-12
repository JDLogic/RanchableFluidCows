package com.jdlogic.ranchablefluidcows;

import com.jdlogic.ranchablefluidcows.handler.ConfigHandler;
import com.jdlogic.ranchablefluidcows.reference.Reference;
import com.jdlogic.ranchablefluidcows.ranchable.RanchableFC;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
      dependencies = "required-after:MineFactoryReloaded;required-after:CoFHCore;after:*")

public class RanchableFluidCows
{
    @Mod.Instance(Reference.MOD_ID)
    public static RanchableFluidCows instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evnet)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("MooFluids"))
        {
            IFactoryRanchable ranchable = new RanchableFC();

            FactoryRegistry.sendMessage("registerRanchable", ranchable);
        }
    }
}