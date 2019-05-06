package com.backend.learning.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelReadTest {

    public static void main(String[] args) throws IOException {
        /**{@link java.nio.channels.FileChannel#read(ByteBuffer)}是将字节序列从此通道的当前位置读入给定的缓冲区的当前位置。
         * 行为与ReadableByteChannel一致，在任意给定的时刻，一个可读取通道上只能有一个读取操作。该方法是同步的*/

        /**1.验证{@link java.nio.channels.FileChannel#read(ByteBuffer)}方法返回值的意义*/
        /**1）正数：代表从通道的当前位置想ByteBuffer缓冲区中读取的字节数
         * 2）0：代表从通道中没有读取任何的数据，也就是0字节，有可能发生的情况就是缓冲区没有remaining空间了
         * 3）-1：代表到达流的末端*/
        testReturn();

    }

    private static void testReturn() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("/localnas\\a.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        //取得5个字节
        int readLength = channel.read(buffer);
        System.out.println(readLength);
        buffer.clear();
        //取得1个字节
        System.out.println(channel.read(buffer));
        //到达流的末尾-1
        System.out.println(channel.read(buffer));
        channel.close();
    }
}
