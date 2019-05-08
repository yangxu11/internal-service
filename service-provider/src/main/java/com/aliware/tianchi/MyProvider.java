package com.aliware.tianchi;

import com.aliware.tianchi.policy.SmallConfig;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ArgumentConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.service.CallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author guohaoice@gmail.com
 */
public class MyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProvider.class);

    private static ApplicationConfig application = new ApplicationConfig();
    private static RegistryConfig registry = new RegistryConfig();
    private static ProtocolConfig protocol = new ProtocolConfig();

  public static void main(String[] args) throws IOException, InterruptedException {
    String env;
    if (args.length != 1) {
      LOGGER.info("No specific args found, use [DEFAULT] to run demo provider");
      env = "small";
    } else {
      env = args[0];
    }
    List<ThrashConfig> configs;
    switch (env) {
      case "small":
        configs = new SmallConfig().allConfig;
        break;
      default:
        configs = new SmallConfig().allConfig;
    }

        // 当前应用配置
        application.setName("service-provider");

        // 连接注册中心配置
        registry.setAddress("N/A");

        // 服务提供者协议配置
        protocol.setName("dubbo");
        protocol.setPort(20880);
        protocol.setThreads(200);

        // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口
        exportHashService(configs);

        try {
            exportCallbackServiceIfNeed();
        } catch (Exception e) {
            LOGGER.error("exportCallbackServiceIfNeed failed", e);
        }


        System.in.read(); // press any key to exit
    }

    private static void exportHashService(List<ThrashConfig> configs) {
        // 服务提供者暴露服务配置
        ServiceConfig<HashInterface> service =
                new ServiceConfig<>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        service.setApplication(application);
        service.setRegistry(registry); // 多个注册中心可以用setRegistries()
        service.setProtocol(protocol); // 多个协议可以用setProtocols()
        service.setInterface(HashInterface.class);
        service.setRef(new HashServiceImpl(System.getenv("salt"), configs));

        // 暴露及注册服务
        service.export();
    }

    private static void exportCallbackServiceIfNeed() {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(CallbackService.class).getSupportedExtensions();
        if (supportedExtensions != null && supportedExtensions.size() == 1) {
            CallbackService callbackService = ExtensionLoader.getExtensionLoader(CallbackService.class).getExtension(supportedExtensions.iterator().next());
            ServiceConfig<CallbackService> callbackServiceServiceConfig = new ServiceConfig<>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
            callbackServiceServiceConfig.setApplication(application);
            callbackServiceServiceConfig.setRegistry(registry); // 多个注册中心可以用setRegistries()
            callbackServiceServiceConfig.setProtocol(protocol); // 多个协议可以用setProtocols()
            callbackServiceServiceConfig.setInterface(CallbackService.class);
            callbackServiceServiceConfig.setRef(callbackService);
            callbackServiceServiceConfig.setCallbacks(1000);
            callbackServiceServiceConfig.setConnections(1);
            MethodConfig methodConfig = new MethodConfig();
            ArgumentConfig argumentConfig = new ArgumentConfig();
            argumentConfig.setCallback(true);
            argumentConfig.setIndex(1);
            methodConfig.setArguments(Collections.singletonList(argumentConfig));
            methodConfig.setName("addListener");
            callbackServiceServiceConfig.setMethods(Collections.singletonList(methodConfig));
            callbackServiceServiceConfig.export();
        }

    }
}
