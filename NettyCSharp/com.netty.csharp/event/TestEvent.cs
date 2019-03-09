#region << 版 本 注 释 >>
/*----------------------------------------------------------------
* 类 名 称 ：TestEvent
* 类 描 述 ：测试事件
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
    public class TestEvent:BaseEvent
    {
        public TestEvent() { }

        public TestEvent(EventType eventType, string from, string content)
        {
            this.SetEventType(eventType);
            this.SetFrom(from);
            this.SetContent(content);
            this.SetTime(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss")); ;
        }

        // 消息内容
        [Key(3)]
        public String content;

        public string GetContent()
        {
            return content;
        }

        public void SetContent(string value)
        {
            content = value;
        }

        public override string ToString()
        {
            return String.Format("【{0},{1}】", time, content);
        }
    }
}
