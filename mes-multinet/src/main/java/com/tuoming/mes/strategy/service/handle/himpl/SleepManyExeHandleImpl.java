package com.tuoming.mes.strategy.service.handle.himpl;

import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.service.handle.SleepExeHandle;

/**
 * 多补一休眠小区执行流程
 *
 * @author Administrator
 */
@Component("sleepManyExeHandle")
public class SleepManyExeHandleImpl implements SleepExeHandle {

    @Autowired
    @Qualifier("sleepExeDao")
    private SleepExeDao dao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    public void handle(List<Map<String, Object>> dataList, int top,
                       SleepExeSetting set, Map<String, Integer> cellCount) {
        int orderId = 0;//下发命令顺序
        Map<String, String> manySleepExe = new HashMap<String, String>();
        for (Map<String, Object> data : dataList) {
            String untiBs = null;//同一个网元标识
            String ne = null;
            String cellid = null;
            String desc = "多补一指令生成小区信息：";
            if (Constant.TD.equalsIgnoreCase(set.getZs())) {
                untiBs = String.valueOf(data.get("src_rnc"));
                ne = String.valueOf(data.get("src_rnc"));
                cellid = String.valueOf(data.get("src_lcid"));
                desc += "[RNC:" + untiBs + "] [CELLID:" + cellid + "]";
            } else if (Constant.LTE.equalsIgnoreCase(set.getZs())) {
                untiBs = String.valueOf(data.get("omm"));
                ne = String.valueOf(data.get("src_enodebid"));
                cellid = String.valueOf(data.get("src_localcellid"));
                desc += "[OMM:" + untiBs + "] [ENODEBID:" + ne + "] [CELLID:" + cellid + "]";
            }
            if (!cellCount.containsKey(untiBs)) {
                cellCount.put(untiBs, 0);
            }
            if (cellCount.get(untiBs) > top) {//假如该网元下的休眠小区数大于指定数目，则该网元下的小区不能休眠
                continue;
            }
            //多补一中，多条休眠小区相同
            if (!manySleepExe.containsKey(ne + "_" + cellid)) {
                AdjustCommand command = new AdjustCommand();//构建休眠小区命令对象
                command.setTimeStamp(DateUtil.currentDate());
                command.setApplied(0);
                command.setOrderId(orderId++);
                command.setAppName(Constant.APP_MULTINET);
                command.setGroupName(Constant.SLEEP_MANY);     //mes_adjust_command表中group_name ：SLEEP_MANY
                command.setOwner(Constant.APP_MULTINET);
                command.setTargetObject(untiBs);
                if (Constant.LTE.equalsIgnoreCase(set.getZs())) {
                    command.setObjectType(ObjectType.OMC);
                } else {
                    command.setObjectType(ObjectType.BSC);
                }
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
                data.put("starttime", com.tuoming.mes.strategy.util.DateUtil.format((Date) data.get("starttime")));
                command.setExtend4(String.valueOf(JSONSerializer.toJSON(data)));
                command.setExtend5(String.valueOf(data.get("bus_type")));
                try {
                    dao.addManySleepArea(data, command);
                    cellCount.put(untiBs, cellCount.get(untiBs) + 1);//将目标网元的小区休眠个数增加一
                } catch (Exception e) {
                    e.printStackTrace();
                    businessLogDao.insertLog(13, desc, 1);
                }

                //指令生成后将该小区记录
                manySleepExe.put(ne + "_" + cellid, ne + "_" + cellid);
            } else {
                //该小区已经休眠，只记录当前休眠小区表，不记录休眠指令表
                dao.addManySleepArea(data);
            }
            businessLogDao.insertLog(13, desc, 0);
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
