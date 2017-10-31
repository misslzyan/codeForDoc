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
        //writers 和 writeRequests两个来进行比较
        while(writers>0||writeRequests>0){
            wait();
        }
        readers++;
    }
}
