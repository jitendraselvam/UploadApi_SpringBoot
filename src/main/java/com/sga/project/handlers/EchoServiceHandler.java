package com.sga.project.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import com.sga.project.services.*;


@Service
public class EchoServiceHandler implements EchoService.Iface{


	@Override
	public String echo(String input) throws TException {
		// TODO Auto-generated method stub
		try {
			System.out.println(input);
            return "from " + InetAddress.getLocalHost().getHostAddress() + " : " + input;
        } catch (UnknownHostException e) {
            throw new TException(e);
        }
	}

}
