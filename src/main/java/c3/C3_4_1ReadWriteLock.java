package c3;

import java.util.HashMap;
import java.util.Map;

/**
 * 可重入锁
 * Created by bjduanweidong on 2017/10/31.
 */
public class C3_4_1ReadWriteLock {

    //当前写锁的线程,重入的次数
    private int writers = 0;
    //当前请求写锁但是还未获取到写锁的线程数
    private int writeRequests=0;
    //存储当前进入锁的线程以及重入数量
    private Map<Thread,Integer> readingThreads = new HashMap<>();

    /**
     * 记录当前的写线程
     */
    private Thread writingThread;


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
        //如果有进行写操作的线程，获取不了
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
        Thread callingThread = Thread.currentThread();
        int accessCount = getAccessCount(callingThread);
        if(accessCount==1){
            readingThreads.remove(callingThread);
        }else{
            readingThreads.put(callingThread,accessCount-1);
        }
        notifyAll();
    }

    /**
     * 获取写锁,写重入锁，可能存在读锁升级为写锁的情况
     */
    public synchronized void lockWrite() throws InterruptedException, IllegalAccessException {
        Thread callingThread = Thread.currentThread();
        //首先增加writeRequests
        writeRequests++;
        //循环等待 读锁或者写锁释放
        while(!canGrantWriteAccess(callingThread)){
            wait();
        }
        //获取到了写锁
        writers++;
        //释放writeRequests 这两条的顺序没有没有区别，因为一旦代码执行到了这里，就是一个原子操作，用synchronized修饰。
        writeRequests--;
        //记录当前的写线程
        writingThread = callingThread;
    }

    private boolean canGrantWriteAccess(Thread callingThread) throws IllegalAccessException {
        //当前写线程，可重入
        if(isWriter(callingThread)){
            return true;
        }
        if(isOnlyReader(callingThread)){
            return true;
        }
        //有其他的写线程，或者存在读线程
        if(writers>0){
            return false;
        }
        return true;

    }

    private boolean isOnlyReader(Thread callingThread) throws IllegalAccessException {
        if(readingThreads.size()==1&&readingThreads.get(callingThread)!=null){
            //只有当前线程获得资源，可以升级为写锁。
            return true;
        }else{
            //不仅仅当前线程获得资源，可能有其他线程获取到了读锁，此时不能进行升级，并且不能阻塞，否则会发生死锁现象。
            if(readingThreads.size()>1&&readingThreads.get(callingThread)!=null){
                throw new IllegalAccessException("有其他线程已经获取到了该资源的读锁，请重试");
            }
        }
        return false;
    }

    private boolean isWriter(Thread callingThread) {
        return writingThread==callingThread;
    }

    public synchronized void unlockWrite(){
        //减少writers的数量
        writers--;
        if(writers==0){
            writingThread=null;
        }
        notifyAll();
    }
}
