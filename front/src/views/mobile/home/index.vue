<template>
    <div class="home">
        <van-nav-bar title="面包商店" />
        
        <!-- 轮播图 -->
        <van-swipe class="swipe" :autoplay="3000" indicator-color="white">
            <van-swipe-item v-for="(image, index) in banners" :key="index">
                <van-image
                    :src="image"
                    class="banner-image"
                >
                    <template #error>
                        <div class="banner-placeholder">
                            <van-icon name="photo-o" size="24" />
                            <p>图片加载失败</p>
                        </div>
                    </template>
                </van-image>
            </van-swipe-item>
        </van-swipe>

        <!-- 分类导航 -->
        <div class="category-nav">
            <van-grid :column-num="4" :border="false">
                <van-grid-item 
                    v-for="category in categories" 
                    :key="category.id"
                    @click="goToMenu(category.id)"
                >
                    <template #icon>
                        <div class="grid-icon-wrapper">
                            <el-icon :size="24">
                                <component :is="getIconComponent(category.name)" />
                            </el-icon>
                        </div>
                    </template>
                    <template #text>
                        <span class="grid-text">{{ category.name }}</span>
                    </template>
                </van-grid-item>
            </van-grid>
        </div>

        <!-- 热门商品 -->
        <div class="section">
            <div class="section-title">
                <span class="title">热门商品</span>
                <van-icon name="arrow" />
            </div>
            <div class="product-list">
                <van-card
                    v-for="product in hotProducts"
                    :key="product.id"
                    :price="product.price.toFixed(2)"
                    :title="product.name"
                    :thumb="getImageUrl(product.imageUrl)"
                    @click="showProductDetail(product)"
                >
                    <template #tags>
                        <van-tag plain type="danger">热销</van-tag>
                    </template>
                </van-card>
            </div>
        </div>

        <!-- 新品上架 -->
        <div class="section">
            <div class="section-title">
                <span class="title">新品上架</span>
                <van-icon name="arrow" />
            </div>
            <div class="product-list">
                <van-card
                    v-for="product in newProducts"
                    :key="product.id"
                    :price="product.price.toFixed(2)"
                    :title="product.name"
                    :thumb="getImageUrl(product.imageUrl)"
                    @click="showProductDetail(product)"
                >
                    <template #tags>
                        <van-tag plain type="primary">新品</van-tag>
                    </template>
                </van-card>
            </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list">
            <van-grid :column-num="2" :gutter="10">
                <van-grid-item
                    v-for="product in products"
                    :key="product.id"
                    @click="showProductDetail(product)"
                >
                    <van-image
                        :src="getImageUrl(product.imageUrl)"
                        width="100%"
                        height="100"
                    />
                    <div class="product-info">
                        <div class="product-name">{{ product.name }}</div>
                        <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
                    </div>
                </van-grid-item>
            </van-grid>
        </div>

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
                            <van-button 
                                size="small" 
                                type="primary"
                                @click="addToCart(currentProduct)"
                            >
                                加入购物车
                            </van-button>
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

<style scoped>
.home {
    min-height: 100vh;
    background: #f7f8fa;
}

.swipe {
    margin-bottom: 12px;
}

.banner-image {
    width: 100%;
    height: 180px;
    object-fit: cover;
}

.category-nav {
    background: #fff;
    margin-bottom: 12px;
    padding: 12px 0;
}

.grid-icon-wrapper {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--van-primary-color-light);
    border-radius: 50%;
}

.grid-icon-wrapper :deep(.el-icon) {
    color: var(--van-primary-color);
}

.grid-text {
    font-size: 14px;
    margin-top: 8px;
    color: #323233;
}

.section {
    background: #fff;
    margin-bottom: 12px;
    padding: 16px;
}

.section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.section-title .title {
    font-size: 16px;
    font-weight: bold;
    position: relative;
    padding-left: 12px;
}

.section-title .title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 16px;
    background: var(--van-primary-color);
    border-radius: 2px;
}

.product-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
}

.product-list :deep(.van-card) {
    background: #f7f8fa;
    border-radius: 8px;
    cursor: pointer;
}

.product-list :deep(.van-card__title) {
    font-size: 14px;
}

.product-list :deep(.van-card__price) {
    color: var(--van-danger-color);
    font-size: 16px;
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

.banner-placeholder {
    width: 100%;
    height: 180px;
    background: #f7f8fa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #969799;
}

.banner-placeholder .van-icon {
    font-size: 48px;
    margin-bottom: 8px;
}
</style>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getCategories } from '@/api/category'
import { getProducts } from '@/api/product'
import { API_BASE_URL } from '@/config'
import { 
    Bowl,
    IceCream,
    Coffee,
    More
} from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'

export default {
    name: 'HomePage',
    components: {
        Bowl,
        IceCream,
        Coffee,
        More
    },
    setup() {
        const router = useRouter()
        const cartStore = useCartStore()
        const categories = ref([])
        const hotProducts = ref([])
        const newProducts = ref([])
        const banners = ref([
            '/images/banner1.jpg',
            '/images/banner2.jpg',
            '/images/banner3.jpg'
        ])
        const showProductPopup = ref(false)
        const currentProduct = ref(null)

        const getIconComponent = (name) => {
            const iconMap = {
                '面包': Bowl,
                '蛋糕': IceCream,
                '饮品': Coffee,
                '其他': More
            }
            return iconMap[name] || More
        }

        const getImageUrl = (url) => {
            if (!url) return ''
            if (url.startsWith('http')) return url
            return `${API_BASE_URL}${url}`
        }

        const goToMenu = (categoryId) => {
            router.push({
                path: '/mobile/menu',
                query: { categoryId }
            })
        }

        const showProductDetail = (product) => {
            currentProduct.value = product
            showProductPopup.value = true
        }

        const addToCart = (product) => {
            cartStore.addToCart(product)
            showToast('已添加到购物车')
            showProductPopup.value = false
        }

        const loadData = async () => {
            try {
                // 加载分类
                const categoryRes = await getCategories()
                categories.value = categoryRes.data

                // 加载商品
                const productRes = await getProducts()
                const products = productRes.data
                // 随机选择4个商品作为热门商品
                hotProducts.value = products.slice(0, 4)
                // 最新的4个商品作为新品
                newProducts.value = products.slice(-4)
            } catch (error) {
                showToast(error.message || '加载数据失败')
            }
        }

        onMounted(() => {
            loadData()
        })

        return {
            categories,
            hotProducts,
            newProducts,
            banners,
            getImageUrl,
            goToMenu,
            showProductPopup,
            currentProduct,
            showProductDetail,
            addToCart,
            getIconComponent
        }
    }
}
</script> 