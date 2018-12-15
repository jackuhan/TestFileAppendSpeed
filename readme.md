# 一、如何写入文件到硬盘I/O
首先了解下换行符在不同平台不一样 String line = System.lineSeparator();
if ("\r\n".equals(line)) {
System.out.println("windows");
} else if ("\n".equals(line)) {
System.out.println("Mac");
}else  if ("\r".equals(line)) {
System.out.println("linux/unix");
}
## 1、FileOutputStream
```
public static void method2(String file, String conent) {
BufferedWriter out = null;
try {
out = new BufferedWriter(new OutputStreamWriter(
new FileOutputStream(file, true)));
out.write(conent+"\r\n");
} catch (Exception e) {
e.printStackTrace();
} finally {
try {
out.close();
} catch (IOException e) {
e.printStackTrace();
}
}
```

## 2、RandomAccessFile + NIO内存映射文件
```
public static void method3(String fileName, String content) {
try {
// 打开一个随机访问文件流，按读写方式
RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
// 文件长度，字节数
long fileLength = randomFile.length();
// 将写文件指针移到文件尾。
randomFile.seek(fileLength);
randomFile.writeBytes(content+"\r\n");//不同平台的换行符不一样
randomFile.close();
} catch (IOException e) {
e.printStackTrace();
}
}
}
```
## [https://blog.csdn.net/jdsjlzx/article/details/51648044](https://blog.csdn.net/jdsjlzx/article/details/51648044)
## RandomAccessFile的绝大多数功能，但不是全部，已经被JDK 1.4的NIO的”内存映射文件(memory-mapped files)”给取代了，你该考虑一下是不是用”内存映射文件”来代替RandomAccessFile了。
**内存映射文件(memory-mapped file)能让你创建和修改那些大到无法读入内存的文件。**

public class LargeMappedFiles {
    static int length = 0x8000000; *// 128 Mb*

    public static void main(String[] args) throws Exception {
        *// 为了以可读可写的方式打开文件，这里使用RandomAccessFile来创建文件。*
        FileChannel fc = new RandomAccessFile("test.dat", "rw").getChannel();
        *//注意，文件通道的可读可写要建立在文件流本身可读写的基础之上*
        MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, 0, length);
        *//写128M的内容*
        for (int i = 0; i < length; i++) {
            out.put((byte) 'x');
        }
        System.out.println("Finished writing");
        *//读取文件中间6个字节内容*
        for (int i = length / 2; i < length / 2 + 6; i++) {
            System.out.print((char) out.get(i));
        }
        fc.close();
    }
## }

## 3、FileWriter  BufferedWriter PrintWriter
### 1、FileWriter
[https://sunnylocus.iteye.com/blog/694666?page=2](https://sunnylocus.iteye.com/blog/694666?page=2)
```
  /** 
     * 将信息记录到日志文件 
     * @param logFile 日志文件 
     * @param mesInfo 信息 
     * @throws IOException  
     */  
public void logMsg(File logFile,String mesInfo) throws IOException{  
if(logFile == null) {  
  throw new IllegalStateException("logFile can not be null!");  
  }  
 Writer txtWriter = new FileWriter(logFile,true);  
 txtWriter.write(dateFormat.format(new Date()) +"\t"+mesInfo+"\n");  
 txtWriter.flush();  
}  
```


### 2、BufferedWriter
[https://github.com/itgoyo/LogToFile/blob/master/logtofilelibrary/src/main/java/com/itgoyo/logtofilelibrary/LogToFileUtils.java](https://github.com/itgoyo/LogToFile/blob/master/logtofilelibrary/src/main/java/com/itgoyo/logtofilelibrary/LogToFileUtils.java)
```
try {
BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
bw.write(logStr);
bw.write("\r\n");
bw.flush();
} catch (Exception e) {
Log.e(tag, "Write failure !!! " + e.toString());
}
```

[https://blog.csdn.net/high2011/article/details/49620125](https://blog.csdn.net/high2011/article/details/49620125) 
FileWriter fw=new FileWriter("d:/x.log",true);//true代表不覆盖文件的内容，而是紧跟着添加内容
BufferedWriter bw=new BufferedWriter(fw);
bw.writer(message); //message是String类型的参数
bw.close() ; //关闭流
fw.close();   //关闭流
区别：如果同时使用bw fw，那么性能会大大提高，而单独使用FileWriter操作字符，每写一次数据，磁盘就有一个写操作，性能很差。
如果加了缓冲，那么会等到缓冲满了以后才会有写操作，效率和性能都有很大提高。


### 3、PrintWriter
[https://blog.csdn.net/flamezyg/article/details/5190796](https://blog.csdn.net/flamezyg/article/details/5190796) 
PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("foo.out")));
 PrintWriter pw = new PrintWriter(System.out);
PrintWriter out
   = new PrintWriter(new BufferedOutputStream(new FileOutputStream("foo.out")));
 将缓冲 PrintWriter 对文件的输出。如果没有缓冲，则每次调用 print() 方法会导致将字符转换为字节，然后立即写入到文件，而这是极其低效的。

[https://blog.csdn.net/qq1175421841/article/details/52411607](https://blog.csdn.net/qq1175421841/article/details/52411607) 
	**Socket编程中,尽量用PrintWriter取代BufferedWriter**
1. PrintWriter的print、println方法可以接受任意类型的参数（字符，字节流），而BufferedWriter的write方法只能接受字符、字符数组和字符串；

2. PrintWriter的println方法自动添加换行，BufferedWriter需要显示调用newLine方法；

3. PrintWriter的方法不会抛异常，若关心异常，需要调用checkError方法看是否有异常发生；

4. PrintWriter构造方法可指定参数，实现自动刷新缓存（autoflush）；

5. PrintWriter的构造方法更广。

### 4、flush
不使用flush()
      String s = "Hello World";
        try {
            PrintWriter pw = new PrintWriter(System.out);
            pw.write(s);
//            pw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

输出：
buffer没有满，输出为空。
使用flush()
        String s = "Hello World";
        try {
            PrintWriter pw = new PrintWriter(System.out);
            pw.write(s);
           pw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
得到期望的输出结果。

### 5、close()和flush()作用有交集！
public static void main(String[] args) {
        BufferedWriter fw =null;
        try {
            fw =  new BufferedWriter(new FileWriter("e:\\test.txt"));
            fw.write("wo shi lucky girl.");
            //fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 //fw.flush();这句有和无并不影响输出结果，不太明白词句是否必要？
因为close的时候，会把你没flush掉的一起flush掉。
缓冲区中的数据保存直到缓冲区满后才写出，也可以使用flush方法将缓冲区中的数据强制写出或使用close()方法关闭流，关闭流之前，缓冲输出流将缓冲区数据一次性写出。在这个例子中，flash()和close()都使数据强制写出，所以两种结果是一样的，如果都不写的话，会发现不能成功写出
### 6、Java默认缓冲区大小是多少？
默认缓冲去大小8192字节。

## 4、Java 各种文件writer 性能大比拼
[http://ju.outofmemory.cn/entry/181638](http://ju.outofmemory.cn/entry/181638)
Map File’s performance is the highest
* RandomAccessFile with ByteBuffer:153600 test2.tmp spent time=508ms 

* RandomAccessFile with ByteBuffer:1048576 test3.tmp spent time=422ms 

* RandomAccessFile + with MappedByteBuffer and FileMap:104857600 test4.tmp spent time=110ms 

* DataOutputStream with BufferedOutputStream :153600 test5.tmp spent time=1103 ms 

* DataOutputStream without BufferedOutputStream :153600 test6.tmp spent time=680543 ms 

* PrintWriter/FileWriter with BufferedWriter:153600 test7.tmp spent time=2209 ms 

* PrintWriter/FileWriter without BufferedWriter:153600 test8.tmp spent time=2284 ms 

* PrintWriter/OutputStreamWriter with BufferedWriter:153600 test9.tmp spent time=1326 ms 

* PrintWriter/OutputStreamWriter without BufferedWriter:153600 test10.tmp spent time=2336 ms

最快代码
```
test3("test4.tmp", 1000000, 1024 * 1024 * 100);
```
RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
    FileChannel fc = raf1.getChannel();
    MappedByteBuffer raf = fc.map(MapMode.READ_WRITE, 0, mapsize);
    raf.clear();
    // ByteBuffer raf = ByteBuffer.allocateDirect(mapsize);
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    long count = 0;
    for (int i = 0; i < loop; i++) {
      if (raf.remaining() < 140) {
        System.out.println("remap");
        count += raf.position();
        raf = fc.map(MapMode.READ_WRITE, count, mapsize);
        // raf = fc.map(MapMode.READ_WRITE, raf.position(),
        // raf.position()+mapsize);
      }
      raf.put(b1);
      raf.putInt(i);
      raf.putInt(i + 1);
      raf.put(utfstr);
      raf.put((byte) '\n');
    }
    fc.close();
    raf1.close();

全部代码
```
public class TestAppendSpeed {
  public static void main(String[] args) throws Exception {
    // Raw RandomAccessFile
    // it is too slow to comment it
    // test1("test1.tmp",100000);
    // nio ByteBuffer RandomAccessFile
    test2("test2.tmp", 1000000, 1024 * 150);
    // nio ByteBuffer RandomAccessFile, with big buffer
    test2("test3.tmp", 1000000, 1024 * 1024);
    // using map file with MappedByteBuffer
    test3("test4.tmp", 1000000, 1024 * 1024 * 100);
    // old io, BufferedOutputStream with BufferedOutputStream
    test4("test5.tmp", 1000000, 1024 * 150);
    // old io, BufferedOutputStream without BufferedOutputStream
    // it is too slow to comment it
    // test5("test6.tmp", 1000000, 1024 * 150);
    // old io, PrintWriter/FileWriter with BufferedWriter
    test6("test7.tmp", 1000000, 1024 * 150);
    // old io, PrintWriter/FileWriter without BufferedWriter
    test7("test8.tmp", 1000000, 1024 * 150);
    // old io, PrintWriter/OutputStreamWriter without BufferedWriter
    test8("test9.tmp", 1000000, 1024 * 150);
    // old io, PrintWriter/FileWriter without BufferedWriter
    test9("test10.tmp", 1000000, 1024 * 150);
  }
  public static void test9(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    PrintWriter raf = new PrintWriter(new OutputStreamWriter(
        new FileOutputStream(file, true)));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.print(b1);
      raf.print(i);
      raf.print(i + 1);
      raf.print(utfstr);
      raf.print((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println("PrintWriter/OutputStreamWriter without BufferedWriter:"
            + mapsize
            + " "
            + file
            + " spent time="
            + (d / 1000000)
            + " ms");
  }
  public static void test8(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    PrintWriter raf = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file, true)),
        mapsize));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.print(b1);
      raf.print(i);
      raf.print(i + 1);
      raf.print(utfstr);
      raf.print((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println("PrintWriter/OutputStreamWriter with BufferedWriter:"
            + mapsize + " " + file + " spent time=" + (d / 1000000)
            + " ms");
  }
  public static void test7(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    PrintWriter raf = new PrintWriter(new FileWriter(file));
    // output = new PrintWriter(new OutputStreamWriter(new
    // FileOutputStream(file, true)));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.print(b1);
      raf.print(i);
      raf.print(i + 1);
      raf.print(utfstr);
      raf.print((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println("PrintWriter/FileWriter without BufferedWriter:"
            + mapsize + " " + file + " spent time=" + (d / 1000000)
            + " ms");
  }
  public static void test6(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    PrintWriter raf = new PrintWriter(new BufferedWriter(new FileWriter(
        file), mapsize));
    // output = new PrintWriter(new OutputStreamWriter(new
    // FileOutputStream(file, true)));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.print(b1);
      raf.print(i);
      raf.print(i + 1);
      raf.print(utfstr);
      raf.print((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println("PrintWriter/FileWriter with BufferedWriter:"
            + mapsize + " " + file + " spent time=" + (d / 1000000)
            + " ms");
  }
  public static void test5(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    DataOutputStream raf = new DataOutputStream(new FileOutputStream(
        new File(file), true));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.write(b1);
      raf.writeInt(i);
      raf.writeInt(i + 1);
      raf.write(utfstr);
      raf.write((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println(" DataOutputStream without BufferedOutputStream :"
            + mapsize + " " + file + " spent time=" + (d / 1000000)
            + " ms");
  }
  public static void test4(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    DataOutputStream raf = new DataOutputStream(new BufferedOutputStream(
        new FileOutputStream(new File(file), true), mapsize));
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.write(b1);
      raf.writeInt(i);
      raf.writeInt(i + 1);
      raf.write(utfstr);
      raf.write((byte) '\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out
        .println(" DataOutputStream with BufferedOutputStream :"
            + mapsize + " " + file + " spent time=" + (d / 1000000)
            + " ms");
  }
  public static void test3(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
    FileChannel fc = raf1.getChannel();
    MappedByteBuffer raf = fc.map(MapMode.READ_WRITE, 0, mapsize);
    raf.clear();
    // ByteBuffer raf = ByteBuffer.allocateDirect(mapsize);
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    long count = 0;
    for (int i = 0; i < loop; i++) {
      if (raf.remaining() < 140) {
        System.out.println("remap");
        count += raf.position();
        raf = fc.map(MapMode.READ_WRITE, count, mapsize);
        // raf = fc.map(MapMode.READ_WRITE, raf.position(),
        // raf.position()+mapsize);
      }
      raf.put(b1);
      raf.putInt(i);
      raf.putInt(i + 1);
      raf.put(utfstr);
      raf.put((byte) '\n');
    }
    fc.close();
    raf1.close();
    long d = System.nanoTime() - s;
    System.out
        .println("RandomAccessFile + with MappedByteBuffer and FileMap:"
            + mapsize
            + " "
            + file
            + " spent time="
            + (d / 1000000)
            + "ms");
  }
  public static void test2(String file, int loop, int mapsize)
      throws Exception {
    new File(file).delete();
    RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
    FileChannel fc = raf1.getChannel();
    ByteBuffer raf = ByteBuffer.allocate(mapsize);
    raf.clear();
    // ByteBuffer raf = ByteBuffer.allocateDirect(mapsize);
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.put(b1);
      raf.putInt(i);
      raf.putInt(i + 1);
      raf.put(utfstr);
      raf.put((byte) '\n');
      if (raf.remaining() < 140) {
        raf.flip();
        fc.write(raf);
        raf.compact();
      }
    }
    raf.flip();
    fc.write(raf);
    fc.close();
    raf1.close();
    long d = System.nanoTime() - s;
    System.out.println("RandomAccessFile with ByteBuffer:" + mapsize + " "
        + file + " spent time=" + (d / 1000000) + "ms");
  }
  public static void test1(String file, int loop) throws Exception {
    new File(file).delete();
    RandomAccessFile raf = new RandomAccessFile(file, "rw");
    byte[] b1 = new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    byte[] utfstr = "this is a test".getBytes("UTF-8");
    long s = System.nanoTime();
    for (int i = 0; i < loop; i++) {
      raf.write(b1);
      raf.writeInt(i);
      raf.writeInt(i + 1);
      raf.write(utfstr);
      raf.write('\n');
    }
    raf.close();
    long d = System.nanoTime() - s;
    System.out.println("RandomAccessFile without any buffer " + file
        + "  spent time=" + (d / 1000000) + "ms");
  }
}
```
# 

# 二、管理多个日志文件，用实例
1、用实例
[https://github.com/InnerNight/log-to-file/blob/master/app/src/main/java/com/liye/log/FileUtils.java](https://github.com/InnerNight/log-to-file/blob/master/app/src/main/java/com/liye/log/FileUtils.java)
 public void writeLogToFile(String logMessage) {
        if (mCurrentLogFile == null || mCurrentLogFile.length() >= LOG_FILE_MAX_SIZE) {
            mCurrentLogFile = getNewLogFile();
        }
        FileUtils.writeToFile(logMessage, mCurrentLogFile.getPath());
    }

public static void writeToFile(String content, String filePath) {
        FileWriter fileWriter = null;
        try {
            //fileWriter = new FileWriter(filePath, true);
PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath,true)));
            fileWriter.write(content);
            fileWriter.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

# 三、对象池
[https://cloud.tencent.com/developer/article/1121248](https://cloud.tencent.com/developer/article/1121248)

org.slf4j.impl.Log4jLoggerFactory类：
public class Log4jLoggerFactory implements ILoggerFactory {

    // 线程安全的ConcurrentHashMap对象，保存日志对象；
    ConcurrentMap<String, Logger> loggerMap;

    public Log4jLoggerFactory() {
        loggerMap = new ConcurrentHashMap<String, Logger>();
    }
    
    //创造Logger对象：实际返回的是与slf4j相结合的日志对象；
    public Logger getLogger(String name) {
        
        //通过类名称获取日志对象：
        Logger slf4jLogger = loggerMap.get(name);
        
        //不为空，则返回：
        if (slf4jLogger != null) {
            return slf4jLogger;
        } else {
            //log4j日志对象：
            org.apache.log4j.Logger log4jLogger;

            if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
                //开始log4j的初始化过程：
                log4jLogger = LogManager.getRootLogger();
            }else {
                //开始log4j的初始化过程：
                log4jLogger = LogManager.getLogger(name);
            }
            
            //通过Log4jLoggerAdapter对象，对log4j的日志对象进行封装，使用了适配器模式：
            Logger newInstance = new Log4jLoggerAdapter(log4jLogger);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }
}
# 四、异步
 因为logback比log4j大约快10倍、消耗更少的内存，迁移成本也很低，自动压缩日志、支持多样化配置、不需要重启就可以恢复I/O异常等优势，又名噪一时。

[https://blog.csdn.net/qq496013218/article/details/76603779](https://blog.csdn.net/qq496013218/article/details/76603779)
logback高级特性，AsyncAppender，异步记录日志。

工作原理：
当Logging Event进入AsyncAppender后，AsyncAppender会调用appender方法，append方法中在将event填入Buffer(这里选用的数据结构为BlockingQueue)中前，会先判断当前buffer的容量以及丢弃日志特性是否开启，当消费能力不如生产能力时，AsyncAppender会超出Buffer容量的Logging Event的级别，进行丢弃，作为消费速度一旦跟不上生产速度，中转buffer的溢出处理的一种方案。AsyncAppender有个线程类Worker，它是一个简单的线程类，是AsyncAppender的后台线程，所要做的工作是：从buffer中取出event交给对应的appender进行后面的日志推送。

从上面的描述中可以看出，AsyncAppender并不处理日志，只是将日志缓冲到一个BlockingQueue里面去，并在内部创建一个工作线程从队列头部获取日志，之后将获取的日志循环记录到附加的其他appender上去，从而达到不阻塞主线程的效果。因此AsynAppender仅仅充当事件转发器，必须引用另一个appender来做事。

在使用AsyncAppender的时候，有些选项还是要注意的。由于使用了BlockingQueue来缓存日志，因此就会出现队列满的情况。正如上面原理中所说的，在这种情况下，AsyncAppender会做出一些处理：默认情况下，如果队列80%已满，AsyncAppender将丢弃TRACE、DEBUG和INFO级别的event，从这点就可以看出，该策略有一个惊人的对event丢失的代价性能的影响。另外其他的一些选项信息，也会对性能产生影响，下面列出常用的几个属性配置信息：
| 属性名   | 类型   | 描述   | 
|:----|:----|:----|
| queueSize   | int   | BlockingQueue的最大容量，默认情况下，大小为256。   | 
| discardingThreshold   | int   | 默认情况下，当BlockingQueue还有20%容量，他将丢弃TRACE、DEBUG和INFO级别的event，只保留WARN和ERROR级别的event。为了保持所有的events，设置该值为0。   | 
| includeCallerData   | boolean   | 提取调用者数据的代价是相当昂贵的。为了提升性能，默认情况下，当event被加入到queue时，event关联的调用者数据不会被提取。默认情况下，只有"cheap"的数据，如线程名。   | 


默认情况下，event queue配置最大容量为256个events。如果队列被填满，应用程序线程被阻止记录新的events，直到工作线程有机会来转发一个或多个events。因此队列深度需要根据业务场景进行相应的测试，做出相应的更改，以达到较好的性能。

下面给出一个使用的配置示例：
<appender name="FILE" class= "ch.qos.logback.core.rolling.RollingFileAppender">  
            *<!-- 按天来回滚，如果需要按小时来回滚，则设置为{yyyy-MM-dd_HH} -->*  
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">  
                 <fileNamePattern>/opt/log/test.%d{yyyy-MM-dd}.log</fileNamePattern>  
                 *<!-- 如果按天来回滚，则最大保存时间为1天，1天之前的都将被清理掉 -->*  
                 <maxHistory>30</maxHistory>  
            *<!-- 日志输出格式 -->*  
            <layout class="ch.qos.logback.classic.PatternLayout">  
                 <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%msg%n</Pattern>  
            </layout>  
</appender>  
     *<!-- 异步输出 -->*  
     <appender name ="ASYNC" class= "ch.qos.logback.classic.AsyncAppender">  
            *<!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->*  
            <discardingThreshold >0</discardingThreshold>  
            *<!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->*  
            <queueSize>512</queueSize>  
            *<!-- 添加附加的appender,最多只能添加一个 -->*  
         <appender-ref ref ="FILE"/>  
     </appender>  
       
     <root level ="trace">  
            <appender-ref ref ="ASYNC"/>  
     </root>  




# 五、连接池

实现的基本思想：
在要使用连接对象之前先创建好规定数量（根据服务器内存的承载能力制定）的连接对象存到放连接池（实现池子的方式一般是用链表结构的集合来实现）中，当应用服务器需要连接对象的时候就从连接池中获取，用完该连接对象时归还连接对象到连接池中。当应用服务器需要连接对象而当前池子中没有连接对象可取时，就让其先等待，如果等待超时还没有回获取到连接对象，就新建一个连接对象给服务器让其使用，用完后销毁该创建的对象。

针对日志，考虑的是多线程环境就需要服用连接，使用连接池。如果不是多线程环境就无必要。


![图片](https://uploader.shimo.im/f/RgOMY2Zl47gPLP3k.png!thumbnail)


```
 
```

