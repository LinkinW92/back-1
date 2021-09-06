package com.skeqi.htd.service;

import com.alibaba.fastjson.JSONObject;
import com.skeqi.htd.common.OrderState;
import com.skeqi.htd.controller.vo.*;
import com.skeqi.htd.po.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.skeqi.htd.common.Constants.DATETIME_FORMATTER;
import static com.skeqi.htd.common.Constants.DATE_FORMATTER;

/**
 * 业务门户类
 *
 * @author linkin
 */
@Service
@Slf4j
public class HtdServiceFacade {
	private final CustomerService customerService;
	private final PurchaseOrderService purchaseOrderService;
	private final SaleOrderService saleOrderService;
	private final SubWarehouseService subWarehouseService;
	private final SupplierService supplierService;
	private final ProductService productService;


	@Autowired
	public HtdServiceFacade(CustomerService customerService,
							PurchaseOrderService purchaseOrderService,
							SaleOrderService saleOrderService,
							SubWarehouseService subWarehouseService,
							SupplierService supplierService,
							ProductService productService) {
		this.customerService = customerService;
		this.purchaseOrderService = purchaseOrderService;
		this.saleOrderService = saleOrderService;
		this.subWarehouseService = subWarehouseService;
		this.supplierService = supplierService;
		this.productService = productService;
	}

	/**
	 * 创建采购订单
	 *
	 * @param vo
	 */
	public void createPurchaserOrder(PurchaseOrderVO.CreateVO vo) {
		this.purchaseOrderService.createOrders(vo.toEntities(this.generateOrderNo()));
	}

	/**
	 * 采购订单查询
	 *
	 * @return
	 */
	public List<PurchaseOrderVO.ItemVO> queryPurchaserOrderList(QueryVO.QueryPurchaserOrdersVO vo) {
		// 1.根据查询条件获取订单的主体信息
		List<PurchaseOrder> orders = this.purchaseOrderService.queryBy(vo);
		return orders.parallelStream().filter(Objects::nonNull)
			.map(o -> {
				PurchaseOrderVO.ItemVO item = new PurchaseOrderVO.ItemVO();
				BeanUtils.copyProperties(o, item);
				item.setLatestAuditTime(o.getUpdateTime().format(DATETIME_FORMATTER));
				item.setOrderTime(o.getOrderTime().format(DATE_FORMATTER));
				item.setDeliveryTime(o.getDeliveryTime().format(DATE_FORMATTER));
				return item;
			}).collect(Collectors.toList());
	}

	/**
	 * 采购订单查询
	 *
	 * @param exOrderNo
	 * @return
	 */
	public PurchaseOrderVO.DetailVO doGetPurchaserOrderDetail(String exOrderNo) {
		// 1.根据查询条件获取订单的主体信息
		List<PurchaseOrder> orders = this.purchaseOrderService.queryBy(QueryVO.QueryPurchaserOrdersVO.with(exOrderNo));

		List<Integer> productIds = orders.parallelStream()
			.filter(Objects::nonNull)
			.map(PurchaseOrder::getId)
			.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(productIds)) {
			return null;
		}
		// 2.转为普通订单对象
		List<CommonOrder> co = new ArrayList<>();
		for (PurchaseOrder order : orders) {
			co.add(order);
		}
		// 3.获取客户联系方式等信息
		Supplier supplier = this.supplierService.getSupplierById(orders.get(0).getSupplierId());
		// 4.关联的销售订单号 //TODO 多比采购订单合并销售
		return PurchaseOrderVO.DetailVO.build(orders.get(0), mergeProductInfo(productIds, co), supplier);
	}


	/**
	 * 新增销售单
	 *
	 * @return
	 */
	public void createSaleOrder(SaleOrderVO.CreateVO vo) {
		this.saleOrderService.createOrders(vo.toEntities(this.generateOrderNo()));
	}

	/**
	 * 销售订单查询
	 *
	 * @param exOrderNo
	 * @return
	 */
	public SaleOrderVO.DetailVO doGetSaleOrderDetail(String exOrderNo) {
		// 1.根据查询条件获取订单的主体信息
		List<SaleOrder> orders = this.saleOrderService.queryBy(QueryVO.QuerySaleOrdersVO.with(exOrderNo));
		List<Integer> productIds = orders.parallelStream()
			.filter(Objects::nonNull)
			.map(SaleOrder::getId)
			.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(productIds)) {
			return null;
		}
		// 2.转为普通订单对象
		List<CommonOrder> co = new ArrayList<>();
		for (SaleOrder order : orders) {
			co.add(order);
		}
		// 3.获取客户联系方式等信息
		Customer customer = this.customerService.getCustomerById(orders.get(0).getCustomerId());
		// 4.关联的销售订单号 //TODO 多比采购订单合并销售
		return SaleOrderVO.DetailVO.build(orders.get(0), this.mergeProductInfo(productIds, co), customer);
	}


	/**
	 * 获取销售订单列表
	 *
	 * @param vo
	 * @return
	 */
	public List<SaleOrderVO.ItemVO> querySaleOrderList(QueryVO.QuerySaleOrdersVO vo) {
		List<SaleOrder> orders = this.saleOrderService.queryBy(vo);
		return orders.parallelStream().filter(Objects::nonNull)
			.map(o -> {
				SaleOrderVO.ItemVO item = new SaleOrderVO.ItemVO();
				BeanUtils.copyProperties(o, item);
				item.setLatestAuditTime(o.getUpdateTime().format(DATETIME_FORMATTER));
				item.setOrderTime(o.getOrderTime().format(DATE_FORMATTER));
				item.setDeliveryTime(o.getDeliveryTime().format(DATE_FORMATTER));
				return item;
			}).collect(Collectors.toList());
	}

	/**
	 * 获取所有的客户名称
	 *
	 * @return
	 */
	public List<String> getAllCustomerNames() {
		return this.customerService.getAllCustomerNames();
	}

	/**
	 * 根据客户名称获取对应客户的联系人列表
	 *
	 * @param customerName
	 * @return
	 */
	public List<CustomerVO> getCustomersByName(String customerName) {
		return this.customerService.getCustomersByName(customerName).parallelStream()
			.filter(Objects::nonNull)
			.map(CustomerVO::toVO).collect(Collectors.toList());
	}


	/**
	 * 获取所有的供应商名称
	 *
	 * @return
	 */
	public List<String> getAllSupplierNames() {
		return this.supplierService.getAllSupplierNames();
	}

	/**
	 * 获取对应供应商的联系信息
	 *
	 * @param supplierName
	 * @return
	 */
	public List<SupplierVO> getSuppliersByName(String supplierName) {
		return this.supplierService.getSuppliersByName(supplierName).parallelStream()
			.filter(Objects::nonNull)
			.map(SupplierVO::toVO).collect(Collectors.toList());
	}


	/**
	 * 修改订单状态
	 */
	public void modifyOrderState(String orderNo, OrderState state) {
		//TODO 校验逻辑
	}

	public List<ProductVO> mergeProductInfo(List<Integer> productIds, List<CommonOrder> orders) {
		Map<Integer, Product> products = this.productService.getProductByIds(productIds)
			.parallelStream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Product::getId, e -> e));
		return orders.parallelStream()
			.filter(Objects::nonNull)
			.map(order -> {
				ProductVO pv = JSONObject.parseObject(order.getProductExt(), ProductVO.class);
				BeanUtils.copyProperties(products.get(order.getProductId()), pv);
				return pv;
			}).collect(Collectors.toList());
	}


	/**
	 * 生成系统内部的主订单号
	 *
	 * @return
	 */
	private String generateOrderNo() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
