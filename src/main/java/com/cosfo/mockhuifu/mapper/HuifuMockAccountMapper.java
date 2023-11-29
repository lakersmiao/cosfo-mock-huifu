package com.cosfo.mockhuifu.mapper;

import com.cosfo.mockhuifu.model.po.HuiFuMockAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟账户 Mapper 接口
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Mapper
public interface HuifuMockAccountMapper extends BaseMapper<HuiFuMockAccount> {

    /**
     * 更新延迟金额
     * @param huiFuId
     * @param increaseDelayedAmt
     */
    int increaseDelayedAmt(@Param("id") String huiFuId, @Param("increaseDelayedAmt") BigDecimal increaseDelayedAmt);
}
