package com.jdlogic.ranchablefluidcows;

import com.jdlogic.ranchablefluidcows.handler.ConfigHandler;
import com.jdlogic.ranchablefluidcows.handler.FakePlayerInteractionHandler;
import com.jdlogic.ranchablefluidcows.network.CowUpdateMessage;
import com.jdlogic.ranchablefluidcows.ranchable.RanchableFC;
import com.jdlogic.ranchablefluidcows.reference.Reference;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import powercrystals.minefactoryreloaded.api.FactoryRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
        dependencies = Reference.MOD_DEPENDENCIES, certificateFingerprint = Reference.MOD_FINGERPRINT)
public class RanchableFluidCows
{
    @Mod.Instance(Reference.MOD_ID)
    public static RanchableFluidCows instance;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

        if (Loader.isModLoaded("moofluids"))
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
    public void init(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("moofluids"))
        {
            FactoryRegistry.sendMessage("registerRanchable", new RanchableFC());
        }
    }

    @Mod.EventHandler
    public static void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        FMLLog.log(Reference.MOD_ID, Level.ERROR,"The mod file is missing its signature or the one found does not match! Unless this is a dev environment," +
                " it is recommended that you stop using this mod file as it has most likely been tampered with!");
    }
}