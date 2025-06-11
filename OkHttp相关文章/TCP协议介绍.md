# TCP协议详解

TCP(Transmission Control Protocol，传输控制协议)是互联网核心协议之一，位于OSI模型的传输层，提供可靠的、面向连接的字节流服务。

## 一、基本特性

1. **可靠性保证**
    - 数据分片编号(序列号机制)
    - 确认应答(ACK)机制
    - 超时重传机制
    - 数据校验和

2. **连接导向**
    - 通信前需建立连接(三次握手)
    - 通信结束需释放连接(四次挥手)

3. **流量控制**
    - 滑动窗口机制
    - 动态调整发送速率

4. **拥塞控制**
    - 慢启动
    - 拥塞避免
    - 快速重传
    - 快速恢复

## 二、报文段结构

```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|          Source Port          |       Destination Port        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                        Sequence Number                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Acknowledgment Number                      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|  Data |           |U|A|P|R|S|F|                               |
| Offset| Reserved  |R|C|S|S|Y|I|            Window             |
|       |           |G|K|H|T|N|N|                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|           Checksum            |         Urgent Pointer        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Options                    |    Padding    |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                             data                              |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

### 关键字段说明：
- **源端口/目的端口**：各16位，标识发送和接收进程
- **序列号(32位)**：本报文段第一个字节的编号
- **确认号(32位)**：期望收到的下一个字节序号
- **数据偏移(4位)**：TCP首部长度(以32位字为单位)
- **控制标志**：
    - URG：紧急指针有效
    - ACK：确认号有效
    - PSH：接收方应立即推送数据
    - RST：重置连接
    - SYN：同步序列号(建立连接)
    - FIN：结束连接
- **窗口大小(16位)**：接收窗口大小(流量控制)
- **校验和(16位)**：首部和数据的校验和
- **紧急指针(16位)**：紧急数据结束位置

## 三、连接管理

### 3.1 三次握手(建立连接)
1. **SYN**：客户端发送SYN=1, seq=x
2. **SYN+ACK**：服务器回复SYN=1, ACK=1, seq=y, ack=x+1
3. **ACK**：客户端发送ACK=1, seq=x+1, ack=y+1

### 3.2 四次挥手(释放连接)
1. **FIN**：主动方发送FIN=1, seq=u
2. **ACK**：被动方回复ACK=1, ack=u+1
3. **FIN**：被动方发送FIN=1, seq=v
4. **ACK**：主动方回复ACK=1, ack=v+1

## 四、可靠性机制

### 4.1 确认应答(ACK)
- 累计确认：确认号表示期望收到的下一个字节序号
- 选择性确认(SACK)：通过TCP选项实现

### 4.2 超时重传
- 动态计算RTO(Retransmission Timeout)
- Karn算法：解决重传二义性问题

### 4.3 流量控制
- 滑动窗口协议
- 零窗口探测：防止窗口更新丢失

## 五、拥塞控制

### 5.1 核心算法
1. **慢启动**：窗口大小指数增长
2. **拥塞避免**：窗口大小线性增长
3. **快速重传**：收到3个重复ACK立即重传
4. **快速恢复**：重传后进入拥塞避免阶段

### 5.2 现代改进
- TCP Tahoe/Reno/NewReno
- TCP Vegas
- TCP BBR(Google提出的基于带宽的算法)

## 六、典型应用

1. **HTTP协议**：Web浏览
2. **FTP协议**：文件传输
3. **SMTP协议**：邮件传输
4. **SSH协议**：安全远程登录
5. **数据库连接**：MySQL/Oracle等

## 七、Wireshark抓包分析示例

```
Transmission Control Protocol, Src Port: 54321, Dst Port: 80, Seq: 1, Ack: 1, Len: 0
    Source Port: 54321
    Destination Port: 80
    [Stream index: 0]
    [TCP Segment Len: 0]
    Sequence number: 1    (relative sequence number)
    Acknowledgment number: 1    (relative ack number)
    Header Length: 20 bytes
    Flags: 0x010 (ACK)
    Window size value: 64240
    [Calculated window size: 64240]
    Checksum: 0x1234 [unverified]
    [Checksum Status: Unverified]
    Urgent pointer: 0
```

## 八、编程接口(BSD Socket示例)

```c
// TCP客户端示例
int sock = socket(AF_INET, SOCK_STREAM, 0);
struct sockaddr_in serv_addr;
serv_addr.sin_family = AF_INET;
serv_addr.sin_port = htons(8080);
inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr);

connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr));
send(sock, "hello", 5, 0);
recv(sock, buffer, sizeof(buffer), 0);
close(sock);
```

TCP协议通过其完善的可靠性机制和流量控制算法，为上层应用提供了稳定的数据传输服务，是互联网基础设施的重要基石。理解TCP协议的工作原理对于网络编程和性能优化至关重要。