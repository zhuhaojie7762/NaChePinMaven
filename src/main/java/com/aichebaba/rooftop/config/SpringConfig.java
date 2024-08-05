package com.aichebaba.rooftop.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.MenuDao;
import com.aichebaba.rooftop.job.JobConfig;
import com.aichebaba.rooftop.service.MenuService;
import com.aichebaba.rooftop.ue.UeController;
import com.alipay.config.AlipayConfig;

@Configuration
@ComponentScan(basePackageClasses = {BaseController.class, MenuDao.class, MenuService.class})
@Import({JobConfig.class})
public class SpringConfig {

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new ClassPathResource("app.properties"),
                new ClassPathResource("sms.properties"), new ClassPathResource("alipay.properties"));
        return configurer;
    }

	@Bean
	public ThreadPoolTaskExecutor getThreadPool() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		// 线程池所使用的缓冲队列
		pool.setQueueCapacity(200);
		// 线程池维护线程的最少数量
		pool.setCorePoolSize(5);
		// 线程池维护线程的最大数量
		pool.setMaxPoolSize(1000);
		// 线程池维护线程所允许的空闲时间
		pool.setKeepAliveSeconds(30000);
		// pool.initialize();
		return pool;
	}

    @Bean
    public UeController ueController() {
        return new UeController();
    }

    @Bean
    public AlipayConfig alipayConfig() {
        return new AlipayConfig();
    }

}
