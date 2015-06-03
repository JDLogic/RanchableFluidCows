package com.jdlogic.ranchablefluidcows.packet;

import com.robrit.moofluids.common.entity.EntityFluidCow;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class CowUpdatePacket implements IMessage
{
    private int entityID;
    private int currentCooldown;

    public CowUpdatePacket() {}

    public CowUpdatePacket(EntityFluidCow cow)
    {
        this.entityID = cow.getEntityId();
        this.currentCooldown = cow.getCurrentUseCooldown();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = ByteBufUtils.readVarInt(buf, 5);
        this.currentCooldown = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.entityID, 5);
        ByteBufUtils.writeVarInt(buf, this.currentCooldown, 5);
    }

    public static class Handler implements IMessageHandler<CowUpdatePacket, IMessage>
    {

        @Override
        public IMessage onMessage(CowUpdatePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.entityID);

                if (entity instanceof EntityFluidCow)
                {
                    ((EntityFluidCow) entity).setCurrentUseCooldown(packet.currentCooldown);
                }
            }

            return null; // no response in this case
        }
    }
}
