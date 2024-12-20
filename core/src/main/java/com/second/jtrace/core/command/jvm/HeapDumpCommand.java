package com.second.jtrace.core.command.jvm;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.HeapDumpResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.StringUtils;
import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


@Data
public class HeapDumpCommand extends AbstractCommand {
    private boolean live = true;
    private boolean returnFile = false;

    @Override
    public IResponse executeForResponse(IClient client) {
        try {
            HeapDumpResponse heapDumpResponse = new HeapDumpResponse();
            String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
            File file = File.createTempFile("heapdump" + date + (live ? "-live" : "") + StringUtils.UUID(true).substring(0, 5), ".hprof");
            String dumpFilePath = file.getAbsolutePath();
            file.delete();
            run(dumpFilePath, live);
            heapDumpResponse.setMsg("dump success.");
            heapDumpResponse.setFilePath(dumpFilePath);
            if (returnFile) {
                byte[] fileContent = Files.readAllBytes(Paths.get(dumpFilePath));
                heapDumpResponse.setFileContent(fileContent);
            }
            return heapDumpResponse;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return HeapDumpResponse.class;
    }

    private void run(String file, boolean live) throws IOException {
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        hotSpotDiagnosticMXBean.dumpHeap(file, live);
    }


}
