package com.ai.alibaba.AudioModel;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;

import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
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

    @Test
    void test() {
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将apiKey替换为自己的API Key
                // .apiKey("yourApikey")
                .model("sambert-zhichu-v1")
                .text("今天天气怎么样")
                .sampleRate(48000)
                .enableWordTimestamp(true)
                .enablePhonemeTimestamp(true)
                .build();

        File file = new File(System.getProperty("user.dir")+"/output.WAV");
        ByteBuffer audio = synthesizer.call(param);

        try(FileOutputStream fos = new FileOutputStream(file)){
            fos.write(audio.array());
            System.out.println("sunthesis done");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}