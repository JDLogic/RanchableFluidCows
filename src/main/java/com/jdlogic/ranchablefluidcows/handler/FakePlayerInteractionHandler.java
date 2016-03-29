package com.jdlogic.ranchablefluidcows.handler;

import com.jdlogic.ranchablefluidcows.ranchable.RanchableFC;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class FakePlayerInteractionHandler
{
    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event)
    {
        if (event.target != null && event.entityPlayer != null && event.target instanceof EntityFluidCow && event.entityPlayer instanceof FakePlayer)
        {
            EntityFluidCow entityFluidCow = (EntityFluidCow)event.target;

            RanchableFC.updateClient(entityFluidCow, entityFluidCow.worldObj.getTotalWorldTime());
        }
    }
}