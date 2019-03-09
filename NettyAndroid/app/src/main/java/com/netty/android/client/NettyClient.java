package com.netty.android.client;

import com.netty.android.coder.MessagePackDecoder;
import com.netty.android.coder.MessagePackEncoder;
import com.netty.android.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Netty Java客户端
 *
 * @author lucher
 */
public class NettyClient extends Thread {

    // Server端IP地址
    private String host;
    // Netty服务端监听端口号
    private int port;
    // 通信管道
    private Channel channel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param event
     */
    public void sendMessage(BaseEvent event) {
        if (channel != null && channel.isActive()) {//已建立连接状态
            channel.writeAndFlush(event);
        } else {
            EventBus.getDefault().post("还未建立连接，不能发送消息");
        }
    }

    /**
     * 开启客户端
     *
     * @throws Exception
     */
    private void startClient() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //NIO客户端启动辅助类，降低客户端开发复杂度
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    // 当执行channelfactory的newChannel方法时,会创建NioSocketChannel实例
                    .channel(NioSocketChannel.class)
                    // ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关
                    // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效
                    // 负载，但是却造成了延时，而该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输，于TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送
                    // 数据，适用于文件传输。
                    .option(ChannelOption.TCP_NODELAY, true)
                    // handler()和childHandler()的主要区别是，handler()是发生在初始化的时候，childHandler()是发生在客户端连接之后
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // Tcp粘包处理，添加一个LengthFieldBasedFrameDecoder解码器，它会在解码时按照消息头的长度来进行解码。
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4));
                            // MessagePack解码器，消息进来后先由frameDecoder处理，再给msgPackDecoder处理
                            pipeline.addLast("msgPackDecoder", new MessagePackDecoder());
                            // Tcp粘包处理，添加一个
                            // LengthFieldPrepender编码器，它会在ByteBuf之前增加4个字节的字段，用于记录消息长度。
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            // MessagePack编码器，消息发出之前先由frameEncoder处理，再给msgPackEncoder处理
                            pipeline.addLast("msgPackEncoder", new MessagePackEncoder());
                            // 消息处理handler
                            pipeline.addLast("handler", new NettyClientHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture f = bootstrap.connect(host, port).sync();
            channel = f.channel();
            System.out.println("客户端已启动");
            // 等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败，服务端是否开启？");
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }
}