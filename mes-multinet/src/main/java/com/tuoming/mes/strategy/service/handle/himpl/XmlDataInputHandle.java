package com.tuoming.mes.strategy.service.handle.himpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.strategy.service.handle.DataInputHandle;
import com.tuoming.mes.strategy.service.handle.FileDecode;
import com.tuoming.mes.strategy.util.CompressUtil;

@Component("XmlDataInputHandle")
public class XmlDataInputHandle implements DataInputHandle {
    private static Logger logger = Logger.getLogger(XmlDataInputHandle.class);
    FileDecode f = AppContext.getBean("LteHWLocalMROFileParser");

    @Override
    public List<String[]> readFile(String filePath) {
        try {
            byte[] b = CompressUtil.decompress(filePath);
            return f.parse(b);
        } catch (Exception e) {
            logger.error("");
        }
        return null;
    }

}
