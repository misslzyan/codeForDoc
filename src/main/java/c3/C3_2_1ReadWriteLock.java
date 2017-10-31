package c3;

/**
 * Created by bjduanweidong on 2017/10/31.
 */
public class C3_2_1ReadWriteLock {
    //当前读锁的线程数
    private int readers = 0;
    //当前写锁的线程数
    private int writers = 0;
    //当前请求写锁但是还未获取到写锁的线程数
    private int writeRequests=0;

    /**
     * 获取读锁
     * @throws InterruptedException
     */
    public synchronized void lockRead() throws InterruptedException {
        //循环获取读锁，因为写锁的优先级比较高，这里使用了
        //writers 和 writeRequests两个来进行比较,而且是while循环，这样唤醒了就一直循环下去。
        while(writers>0||writeRequests>0){
            wait();
        }
        readers++;
    }

    /**
     * 释放读锁
     */
    public synchronized void unlockRead(){
        readers--;
        notifyAll();
    }

    /**
     * 获取写锁
     */
    public synchronized void lockWrite() throws InterruptedException {
        //首先增加writeRequests
        writeRequests++;
        //循环等待 读锁或者写锁释放
        while(readers>0||writers>0){
            wait();
        }
        //获取到了写锁
        writers++;
        //释放writeRequests 这两条的顺序没有没有区别，因为一旦代码执行到了这里，就是一个原子操作，用synchronized修饰。
        writeRequests--;
    }

    public synchronized void unlockWrite(){
        writers--;
        notifyAll();
    }
}
