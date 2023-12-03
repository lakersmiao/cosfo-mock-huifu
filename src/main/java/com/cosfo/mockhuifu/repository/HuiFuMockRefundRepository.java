package com.cosfo.mockhuifu.repository;

import com.cosfo.mockhuifu.model.po.HuiFuMockRefund;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 汇付模拟退款表 服务类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
public interface HuiFuMockRefundRepository extends IService<HuiFuMockRefund> {


    /**
     * 根据请求日期和请求流水号查询历史退款总金额
     * @param orgReqDate
     * @param orgReqSeqId
     * @param stat
     * @return
     */
    BigDecimal queryTotalRefundAmtByReqDateAndReqSeqId(String orgReqDate, String orgReqSeqId, String stat);
}
