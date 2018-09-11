package com.sga.project;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Service
public class EchoServiceHandler implements EchoService.Iface{

	@Override
	public String echo(String input) throws TException {
		// TODO Auto-generated method stub
		try {
            return "from " + InetAddress.getLocalHost().getHostAddress() + " : " + input;
        } catch (UnknownHostException e) {
            throw new TException(e);
        }
	}

}
