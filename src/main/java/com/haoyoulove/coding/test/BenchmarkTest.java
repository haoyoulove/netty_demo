//package com.haoyoulove.coding.test;
//
//import java.util.concurrent.CyclicBarrier;
//
///**
// * 2017
// *
// * @author bobi create by 2018-03-30 下午5:39
// **/
//
//public class BenchmarkTest {
//    private Counter counter;
//    private CyclicBarrier barrier;
//    private int threadNum;
//    private int loopNum;
//    private String testName;
//
//    public BenchmarkTest(Counter counter, int threadNum, int loopNum, String testName) {
//        this.counter = counter;
//        barrier = new CyclicBarrier(threadNum + 1); // 关卡计数=线程数
//        this.threadNum = threadNum;
//        this.loopNum = loopNum;
//        this.testName = testName;
//    }
//
//    public static void main(String args[]) throws Exception {
//        int threadNum = 500;
//        int loopNum = 1000;
//        new BenchmarkTest(new SynchronizedBenchmarkDemo(), threadNum, loopNum, "内部锁").test();
//
//        new BenchmarkTest(new ReentrantLockUnfairBeanchmarkDemo(), threadNum, loopNum, "不公平重入锁").test();
//
//        new BenchmarkTest(new ReentrantLockFairBeanchmarkDemo(), threadNum, loopNum, "公平重入锁").test();
//    }
//
//    public void test() throws Exception {
//        try {
//            for (int i = 0; i < threadNum; i++) {
//                // 所有线程
//                new TestThread(counter, loopNum).start();
//            }
//            long start = System.currentTimeMillis();
//
//            barrier.await(); // 等待所有任务线程创建,然后通过关卡，统一执行
//
//            System.out.println("计算中");
//            barrier.await(); // 等待所有任务计算完成
//            long end = System.currentTimeMillis();
//
//            System.out.println(this.testName + " count value:" + counter.getValue());
//            System.out.println(this.testName + " 花费时间:" + (end - start) + "毫秒");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    class TestThread extends Thread {
//        int loopNum = 100;
//        private Counter counter;
//
//        public TestThread(final Counter counter, int loopNum) {
//            this.counter = counter;
//            this.loopNum = loopNum;
//        }
//
//        @Override
//        public void run() {
//            try {
//                barrier.await();// 等待所有的线程开始
//                for (int i = 0; i < this.loopNum; i++) {
//                    counter.increment();
//                }
//                barrier.await();// 等待所有的线程完成
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}