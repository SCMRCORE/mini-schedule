package com.example.minischedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class ScheduleService {
    ExecutorService executorService = Executors.newFixedThreadPool(6);
    Trigger trigger  = new Trigger();
    public void schedule(Runnable task, long delay){//这里不加TimeUnit了默认毫秒
        Job job = new Job(task, System.currentTimeMillis()+delay, delay);
        trigger.jobqueue.offer(job);
        trigger.wakeUp();//下面提到的添加后就唤醒
    }

    //触发器，等待合适时机，把任务扔到线程池中
    class Trigger{
        //Trigger要一直拿着任务，所以肯定不能阻塞主线程，new一个Thead
        //Thread的任务就简单了：从某个容器中拿到一个job，判断now和startTime时间，选择阻塞/执行
        PriorityBlockingQueue<Job> jobqueue = new PriorityBlockingQueue<>();
        Thread thread = new Thread(()->{
            while(true){
                while(jobqueue.isEmpty()){//不能用if，防止虚假唤醒
                    LockSupport.park();
                }
                //实现逻辑是：拿一个最新的job出来看看当前是否在执行
                Job latestJob = jobqueue.peek();
                if(latestJob.getStartTime()<System.currentTimeMillis()){
                    //即使多线程环境下，加进来一个更短的任务，也会先执行这个更短的。
                    //也能解决唤醒任务后继续执行这个业务，而不是新的最短业务问题
                    latestJob = jobqueue.poll();
                    executorService.execute(latestJob.getTask());
                    //计算下一次执行时间，放回容器
                    Job nextJob = new Job(latestJob.getTask(), latestJob.getStartTime()+latestJob.getDelay(), latestJob.getDelay());
                    jobqueue.add(nextJob);
                }else{
                    //如果时间还没到最短任务开始时间，则等到最新任务的开始时间，然后重走一遍流程
                    LockSupport.parkUntil(latestJob.getStartTime());//参数是deadline
                }
            }
        });

        {
            thread.start();
            System.out.println("触发器启动了");
        }

        //我们就只需要在加任务时，再唤醒一次trigger就行
        void wakeUp(){
            LockSupport.unpark(thread);
        }
    }
}
