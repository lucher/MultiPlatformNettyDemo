#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：MessagePackEncoder
* 类 描 述 ：MessagePack实现的消息编码器
* 作    者 ：lucher
* 版 本 号 ：v1.0.0
*******************************************************************
* Copyright @ lucher 2019. All rights reserved.
*******************************************************************
//----------------------------------------------------------------*/
#endregion
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;
using DotNetty.Buffers;
using DotNetty.Codecs;
using DotNetty.Transport.Channels;
using MessagePack;

namespace NettyCSharp
{
    class MessagePackEncoder : MessageToByteEncoder<Object>
    {
        protected override void Encode(IChannelHandlerContext context, object message, IByteBuffer output)
        {
            byte[] temp = MessagePackSerializer.Serialize(message);
            output.WriteBytes(temp);
        }
    }
}
