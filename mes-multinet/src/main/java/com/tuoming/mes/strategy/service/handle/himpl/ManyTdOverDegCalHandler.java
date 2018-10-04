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
 * TD华为MRO计算重叠覆盖度逻辑处理器
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("manyTdOverDegCalHandler")
public class ManyTdOverDegCalHandler extends ManyOverDegCalHandler {

    public int overDag = ConfigurationManager.getDefaultConfig().getInteger("TDMANYOVERDAG_OVERDAG", 80);
    public int singleNcRate = ConfigurationManager.getDefaultConfig().getInteger("TDMANYOVERDAG_SINGLE_NC_RATE", 30);
    @Autowired
    @Qualifier("overlayDegreeDao")
    private OverlayDegreeDao overlayDegreeDao;

    @Override
    public void assemblyQuerySql(Map<String, Object> data, ManyOverDegCalModel manyInfoModel) {
        if (manyInfoModel.getQuerySql().length() > 0) {
            manyInfoModel.addQuerySql(" or ");
        } else {
            manyInfoModel.addQuerySql(" rncid=");
            manyInfoModel.addQuerySql(data.get("rncid"));
            manyInfoModel.addQuerySql(" and cellid=");
            manyInfoModel.addQuerySql(data.get("cellid"));
            manyInfoModel.addQuerySql(" and (");
        }
        manyInfoModel.addQuerySql("(ncUarfcn=");
        manyInfoModel.addQuerySql(data.get("ncUarfcn"));
        manyInfoModel.addQuerySql(" and ncSc=");
        manyInfoModel.addQuerySql(data.get("ncSc"));
        manyInfoModel.addQuerySql(")");
    }

    @Override
    public int queryTrueCellCount(String sql) {
        return overlayDegreeDao.queryTrueTdCellCoint(sql);
    }

}
