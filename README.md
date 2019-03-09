Netty多语言（Java、Android 、C#、WebSocket）通信实例Demo  
===
包括Java、Android、C#、Websockt通信源码  
博客地址：https://mp.csdn.net/postedit/88358240  

为了方便clone，把三个项目都放到了一起，项目目录介绍：  
1.NettyServer：Java Netty 服务端，客户端，Websocket端  
2.NettyAndroid：Android Netty客户端  
3.NettyCSharp：C# DotNetty客户端  
  需要使用的依赖库：  
  Install-Package DotNetty.Codecs  
  Install-Package DotNetty.Handlers  
  Install-Package MessagePack  
  Install-Package EventBus  

注：现在Demo中使用的端口号为"8888"，Java、C#、WebSocket中IP使用"127.0.0.1",Android中IP使用"192.168.1.111"  
根据实际情况进行修改，项目对应的修改位置为：  
1.NettyServer：com/netty/test/RunServer.java //Java Server  
                 com/netty/test/RunClient.java //Java Client  
               web/index.html //WebSocket Client  
2.NettyAndroid：com/netty/android/MainActivity.java //Android Client  
3.NettyCSharp：MainWindow.xaml.cs //C# Client  

效果图：
---
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/20190307-214118.gif)  
Java服务端  
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/Java%20Server.png)  
Java客户端  
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/Java%20Client.png)  
Android客户端  
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/Android%20Client.png)  
C#客户端  
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/CShart%20Client.png)  
WebSocket客户端  
![](https://github.com/lucher/MultiPlatformNettyDemo/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/WebSocket%20Client.png)  
