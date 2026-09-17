package com.ai.alibaba;

import com.alibaba.cloud.ai.dashscope.video.VideoModel;
import com.alibaba.cloud.ai.dashscope.video.VideoPrompt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VideoModelTest {

    @Autowired
    private VideoModel videoModel;

    @Test
    void testVideo() {
        String url = this.videoModel.call(new VideoPrompt("一只小猫在奔跑"))
                .getResult()
                .getOutput()
                .getVideoUrl();
        System.out.println(url);
    }

}
