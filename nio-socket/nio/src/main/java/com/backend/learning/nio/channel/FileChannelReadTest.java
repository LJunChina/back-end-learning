package com.backend.learning.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class FileChannelReadTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        /**{@link java.nio.channels.FileChannel#read(ByteBuffer)}是将字节序列从此通道的当前位置读入给定的缓冲区的当前位置。
         * 行为与ReadableByteChannel一致，在任意给定的时刻，一个可读取通道上只能有一个读取操作。该方法是同步的*/

        /**1.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法返回值的意义*/
        /**1）正数：代表从通道的当前位置想ByteBuffer缓冲区中读取的字节数
         * 2）0：代表从通道中没有读取任何的数据，也就是0字节，有可能发生的情况就是缓冲区没有remaining空间了
         * 3）-1：代表到达流的末端*/
        testReturn();
        /**2.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法是从通道的当前位置开始读取的*/
        testCurrentPosition();

        /**3.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法将字节放入ByteBuffer当前位置*/
        testCurrentPositionSet();
        /**4.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法将具有同步性*/
        System.out.println("=====================");
        testSyncRead();
        /**4.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法从通道读取的字节放入缓冲区的remaining空间中*/
        System.out.println("=====================");
        testRemaining();
    }

    public static void testRemaining() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.position(1);
        buffer.limit(3);
        channel.read(buffer);
        channel.close();
        buffer.rewind();
        //remaining = limit - position
        System.out.println(buffer.limit());
        for (int i = 0;i < buffer.limit();i++){
            byte b = buffer.get();//position自增特性
            if(b == 0){
                System.out.println("空格");
            }else {
                System.out.println((char) b);
            }

        }

    }

    private static void testSyncRead() throws IOException, InterruptedException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        for(int i = 0;i < 3;i++){
            new Thread(() -> {
                ByteBuffer buffer = ByteBuffer.allocate(1);
                try {
                    channel.read(buffer);
                    System.out.println((char) buffer.array()[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
            new Thread(() -> {
                ByteBuffer buffer = ByteBuffer.allocate(1);
                try {
                    channel.read(buffer);
                    System.out.println((char) buffer.array()[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(5);
        channel.close();
    }

    private static void testCurrentPositionSet() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        channel.position(2);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.position(2);
        channel.read(buffer);
        byte[] array = buffer.array();
        for (int i = 0;i < array.length;i++){
            System.out.println((char) array[i]);
        }
        //文本中的字符串为“ababcd” 实际输出为“abcd”
        channel.close();
    }

    private static void testReturn() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        //取得5个字节
        int readLength = channel.read(buffer);
        System.out.println(readLength);
        buffer.clear();//若注释 则下面的代码返回值为0 表示buffer中没有剩余空间了
        //取得1个字节
        System.out.println(channel.read(buffer));
        //到达流的末尾-1
        System.out.println(channel.read(buffer));
        channel.close();
    }

    private static void testCurrentPosition() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        //设置通道position
        channel.position(2);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        channel.read(buffer);

        byte[] array = buffer.array();
        for (int i = 0;i < array.length;i++){
            System.out.println((char) array[i]);
        }
        //文本中的字符串为“ababcd” 实际输出为“abcd”
        channel.close();
    }
}
