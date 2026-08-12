package com.jmall.merchant.service;
import com.jmall.api.merchant.dto.ShopDTO;
import com.jmall.merchant.entity.Merchant;
import com.jmall.merchant.entity.MerchantApply;
import com.jmall.merchant.entity.Shop;
import java.util.List;
public interface MerchantService {
 MerchantApply apply(MerchantApply apply);
 List<MerchantApply> listApply(Long userId);
 Shop saveShop(Shop shop);
 Shop updateShop(Long shopId, Shop shop);
 ShopDTO getShopByMerchantId(Long merchantId);
 ShopDTO getShopById(Long shopId);
 boolean isApproved(Long merchantId);
}
