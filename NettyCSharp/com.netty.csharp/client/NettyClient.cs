#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：NettyClient
* 类 描 述 ：Netty C#客户端
* 作    者 ：lucher
* 版 本 号 ：v1.0.0
*******************************************************************
* Copyright @ lucher 2019. All rights reserved.
*******************************************************************
//----------------------------------------------------------------*/
#endregion
using DotNetty.Codecs;
using DotNetty.Transport.Bootstrapping;
using DotNetty.Transport.Channels;
using DotNetty.Transport.Channels.Sockets;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace NettyCSharp
{
    class NettyClient
    {
        // Server端IP地址
        private IPAddress Host;
        // Netty服务端监听端口号
        private int Port;
        // 通信管道
        private IChannel channel;

        public NettyClient(IPAddress Host, int Port)
        {
            this.Host = Host;
            this.Port = Port;
        }

        /**
         * 发送消息
         *
         * @param event
         */
        public void SendMessage(BaseEvent e) {
            if (channel != null && channel.Active)
            {//已建立连接状态
                channel.WriteAndFlushAsync(e);
            } else {
                //EventBus.getDefault().post("还未建立连接，不能发送消息");
            }
        }

        //开启客户端
        public async Task StartClient()
        {
            var group = new MultithreadEventLoopGroup();
            try
            {
                var bootstrap = new Bootstrap();
                bootstrap
                    .Group(group)
                    // 当执行channelfactory的newChannel方法时,会创建TcpSocketChannel实例
                    .Channel<TcpSocketChannel>()
                    // ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关
                    // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效
                    // 负载，但是却造成了延时，而该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输，于TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送
                    // 数据，适用于文件传输。
                    .Option(ChannelOption.TcpNodelay, true)
                    .Handler(new ActionChannelInitializer<ISocketChannel>(channel =>
                    {
                        IChannelPipeline pipeline = channel.Pipeline;
                        // Tcp粘包处理，添加一个LengthFieldBasedFrameDecoder解码器，它会在解码时按照消息头的长度来进行解码。
                        pipeline.AddLast("frameDecoder", new LengthFieldBasedFrameDecoder(ushort.MaxValue, 0, 4, 0, 4));
                        // MessagePack解码器，消息进来后先由frameDecoder处理，再给msgPackDecoder处理
                        pipeline.AddLast("msgPackDecoder", new MessagePackDecoder());
                        // Tcp粘包处理，添加一个
                        // LengthFieldPrepender编码器，它会在ByteBuf之前增加4个字节的字段，用于记录消息长度。
                        pipeline.AddLast("frameEncoder", new LengthFieldPrepender(4));
                        // MessagePack编码器，消息发出之前先由frameEncoder处理，再给msgPackEncoder处理
                        pipeline.AddLast("msgPackEncoder", new MessagePackEncoder());
                        // 消息处理handler
                        pipeline.AddLast("handler", new NettyClientHandler());
                    }));
                // 发起连接操作
                channel = await bootstrap.ConnectAsync(new IPEndPoint(Host, Port));
                Console.WriteLine("客户端已启动");
                Console.ReadLine();
                // 等待客户端链路关闭
                //await clientChannel.CloseAsync();
            }
            finally
            {
                //group.ShutdownGracefullyAsync().Wait(1000);
                //await group.ShutdownGracefullyAsync(TimeSpan.FromMilliseconds(100), TimeSpan.FromSeconds(1));
            }
        }
    }
}
