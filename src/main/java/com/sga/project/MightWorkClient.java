package com.sga.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Scanner;
import javax.naming.AuthenticationException;
import javax.servlet.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import com.sga.project.services.*;
import com.sga.project.transferables.*;

public class MightWorkClient {

	public static void main(String[] args) throws AuthenticationException, TException, UnavailableException, IOException {
	    
	    Scanner scan = new Scanner(System.in);
	    int choice = 0;
	    do {
	    	printMenu();
	    	choice = scan.nextInt();
	    	
	    	switch(choice)
	    	{
	    		case 1:
	    			THttpClient transport = new THttpClient("http://localhost:8888/echo");
	    		    
	    		    TProtocol protocol = new TBinaryProtocol(transport);
	    		    
	    		    EchoService.Client client = new EchoService.Client(protocol);
	    		    System.out.print("Enter the message to echo: ");
	    		    scan.nextLine();
	                String msg = scan.nextLine();
	                
	    		    String reply = client.echo(msg);
	    		    System.out.println(reply);
	    		    break;
	    		
	    		case 2:
	    			System.out.print("Input file path: ");
	    			scan.nextLine();
	                String path = scan.nextLine();
	                
	                upload(path);
	                
	    			break;
	    		case 3:
	    			List<String> files = getAvailableFileList();
	                if (files.isEmpty()) {
	                    System.out.println("No files are available.");
	                } else {
	                    System.out.println("List of files");
	                    for (int i = 0; i < files.size(); i++) {
	                        System.out.println("[" + i + "] " + files.get(i));
	                    }
	                    System.out.print("Select file: ");
	                    scan.nextLine();
	                    String fileNumber = scan.nextLine();
	                    int selected = Integer.parseInt(fileNumber);
	                    if (selected >= 0 && selected < files.size()) {
	                        download(files.get(selected));
	                    } else {
	                        System.out.println(selected + " is wrong number.");
	                    }
	                }
	    			break;
	    		default:
	    			System.out.println("Exiting....");
	    	}
	    	
	    }while(choice < 4);

	}
	
	/** 
	 * This method downloads the specified file which were
	 * uploaded into the download folder in the server.
	 * */
	private static void download(String fileName) throws IOException {
		// TODO Auto-generated method stub
		int dot = fileName.lastIndexOf('.');
        String name = dot != -1 ? fileName.substring(0, fileName.indexOf('.')) : fileName;
        String ext = dot != -1 ? fileName.substring(name.length()) : ".unknown";
        File destinationDir = new File("downloads");
        File destination = File.createTempFile(name, ext, destinationDir);
        FileOutputStream fos = new FileOutputStream(destination);

        try {
            TransferInfo reqInfo =  new  TransferInfo ();
            reqInfo.type = TransferType.REQUEST;
            reqInfo.fileName = fileName;

            THttpClient transport = new THttpClient("http://localhost:8888/download");
    	    
    	    TProtocol protocol = new TBinaryProtocol(transport);
            DownloadService.Client client = new DownloadService.Client(protocol);
            TransferInfo recvInfo = client.download(reqInfo);

            if (!destinationDir.exists()) {
                destinationDir . mkdirs ();
            }

            long total = recvInfo.length;
            long offset = 0;
            reqInfo.type = TransferType.PROGRESS;
            do {
                recvInfo = client.download(reqInfo);
                offset += recvInfo.length;
                fos.getChannel().write(recvInfo.data);
            } while (total > offset);
            System.out.println("Successfully downloaded.");
        } catch (TException e) {
            e . printStackTrace ();
            fos.close();
            fos = null;
            destination.delete();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
	}

	
	/** 
	 * This method retrieves a list of files which were
	 * uploaded into the download folder in the server.
	 * */
	private static List<String> getAvailableFileList() throws TTransportException {
		// TODO Auto-generated method stub
		THttpClient transport = new THttpClient("http://localhost:8888/download");
	    
	    TProtocol protocol = new TBinaryProtocol(transport);
	    
		try {
            DownloadService.Client client = new DownloadService.Client(protocol);
            List<String> list = client.getFileList();
            return list;
        } catch (TException e) {
            e . printStackTrace ();
        } finally {
        }
        return null;
	}
	
	/** 
	 * This method uploads a file from the localhost 
	 * to the server into the downloads folder.
	 * */
	private static void upload(String path) throws IOException, TTransportException {
		// TODO Auto-generated method stub
		
		THttpClient uploadTransport = new THttpClient("http://localhost:8888/upload");
	    
	    TProtocol uploadProtocol = new TBinaryProtocol(uploadTransport);
	    
	    UploadService.Client uploadClient = new UploadService.Client(uploadProtocol);
		
		File file = new File(path);
        boolean in = false;
        if (!file.exists()) {
            System.out.println("File not found \"" + file.getAbsolutePath() + "\"");
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        try {
        	TransferInfo reqInfo =  new  TransferInfo ();
            reqInfo.type = TransferType.REQUEST;
            reqInfo.fileName = file.getName();
            reqInfo.length = file.length();

            uploadClient.upload(reqInfo);

            reqInfo.type = TransferType.PROGRESS;
            reqInfo.data = ByteBuffer.allocate(1024 * 10);
            FileChannel fileChannel = fis.getChannel();
            while ((reqInfo.length = fileChannel.read(reqInfo.data)) > 0) {
                reqInfo.data.flip();
                uploadClient.upload(reqInfo);
                System.out.println(reqInfo.data);
                reqInfo.data.clear();
            }
            System.out.println("Success to upload.");
        } catch (TException e) {
            e . printStackTrace ();
            fis.close();
            in =  true ;
            file.delete();
        } finally {
            if (!in ) {
                fis.close();
            }
        }
		
	}
	
	private static void printMenu() {
		// TODO Auto-generated method stub
		 System.out.print("Select a number\n" +
	                "1) Echo\n" +
	                "2) File upload\n" +
	                "3) File download\n" +
	                "4) Exit\n" +
	                "Select: ");
	}

}
