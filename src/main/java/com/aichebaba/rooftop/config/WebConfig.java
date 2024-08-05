package com.aichebaba.rooftop.config;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.beetl.ext.jfinal.BeetlRenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.aichebaba.rooftop.interceptor.CommonInterceptor;
import com.aichebaba.rooftop.interceptor.ErrorInterceptor;
import com.aichebaba.rooftop.interceptor.ResponseInterceptor;
import com.aichebaba.rooftop.interceptor.admin.LoginInterceptor;
import com.aichebaba.rooftop.interceptor.admin.MenusInterceptor;
import com.aichebaba.rooftop.interceptor.web.ClassSearchInterceptor;
import com.aichebaba.rooftop.interceptor.web.GoodsInterceptor;
import com.aichebaba.rooftop.interceptor.web.MemberInterceptor;
import com.aichebaba.rooftop.interceptor.web.RedirectInterceptor;
import com.aichebaba.rooftop.plugins.dbcp.DbcpPlugin;
import com.aichebaba.rooftop.ue.UeController;
import com.google.common.collect.ImmutableMap;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.tx.ArTxProvider;
import com.jfinal.plugin.spring.SpringPlugin;
import com.jfinal.plugin.spring.SpringUtils;

public class WebConfig extends JFinalConfig {

	private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void configConstant(Constants constants) {
        loadPropertyFile("jdbc.properties");
        BeetlConfig.config(constants);
        BeetlRenderFactory.groupTemplate
                .setSharedVars(ImmutableMap.of("v", getProperty("web.version")));
        constants.addStaticBasePaths("/static", "/uploads", "/favicon.ico", "robots.txt", "/img");
        constants.setError404View("/web/page404.html");
        constants.setError500View("/web/page500.html");
        constants.setMaxPostSize(200 * 1024 * 1024);
        constants.openTx(new ArTxProvider());
    }

    @Override
    public void configRoute(Routes routes) {

        routes.add("/ue/upload", UeController.class);

        routes.add(new WebRoutes());
        routes.add(new AdminRoutes());
    }

    @Override
    public void configPlugin(Plugins plugins) {
        //jdbc datasource
        DbcpPlugin ds = new DbcpPlugin(getProperty("db.url"), getProperty("db.username"), getProperty("db.password"));
        ActiveRecordPlugin arp = new ActiveRecordPlugin(ds);
        plugins.add(ds, arp);

        //spring config
        SpringPlugin sp = new SpringPlugin(SpringConfig.class);
        plugins.add(sp);
    }

	/**
	 * 注册拦截器
	 * @param interceptors
	 */
	@Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new ErrorInterceptor());
        interceptors.add(new CommonInterceptor());
		interceptors.add(new ResponseInterceptor());

        //admin
        interceptors.add(new LoginInterceptor());
        interceptors.add(new MenusInterceptor());

        //web
        interceptors.add(new RedirectInterceptor());
        interceptors.add(new GoodsInterceptor());
        interceptors.add(new MemberInterceptor());
        interceptors.add(new ClassSearchInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new MyHandler());
    }

	@Override
	public void beforeJFinalStop() {
		logger.info("beforeJFinalStop");
		SpringUtils.getBean(ThreadPoolTaskScheduler.class).destroy();
		SpringUtils.getBean(ThreadPoolTaskExecutor.class).destroy();
		immolate();
		super.beforeJFinalStop();
	}

	/**
	 * 清除shutdown beetl错误
	 *
	 * @return
	 */
	private Integer immolate() {
		int count = 0;
		try {
			final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			final Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
			inheritableThreadLocalsField.setAccessible(true);
			for (final Thread thread : Thread.getAllStackTraces().keySet()) {
				count += clear(threadLocalsField.get(thread));
				count += clear(inheritableThreadLocalsField.get(thread));
			}
			logger.info("immolated " + count + " values in ThreadLocals");
		} catch (Exception e) {
			throw new Error("ThreadLocalImmolater.immolate()", e);
		}
		return count;
	}

	private int clear(final Object threadLocalMap) throws Exception {
		if (threadLocalMap == null) {
			return 0;
		}
		int count = 0;
		final Field tableField = threadLocalMap.getClass().getDeclaredField("table");
		tableField.setAccessible(true);
		final Object table = tableField.get(threadLocalMap);
		for (int i = 0, length = Array.getLength(table); i < length; ++i) {
			final Object entry = Array.get(table, i);
			if (entry != null) {
				final Object threadLocal = ((WeakReference<?>) entry).get();
				if (threadLocal != null) {
					Array.set(table, i, null);
					++count;
				}
			}
		}
		return count;
	}
}
