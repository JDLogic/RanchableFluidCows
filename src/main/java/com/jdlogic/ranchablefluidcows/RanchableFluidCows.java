package com.jdlogic.ranchablefluidcows;

import com.jdlogic.ranchablefluidcows.handler.ConfigHandler;
import com.jdlogic.ranchablefluidcows.handler.FakePlayerInteractionHandler;
import com.jdlogic.ranchablefluidcows.network.CowUpdateMessage;
import com.jdlogic.ranchablefluidcows.ranchable.RanchableFC;
import com.jdlogic.ranchablefluidcows.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
        dependencies = Reference.MOD_DEPENDENCIES)

public class RanchableFluidCows
{
    @Mod.Instance(Reference.MOD_ID)
    public static RanchableFluidCows instance;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigHandler());

        if (Loader.isModLoaded("MooFluids"))
        {
            network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

            if (event.getSide() == Side.CLIENT)
            {
                network.registerMessage(CowUpdateMessage.Handler.class, CowUpdateMessage.class, 0, Side.CLIENT);
            }

            if (ConfigHandler.fakePlayerFix)
            {
                MinecraftForge.EVENT_BUS.register(new FakePlayerInteractionHandler());
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evnet) {}

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