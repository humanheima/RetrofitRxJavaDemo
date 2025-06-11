# WebSocket 协议详解

WebSocket 是一种现代网络协议，它提供了在单个 TCP 连接上进行全双工通信的能力，特别适合需要实时数据交换的应用程序。

## 一、基本概念

### 1.1 协议本质
- **基于TCP**：运行在传输层TCP协议之上
- **全双工通信**：允许客户端和服务器同时独立地发送数据
- **持久连接**：一次握手后保持长期连接
- **低延迟**：避免了HTTP的请求-响应循环

### 1.2 协议标识
- 非加密连接：`ws://` (默认端口80)
- SSL加密连接：`wss://` (默认端口443)

## 二、协议握手过程

### 2.1 客户端请求
```http
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==
Sec-WebSocket-Version: 13
Origin: http://example.com
```

关键字段说明：
- `Upgrade: websocket` - 请求协议升级
- `Sec-WebSocket-Key` - 随机生成的Base64编码密钥
- `Sec-WebSocket-Version` - 指定协议版本(13为当前标准)

### 2.2 服务器响应
```http
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
```

关键字段说明：
- `101状态码` - 表示协议切换成功
- `Sec-WebSocket-Accept` - 对客户端密钥计算后的响应值

## 三、数据帧结构详解

WebSocket 数据帧采用二进制格式：

```
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-------+-+-------------+-------------------------------+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
|N|V|V|V|       |S|             |   (if payload len==126/127)   |
| |1|2|3|       |K|             |                               |
+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
|     Extended payload length continued, if payload len == 127  |
+ - - - - - - - - - - - - - - - +-------------------------------+
|                               |Masking-key, if MASK set to 1  |
+-------------------------------+-------------------------------+
| Masking-key (continued)       |          Payload Data         |
+-------------------------------- - - - - - - - - - - - - - - - +
:                     Payload Data continued ...                :
+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
|                     Payload Data continued ...                |
+---------------------------------------------------------------+
```

### 3.1 关键字段说明

1. **FIN (1bit)**：是否为消息的最后一帧
2. **RSV1-3 (各1bit)**：保留位，通常为0
3. **Opcode (4bits)**：
    - 0x0: 延续帧
    - 0x1: 文本帧
    - 0x2: 二进制帧
    - 0x8: 连接关闭
    - 0x9: Ping帧
    - 0xA: Pong帧
4. **Mask (1bit)**：客户端→服务器必须置1(掩码处理)
5. **Payload length**：
    - 0-125: 实际长度
    - 126: 后续2字节表示长度
    - 127: 后续8字节表示长度
6. **Masking-key (0或4字节)**：掩码密钥
7. **Payload data**：实际应用数据

## 四、心跳机制

WebSocket 通过Ping/Pong帧维持连接：

- **Ping帧**：可由任一方发送，包含应用数据(可选)
- **Pong帧**：必须对Ping做出响应，携带相同数据

## 五、协议关闭流程

1. 发送关闭帧(opcode=0x8)
2. 可包含关闭原因(status code + reason)
3. 收到关闭帧后应回送关闭帧
4. 完全关闭TCP连接

## 六、实际应用示例

### 6.1 JavaScript客户端
```javascript
// 创建WebSocket连接
const socket = new WebSocket('wss://echo.websocket.org');

// 连接打开事件
socket.addEventListener('open', (event) => {
  socket.send('Hello Server!');
  console.log('连接已建立');
});

// 接收消息事件
socket.addEventListener('message', (event) => {
  console.log('收到消息:', event.data);
});

// 错误处理
socket.addEventListener('error', (error) => {
  console.error('WebSocket错误:', error);
});

// 连接关闭事件
socket.addEventListener('close', (event) => {
  console.log('连接关闭:', event.code, event.reason);
});
```

### 6.2 Java服务器端(使用Tyrus)
```java
@ServerEndpoint("/chat")
public class ChatEndpoint {
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("新连接: " + session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息: " + message);
        session.getAsyncRemote().sendText("ECHO: " + message);
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("连接关闭: " + session.getId());
    }
    
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }
}
```

## 七、性能优化建议

1. **消息压缩**：使用 permessage-deflate 扩展
2. **批处理**：合并小消息为单个帧
3. **二进制传输**：优先使用二进制帧而非文本帧
4. **连接复用**：避免频繁重建连接
5. **合理心跳**：设置适当的心跳间隔(通常30-60秒)

WebSocket协议为现代实时Web应用提供了高效的双向通信机制，理解其底层原理和最佳实践对于构建高性能实时应用至关重要。