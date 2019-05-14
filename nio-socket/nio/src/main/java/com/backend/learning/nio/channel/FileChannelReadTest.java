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
        /**批量读操作：
         * {@link FileChannel#read(ByteBuffer[])}方法实现的是{@link java.nio.channels.ScatteringByteChannel#read(ByteBuffer[])},故接口特性具有：
         * 1.将通道当前位置中的字节序列读入1个ByteBuffer缓冲区的remaining中
         * 2.该方法是同步的
         * 3.将通道当前位置的字节序列读入多个ByteBuffer缓冲区的remaining剩余空间中*/
        /**1.验证{@link FileChannel#read(ByteBuffer[])}方法返回值的意义*/
        testReadBuffersReturn();
        /**2.验证{@link FileChannel#read(ByteBuffer[])}方法是从通道的当前位置开始读取的*/
        System.out.println("============");
        testReadFromCurrentPosition();
        /**2.验证{@link FileChannel#read(ByteBuffer[])}方法将字节放入ByteBuffer的当前位置*/
        System.out.println("============");
        testReadToBuffersCurrentPosition();
        /**2.验证{@link FileChannel#read(ByteBuffer[])}方法具有同步性*/
        System.out.println("============");
        testReadToBuffersSync();
        System.out.println("=============");
        /**部分批量读操作
         * {@link java.nio.channels.ScatteringByteChannel#read(ByteBuffer[], int, int)}方法的作用是将通道中当前位置的字节序列
         * 读入下标为offset开始的ByteBuffer[]数组中的remaining剩余空间中，并且连续写入length个ByteBuffer缓冲区。
         * 该方法实现的是{@link java.nio.channels.ScatteringByteChannel#read(ByteBuffer[], int, int)}接口中的同名方法，而该
         * 接口的父类是{@link java.nio.channels.ReadableByteChannel},故具备以下特性：
         * 1.将通道当前位置的字节序列读入1个ByteBuffer缓冲区的remaining空间中
         * 2.该方法是同步的*/
        /**1.验证{@link java.nio.channels.ScatteringByteChannel#read(ByteBuffer[], int, int)}方法的返回值的意义*/

        testReturnOfScatterRead();
    }

    /**
     * 验证返回值的意义P120
     */
    private static void testReturnOfScatterRead() throws IOException {
        FileChannel fileChannel = FileChannel.open(Paths.get("/localnas\\g.txt"), StandardOpenOption.READ);
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(2);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(2);
        ByteBuffer[] byteBuffers = new ByteBuffer[]{byteBuffer1,byteBuffer2};
        long i = fileChannel.read(byteBuffers, 0, 2);
        System.out.println(i);
        byteBuffer1.clear();
        byteBuffer2.clear();

        i = fileChannel.read(byteBuffers, 0, 2);
        System.out.println(i);
        byteBuffer1.clear();
        byteBuffer2.clear();
        /*到达流末尾*/
        i = fileChannel.read(byteBuffers, 0, 2);
        System.out.println(i);
        byteBuffer1.clear();
        byteBuffer2.clear();
    }



    private static void testReadToBuffersSync() throws IOException, InterruptedException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> read(channel)).start();


            new Thread(() -> read(channel)).start();
        }
        TimeUnit.SECONDS.sleep(3);
        channel.close();



    }

    private static void read(FileChannel fileChannel){
        ByteBuffer buffer1 = ByteBuffer.allocate(1);
        ByteBuffer buffer2 = ByteBuffer.allocate(1);
        try {
            fileChannel.read(new ByteBuffer[]{buffer1,buffer2});
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < buffer1.array().length; j++) {
            System.out.print((char) buffer1.array()[j]);
        }
        for (int j = 0; j < buffer2.array().length; j++) {
            System.out.print((char) buffer2.array()[j]);
        }
    }

    private static void testReadToBuffersCurrentPosition() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ);
        ByteBuffer buffer1 = ByteBuffer.allocate(2);
        buffer1.position(1);
        ByteBuffer buffer2 = ByteBuffer.allocate(2);
        buffer2.position(1);
        channel.read(new ByteBuffer[]{buffer1,buffer2});

        for (int i = 0; i < buffer1.array().length; i++) {
            System.out.print((char) buffer1.array()[i]);
        }
        for (int i = 0; i < buffer2.array().length; i++) {
            System.out.print((char) buffer2.array()[i]);
        }

        channel.close();

    }

    private static void testReadFromCurrentPosition() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ);
        channel.position(1);
        ByteBuffer buffer1 = ByteBuffer.allocate(2);
        ByteBuffer buffer2 = ByteBuffer.allocate(2);
        channel.read(new ByteBuffer[]{buffer1, buffer2});

        for (int i = 0; i < buffer1.array().length; i++) {
            System.out.println((char) buffer1.array()[i]);
        }
        for (int i = 0; i < buffer2.array().length; i++) {
            System.out.println((char) buffer2.array()[i]);
        }

        channel.close();
    }

    private static void testReadBuffersReturn() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ);
        ByteBuffer buffer1 = ByteBuffer.allocate(2);
        ByteBuffer buffer2 = ByteBuffer.allocate(2);
        long i = channel.read(new ByteBuffer[]{buffer1, buffer2});
        System.out.println(i);
        buffer1.clear();
        buffer2.clear();
        //不使用rewind()的原因是因为rewind()未还原limit
        System.out.println(channel.read(new ByteBuffer[]{buffer1, buffer2}));
        buffer1.clear();
        buffer2.clear();
        System.out.println(channel.read(new ByteBuffer[]{buffer1, buffer2}));

    }

    public static void testRemaining() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.position(1);
        buffer.limit(3);
        channel.read(buffer);
        channel.close();
        buffer.rewind();
        //remaining = limit - position
        System.out.println(buffer.limit());
        for (int i = 0; i < buffer.limit(); i++) {
            byte b = buffer.get();//position自增特性
            if (b == 0) {
                System.out.println("空格");
            } else {
                System.out.println((char) b);
            }

        }

    }

    private static void testSyncRead() throws IOException, InterruptedException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        for (int i = 0; i < 3; i++) {
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
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        channel.position(2);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.position(2);
        channel.read(buffer);
        byte[] array = buffer.array();
        for (int i = 0; i < array.length; i++) {
            System.out.println((char) array[i]);
        }
        //文本中的字符串为“ababcd” 实际输出为“abcd”
        channel.close();
    }

    private static void testReturn() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
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
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        //设置通道position
        channel.position(2);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        channel.read(buffer);

        byte[] array = buffer.array();
        for (int i = 0; i < array.length; i++) {
            System.out.println((char) array[i]);
        }
        //文本中的字符串为“ababcd” 实际输出为“abcd”
        channel.close();
    }
}
