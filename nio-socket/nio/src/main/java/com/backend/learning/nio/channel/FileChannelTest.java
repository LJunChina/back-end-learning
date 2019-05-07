package com.backend.learning.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class FileChannelTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        //自动关闭接口
        try(StreamOperator operator = new StreamOperator()) {
            System.out.println("使用流操作");
        }catch (Exception e){

        }
        /**Channel分支核心接口及类：
         * 1.AsynchronousChannel异步I/O通道，回调CompletionHandler
         * 2.ReadableByteChannel使通道允许对字节进行读取操作，只允许1个读操作
         * 3.ScatteringByteChannel可以从通道中读取字节到多个缓冲区中
         * 4.WritableByteChannel使通道允许对字节进行写操作，与ReadableByteChannel对应，只允许1个写操作
         * 5.GatheringByteChannel与ScatteringByteChannel对应，将多个缓冲区的数据写入通道
         * 6.ByteChannel统一了WritableByteChannel和ReadableByteChannel，具备双向操作
         * 7.SeekableByteChannel在字节通道中维护position，以及改变position.父类为ByteChannel
         * 8.NetWorkChannel继承于Channel.使通道与Socket关联，使通道中的数据能在Socket技术上进行传输
         * 9.MultiCastChannel继承于NetworkChannel，使通道支持Internet Protocol（IP）多播。
         * 10.InterruptibleChannel使通道能以异步的方式进行关闭和中断
         * 11.AbstractInterruptibleChannel为FileChannel的父类，它提供了一个可以被中断的通道基本实现类
         * 12.FileChannel用于读取、写入、映射和操作文件的通道。该通道永远是阻塞的操作*/

        /**FileChannel：
         * 获得文件通道方式：FileInputStream、FileOutPutStream、RandomAccessFile、FileChannel.open() 1.7新增
         * */
        /**{@link java.nio.channels.FileChannel#write(ByteBuffer)}*/
        /**在任意给定时刻，一个可写入通道上只能进行一个写入操作。该方法是同步的*/
        /**验证{@link FileChannel#write(ByteBuffer)}方法是从通道的当前位置开始写入的*/
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.wrap("abcd".getBytes());
        System.out.println("channel position:" + channel.position());
        int b = channel.write(buffer);
        System.out.println("write:"+ b);
        System.out.println("channel position:" + channel.position());
        channel.position(2);
        //还原buffer后再写入
        buffer.rewind();
        //再写入
        int c = channel.write(buffer);
        System.out.println("write:"+ c);
        System.out.println("channel position:" + channel.position());
        channel.close();

        /**验证{@link FileChannel#write(ByteBuffer)}方法将ByteBuffer的remaining写入通道*/
        testRemaining();

        /**验证{@link FileChannel#write(ByteBuffer)}方法具有同步性*/
        testSync();

        /**批量写操作{@link FileChannel#write(ByteBuffer[])} 实现于 {@link java.nio.channels.GatheringByteChannel#write(ByteBuffer[])}
         * 说明其具备三个特性：
         * 1.将1个ByteBuffer缓冲区中的remaining字节序列写入通道的当前位置
         * 2.方法write是同步的
         * 3.将多个ByteBuffer缓冲区中的remaining剩余字节序列写入通道的当前位置*/
        System.out.println("======================");
        /**1.验证{@link FileChannel#write(ByteBuffer[])}方法是从通道的当前位置开始写入的*/



        //包含3个空格
        testCurrentPosition();



    }

    private static void testCurrentPosition() throws IOException {
        FileChannel fileChannel = FileChannel.open(Paths.get("/localnas\\d.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer numByte1 = ByteBuffer.wrap("12345678".getBytes());
        ByteBuffer numByte2 = ByteBuffer.wrap("abcdefgh".getBytes());
        ByteBuffer numByte3 = ByteBuffer.wrap("a1b2c3d4e5".getBytes());
        fileChannel.position(3);
        fileChannel.write(new ByteBuffer[] {numByte1,numByte2,numByte3});
        fileChannel.close();
    }


    private static void testRemaining(){
        FileChannel fileChannel = null;
        try {
            fileChannel = FileChannel.open(Paths.get("/localnas\\b.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            ByteBuffer buffer1 = ByteBuffer.wrap("abcde".getBytes());
            ByteBuffer buffer2 = ByteBuffer.wrap("12345".getBytes());
            fileChannel.write(buffer1);
            buffer2.position(1);
            buffer2.limit(3);
            fileChannel.position(2);
            fileChannel.write(buffer2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(fileChannel != null){
                fileChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testSync() throws IOException, InterruptedException {
        FileChannel fileChannel = FileChannel.open(Paths.get("/localnas\\c.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        for (int i = 0;i < 10;i++){
            Runnable thread1 = () -> {
                ByteBuffer buffer1 = ByteBuffer.wrap("abcde\n".getBytes());
                try {
                    fileChannel.write(buffer1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Runnable thread2 = () -> {
                ByteBuffer buffer = ByteBuffer.wrap("个地方官的\n".getBytes());
                try {
                    fileChannel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            new Thread(thread1).start();
            new Thread(thread2).start();
        }
        TimeUnit.SECONDS.sleep(5);
        try {
            if(fileChannel != null){
                fileChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class StreamOperator implements AutoCloseable{
        /*实现AutoCloseable接口的类可自动关闭资源，不需要显示的调用close()方法**/
        @Override
        public void close() throws Exception {
            System.out.println("close");
        }
    }
}
