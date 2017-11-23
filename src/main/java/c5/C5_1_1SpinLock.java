package c5;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 自旋锁的实现代码
 * Created by bjduanweidong on 2017/11/23.
 */
public class C5_1_1SpinLock {
    //获取锁的状态。0代码没有任何线程获取锁， 1 代表该锁已经被其他线程拥有。
    private volatile int  state = 0;

    /**
     * 拥有该锁的线程。
     */
    private volatile Thread thread;

    private static final AtomicIntegerFieldUpdater UPDATER = AtomicIntegerFieldUpdater.newUpdater(C5_1_1SpinLock.class,"state");

    public void lock(){
        //
        while(!UPDATER.compareAndSet(this,0,1));
        thread = Thread.currentThread();
    }

    public void unlock(){
        if(thread==Thread.currentThread()){
            state=0;
            thread=null;
        }
    }
}
