package com.example.minischedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

/**
 * 为了让我们Trigger知道要执行的任务和执行时间，我们定义一个Job类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job implements Comparable<Job>{
    private Runnable task;
    private long startTime;
    private long delay;

    @Override
    public int compareTo(Job o) {
        return Long.compare(this.startTime, o.startTime);
    }
    //startTime-now大于0说明还没到时间要阻塞这么多时间；小于0说明已经到时间了要执行任务
}
