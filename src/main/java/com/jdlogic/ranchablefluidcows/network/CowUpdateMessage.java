package com.jdlogic.ranchablefluidcows.network;

import com.robrit.moofluids.common.entity.EntityFluidCow;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class CowUpdateMessage implements IMessage
{
    private int entityID;
    private int currentCooldown;

    public CowUpdateMessage() {}

    public CowUpdateMessage(EntityFluidCow cow)
    {
        this.entityID = cow.getEntityId();
        this.currentCooldown = cow.getCurrentUseCooldown();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = ByteBufUtils.readVarInt(buf, 4);
        this.currentCooldown = ByteBufUtils.readVarInt(buf, 4);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.entityID, 4);
        ByteBufUtils.writeVarInt(buf, this.currentCooldown, 4);
    }

    public static class Handler implements IMessageHandler<CowUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage(CowUpdateMessage message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT && message != null)
            {
                World world = FMLClientHandler.instance().getWorldClient();

                if (world != null)
                {
                    Entity entity = world.getEntityByID(message.entityID);

                    if (entity != null && entity instanceof EntityFluidCow)
                    {
                        ((EntityFluidCow)entity).setCurrentUseCooldown(message.currentCooldown);
                    }
                }
            }

            return null; // no response in this case
        }
    }
}