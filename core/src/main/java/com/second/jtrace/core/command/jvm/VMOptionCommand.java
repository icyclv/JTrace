package com.second.jtrace.core.command.jvm;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.VMOptionResponse;
import com.second.jtrace.core.command.jvm.vo.VMOptionVO;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.IResponse;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;


public class VMOptionCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        List<VMOption> vmOptions = hotSpotDiagnosticMXBean.getDiagnosticOptions();

        VMOptionResponse vmOptionResponse = new VMOptionResponse();
        List<VMOptionVO> vmOptionVOS = new ArrayList<>();
        for (VMOption vmOption : vmOptions) {
            vmOptionVOS.add(castVMOption(vmOption));
        }
        vmOptionResponse.setVmOptions(vmOptionVOS);
        return vmOptionResponse;
    }

    /*
        * Cast VMOption to VMOptionVO, as VMOption from sun.management package.
     */
    public VMOptionVO castVMOption(VMOption vmOption) {
        return new VMOptionVO(vmOption.getName(), vmOption.getValue(), vmOption.isWriteable(),vmOption.getOrigin().name());
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return VMOptionResponse.class;
    }

    @Override
    public int getMessageTypeId() {
        return MessageTypeMapper.TypeList.VmOptionCommand.ordinal();
    }
}
