package com.ai.alibaba;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SpringBootTest
public class AudioModelTest {

    @Autowired
    private DashScopeSpeechSynthesisModel speechSynthesisModel;

    private static final String TEXT = "白日依山尽,黄河入海流";

    @Test
    void tts() {
        SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(TEXT);
        SpeechSynthesisResponse response = speechSynthesisModel.call(prompt);

        File file = new File(System.getProperty("user.dir") + "/out.mp3");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer audio = response.getResult().getOutput().getAudio();
            fos.write(audio.array());
        } catch (IOException e) {
            System.out.println("写入文件失败");
        }
    }

    @Test
    void setSetAudioModel() {
        DashScopeSpeechSynthesisOptions.builder()
                .model("cosyvoice-v1")
                .voice("longxiaobai")
                .speed(0.5f)
                .build();
        SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(TEXT);
        SpeechSynthesisResponse response = speechSynthesisModel.call(prompt);

        File file = new File(System.getProperty("user.dir") + "/out.mp3");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer audio = response.getResult().getOutput().getAudio();
            fos.write(audio.array());
        } catch (IOException e) {
            System.out.println("写入文件失败");
        }
    }
}
