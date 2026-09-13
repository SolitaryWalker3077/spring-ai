package com.ai.alibaba;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageModelTest {
    @Autowired
    private DashScopeImageModel imageModel;

    @Test
    void imageChat() {
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("孩子在海边玩耍"));
        String imgUrl = imageResponse.getResult().getOutput().getUrl();
        System.out.println(imgUrl);
    }

    @Test
    void setImageModel() {
        //设置模型为 : qwen-image-plus
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .withModel("qwen-image-plus")
                .build();
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("孩子在海边玩耍"));
        String imgUrl = imageResponse.getResult().getOutput().getUrl();
        System.out.println(imgUrl);
    }
}
