package com.ince.gigalike.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置类
 * 优化后的配置提供更清晰的验证码图片
 */
@Configuration
public class CaptchaConfig {

    //    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 边框厚度
        properties.setProperty("kaptcha.border.thickness", "1");

        // 图片尺寸 - 增大尺寸提高清晰度
        properties.setProperty("kaptcha.image.width", "160");
        properties.setProperty("kaptcha.image.height", "60");

        // 字体配置 - 优化字体和大小
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Times New Roman,Courier");

        // 验证码内容
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 移除容易混淆的字符：0,1,O,I,l
        properties.setProperty("kaptcha.textproducer.char.string", "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.space", "5");

        // 背景配置 - 使用渐变背景（使用RGB值）
        properties.setProperty("kaptcha.background.clear.from", "211,211,211");
        properties.setProperty("kaptcha.background.clear.to", "255,255,255");

        // 干扰线配置 - 减少干扰提高清晰度（使用RGB值）
        properties.setProperty("kaptcha.noise.color", "211,211,211");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 图片样式 - 移除水波纹效果，提高清晰度
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");

        // 文字渲染器 - 使用默认渲染器
        properties.setProperty("kaptcha.word.impl", "com.google.code.kaptcha.text.impl.DefaultWordRenderer");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * 备选配置1：极简清晰版本
     * 如果需要更清晰的验证码，可以替换上面的配置
     */
//     @Bean
    public DefaultKaptcha ultraClearKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        // 无边框
        properties.setProperty("kaptcha.border", "no");

        // 更大的图片尺寸
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "80");

        // 字体配置
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.font.size", "50");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");

        // 验证码内容
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.char.string", "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.space", "8");

        // 纯白背景（使用RGB值）
        properties.setProperty("kaptcha.background.clear.from", "255,255,255");
        properties.setProperty("kaptcha.background.clear.to", "255,255,255");

        // 无干扰
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 无图片效果
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.StraightLineTextRenderer");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * 备选配置2：彩色清晰版本
     * 如果需要彩色但清晰的验证码，可以使用此配置
     */
    @Bean
    public DefaultKaptcha colorfulClearKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();

        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "0,0,255"); // 蓝色边框
        properties.setProperty("kaptcha.border.thickness", "2");

        // 图片尺寸
        properties.setProperty("kaptcha.image.width", "180");
        properties.setProperty("kaptcha.image.height", "70");

        // 字体配置 - 使用深蓝色提高可读性
        properties.setProperty("kaptcha.textproducer.font.color", "25,25,112"); // 深蓝色
        properties.setProperty("kaptcha.textproducer.font.size", "45");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Verdana,Tahoma");

        // 验证码内容
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.char.string", "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.space", "6");

        // 渐变背景（使用RGB值）- 浅蓝到白色
        properties.setProperty("kaptcha.background.clear.from", "173,216,230");
        properties.setProperty("kaptcha.background.clear.to", "255,255,255");

        // 轻微干扰线（使用RGB值）
        properties.setProperty("kaptcha.noise.color", "211,211,211");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");

        // 轻微扭曲效果
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.FishEyeGimpy");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
} 