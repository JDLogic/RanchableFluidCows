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
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

        if (event.getSide() == Side.CLIENT)
        {
            network.registerMessage(CowUpdateMessage.Handler.class, CowUpdateMessage.class, 0, Side.CLIENT);
        }

        if (ConfigHandler.fakePlayerFix)
        {
            MinecraftForge.EVENT_BUS.register(new FakePlayerInteractionHandler());
        }

        sendMessage("registerRanchable", new RanchableFC());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @Mod.EventHandler
    public static void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        FMLLog.log(Reference.MOD_ID, Level.ERROR,"The mod file is missing its signature or the one found does not match! Unless this is a dev environment," +
                " it is recommended that you stop using this mod file as it has most likely been tampered with!");
    }

    /*
    *   Copy of MFR's 'FactoryRegistry.sendMessage()' as it is currently broken.
    *   It uses the old modid and the issue has been reported (#653).
    */
    public static void sendMessage(String message, Object value)
    {
        if (!Loader.isModLoaded("minefactoryreloaded") || Loader.instance().activeModContainer() == null)
            return;
        try
        {
            Method m = FMLInterModComms.class.getDeclaredMethod("enqueueMessage", Object.class, String.class, FMLInterModComms.IMCMessage.class);
            m.setAccessible(true);
            Constructor<FMLInterModComms.IMCMessage> c = FMLInterModComms.IMCMessage.class.getDeclaredConstructor(String.class, Object.class);
            c.setAccessible(true);
            m.invoke(null, Loader.instance().activeModContainer(), "minefactoryreloaded", c.newInstance(message, value));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}