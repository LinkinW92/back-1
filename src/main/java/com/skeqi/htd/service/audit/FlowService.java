package com.skeqi.htd.service.audit;

import com.skeqi.htd.common.Asserts;
import com.skeqi.htd.common.BizException;
import com.skeqi.htd.common.OrderType;
import com.skeqi.htd.po.entity.OperatorFlow;
import com.skeqi.htd.po.mapper.OperatorFlowMapper;
import com.skeqi.htd.service.audit.flow.FlowTemplate;
import com.skeqi.htd.service.audit.flow.Valve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * （审批）流程服务
 * 1.每经过一个阀门，都会产生一条流水记录
 * 2.从当前处理的阀门，可以推出下一个需要处理的阀门
 * 流程触发时机：
 * 1.销售/采购订单生成后，自动绑定一个流程模板
 * 2.当前阀门逻辑处理完成后，若非终止状态，更新到下一级阀门，并推消息给下一级阀门的审核人
 *
 * @author linkin
 * @apiNote 当前版本中，即htm.version = 1.0.0时，一种订单类型只对应一个流程模板，后期有
 * 业务需要再拓展称一对多的模式。
 */
@Service
@Slf4j
public class FlowService {

	private final OperatorFlowMapper mapper;

	public FlowService(OperatorFlowMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * 创建订单时，创建对应的流程。这一步只是记录流程的初始化信息，即往数据库中存储一条初始阀门。
	 *
	 * @param orderNo
	 * @param type
	 */
	public void initialize(String orderNo, OrderType type) {
		Asserts.notEmpty(orderNo, "订单号不可为空");
		Asserts.notNull(type, "订单类型不可为空");
		final FlowTemplate template = this.getTemplateByOrderType(type);
		if (null == template) {
			throw new BizException("未获取到对应订单类型的流程模板");
		}
		Valve first = template.firstValve();
	}

	/**
	 * 获取订单的流程状态
	 * // TODO 如果是审核人，当前流程步骤完成需要获取下一个阀门的状态
	 *
	 * @param orderNo
	 * @return
	 */
	public String getFlowStateBy(String orderNo) {
		OperatorFlow latest = this.mapper.getLatestFlow(orderNo);
		return Optional.ofNullable(latest.getEndState()).orElse(latest.getStartState());
	}

	/**
	 * 更新流程当前步骤的终态，如果还有下一个步骤，需要发送消息到下一个审核负责人进行提醒
	 *
	 * @param orderNo
	 */
	public void updateFlowEndState(String orderNo) {
		// 1.更新当前流程步骤的终态
		// 2.通知下一个阀门的审核人
	}

	private FlowTemplate getTemplateByOrderType(OrderType type) {
		return new FlowTemplate();
	}
}
