#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：BaseEvent
* 类 描 述 ：事件基类
* 作    者 ：lucher
* 版 本 号 ：v1.0.0
*******************************************************************
* Copyright @ lucher 2019. All rights reserved.
*******************************************************************
//----------------------------------------------------------------*/
#endregion
using MessagePack;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NettyCSharp
{
    [MessagePackObject]// 使用MessagePack传输的实体类需要加入该注解
    public abstract class BaseEvent
    {
        //事件类别
        [Key(0)]
        public EventType eventType;
        // 消息来源，如server，android etc
        [Key(1)]
        public String from;
        [Key(2)]
        public String time;

        protected EventType GetEventType()
        {
            return eventType;
        }

        protected void SetEventType(EventType value)
        {
            eventType = value;
        }

        protected string GetFrom()
        {
            return from;
        }

        protected void SetFrom(string value)
        {
            from = value;
        }

        protected string GetTime()
        {
            return time;
        }

        protected void SetTime(string value)
        {
            time = value;
        }
    }
}
