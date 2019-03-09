#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：MessagePackDecoder
* 类 描 述 ：MessagePack实现的消息解码器
* 作    者 ：lucher
* 版 本 号 ：v1.0.0
*******************************************************************
* Copyright @ lucher 2019. All rights reserved.
*******************************************************************
//----------------------------------------------------------------*/
#endregion
using DotNetty.Buffers;
using DotNetty.Codecs;
using DotNetty.Transport.Channels;
using MessagePack;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;

namespace NettyCSharp
{
    class MessagePackDecoder : MessageToMessageDecoder<IByteBuffer>
    {
        protected override void Decode(IChannelHandlerContext context, IByteBuffer input, List<object> output)
        {
            byte[] array;
            int length = input.ReadableBytes;
            array = new byte[length];
            input.GetBytes(input.ReaderIndex, array, 0, length);

            output.Add(MessagePackSerializer.Deserialize<Object>(array));
            //output.Add(MessagePackSerializer.ToJson(array));
        }
    }
}
