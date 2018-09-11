package com.sga.project.Configs;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import com.sga.project.services.*;

import com.sga.project.Configs.DownloadConfig;
import com.sga.project.Configs.EchoConfig;
import com.sga.project.Configs.UploadConfig;
import com.sga.project.handlers.*;

@Configuration
@SpringBootApplication(exclude=DispatcherServletAutoConfiguration.class)
@ComponentScan
public class ThriftServerConfig {

	@Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
	
	@Bean
    public ServletRegistrationBean thriftEchoServlet(TProtocolFactory protocolFactory, EchoServiceHandler handler) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();   
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(EchoConfig.class);
        dispatcherServlet.setApplicationContext(applicationContext);
        TServlet tServlet = new TServlet(new EchoService.Processor<>(handler), protocolFactory);
        ServletRegistrationBean srb = new ServletRegistrationBean(tServlet, "/echo");
        srb.setName("echo");
        return srb;
	}
	
	@Bean
    public ServletRegistrationBean thriftUploadServlet(TProtocolFactory protocolFactory, UploadServiceHandler handler) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();   
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(UploadConfig.class);
        dispatcherServlet.setApplicationContext(applicationContext);
        TServlet tServlet = new TServlet(new UploadService.Processor<>(handler), protocolFactory);
        ServletRegistrationBean srb = new ServletRegistrationBean(tServlet, "/upload");
        srb.setName("upload");
        return srb;
	}
	
	@Bean
    public ServletRegistrationBean thriftDownloadServlet(TProtocolFactory protocolFactory, DownloadServiceHandler handler) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();   
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(DownloadConfig.class);
        dispatcherServlet.setApplicationContext(applicationContext);
        TServlet tServlet = new TServlet(new DownloadService.Processor<>(handler), protocolFactory);
        ServletRegistrationBean srb = new ServletRegistrationBean(tServlet, "/download");
        srb.setName("download");
        return srb;
	}
}
