using DotNetty.Codecs;
using DotNetty.Transport.Bootstrapping;
using DotNetty.Transport.Channels;
using DotNetty.Transport.Channels.Sockets;
using EventBus;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace NettyCSharp
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {

        // Server端IP地址，根据实际情况进行修改
        public static IPAddress Host = IPAddress.Parse("127.0.0.1");
        // Netty服务端监听端口号
        public static int Port = 8888;
        //Netty客户端
        private NettyClient mClient;

        public MainWindow()
        {
            InitializeComponent();
            //加入窗口关闭监听
            this.Closing += Window_Closing;

            //注册EventBus监听
            SimpleEventBus eventBus = SimpleEventBus.GetDefaultEventBus();
            eventBus.Register(this);
            //连接服务端
            mClient = new NettyClient(Host, Port);
            mClient.StartClient();
        }

        //发送按钮点击
        private void Send_Click(object sender, RoutedEventArgs e)
        {
            mClient.SendMessage(new TestEvent(EventType.TEST_EVENT, "C# ", TextBox.Text));
        }

        //事件处理
        [EventSubscriber]
        public void HandleEvent(string message)
        {
            Action action = () =>
            {
                TextBlock.Text =  message+ "\n" + TextBlock.Text;
            };
            TextBlock.Dispatcher.BeginInvoke(action);
        }

        //窗口关闭监听
        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (MessageBox.Show("是否要关闭？", "确认", MessageBoxButton.YesNo) == MessageBoxResult.Yes)
            {
                e.Cancel = false;
            }
            else
            {
                e.Cancel = true;
                SimpleEventBus.GetDefaultEventBus().Deregister(this);
            }
        }

    }
}
