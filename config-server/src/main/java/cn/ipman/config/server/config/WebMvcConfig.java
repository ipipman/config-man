package cn.ipman.config.server.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类，用于自定义Spring MVC的配置
 *
 * @Author IpMan
 * @Date 2024/5/26 10:29
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 定义并配置一个线程池任务执行器，用于处理异步请求。
     *
     * @return 配置好的ThreadPoolTaskExecutor实例。
     */
    @Bean
    public ThreadPoolTaskExecutor mvcTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);    // 核心线程数
        executor.setQueueCapacity(100);  // 队列容量
        executor.setMaxPoolSize(25);     // 最大线程数
        return executor;
    }


    /**
     * 配置异步请求支持，设置任务执行器和超时时间。
     *
     * @param configurer 异步支持配置器
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(mvcTaskExecutor());
        configurer.setDefaultTimeout(10_000L); // 设置默认超时时间 10s
    }

    /**
     * 全局异常处理器，捕获并处理异步请求超时异常。
     */
    @ControllerAdvice
    static class GlobalExceptionHandler {
        /**
         * 处理异步请求超时异常，返回304状态码。
         *
         * @param e 异常实例
         * @param request HTTP请求
         */
        @ResponseStatus(HttpStatus.NOT_MODIFIED) //返回 304 状态码
        @ResponseBody
        @ExceptionHandler(AsyncRequestTimeoutException.class) //捕获特定异常
        public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
            System.out.println("handleAsyncRequestTimeoutException");
        }
    }
}
