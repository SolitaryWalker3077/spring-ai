package com.ai.alibaba.AudioModel;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.transcription.Transcription;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionQueryParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Arrays;


@SpringBootTest
public class AudioTranscriptionModelTest {

    @Autowired
    private DashScopeAudioTranscriptionModel transcriptionModel;

    private static final String DEFAULT_MODEL = "paraformer-v2";

    /**
     * 音频文件识别
     * 音频文件转文本
     * */
//    @Test
//    void callSTT() {
//        Resource resource = new DefaultResourceLoader()
//                .getResource("https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav");
//
//        AudioTranscriptionResponse response = transcriptionModel.call(
//                new AudioTranscriptionPrompt(
//                        resource,
//                        DashScopeAudioTranscriptionOptions.builder()
//                                .withModel(DEFAULT_MODEL)
//                                .build()
//                )
//        );
//        System.out.println(response.getResult().getOutput());
//    }

    @Test
    void sttByDashScopeSdk() {
        // 创建转写请求参数
        TranscriptionParam param =
                TranscriptionParam.builder()
                        // 若没有将API Key配置到环境变量中，需将apiKey替换为自己的API Key
                        //.apiKey("apikey")
                        .model("paraformer-v2")
                        // “language_hints”只支持paraformer-v2模型
                        .parameter("language_hints", new String[]{"zh", "en"})
                        .fileUrls(
                                Arrays.asList(
                                        "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav",
                                        "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_male2.wav"))
                        .build();
        try {
            Transcription transcription = new Transcription();
            // 提交转写请求
            TranscriptionResult result = transcription.asyncCall(param);
            System.out.println("RequestId: " + result.getRequestId());
            // 阻塞等待任务完成并获取结果
            result = transcription.wait(
                    TranscriptionQueryParam.FromTranscriptionParam(param, result.getTaskId()));

            // 打印结果
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(result.getOutput()));
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        System.exit(0);
    }
}
