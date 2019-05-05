package com.backend.learning.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

public class BufferTest {

    @Test
    public void testBuffer(){
        byte[] bytes = new byte[]{1,2,3};
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        System.out.println(buffer.getClass().getName());
    }
}
