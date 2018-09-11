package com.sga.project;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftServerConfig {

	@Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
	
	@Bean
    public ServletRegistrationBean thriftBookServlet(TProtocolFactory protocolFactory, EchoServiceHandler handler) {
        TServlet tServlet = new TServlet(new EchoService.Processor<>(handler), protocolFactory);

        return new ServletRegistrationBean(tServlet, "/echo");
    }
	
	@Bean
	public ServletRegistrationBean thriftDownloadServlet(TProtocolFactory protocolFactory, DownloadServiceHandler handler) {
		TServlet tServlet = new TServlet(new DownloadService.Processor<>(handler), protocolFactory);

        return new ServletRegistrationBean(tServlet, "/download");
	}
	
	@Bean
	public ServletRegistrationBean thriftUploadServlet(TProtocolFactory protocolFactory, UploadServiceHandler handler) {
		TServlet tServlet = new TServlet(new UploadService.Processor<>(handler), protocolFactory);

        return new ServletRegistrationBean(tServlet, "/upload");
	}
}
