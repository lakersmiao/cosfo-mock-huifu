package com.cosfo.mockhuifu.mapper;

import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟支付单 Mapper 接口
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Mapper
public interface HuifuMockPaymentMapper extends BaseMapper<HuiFuMockPayment> {

    /**
     * 更新状态
     * @param id
     * @param orgStat
     * @param finalStat
     * @return
     */
    int updateStatusById(@Param("id") Long id, @Param("orgStat") String orgStat, @Param("finalStat") String finalStat);
}
