package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import com.pyrlong.configuration.ConfigurationManager;
import com.tuoming.mes.strategy.dao.OverlayDegreeDao;
import com.tuoming.mes.strategy.model.ManyOverDegCalModel;

/**
 * Lte华为MRO计算重叠覆盖度逻辑处理器
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("manyLteOverDegCalHandler")
public class ManyLteOverDegCalHandler extends ManyOverDegCalHandler {

    public int overDag = ConfigurationManager.getDefaultConfig().getInteger("LTEMANYOVERDAG_OVERDAG", 80);
    public int singleNcRate = ConfigurationManager.getDefaultConfig().getInteger("LTEMANYOVERDAG_SINGLE_NC_RATE", 30);
    @Autowired
    @Qualifier("overlayDegreeDao")
    private OverlayDegreeDao overlayDegreeDao;

    @Override
    public void assemblyQuerySql(Map<String, Object> data, ManyOverDegCalModel manyInfoModel) {
        if (manyInfoModel.getQuerySql().length() > 0) {
            manyInfoModel.addQuerySql(" or ");
        } else {
            manyInfoModel.addQuerySql(" enodebid=");
            manyInfoModel.addQuerySql(data.get("enodebid"));
            manyInfoModel.addQuerySql(" and localcellid=");
            manyInfoModel.addQuerySql(data.get("localcellid"));
            manyInfoModel.addQuerySql(" and (");
        }
        manyInfoModel.addQuerySql("(nearfcn=");
        manyInfoModel.addQuerySql(data.get("nearfcn"));
        manyInfoModel.addQuerySql(" and pci=");
        manyInfoModel.addQuerySql(data.get("pci"));
        manyInfoModel.addQuerySql(")");
    }

    @Override
    public int queryTrueCellCount(String sql) {
        return overlayDegreeDao.queryTrueLteCellCoint(sql);
    }


}
