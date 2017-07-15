package com.jdlogic.ranchablefluidcows.handler;

import com.jdlogic.ranchablefluidcows.ranchable.RanchableFC;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class FakePlayerInteractionHandler
{
    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteract event)
    {
        if (event.getTarget() instanceof EntityFluidCow && event.getEntityPlayer() instanceof FakePlayer)
        {
            EntityFluidCow entityFluidCow = (EntityFluidCow)event.getTarget();

            RanchableFC.updateClient(entityFluidCow, entityFluidCow.worldObj.getTotalWorldTime());
        }
    }
}