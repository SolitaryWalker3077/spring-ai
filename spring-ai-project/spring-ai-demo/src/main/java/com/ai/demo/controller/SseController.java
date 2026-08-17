package com.ai.demo.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Locked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {
    @RequestMapping("/data")
    public void data(HttpServletResponse response) throws IOException, InterruptedException {
        response.setContentType("text/event-stream;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String s = "";
        for (int i = 0; i < 20; i++) {
            s = "data: "+new Date()+"\n\n";
            printWriter.write(s);
            printWriter.flush();
            Thread.sleep(1000L);
        }
    }


    @RequestMapping("/retry")
    public void retry(HttpServletResponse response) throws IOException {
        log.info("发起请求:retry");
        response.setContentType("text/event-stream;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String s = "retry :2000\n";
        s+="data: "+new Date()+"\n\n";
        printWriter.write(s);
        printWriter.flush();
    }

    @RequestMapping("/event")
    public void event(HttpServletResponse response) throws IOException, InterruptedException {
        log.info("发起请求:event");
        response.setContentType("text/event-stream;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        for (int i = 0; i < 10; i++) {
            String s = "event:foo\n";
            s+="data: " + new Date() +"\n\n";
            printWriter.write(s);
            printWriter.flush();
            Thread.sleep(1000L);
        }
    }

    @RequestMapping("/end")
    public void end(HttpServletResponse response) throws IOException, InterruptedException {
        log.info("发起请求:end");
        response.setContentType("text/event-stream;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        for (int i = 0;i<10;i++) {
            String s = "event:end\n";
            s+="data" + new Date() +"\n\n";
            printWriter.write(s);
            printWriter.flush();
            Thread.sleep(1000L);
        }
        //定义end事件, 表示当前流传输结束
        printWriter.write("event: end\ndata: EOF\n\n");
    }


    @RequestMapping(value = "stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(1)).map(s->new Date().toString());
    }
}
