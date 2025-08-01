package org.projectai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class Invoice {
    // 发票代码
    @JsonProperty("发票代码")
    private String invoiceCode;

    // 发票号码
    @JsonProperty("发票号码")
    private String invoiceNumber;

    // 开票日期
    @JsonProperty("开票日期")
    private String invoiceDate;

    // 销售方名称
    @JsonProperty("销售方名称")
    private String sellerName;

    // 销售方纳税人识别号
    @JsonProperty("销售方纳税人识别号")
    private String sellerTaxNumber;

    // 销售方地址及电话
    @JsonProperty("销售方地址及电话")
    private String sellerAddressPhone;

    // 销售方开户行及账号
    @JsonProperty("销售方开户行及账号")
    private String sellerBankAccount;

    // 购买方名称
    @JsonProperty("购买方名称")
    private String buyerName;

    // 购买方纳税人识别号
    @JsonProperty("购买方纳税人识别号")
    private String buyerTaxNumber;

    // 购买方地址及电话
    @JsonProperty("购买方地址及电话")
    private String buyerAddressPhone;

    // 商品名称
    @JsonProperty("商品名称")
    private String productName;

    // 金额
    @JsonProperty("金额")
    private String amount;

    // 税率
    @JsonProperty("税率")
    private String taxRate;

    // 税额
    @JsonProperty("税额")
    private String taxAmount;

    // 价税合计
    @JsonProperty("价税合计")
    private String totalAmount;


}
