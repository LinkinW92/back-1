package com.skeqi.htd.service.audit.flow;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

/**
 * 流程模板,流程模板中有多个阀门{@link Valve}, 多个阀门按顺序排好，按顺序执行
 *
 * @author linkin
 */
@Data
public class FlowTemplate {
	/**
	 * 模板id
	 */
	private Integer templateId;
	/**
	 * 流程作用的单据类型
	 */
	private String orderType;
	/**
	 * 阀门
	 */
	private List<Valve> valves;

	public Valve firstValve() {
		return this.valves.parallelStream().min(Comparator.comparing(Valve::getOrder)).get();
	}
}
