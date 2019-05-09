package com.aliware.tianchi.netty;

import com.aliware.tianchi.HashInterface;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.listener.CallbackListener;
import org.apache.dubbo.rpc.service.CallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.INTERNAL_SERVER_ERROR;

@ChannelHandler.Sharable
public class HttpProcessHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProcessHandler.class);
    public final ApplicationConfig application = new ApplicationConfig();

    private final FullHttpResponse ok =
            new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("OK", CharsetUtil.UTF_8));
    private final FullHttpResponse error =
            new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
    private final AtomicBoolean init = new AtomicBoolean(false);
    private HashInterface hashInterface;
    private String salt = System.getenv("salt");

    public HttpProcessHandler() {
        this.hashInterface = getServiceStub();
        ok.headers().add(HttpHeaderNames.CONTENT_LENGTH, 2);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        long start = System.currentTimeMillis();
        String content = RandomStringUtils.randomAlphanumeric(16);
        int expected = (content + salt).hashCode();
        if (init.compareAndSet(false, true)) {
            initCallbackListener();
        }

        hashInterface
                .hash(content)
                .whenComplete(
                        (h, t) -> {
                            if (h.equals(expected)) {
                                ctx.writeAndFlush(ok.retain());
                                if (LOGGER.isInfoEnabled()) {
                                    LOGGER.info(
                                            "Request result:success cost:{} ms", System.currentTimeMillis() - start);
                                }
                            } else {
                                ctx.writeAndFlush(error.retain());
                                LOGGER.info(
                                        "Request result:failure cost:{} ms", System.currentTimeMillis() - start);
                            }
                        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private List<URL> buildUrls(String interfaceName, Map<String, String> attributes) {
        List<URL> urls = new ArrayList<>();
        urls.add(new URL(Constants.DUBBO_PROTOCOL, "provider-small", 20880, interfaceName, attributes));
//    urls.add(new URL(Constants.DUBBO_PROTOCOL, "39.100.66.90", 20880, interfaceName, attributes));
        return urls;
    }

    private HashInterface getServiceStub() {
        application.setName("service-gateway");

        // 直连方式，不使用注册中心
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("N/A");

        ReferenceConfig<HashInterface> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(HashInterface.class);
        List<URL> urls = reference.toUrls();
        urls.addAll(buildUrls(HashInterface.class.getName(), new HashMap<>()));
        return reference.get();
    }

    private void initCallbackListener() {
        Set<String> supportedExtensions =
                ExtensionLoader.getExtensionLoader(CallbackListener.class).getSupportedExtensions();
        if (!supportedExtensions.isEmpty()) {
            for (String supportedExtension : supportedExtensions) {
                CallbackListener extension =
                        ExtensionLoader.getExtensionLoader(CallbackListener.class)
                                .getExtension(supportedExtension);

                ReferenceConfig<CallbackService> reference = new ReferenceConfig<>();
                reference.setApplication(application);
                reference.setInterface(CallbackService.class);

                Map<String, String> attributes = new HashMap<>();
                attributes.put("addListener.1.callback", "true");
                attributes.put("callbacks", "1000");
                attributes.put("connections", "1");
                attributes.put("dubbo", "2.0.2");
                attributes.put("dynamic", "true");
                attributes.put("generic", "false");
                attributes.put("interface", "org.apache.dubbo.rpc.service.CallbackService");
                attributes.put("methods", "addListener");

                List<URL> urls = buildUrls(CallbackService.class.getName(), attributes);
                for (URL url : urls) {
                    reference.toUrls().clear();
                    reference.toUrls().add(url);
                    // TODO: remote call may fail
                    reference.get().addListener("env.listener", extension);
                }
            }
        }
    }
}
