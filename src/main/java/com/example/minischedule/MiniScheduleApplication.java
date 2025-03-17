package com.example.minischedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class MiniScheduleApplication {

    public static void main(String[] args) throws InterruptedException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss SSS");

        ScheduleService scheduleService = new ScheduleService();
        scheduleService.schedule(()->{
            System.out.println(LocalDateTime.now().format(dateTimeFormatter)+" 这是一个100ms一次的任务");
        }, 100);

        Thread.sleep(1000);

        System.out.println("添加一个每200ms打印一个2的定时任务");
        scheduleService.schedule(()->{
            System.out.println(LocalDateTime.now().format(dateTimeFormatter)+" 这是一个200ms一次的任务");
        }, 200);
    }

}
