#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：NettyClientHandler
* 类 描 述 ：处理客户端的channel
* 作    者 ：lucher
* 版 本 号 ：v1.0.0
*******************************************************************
* Copyright @ lucher 2019. All rights reserved.
*******************************************************************
//----------------------------------------------------------------*/
#endregion
using DotNetty.Buffers;
using DotNetty.Transport.Channels;
using EventBus;
using MessagePack;
using System;
using System.Collections.Generic;
using System.Text;

namespace NettyCSharp
{
    public class NettyClientHandler : SimpleChannelInboundHandler<Object>
    {

        protected override void ChannelRead0(IChannelHandlerContext ctx, object msg)
        {
            //String json = msg as String;
            //MessagePackSerializer.;
            //读取到消息，对消息进行解析
            //这样判断感觉有些尴尬，由于对C#不熟，暂时还没有更好的办法，有好的实现方法还望给出宝贵建议
            object[] values = (object[])msg;
            int index = Convert.ToInt32(values[0]);//获取消息类别，第一个字段为EventType，这是约定好的
            String eventType = Enum.GetName(typeof(EventType), index);
            if (eventType.Equals(EventType.TEST_EVENT.ToString())) {//测试类事件
                TestEvent testEvent = MessagePackSerializer.Deserialize<TestEvent>(MessagePackSerializer.Serialize(msg));
                Console.WriteLine("Client接收到消息:" + testEvent);
                SimpleEventBus.GetDefaultEventBus().Post(testEvent.ToString(), TimeSpan.Zero);
            }
            else if (eventType.Equals(EventType.OTHER_EVENT.ToString()))
            {//其他类事件
                Console.WriteLine("Client接收到消息:其他类事件，暂未处理");
            }

        }

        public override void ChannelReadComplete(IChannelHandlerContext context)
        {
            base.ChannelReadComplete(context);
            context.Flush();
            Console.WriteLine("ChannelReadComplete:" + context);
        }

        public override void ChannelRegistered(IChannelHandlerContext context)
        {
            base.ChannelRegistered(context);
            Console.WriteLine("Client ChannelRegistered:" + context);
        }

        public override void ChannelActive(IChannelHandlerContext context)
        {
            Console.WriteLine("Client channelActive:" + context);
            SimpleEventBus.GetDefaultEventBus().Post("建立连接："+context, TimeSpan.Zero);
        }

        public override void ChannelInactive(IChannelHandlerContext context)
        {
            base.ChannelInactive(context);
            Console.WriteLine("Client ChannelInactive:" + context);
            SimpleEventBus.GetDefaultEventBus().Post("连接断开：" + context, TimeSpan.Zero);
        }

        public override void ChannelUnregistered(IChannelHandlerContext context)
        {
            base.ChannelUnregistered(context);
            Console.WriteLine("Client ChannelUnregistered:" + context);
        }

        public override void ExceptionCaught(IChannelHandlerContext context, Exception exception)
        {
            Console.WriteLine("Exception: " + exception);
            context.CloseAsync();
        }
    }
}