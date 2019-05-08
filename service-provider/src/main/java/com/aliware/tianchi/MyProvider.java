package com.aliware.tianchi;

import java.io.IOException;
import java.util.List;
import com.aliware.tianchi.policy.SmallConfig;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author guohaoice@gmail.com */
public class MyProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(MyProvider.class);

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

    ApplicationConfig application = new ApplicationConfig();
    application.setName("service-provider");

    RegistryConfig registry = new RegistryConfig();
    registry.setAddress("N/A");

    ProtocolConfig protocol = new ProtocolConfig();
    protocol.setName("dubbo");
    protocol.setPort(20880);
    protocol.setThreads(200);

    ServiceConfig<HashInterface> service =
        new ServiceConfig<>();
    service.setApplication(application);
    service.setRegistry(registry);
    service.setProtocol(protocol);
    service.setInterface(HashInterface.class);
    service.setRef(new HashServiceImpl(System.getenv("salt"), configs));
    service.export();

    Thread.currentThread().join();
  }
}
