package c3;

import java.util.HashMap;
import java.util.Map;

/**
 * 可重入锁
 * Created by bjduanweidong on 2017/10/31.
 */
public class C3_3_1ReadWriteLock {
    //当前读锁的线程数
    private int readers = 0;
    //当前写锁的线程数
    private int writers = 0;
    //当前请求写锁但是还未获取到写锁的线程数
    private int writeRequests=0;
    //存储当前进入锁的线程以及重入数量
    private Map<Thread,Integer> readingThreads = new HashMap<>();


    /**
     * 获取读锁
     * @throws InterruptedException
     */
    public synchronized void lockRead() throws InterruptedException {
        Thread callingThread = Thread.currentThread();
        while(!canGrantReadAccess(callingThread)) {
            wait();
        }
        //获取到锁以后，进行+1
        readingThreads.put(callingThread,getAccessCount(callingThread)+1);
    }

    /**
     * 获取读权限
     * @param callingThread
     * @return
     */
    private boolean canGrantReadAccess(Thread callingThread) {
        //如果进行写操作，获取不了
        if(writers>0)return false;
        //当前读线程，可以读
        if(isReader(callingThread))return true;
        //如果有写等待，不可以
        if(writeRequests>0)return false;
        return true;
    }

    private boolean isReader(Thread callingThread) {
        return readingThreads.get(callingThread)!=null;
    }

    private int getAccessCount(Thread callingThread) {
        Integer count = readingThreads.get(callingThread);
        if(count==null){
            return 0;
        }else{
            return count.intValue();
        }
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
