package com.skeqi.htd.service.audit.flow;

import lombok.Data;

/**
 * 审批流中的一个步骤，即阀门
 *
 * @author linkin
 */
@Data
public class Valve {
	/**
	 * 审核人
	 */
	private String auditor;
	/**
	 * 审核人id
	 */
	private Integer auditorId;
	/**
	 * 阀门在整个流程中的位置
	 */
	private Integer order;
	/**
	 * 审核人对应的部门
	 */
	private String dept;
}
