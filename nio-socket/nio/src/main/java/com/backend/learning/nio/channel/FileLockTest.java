package com.backend.learning.nio.channel;

/**
 * 文件锁定操作
 * {@link java.nio.channels.FileChannel#lock(long, long, boolean)}方法的作用是获取此通道的文件给
 * 定区域上的锁定。在可以锁定该区域之前、已关闭此通道之前或者已中断调用线程之前（以先到者为准），将阻塞
 * 此防范的调用。
 *
 * 在此方法调用期间，如果另一个线程关闭了此通道，则抛出{@link java.nio.channels.AsynchronousCloseException}
 * 异常。
 *
 * 如果在等待获取锁定的同时中断了调用线程，则将状态设置为中断并抛出{@link java.nio.channels.FileLockInterruptionException}
 * 异常。如果调用此方法时已设置调用方的中断状态，则立即抛出该异常；不更改该线程的中断状态。
 * 有{@code position}和{@code size}参数所指定的区域无须包含在实际的底层文件中，甚至无须与文件重叠。
 *
 * 锁定的区域的大小是固定的；如果某个已锁定区域最初包含整个文件，并且文件因扩大而超出了该区域，则该锁定
 * 不覆盖此文件的新部分。如果期望文件大小扩大并且要求锁定整个文件，则应该锁定的position从零开始，size
 * 传入大于或等于预计文件的最大值。零参数的lock()方法只是锁定大小为{@link Long#MAX_VALUE}区域。
 *
 * 文件锁定要么是独占要么是共享的。共享锁定可以阻止其他并发运行的程序获取重叠的独占锁定，但是允许该程序
 * 获取重叠的共享锁定。独占锁定则组织其他程序获取共享或独占类型的重叠锁定。
 * @author Jon_China
 * @since 2019年5月14日21:09:18
 * @version 1.0
 */
public class FileLockTest {

    public static void main(String[] args) {
        /***/

    }
}
