package com.jdlogic.ranchablefluidcows.ranchable;

import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;
import com.jdlogic.ranchablefluidcows.RanchableFluidCows;
import com.jdlogic.ranchablefluidcows.handler.ConfigHandler;
import com.jdlogic.ranchablefluidcows.network.CowUpdateMessage;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityRancher;

import java.util.LinkedList;
import java.util.List;

public class RanchableFC implements IFactoryRanchable
{
    public Class<? extends EntityLivingBase> getRanchableEntity()
    {
        return EntityFluidCow.class;
    }

    public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher)
    {
        if (entity instanceof EntityFluidCow)
        {
            EntityFluidCow entityFluidCow = (EntityFluidCow)entity;

            if (entityFluidCow.getCurrentUseCooldown() > 0) // Cow is not ready
            {
                updateClient(entityFluidCow, world.getTotalWorldTime()); // Sync cooldown with the client
                return null;
            }

            List<RanchedItem> retList = getAcceptableItemList(entityFluidCow.getEntityFluid(), rancher);

            if (retList != null) // The rancher can accept something
            {
                int maxCooldown = (int)(entityFluidCow.getEntityTypeData().getMaxUseCooldown() * ConfigHandler.penaltyMultiplier);

                entityFluidCow.setCurrentUseCooldown(maxCooldown);

                sendPacket(entityFluidCow);

                return retList;
            }
        }

        return null;
    }

    public static void updateClient(EntityFluidCow fluidCow, long worldTime)
    {
        NBTTagCompound tag = fluidCow.getEntityData();

        if (tag.hasKey("rfc:nextUpdate") && tag.getLong("rfc:nextUpdate") > worldTime)
        {
            return;
        }
        
        sendPacket(fluidCow);
        tag.setLong("rfc:nextUpdate", worldTime + 20 * 10);
    }

    public static void sendPacket(EntityFluidCow fluidCow)
    {
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(fluidCow.dimension, fluidCow.posX, fluidCow.posY, fluidCow.posZ, 128D);

        RanchableFluidCows.network.sendToAllAround(new CowUpdateMessage(fluidCow), targetPoint);
    }

    public static List<RanchedItem> getAcceptableItemList(Fluid cowFluid, IInventory inv)
    {
        FluidStack localFluidStack = new FluidStack(cowFluid, 1000);

        IInventoryManager localIInventoryManager = InventoryManager.create(inv, EnumFacing.UP);

        ItemStack bucket = new ItemStack(Items.BUCKET);

        int i = localIInventoryManager.findItem(bucket);

        if (i >= 0) // The rancher has a bucket
        {
            IFluidHandler fluidHandler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

            if (fluidHandler != null && fluidHandler.fill(localFluidStack, true) == Fluid.BUCKET_VOLUME)
            {
                LinkedList<RanchedItem> retList = new LinkedList<RanchedItem>();

                retList.add(new RanchedItem(bucket));

                inv.decrStackSize(i, 1);

                return retList;
            }
        }

        if (inv instanceof TileEntityRancher) // No bucket found or it cannot hold the fluid; Let's try the tank
        {
            TileEntityRancher rancher = (TileEntityRancher)inv;

            FluidTankInfo[] tankInfoArr = rancher.getTankInfo();

            for (FluidTankInfo tankInfo : tankInfoArr)
            {
                // The tank is empty OR (the fluid in the tank is the same AND the tank has room to accept it)
                if (tankInfo.fluid == null || (tankInfo.fluid.getFluid() == cowFluid && rancher.fill(null, localFluidStack, false) == 1000))
                {
                    LinkedList<RanchedItem> retList = new LinkedList<RanchedItem>();

                    retList.add(new RanchedItem(localFluidStack));

                    return retList;
                }
            }
        }

        return null; // No buckets and the tank is not usable
    }
}