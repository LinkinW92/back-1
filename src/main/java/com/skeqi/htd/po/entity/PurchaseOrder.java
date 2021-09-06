package com.skeqi.htd.po.entity;

import lombok.Data;

/**
 * 采购订单
 *
 * @author linkin
 */
@Data
public class PurchaseOrder extends CommonOrder {
	/**
	 * 采购人员
	 */
	private Integer purchaserId;
	private String purchaser;
	/**
	 * 供应商id, 对应Supplier表的主键id
	 */
	private Integer supplierId;
	private String supplier;
}
