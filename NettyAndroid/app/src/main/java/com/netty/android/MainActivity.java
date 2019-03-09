package com.netty.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.netty.android.client.NettyClient;
import com.netty.android.event.EventType;
import com.netty.android.event.TestEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Netty示例安卓端主界面
 *
 * @author lucher
 */
public class MainActivity extends AppCompatActivity {

    // Server端IP地址，根据实际情况进行修改
    static final String HOST = System.getProperty("host", "192.168.1.111");
    // Netty服务端监听端口号
    static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));

    //发送内容编辑框
    @BindView(R.id.editText)
    EditText editText;
    //内容显示区域
    @BindView(R.id.textView)
    TextView textView;

    //Netty 客户端
    private NettyClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //连接服务器端
        mClient = new NettyClient(HOST, PORT);
        mClient.start();
        //让内容区可以滚动
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * 开始按钮点击
     */
    @OnClick(R.id.btnSend)
    public void onSendClick() {
        try {
            // 发送测试消息
            mClient.sendMessage(new TestEvent(EventType.TEST_EVENT, "Android", editText.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * EventBus事件处理
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String msg) {
        textView.setText(msg + "\n" + textView.getText().toString());
    }

    //EventBus注册
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    //取消EventBus注册
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
