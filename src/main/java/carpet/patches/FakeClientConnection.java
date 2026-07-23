package carpet.patches;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jspecify.annotations.Nullable;

public class FakeClientConnection extends Connection
{
    public FakeClientConnection(PacketFlow p)
    {
        super(p);
        this.channel = new EmbeddedChannel();
    }

    @Override
    public void setReadOnly()
    {
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean bl)
    {
        if (listener != null) {
            try {
                ChannelFuture future = this.channel.newSucceededFuture();
                listener.operationComplete(future);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, T packetListener)
    {
    }
}
