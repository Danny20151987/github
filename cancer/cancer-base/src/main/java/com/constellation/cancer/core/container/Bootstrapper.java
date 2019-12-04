package com.constellation.cancer.core.container;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;
import com.alibaba.dubbo.rpc.Protocol;
import com.constellation.cancer.core.component.CancerComponentLoader;
import com.constellation.cancer.core.component.ComponentShutdownHook;
import com.constellation.cancer.core.config.BootstrapConfigUtil;
import com.constellation.cancer.core.context.ContextUtils;
import com.constellation.cancer.core.health.HealthManager;
import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 3:56 PM
 */
public class Bootstrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrapper.class);
    private AbstractApplicationContext cancerContext = null;
    private AbstractApplicationContext appContext = null;
    private CountDownLatch latch;
    private static final AtomicBoolean destroyed = new AtomicBoolean(false);


    public Bootstrapper() {

    }

    public static void main(String[] args) {
        Bootstrapper bootstrapper = new Bootstrapper();

        try{
            bootstrapper.init();
            bootstrapper.start();
            bootstrapper.blocking();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            bootstrapper.shutdown();
            System.exit(1);
        }
    }

    /**
     * 启动前进行配置文件加载
     */
    protected void init(){
        CancerComponentLoader cancerComponentLoader = new CancerComponentLoader();
        printAppInfo();
        ContextUtils.getDockerContext().init();
        String[] componentsArr = null;

        String springContextMain;
        try {
            springContextMain = BootstrapConfigUtil.getProperty(CoreConstants.LOAD_COMPONENTS);
            if (springContextMain != null && !StringUtils.isEmpty(springContextMain)) {
                componentsArr = springContextMain.split("\\|");

                for(int i = 0; i < componentsArr.length; ++i) {
                    if (!StringUtils.isEmpty(componentsArr[i])) {
                        componentsArr[i] = CoreConstants.PATH_PRE + componentsArr[i] + CoreConstants.PATH_SUF;
                    }
                }
            }

            cancerComponentLoader.loadComponents(springContextMain);
        } catch (RuntimeException ex) {
            LOGGER.error("平台组件配置文件加载失败!");
            throw ex;
        }

        try {
            ContextReferenceRefresher refresher = context -> ContextUtils.setSpringCancerContext(context);
            this.cancerContext = componentsArr != null ? new CancerXmlApplicationContext(refresher, componentsArr) : new CancerXmlApplicationContext(refresher, new String[]{CoreConstants.PATH_PRE + "*" + CoreConstants.PATH_SUF});
        } catch (RuntimeException ex) {
            LOGGER.error("平台组件Spring上下文加载失败!");
            throw ex;
        }

        try {
            springContextMain = BootstrapConfigUtil.getProperty(CoreConstants.SPRING_CONTEXT_MAIN);
            if (springContextMain == null || springContextMain.trim().length() == 0) {
                springContextMain = CoreConstants.CANCER_PATH_MAIN;
            }

            this.appContext = new CancerXmlApplicationContext(this.cancerContext, context -> ContextUtils.setSpringAppContext(context), new String[]{springContextMain});
        } catch (RuntimeException ex) {
            LOGGER.error("应用Spring上下文加载失败!");
            throw ex;
        }
    }

    protected void start(){
        if (this.appContext == null) {
            LOGGER.error("容器启动失败, 未初始化!");
        }

        try {
            this.appContext.start();
            this.appContext.registerShutdownHook();
        } catch (RuntimeException ex) {
            LOGGER.error("容器启动失败!");
            throw ex;
        }

        try {
            HealthManager healthManager = this.appContext.getBean(HealthManager.class);
            healthManager.healthcheck();
        } catch (RuntimeException var2) {
            LOGGER.error("平台组件健康检查失败!");
            throw var2;
        }

        LOGGER.info(" ********************************************************");
        LOGGER.info(" *** Distrbuted Application Platform is started! ***");
        LOGGER.info(" ********************************************************");
        ContextUtils.setApplicationStarted(true);
        if (LOGGER.isDebugEnabled()) {
            printBeans(this.appContext);
        }
    }

    protected void blocking(){

        this.latch = new CountDownLatch(1);

        try {
            this.latch.await();
        } catch (InterruptedException var2) {
            LOGGER.error("创建平台后台运行daemon失败", var2);
        }
    }

    protected void shutdown(){

        if (this.latch != null) {
            this.latch.countDown();
        }

        destroyAll();
        if (this.appContext != null && this.appContext.isActive()) {
            this.appContext.close();
        }

        if (this.cancerContext != null && this.cancerContext.isActive()) {

            this.cancerContext.getBeansOfType(ComponentShutdownHook.class).entrySet().stream().forEach(
                item->{
                    LOGGER.info("Destroy {} ", item.getKey());

                    try {
                        (item.getValue()).register();
                    } catch (Exception var4) {
                        LOGGER.warn("Destroy {} cause an Exception !", item.getKey(), var4);
                    }
                }
            );

            this.cancerContext.close();
        }

        LOGGER.info(" ********************************************************");
        LOGGER.info(" *** Distributed Application Platform is shutdown! ***");
        LOGGER.info(" ********************************************************");

    }

    protected static void printBeans(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        StringBuilder sb = new StringBuilder();

        for(String beanName : beanNames){
            sb.append("\t").append(beanName).append("\r\n");
        }

        LOGGER.debug("Context included bean names:" + sb.toString());
    }

    private static void printAppInfo() {
        LOGGER.info(" ********************************************************");
        Object[] appInfo = new Object[]{BootstrapConfigUtil.getProperty(CoreConstants.APP_TENANTID), BootstrapConfigUtil.getProperty(CoreConstants.APP_NAME),
                BootstrapConfigUtil.getProperty(CoreConstants.APP_ENV), BootstrapConfigUtil.getProperty(CoreConstants.APP_VERSION)};
        LOGGER.info("Application Info:TenantId:[{}],AppName[{}],Env[{}],Version[{}]", appInfo);
        LOGGER.info(" ********************************************************");
    }

    protected static void destroyAll() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        // destroy all the registries
        AbstractRegistryFactory.destroyAll();
        // destroy all the protocols
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        for (String protocolName : loader.getLoadedExtensions()) {
            try {
                Protocol protocol = loader.getLoadedExtension(protocolName);
                if (protocol != null) {
                    protocol.destroy();
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }


}
