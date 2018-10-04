package com.tuoming.mes.strategy.service.handle.himpl;

import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.service.handle.TdOffSleepExeHandle;

@Component("tdOffSleepExeHandle")
public class TdOffSleepExeHandleImpl implements TdOffSleepExeHandle {

    @Autowired
    @Qualifier("sleepExeDao")
    private SleepExeDao dao;

    @Override
    public void tdOffHandle(List<Map<String, Object>> dataList, int top,
                            SleepExeSetting set, Map<String, Integer> cellCount) {

        int orderId = 0;//下发命令顺序
        for (Map<String, Object> data : dataList) {
            String untiBs = String.valueOf(data.get("src_rnc"));
            String ne = String.valueOf(data.get("src_rnc"));
            String cellid = String.valueOf(data.get("src_cellid"));

//		    if(!cellCount.containsKey(untiBs)) {
//		    	cellCount.put(untiBs, 0);
//		    }
//		    if(cellCount.get(untiBs)>top) {//假如该网元下的休眠小区数大于指定数目，则该网元下的小区不能休眠
//		    	continue;
//		    }
            AdjustCommand command = new AdjustCommand();//构建休眠小区命令对象
            command.setTimeStamp(DateUtil.currentDate());
            command.setApplied(0);
            command.setOrderId(orderId++);
            command.setAppName(Constant.APP_MULTINET);
            command.setGroupName(Constant.TD_NETWORK_OFF_SLEEP);
            command.setOwner(Constant.APP_MULTINET);
            command.setTargetObject(untiBs);
            command.setObjectType(ObjectType.BSC);
            command.setBatchId(Constant.CURRENT_BATCH);
            String spiltChar = "@";
            String commandText = this.buildCommand(set, data, spiltChar);
            String queryCommandText = this.buildQueryCommand(set, data, spiltChar);
            if (StringUtils.isEmpty(commandText)) {
                continue;
            }
            command.setCommand(commandText);//将休眠命令存入命令表中
            command.setExtend1(queryCommandText);
            command.setExtend2(ne);
            command.setExtend3(cellid);
            data.put("command", commandText);
            data.put("starttime", DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
            command.setExtend4(String.valueOf(JSONSerializer.toJSON(data)));
            command.setExtend5(String.valueOf(data.get("bus_type")));
            try {
                dao.addtdOffSleepArea(data, command, data.get("sleep_type").toString());
//	        	cellCount.put(untiBs, cellCount.get(untiBs)+1);//将目标网元的小区休眠个数增加一
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据结果和配置生成休眠指令
     *
     * @param set
     * @param data
     * @return
     */
    private String buildCommand(SleepExeSetting set, Map<String, Object> data, String spiltChar) {
        String commandMap = "";
        String cp_str = "#RMV CAGROUPCELL:CAGROUPID=[cagroupid],LOCALCELLID=[src_localcellid],ENODEBID=[src_enodebid];";
        if (data.get("cagroupid") == null || data.get("cagroupid") == "") {
            commandMap = set.getCommandMap().split(spiltChar)[0].replace(cp_str, "");
        } else {
            commandMap = set.getCommandMap().split(spiltChar)[0];
        }
        String command = (String) ((Map) DSLUtil.getDefaultInstance().compute(commandMap, data))
                .get(data.get("src_vender"));
        return command;
    }

    /**
     * 根据结果和配置生成休眠小区查询指令
     *
     * @param set
     * @param data
     * @return
     */
    private String buildQueryCommand(SleepExeSetting set, Map<String, Object> data, String spiltChar) {
        String queryCommand = (String) ((Map) DSLUtil.getDefaultInstance().compute(set.getCommandMap().split(spiltChar)[1], data))
                .get(data.get("src_vender"));
        return queryCommand;
    }
}
