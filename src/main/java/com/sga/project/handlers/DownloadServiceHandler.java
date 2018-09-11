package com.sga.project.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.sga.project.transferables.*;
import com.sga.project.services.*;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Service
public class DownloadServiceHandler implements DownloadService.Iface {

	
	private static final File BASE_DIRECTORY = new File("downloads");
    private ByteBuffer ioBuffer = ByteBuffer.allocate(1024 * 10);
    private Context context;
	
    @Override
	public TransferInfo download(TransferInfo info) throws TException {
		// TODO Auto-generated method stub
		switch (info.type) {
    	case  REQUEST :
            return beginDownload (info);

        case PROGRESS:
            return progressDownload();

        default:
            throw new TException();
		}
	}
	
	@Override
	public List<String> getFileList() throws TException {
		// TODO Auto-generated method stub
		String[] arr = BASE_DIRECTORY.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File each = new File(dir, name);
                return each.isFile() && !each.isHidden();
            }
        });
        ArrayList<String> list = new ArrayList<String>(arr.length);
        for (String file : arr) {
            list.add(file);
        }
        return list;
	}

	private TransferInfo beginDownload(TransferInfo reqInfo) throws TException {
        TransferInfo recvInfo =  new  TransferInfo ();
        File src = new File(BASE_DIRECTORY, reqInfo.fileName);
        if (src.exists()) {
            try {
                context = new Context();
                context.raf = new RandomAccessFile(src, "r");
                recvInfo.length = src.length();
                return recvInfo;
            } catch (FileNotFoundException e) {
                try {
                    context.raf.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                throw new TException(e);
            }
        }
        return recvInfo;
    }

    private TransferInfo progressDownload() throws TException {
        try {
            TransferInfo recvInfo =  new  TransferInfo ();
            recvInfo.data = ioBuffer;
            ioBuffer.clear();
            recvInfo.length = context.raf.getChannel().read(ioBuffer);
            ioBuffer.flip();
            return recvInfo;
        } catch (IOException e) {
            try {
                context.raf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new TException(e);
        }
    }

    private static class Context {
        private  RandomAccessFile raf;
    }

}
