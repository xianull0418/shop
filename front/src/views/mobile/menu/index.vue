<template>
    <div class="menu">
        <van-nav-bar 
            title="点单" 
            left-arrow 
            @click-left="onClickLeft"
        />
        
        <div class="menu-container">
            <!-- 左侧分类列表 -->
            <div class="category-list">
                <div
                    v-for="category in categories"
                    :key="category.id"
                    class="category-item"
                    :class="{ active: currentCategory?.id === category.id }"
                    @click="selectCategory(category)"
                >
                    <div class="category-icon-wrapper">
                        <el-icon :size="24">
                            <component :is="getIconComponent(category.name)" />
                        </el-icon>
                    </div>
                    <span>{{ category.name }}</span>
                </div>
            </div>

            <!-- 右侧商品列表 -->
            <div class="product-list">
                <van-card
                    v-for="product in currentProducts"
                    :key="product.id"
                    :price="product.price.toFixed(2)"
                    :title="product.name"
                    :thumb="getImageUrl(product.imageUrl)"
                    @click-thumb="showProductDetail(product)"
                >
                    <template #footer>
                        <div class="product-stepper">
                            <template v-if="getProductCount(product.id) > 0">
                                <van-button 
                                    size="mini" 
                                    type="primary"
                                    @click="decreaseFromCart(product)"
                                >
                                    -
                                </van-button>
                                <span class="count">{{ getProductCount(product.id) }}</span>
                            </template>
                            <van-button 
                                size="mini" 
                                type="primary"
                                @click="addToCart(product)"
                            >
                                {{ getProductCount(product.id) > 0 ? '+' : '加入购物车' }}
                            </van-button>
                        </div>
                    </template>
                </van-card>
            </div>
        </div>

        <!-- 购物车结算栏 -->
        <div class="cart-bar" v-show="cartStore.totalCount > 0">
            <div class="cart-info" @click="showCartPopup = true">
                <div class="cart-icon">
                    <van-icon name="shopping-cart-o" :badge="cartStore.totalCount" />
                </div>
                <div class="price-info">
                    <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
                </div>
            </div>
            <van-button 
                type="primary" 
                class="submit-btn"
                :disabled="cartStore.totalCount === 0"
                @click="goToCheckout"
            >
                去结算
            </van-button>
        </div>

        <!-- 购物车详情弹窗 -->
        <van-popup
            v-model:show="showCartPopup"
            position="bottom"
            round
            class="cart-popup"
        >
            <div class="popup-header">
                <span class="title">购物车</span>
                <span class="clear" @click="clearCart">清空购物车</span>
            </div>
            <div class="cart-list">
                <div 
                    v-for="item in cartStore.cartItems" 
                    :key="item.id"
                    class="cart-item"
                >
                    <div class="item-info">
                        <h4>{{ item.name }}</h4>
                        <span class="price">¥{{ item.price.toFixed(2) }}</span>
                    </div>
                    <div class="item-stepper">
                        <van-button 
                            size="mini" 
                            type="primary"
                            @click="decreaseFromCart(item)"
                        >
                            -
                        </van-button>
                        <span class="count">{{ item.count }}</span>
                        <van-button 
                            size="mini" 
                            type="primary"
                            @click="addToCart(item)"
                        >
                            +
                        </van-button>
                    </div>
                </div>
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
                        :src="getImageUrl(currentProduct.imageUrl)"
                    />
                </div>
                <div class="product-content">
                    <h3 class="product-name">{{ currentProduct.name }}</h3>
                    <div class="product-price-info">
                        <span class="product-price">¥{{ currentProduct.price }}</span>
                        <div class="stepper">
                            <van-stepper 
                                v-model="currentProduct.count" 
                                :min="0"
                                :max="currentProduct.stock"
                                @change="handleStepperChange(currentProduct)"
                            />
                        </div>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getCategories } from '@/api/category'
import { getProducts } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { API_BASE_URL } from '@/config'
import { 
    Bowl,
    IceCream,
    Coffee,
    More
} from '@element-plus/icons-vue'

export default {
    name: 'MenuPage',
    components: {
        Bowl,
        IceCream,
        Coffee,
        More
    },
    setup() {
        const router = useRouter()
        const route = useRoute()
        const cartStore = useCartStore()
        const showCartPopup = ref(false)
        const showProductPopup = ref(false)
        const currentProduct = ref(null)
        
        const categories = ref([])
        const products = ref([])
        const currentCategory = ref(null)

        const getIconComponent = (name) => {
            const iconMap = {
                '面包': Bowl,
                '蛋糕': IceCream,
                '饮品': Coffee,
                '其他': More
            }
            return iconMap[name] || More
        }

        const currentProducts = computed(() => {
            if (!currentCategory.value) return []
            return products.value.filter(p => p.categoryId === currentCategory.value.id)
        })

        const getImageUrl = (url) => {
            if (!url) return ''
            if (url.startsWith('http')) return url
            return `${API_BASE_URL}${url}`
        }

        const selectCategory = (category) => {
            currentCategory.value = category
        }

        const getProductCount = (productId) => {
            return cartStore.getProductCount(productId)
        }

        const addToCart = (product) => {
            cartStore.addToCart(product)
        }

        const decreaseFromCart = (product) => {
            cartStore.decreaseFromCart(product)
        }

        const clearCart = () => {
            showDialog({
                title: '提示',
                message: '确定要清空购物车吗？',
                showCancelButton: true
            }).then(() => {
                cartStore.clearCart()
                showCartPopup.value = false
            })
        }

        const goToCheckout = () => {
            if (!cartStore.totalCount) {
                showToast('请先选择商品')
                return
            }
            router.push('/mobile/order/checkout')
        }

        const onClickLeft = () => {
            router.back()
        }

        const loadData = async () => {
            try {
                // 加载分类
                const categoryRes = await getCategories()
                categories.value = categoryRes.data
                
                // 加载商品
                const productRes = await getProducts()
                products.value = productRes.data

                // 设置当前分类
                const categoryId = parseInt(route.query.categoryId)
                if (categoryId && categories.value.length) {
                    currentCategory.value = categories.value.find(c => c.id === categoryId) || categories.value[0]
                } else if (categories.value.length) {
                    currentCategory.value = categories.value[0]
                }
            } catch (error) {
                showToast(error.message || '加载数据失败')
            }
        }

        // 显示商品详情
        const showProductDetail = (product) => {
            currentProduct.value = product
            showProductPopup.value = true
        }

        onMounted(() => {
            loadData()
        })

        return {
            categories,
            currentCategory,
            currentProducts,
            cartStore,
            showCartPopup,
            getImageUrl,
            selectCategory,
            addToCart,
            decreaseFromCart,
            clearCart,
            onClickLeft,
            getIconComponent,
            getProductCount,
            goToCheckout,
            showProductPopup,
            currentProduct,
            showProductDetail,
        }
    }
}
</script>

<style scoped>
.menu {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f8f9fa;
}

.menu-container {
    flex: 1;
    display: flex;
    overflow: hidden;
    padding: 12px 0 62px;
}

.category-list {
    width: 85px;
    background: #fff;
    overflow-y: auto;
    padding: 8px 0;
    margin-right: 1px;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
}

.category-item {
    padding: 16px 8px;
    text-align: center;
    font-size: 13px;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    position: relative;
    transition: all 0.3s ease;
}

.category-item.active {
    background: #f0f7ff;
    color: var(--van-primary-color);
    font-weight: 500;
}

.category-item.active::after {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 20px;
    background: var(--van-primary-color);
    border-radius: 0 4px 4px 0;
}

.category-icon-wrapper {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: var(--van-primary-color-light);
    transition: all 0.3s ease;
}

.category-item.active .category-icon-wrapper {
    background: var(--van-primary-color);
    transform: scale(1.05);
}

.category-icon-wrapper :deep(.el-icon) {
    color: var(--van-primary-color);
    transition: all 0.3s ease;
    font-size: 20px;
}

.category-item.active .category-icon-wrapper :deep(.el-icon) {
    color: #fff;
}

.product-list {
    flex: 1;
    padding: 0 12px;
    overflow-y: auto;
    background: #f8f9fa;
}

.product-list :deep(.van-card) {
    background: #fff;
    margin-bottom: 12px;
    border-radius: 16px;
    padding: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
    transition: all 0.3s ease;
    border: 1px solid rgba(0, 0, 0, 0.02);
}

.product-list :deep(.van-card:hover) {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.product-list :deep(.van-card__title) {
    font-size: 15px;
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 4px;
}

.product-list :deep(.van-card__price) {
    color: #ff6b6b;
    font-size: 17px;
    font-weight: bold;
}

.product-list :deep(.van-card__price-integer) {
    font-size: 20px;
}

.product-list :deep(.van-card__thumb) {
    width: 100px;
    height: 100px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.product-stepper {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 12px;
}

.product-stepper .count {
    min-width: 32px;
    text-align: center;
    font-size: 15px;
    font-weight: 500;
    color: #2c3e50;
}

.product-stepper :deep(.van-button) {
    padding: 0;
    transition: all 0.2s ease;
    border: none;
}

.product-stepper :deep(.van-button--mini) {
    height: 32px;
}

/* 加减按钮样式 */
.product-stepper :deep(.van-button--mini:not(:last-child)) {
    width: 32px;
    border-radius: 50%;
}

/* 加入购物车按钮样式 */
.product-stepper :deep(.van-button--mini:last-child) {
    padding: 0 16px;
    border-radius: 16px;
    background: linear-gradient(135deg, var(--van-primary-color), #4dabf7);
    box-shadow: 0 2px 8px rgba(var(--van-primary-color-rgb), 0.2);
}

.product-stepper :deep(.van-button:active) {
    transform: scale(0.95);
}

.product-stepper :deep(.van-button--normal) {
    font-size: 14px;
    color: #fff;
    font-weight: 500;
}

/* 购物车结算栏 */
.cart-bar {
    position: fixed;
    left: 12px;
    right: 12px;
    bottom: 62px;
    height: 56px;
    background: #fff;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-radius: 28px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.cart-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
}

.cart-icon {
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, var(--van-primary-color), #4dabf7);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(var(--van-primary-color-rgb), 0.2);
    transition: all 0.3s ease;
}

.cart-icon:active {
    transform: scale(0.95);
}

.cart-icon :deep(.van-icon) {
    font-size: 24px;
    color: #fff;
}

.price-info {
    flex: 1;
}

.total-price {
    font-size: 22px;
    font-weight: bold;
    color: #ff6b6b;
    text-shadow: 0 1px 2px rgba(255, 107, 107, 0.1);
}

.submit-btn {
    width: 120px;
    height: 44px;
    border-radius: 22px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(135deg, var(--van-primary-color), #4dabf7);
    border: none;
    box-shadow: 0 4px 12px rgba(var(--van-primary-color-rgb), 0.2);
    transition: all 0.3s ease;
}

.submit-btn:active {
    transform: scale(0.98);
}

/* 购物车弹窗 */
.cart-popup {
    max-height: 80vh;
    overflow-y: auto;
    border-radius: 24px 24px 0 0;
    background: #f8f9fa;
}

.popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #fff;
    position: sticky;
    top: 0;
    z-index: 1;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.popup-header .title {
    font-size: 18px;
    font-weight: 600;
    color: #2c3e50;
}

.popup-header .clear {
    color: #666;
    font-size: 14px;
    padding: 8px 16px;
    background: #f1f3f5;
    border-radius: 20px;
    transition: all 0.3s ease;
}

.popup-header .clear:active {
    background: #e9ecef;
}

.cart-list {
    padding: 16px 20px;
}

.cart-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    margin-bottom: 12px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.cart-item:last-child {
    margin-bottom: 0;
}

.item-info h4 {
    margin: 0 0 6px;
    font-size: 15px;
    font-weight: 500;
    color: #2c3e50;
}

.item-info .price {
    color: #ff6b6b;
    font-size: 17px;
    font-weight: bold;
}

.item-stepper {
    display: flex;
    align-items: center;
    gap: 12px;
}

.item-stepper .count {
    min-width: 32px;
    text-align: center;
    font-size: 16px;
    font-weight: 500;
    color: #2c3e50;
}

.item-stepper :deep(.van-button) {
    width: 32px;
    height: 32px;
    padding: 0;
    border-radius: 50%;
    background: var(--van-primary-color);
    border: none;
    color: #fff;
    transition: all 0.2s ease;
}

.item-stepper :deep(.van-button:active) {
    transform: scale(0.95);
}

/* 商品详情弹窗 */
.product-popup {
    width: 92%;
    max-width: 375px;
    border-radius: 24px;
    overflow: hidden;
    background: #f8f9fa;
}

.product-detail {
    position: relative;
    background: #fff;
}

.product-header {
    position: relative;
    width: 100%;
    height: 280px;
    overflow: hidden;
}

.product-header :deep(.van-image) {
    width: 100%;
    height: 100%;
}

.product-header :deep(.van-image img) {
    object-fit: cover;
    transform: scale(1.02);
}

.product-content {
    padding: 24px;
    background: #fff;
    border-radius: 24px 24px 0 0;
    margin-top: -24px;
    position: relative;
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.06);
}

.product-name {
    margin: 0 0 16px;
    font-size: 22px;
    font-weight: bold;
    color: #2c3e50;
    line-height: 1.4;
}

.product-price-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
}

.product-price {
    font-size: 28px;
    font-weight: bold;
    color: #ff6b6b;
    text-shadow: 0 1px 2px rgba(255, 107, 107, 0.1);
}

.stepper {
    display: flex;
    align-items: center;
}

.stepper :deep(.van-stepper) {
    height: 36px;
    background: #f8f9fa;
    border-radius: 18px;
    padding: 0 4px;
}

.stepper :deep(.van-stepper__input) {
    height: 36px;
    font-size: 16px;
    background: transparent;
}

.stepper :deep(.van-stepper__minus),
.stepper :deep(.van-stepper__plus) {
    width: 36px;
    height: 36px;
    background: var(--van-primary-color);
    color: #fff;
    border: none;
    border-radius: 50%;
    margin: 0;
}

.product-description {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #edf2f7;
}

.product-description h4 {
    margin: 0 0 12px;
    font-size: 17px;
    color: #2c3e50;
    font-weight: 600;
}

.product-description p {
    margin: 0;
    font-size: 15px;
    color: #4a5568;
    line-height: 1.6;
}

.popup-close {
    position: absolute;
    top: 16px;
    right: 16px;
    width: 36px;
    height: 36px;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    backdrop-filter: blur(8px);
    transition: all 0.3s ease;
}

.popup-close:hover {
    background: rgba(0, 0, 0, 0.5);
    transform: rotate(90deg);
}

.popup-close :deep(.van-icon) {
    color: #fff;
    font-size: 20px;
}
</style> 