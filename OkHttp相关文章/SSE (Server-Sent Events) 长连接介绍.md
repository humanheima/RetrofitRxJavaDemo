# SSE (Server-Sent Events) 长连接介绍

SSE (Server-Sent Events) 是一种基于 HTTP 的服务器推送技术，允许服务器通过长连接向客户端实时推送数据。

## 主要特点

1. **单向通信**：只支持服务器向客户端推送数据
2. **基于 HTTP**：使用标准 HTTP 协议，不需要特殊协议
3. **长连接**：保持连接开放以便服务器可以持续发送数据
4. **自动重连**：浏览器内置自动重连机制
5. **轻量级**：相比 WebSocket 更简单易用

## 工作原理

1. 客户端通过 EventSource API 发起请求
2. 服务器保持连接打开 (HTTP 1.1 持久连接)
3. 服务器可以随时通过该连接发送事件
4. 连接保持打开状态直到任一方主动关闭

## 与 WebSocket 对比

| 特性        | SSE            | WebSocket         |
|------------|----------------|-------------------|
| 通信方向    | 服务器→客户端单向      | 全双工双向        |
| 协议        | HTTP           | 独立协议 (ws://)  |
| 数据格式    | 文本/或者编码后的二进制数据 | 二进制或文本      |
| 自动重连    | 内置支持           | 需要手动实现      |
| 复杂性      | 简单             | 相对复杂          |

## 适用场景

- 实时通知系统
- 股票行情推送
- 新闻实时更新
- 社交媒体动态
- 需要服务器推送但不需要客户端发送数据的场景

## 基本实现示例

**客户端代码 (JavaScript):**
```javascript
const eventSource = new EventSource('/sse-endpoint');

eventSource.onmessage = (event) => {
  console.log('收到消息:', event.data);
};

eventSource.onerror = (error) => {
  console.error('SSE错误:', error);
};
```

**服务器端示例 (Node.js):**
```javascript
app.get('/sse-endpoint', (req, res) => {
  res.writeHead(200, {
    'Content-Type': 'text/event-stream',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive'
  });

  // 定期发送数据
  const interval = setInterval(() => {
    res.write(`data: ${JSON.stringify({time: new Date()})}\n\n`);
  }, 1000);

  // 客户端断开连接时清理
  req.on('close', () => {
    clearInterval(interval);
  });
});
```

SSE 是一种简单有效的服务器推送技术，特别适合需要从服务器到客户端的单向实时数据流场景。


# SSE 是否可以发送二进制数据

SSE (Server-Sent Events) 协议本身是设计用于传输文本数据的，**原生不支持直接发送二进制数据**。这是它与 WebSocket 的一个重要区别。

## SSE 数据传输特性

1. **文本格式**：SSE 规范要求数据以 UTF-8 编码的文本形式传输
2. **消息结构**：每条消息格式为 `field: value\n`，最后以两个换行符 `\n\n` 结束
3. **事件流**：内容类型必须为 `text/event-stream`

## 变通方案

虽然不能直接发送二进制数据，但有几种方法可以在 SSE 中传输二进制内容：

### 1. Base64 编码

将二进制数据转换为 Base64 字符串：

```javascript
// 服务器端 Node.js 示例
const buffer = getSomeBinaryData(); // 获取二进制数据
const base64Data = buffer.toString('base64');
res.write(`data: ${base64Data}\n\n`);
```

客户端解码：
```java
// Android 端处理
@Override
public void onEvent(String event, String data) {
    byte[] binaryData = Base64.decode(data, Base64.DEFAULT);
    // 处理二进制数据
}
```

### 2. 十六进制编码

将二进制数据转换为十六进制字符串：

```javascript
// 服务器端
const hexString = buffer.toString('hex');
res.write(`data: ${hexString}\n\n`);
```

### 3. 发送二进制数据的引用

发送二进制数据的 URL 或标识符，让客户端再发起请求获取：

```javascript
res.write(`event: binary-ref\ndata: {"url":"/binary-data/123"}\n\n`);
```

## 限制与考虑

1. **性能影响**：
    - 编码后的数据大小会增加约33%（Base64）
    - 需要额外的编码/解码步骤

2. **带宽消耗**：
    - 文本表示比原始二进制占用更多带宽

3. **延迟增加**：
    - 编码/解码过程会引入少量处理延迟

## 结论

虽然 SSE 不能直接发送二进制数据，但通过编码转换可以实现二进制内容的传输。对于二进制数据密集型的应用，WebSocket 通常是更好的选择，而 SSE 更适合文本为主的实时数据推送场景。

# 和 WebSocket 对比

# WebSocket 协议介绍

WebSocket 是一种在单个 TCP 连接上进行全双工通信的协议，它实现了浏览器与服务器之间的实时双向数据传输。

## 核心特性

1. **全双工通信**：客户端和服务器可以同时发送和接收数据
2. **基于TCP**：运行在TCP协议之上
3. **低延迟**：建立连接后数据可以即时传输
4. **轻量级**：相比HTTP头部开销小
5. **跨域支持**：内置跨域通信能力

## 协议握手过程

WebSocket 连接通过HTTP升级机制建立：

1. **客户端发起握手请求**：
   ```
   GET /chat HTTP/1.1
   Host: example.com
   Upgrade: websocket
   Connection: Upgrade
   Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
   Sec-WebSocket-Version: 13
   ```

2. **服务器响应握手**：
   ```
   HTTP/1.1 101 Switching Protocols
   Upgrade: websocket
   Connection: Upgrade
   Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=
   ```

## 数据帧格式

WebSocket 数据帧包含以下部分：

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

## 主要应用场景

1. 实时聊天应用
2. 多人在线游戏
3. 实时数据监控系统
4. 协同编辑工具
5. 股票交易平台
6. 实时体育赛事更新

## 与HTTP对比

| 特性          | WebSocket                  | HTTP                      |
|---------------|---------------------------|--------------------------|
| 通信模式      | 全双工                    | 半双工(请求-响应)        |
| 连接持久性    | 持久连接                  | 短连接(通常)             |
| 头部开销      | 初始握手后很小            | 每个请求都有完整头部     |
| 数据推送      | 服务器可主动推送          | 只能客户端发起请求       |
| 延迟          | 低                        | 较高                     |

## 客户端实现示例

**JavaScript客户端**：
```javascript
const socket = new WebSocket('wss://example.com/chat');

socket.onopen = function(e) {
  console.log("连接已建立");
  socket.send("Hello Server!");
};

socket.onmessage = function(event) {
  console.log(`收到数据: ${event.data}`);
};

socket.onclose = function(event) {
  console.log(`连接关闭，代码=${event.code} 原因=${event.reason}`);
};

socket.onerror = function(error) {
  console.error(`发生错误: ${error.message}`);
};
```

**Android客户端(使用OkHttp)**:
```java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
    .url("ws://example.com/chat")
    .build();

WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("Hello Server!");
    }
    
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("收到消息: " + text);
    }
    
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("连接关闭: " + reason);
    }
    
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
});
```

## 服务器端实现示例

**Node.js (使用ws库)**:
```javascript
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

wss.on('connection', function connection(ws) {
  console.log('新客户端连接');
  
  ws.on('message', function incoming(message) {
    console.log('收到消息: %s', message);
    ws.send(`服务器回复: ${message}`);
  });
  
  ws.on('close', function close() {
    console.log('客户端断开连接');
  });
});
```

## 安全性考虑

1. 始终使用 `wss://` (WebSocket Secure) 替代 `ws://`
2. 验证来源(Origin)头防止跨站劫持
3. 实现速率限制防止滥用
4. 对传输数据进行验证和清理

## 协议优势

1. 相比轮询和长轮询更高效
2. 适合需要低延迟的实时应用
3. 减少不必要的网络流量
4. 现代浏览器广泛支持
5. 可以传输文本和二进制数据


参考链接：

感觉可以直接问 DeepSeek

* [Server-Sent Events 教程](https://www.ruanyifeng.com/blog/2017/05/server-sent_events.html)
