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
                        :src="currentProduct.imageUrl || '/placeholder.png'"
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
    padding-bottom: 50px;
}

.menu-container {
    flex: 1;
    display: flex;
    overflow: hidden;
}

.category-list {
    width: 100px;
    background: #f7f8fa;
    overflow-y: auto;
}

.category-item {
    padding: 16px 12px;
    text-align: center;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.category-item.active {
    background: #fff;
    color: var(--van-primary-color);
}

.category-icon-wrapper {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: var(--van-primary-color-light);
    transition: all 0.3s;
}

.category-item.active .category-icon-wrapper {
    background: var(--van-primary-color);
}

.category-icon-wrapper :deep(.el-icon) {
    color: var(--van-primary-color);
    transition: all 0.3s;
}

.category-item.active .category-icon-wrapper :deep(.el-icon) {
    color: #fff;
}

.product-list {
    flex: 1;
    padding: 12px;
    overflow-y: auto;
    background: #fff;
}

.product-list :deep(.van-card) {
    background: #f7f8fa;
    margin-bottom: 12px;
    border-radius: 8px;
}

.product-list :deep(.van-card__title) {
    font-size: 14px;
}

.product-list :deep(.van-card__price) {
    color: var(--van-danger-color);
    font-size: 16px;
}

.product-stepper {
    display: flex;
    align-items: center;
    gap: 8px;
}

.product-stepper .count {
    min-width: 24px;
    text-align: center;
}

/* 购物车结算栏 */
.cart-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 50px;
    height: 50px;
    background: #fff;
    display: flex;
    align-items: center;
    padding: 0 16px;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.cart-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
}

.cart-icon {
    width: 40px;
    height: 40px;
    background: var(--van-primary-color);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.cart-icon :deep(.van-icon) {
    font-size: 24px;
    color: #fff;
}

.price-info {
    flex: 1;
}

.total-price {
    font-size: 20px;
    font-weight: bold;
    color: var(--van-danger-color);
}

.submit-btn {
    width: 100px;
    height: 36px;
    border-radius: 18px;
}

/* 购物车弹窗 */
.cart-popup {
    max-height: 70vh;
    overflow-y: auto;
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

.popup-header .clear {
    color: #999;
    font-size: 14px;
}

.cart-list {
    padding: 16px;
}

.cart-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.item-info h4 {
    margin: 0 0 4px;
    font-size: 14px;
}

.item-info .price {
    color: var(--van-danger-color);
    font-size: 16px;
}

.item-stepper {
    display: flex;
    align-items: center;
    gap: 8px;
}

.item-stepper .count {
    min-width: 24px;
    text-align: center;
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
    justify-content: space-between;
    margin-bottom: 16px;
}

.product-price {
    font-size: 20px;
    font-weight: bold;
    color: var(--van-danger-color);
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