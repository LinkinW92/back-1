package com.skeqi.htd.po.entity;

import lombok.Data;

/**
 * 订单操作流水
 *
 * @author linkin
 */
@Data
public class OperatorFlow extends Entity {

	private String orderNo;
	/**
	 * 阀门初始状态，例如待审核
	 */
	private String startState;
	/**
	 * 阀门初始状态，例如审核通过
	 */
	private String endState;
	/**
	 * 对应的流程模板和阀门ID
	 */
	private Integer templateId;
	private Integer valveId;
	/**
	 * 操作人信息
	 */
	private String operator;
	private Integer operatorId;
	/**
	 * 操作内容, 对应阀门的名称
	 */
	private String valveName;
	/**
	 * 创建者
	 */
	private String creator;
	/**
	 * 更新人
	 */
	private String updater;

}
