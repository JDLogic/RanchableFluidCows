package com.jdlogic.ranchablefluidcows.handler;

import com.jdlogic.ranchablefluidcows.RanchableFluidCows;
import com.jdlogic.ranchablefluidcows.packet.CowUpdatePacket;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class FakePlayerInteractionHandler
{
    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {
        if (event.target instanceof EntityFluidCow && event.entityPlayer instanceof FakePlayer)
        {
            EntityFluidCow entityFluidCow = (EntityFluidCow) event.target;

            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(entityFluidCow.dimension, entityFluidCow.posX, entityFluidCow.posY, entityFluidCow.posZ, 128D);

            RanchableFluidCows.network.sendToAllAround(new CowUpdatePacket(entityFluidCow), targetPoint);
        }
    }
}
