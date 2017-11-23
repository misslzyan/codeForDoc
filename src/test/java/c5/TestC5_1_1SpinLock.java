package c5;

import org.junit.Test;

/**
 * Created by bjduanweidong on 2017/11/23.
 */
public class TestC5_1_1SpinLock {

    int n = 0;
    @Test
    public void test1() throws InterruptedException {
        C5_1_1SpinLock lock = new C5_1_1SpinLock();

        Thread t1 = new Thread(()->{
            while(true){
                try {
                    lock.lock();
                    System.out.println("t1   "+n++);
                }finally {
                    lock.unlock();
                }
            }

        });

        Thread t2 = new Thread(()->{
            while(true){
                try {
                    lock.lock();
                    System.out.println("t2   "+n++);
                }finally {
                    lock.unlock();
                }

            }

        });

        t1.start();
        t2.start();
        Thread.sleep(2000);
    }

}
