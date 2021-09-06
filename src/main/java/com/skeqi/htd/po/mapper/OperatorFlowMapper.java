package com.skeqi.htd.po.mapper;

import com.skeqi.htd.po.entity.OperatorFlow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作流水mapper
 *
 * @author linkin
 */
@Mapper
public interface OperatorFlowMapper {
	/**
	 * 根据订单号获取最近的一条操作记录
	 *
	 * @param orderNo
	 * @return
	 */
	OperatorFlow getLatestFlow(String orderNo);
}
