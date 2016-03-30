package com.jdlogic.ranchablefluidcows.ranchable;

import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;
import com.jdlogic.ranchablefluidcows.RanchableFluidCows;
import com.jdlogic.ranchablefluidcows.handler.ConfigHandler;
import com.jdlogic.ranchablefluidcows.network.CowUpdateMessage;
import com.robrit.moofluids.common.entity.EntityFluidCow;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;

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
        LinkedList<RanchedItem> localLinkedList = new LinkedList<RanchedItem>();

        if (entity instanceof EntityFluidCow)
        {
            EntityFluidCow entityFluidCow = (EntityFluidCow)entity;

            int currentUseCooldown = entityFluidCow.getCurrentUseCooldown();

            if (currentUseCooldown > 0)
            {
                updateClient(entityFluidCow, world.getTotalWorldTime());
                return null;
            }

            int maxCooldown = (int)(entityFluidCow.getEntityTypeData().getMaxUseCooldown() * ConfigHandler.penaltyMultiplier);

            entityFluidCow.setCurrentUseCooldown(maxCooldown);

            sendPacket(entityFluidCow);

            IInventoryManager localIInventoryManager = InventoryManager.create(rancher, ForgeDirection.UP);

            ItemStack bucket = new ItemStack(Items.bucket);

            int i = localIInventoryManager.findItem(bucket);

            Fluid fluid = entityFluidCow.getEntityFluid();

            FluidStack localFluidStack = new FluidStack(fluid, 1000);

            if (i >= 0)
            {
                ItemStack filledItemStack = FluidContainerRegistry.fillFluidContainer(localFluidStack, bucket);

                if (filledItemStack == null)
                {
                    return null;
                }

                localLinkedList.add(new RanchedItem(filledItemStack));

                rancher.decrStackSize(i, 1);
            }
            else
            {
                localLinkedList.add(new RanchedItem(localFluidStack));
            }

            return localLinkedList;
        }
        else
        {
            return null;
        }
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
}