package com.ai.alibaba.tool;

public class WeatherTools {

    String getCurrentWeatherByCityName(String cityName) {
        switch (cityName) {
            case "武汉" : return "晴天";
            case "上海": return "上海今天天⽓: 电闪雷鸣";
            case "⼴州": return "⼴州今天天⽓: 细⾬蒙蒙";
            default: return "没有该城市的天⽓信息";
        }
    }
}
