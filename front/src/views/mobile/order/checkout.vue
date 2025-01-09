<template>
    <div class="checkout">
        <!-- 导航栏 -->
        <van-nav-bar 
            title="订单结算" 
            left-text="返回点单"
            left-arrow 
            @click-left="onClickLeft"
        />
        
        <!-- 收货信息 -->
        <div class="delivery-info">
            <div class="section-title">收货信息</div>
            <van-cell-group>
                <van-cell 
                    title="收货地址" 
                    :value="address || '请选择收货地址'"
                    is-link
                    @click="showAddressPopup = true; loadAddresses()"
                />
                <van-cell 
                    title="联系电话" 
                    :value="phone || '请选择收货地址'"
                />
            </van-cell-group>
        </div>
        
        <!-- 商品列表 -->
        <div class="order-items">
            <div class="section-title">商品清单</div>
            <div 
                v-for="item in cartStore.cartItems" 
                :key="item.id"
                class="order-item"
            >
                <van-image
                    width="60"
                    height="60"
                    :src="getImageUrl(item.imageUrl)"
                    @click="showProductDetail(item)"
                />
                <div class="item-info">
                    <h4>{{ item.name }}</h4>
                    <p class="count">x{{ item.count }}</p>
                </div>
                <div class="item-price">¥{{ (item.price * item.count).toFixed(2) }}</div>
            </div>
        </div>

        <!-- 备注 -->
        <div class="remark-section">
            <div class="section-title">订单备注</div>
            <van-field
                v-model="remark"
                rows="2"
                autosize
                type="textarea"
                placeholder="请输入备注信息（选填）"
            />
        </div>

        <!-- 结算栏 -->
        <div class="submit-bar">
            <div class="price-info">
                <span>共{{ cartStore.totalCount }}件，</span>
                <span>合计：</span>
                <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="button-group">
                <van-button 
                    plain
                    type="primary" 
                    class="action-btn"
                    @click="onClickLeft"
                >
                    继续点单
                </van-button>
                <van-button 
                    type="primary" 
                    class="action-btn"
                    :loading="isSubmitting"
                    :disabled="!isValid"
                    @click="handleCheckout"
                >
                    立即结算
                </van-button>
            </div>
        </div>

        <!-- 提交订单确认弹窗 -->
        <van-dialog
            v-model:show="showConfirmDialog"
            title="确认提交订单"
            :show-cancel-button="true"
            @confirm="confirmSubmit"
        >
            <div class="confirm-content">
                <p>收货地址：{{ address }}</p>
                <p>联系电话：{{ phone }}</p>
                <p>商品数量：{{ cartStore.totalCount }}件</p>
                <p>订单金额：¥{{ cartStore.totalPrice.toFixed(2) }}</p>
                <p v-if="remark">备注信息：{{ remark }}</p>
            </div>
        </van-dialog>

        <!-- 地址选择弹窗 -->
        <van-popup
            v-model:show="showAddressPopup"
            position="bottom"
            round
            class="address-popup"
        >
            <div class="popup-header">
                <span class="title">选择收货地址</span>
                <van-button 
                    plain 
                    type="primary" 
                    size="small"
                    to="/mobile/address"
                >
                    新增地址
                </van-button>
            </div>
            <div class="address-list">
                <van-radio-group v-model="selectedAddress">
                    <div 
                        v-for="addr in addresses" 
                        :key="addr.id"
                        class="address-item"
                        @click="selectAddress(addr)"
                    >
                        <div class="address-info">
                            <div class="contact-info">
                                <span class="name">{{ addr.receiverName }}</span>
                                <span class="phone">{{ addr.receiverPhone }}</span>
                                <van-tag v-if="addr.isDefault" type="primary" size="small">默认</van-tag>
                            </div>
                            <div class="address-detail">
                                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
                            </div>
                        </div>
                        <van-radio :name="addr" />
                    </div>
                </van-radio-group>
            </div>
        </van-popup>

        <!-- 商品详情弹窗 -->
        <van-popup
            v-model:show="showProductPopup"
            round
            class="product-popup"
        >
            <div class="product-detail" v-if="currentProduct">
                <div class="product-header">
                    <van-image
                        width="100%"
                        height="200"
                        fit="cover"
                        :src="currentProduct.imageUrl || '/placeholder.png'"
                    />
                </div>
                <div class="product-content">
                    <h3 class="product-name">{{ currentProduct.name }}</h3>
                    <div class="product-price-info">
                        <span class="product-price">¥{{ currentProduct.price }}</span>
                        <span class="product-count">x {{ currentProduct.count }}</span>
                    </div>
                    <div class="product-description" v-if="currentProduct.description">
                        <h4>商品简介</h4>
                        <p>{{ currentProduct.description }}</p>
                    </div>
                </div>
                <div class="popup-close">
                    <van-icon name="cross" @click="showProductPopup = false" />
                </div>
            </div>
        </van-popup>
    </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/order'
import { getDefaultAddress, getAddressList } from '@/api/address'
import { API_BASE_URL } from '@/config'

export default {
    name: 'CheckoutPage',
    setup() {
        const router = useRouter()
        const cartStore = useCartStore()
        const userStore = useUserStore()
        const address = ref('')
        const phone = ref('')
        const remark = ref('')
        const isSubmitting = ref(false)
        const showConfirmDialog = ref(false)
        const showAddressPopup = ref(false)
        const addresses = ref([])
        const selectedAddress = ref(null)
        const showProductPopup = ref(false)
        const currentProduct = ref(null)

        // 检查登录状态
        if (!userStore.token) {
            router.replace({
                path: '/mobile/login',
                query: { redirect: '/mobile/order/checkout' }
            })
            return
        }

        // 检查购物车是否为空
        if (cartStore.totalCount === 0) {
            showToast('购物车为空')
            router.replace('/mobile/menu')
            return
        }

        // 加载默认地址
        const loadDefaultAddress = async () => {
            try {
                const res = await getDefaultAddress()
                if (res.data) {
                    selectedAddress.value = res.data
                    address.value = `${res.data.province}${res.data.city}${res.data.district}${res.data.detailAddress}`
                    phone.value = res.data.receiverPhone
                }
            } catch (error) {
                showToast('加载默认地址失败')
            }
        }

        // 加载所有地址
        const loadAddresses = async () => {
            try {
                const res = await getAddressList()
                addresses.value = res.data
            } catch (error) {
                showToast('加载地址列表失败')
            }
        }

        // 选择地址
        const selectAddress = (addr) => {
            selectedAddress.value = addr
            address.value = `${addr.province}${addr.city}${addr.district}${addr.detailAddress}`
            phone.value = addr.receiverPhone
            showAddressPopup.value = false
        }

        // 表单验证
        const isValid = computed(() => {
            return address.value && phone.value
        })

        const onClickLeft = () => {
            router.push('/mobile/menu')
        }

        // 点击结算按钮
        const handleCheckout = () => {
            if (!isValid.value) {
                showToast('请填写完整的收货信息')
                return
            }
            showConfirmDialog.value = true
        }

        // 确认提交订单
        const confirmSubmit = async () => {
            try {
                isSubmitting.value = true
                
                // 构建订单数据
                const orderData = {
                    items: cartStore.cartItems.map(item => ({
                        productId: item.id,
                        productName: item.name,
                        quantity: item.count,
                        price: item.price
                    })),
                    totalAmount: cartStore.totalPrice,
                    address: address.value,
                    phone: phone.value,
                    remark: remark.value
                }

                // 创建订单
                const res = await createOrder(orderData)
                
                // 清空购物车
                cartStore.clearCart()
                
                // 显示成功提示
                await showDialog({
                    title: '下单成功',
                    message: '订单已提交，请及时支付',
                    confirmButtonText: '查看订单',
                    showCancelButton: true,
                    cancelButtonText: '继续购物'
                }).then((action) => {
                    if (action === 'confirm') {
                        router.push(`/mobile/order?status=pending`)
                    } else {
                        router.push('/mobile/menu')
                    }
                })
            } catch (error) {
                if (error.response?.status === 400) {
                    showToast(error.response.data.message || '库存不足')
                } else {
                    showToast(error.message || '提交订单失败')
                }
            } finally {
                isSubmitting.value = false
                showConfirmDialog.value = false
            }
        }

        // 初始化加载
        loadDefaultAddress()

        const getImageUrl = (url) => {
            if (!url) return '/placeholder.png'
            if (url.startsWith('http')) return url
            return `${API_BASE_URL}${url}`
        }

        // 显示商品详情
        const showProductDetail = (product) => {
            currentProduct.value = product
            showProductPopup.value = true
        }

        return {
            cartStore,
            address,
            phone,
            remark,
            isSubmitting,
            isValid,
            showConfirmDialog,
            showAddressPopup,
            addresses,
            selectedAddress,
            onClickLeft,
            handleCheckout,
            confirmSubmit,
            loadAddresses,
            selectAddress,
            showProductPopup,
            currentProduct,
            showProductDetail,
            getImageUrl,
        }
    }
}
</script>

<style scoped>
.checkout {
    min-height: 100vh;
    background: #f7f8fa;
    padding-bottom: 100px;
}

.section-title {
    padding: 16px;
    font-size: 14px;
    color: #969799;
    background: #fff;
    border-bottom: 1px solid #f5f5f5;
}

.delivery-info {
    margin-bottom: 12px;
}

.order-items {
    margin-bottom: 12px;
    background: #fff;
}

.order-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #fff;
    border-bottom: 1px solid #f5f5f5;
}

.item-info {
    flex: 1;
    margin-left: 12px;
}

.item-info h4 {
    margin: 0 0 4px;
    font-size: 14px;
    color: #323233;
}

.item-info .count {
    margin: 0;
    font-size: 12px;
    color: #969799;
}

.item-price {
    font-size: 14px;
    color: #323233;
    font-weight: 500;
}

.remark-section {
    background: #fff;
    margin-bottom: 12px;
}

.submit-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.price-info {
    font-size: 14px;
    color: #323233;
}

.total-price {
    font-size: 20px;
    font-weight: bold;
    color: var(--van-danger-color);
}

.button-group {
    display: flex;
    gap: 12px;
}

.action-btn {
    height: 36px;
    padding: 0 20px;
    border-radius: 18px;
}

.confirm-content {
    padding: 16px;
    font-size: 14px;
    line-height: 1.8;
}

.confirm-content p {
    margin: 0;
    color: #323233;
}

.address-popup {
    max-height: 70vh;
}

.popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #f5f5f5;
}

.popup-header .title {
    font-size: 16px;
    font-weight: bold;
}

.address-list {
    padding: 16px;
}

.address-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    border-bottom: 1px solid #f5f5f5;
    cursor: pointer;
}

.address-info {
    flex: 1;
    margin-right: 16px;
}

.contact-info {
    margin-bottom: 8px;
}

.contact-info .name {
    font-size: 14px;
    font-weight: bold;
    margin-right: 8px;
}

.contact-info .phone {
    color: #666;
}

.address-detail {
    font-size: 13px;
    color: #666;
    line-height: 1.4;
}

.product-popup {
    width: 90%;
    max-width: 375px;
    border-radius: 16px;
}

.product-detail {
    position: relative;
}

.product-header {
    border-radius: 16px 16px 0 0;
    overflow: hidden;
}

.product-content {
    padding: 16px;
}

.product-name {
    margin: 0 0 12px;
    font-size: 18px;
    font-weight: bold;
    color: #323233;
}

.product-price-info {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
}

.product-price {
    font-size: 20px;
    font-weight: bold;
    color: var(--van-danger-color);
    margin-right: 12px;
}

.product-count {
    font-size: 14px;
    color: #969799;
}

.product-description {
    border-top: 1px solid #f5f5f5;
    padding-top: 16px;
}

.product-description h4 {
    margin: 0 0 8px;
    font-size: 14px;
    color: #323233;
}

.product-description p {
    margin: 0;
    font-size: 14px;
    color: #666;
    line-height: 1.6;
}

.popup-close {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 24px;
    height: 24px;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}

.popup-close :deep(.van-icon) {
    color: #fff;
    font-size: 16px;
}
</style> 