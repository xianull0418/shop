<template>
    <div class="mine">
        <van-nav-bar title="我的" />
        
        <div class="user-info" @click="handleUploadAvatar">
            <div class="avatar">
                <van-image
                    round
                    width="60"
                    height="60"
                    :src="getAvatarUrl(userStore.userInfo?.avatar)"
                    fit="cover"
                >
                    <template #error>
                        <van-image
                            round
                            width="60"
                            height="60"
                            :src="defaultAvatar"
                            fit="cover"
                        />
                    </template>
                </van-image>
            </div>
            <div class="info">
                <h3>{{ userStore.userInfo?.username || '未登录' }}</h3>
                <p>{{ userStore.userInfo ? '点击更换头像' : '点击登录' }}</p>
            </div>
        </div>
        
        <van-cell-group class="menu-list">
            <van-cell
                title="我的订单"
                is-link
                @click="handleOrderClick('/mobile/order')"
                value="全部订单"
            />
            <van-grid :column-num="4">
                <van-grid-item
                    icon="pending-payment"
                    text="待付款"
                    @click="handleOrderClick('/mobile/order?status=pending')"
                />
                <van-grid-item
                    icon="paid"
                    text="已付款"
                    @click="handleOrderClick('/mobile/order?status=paid')"
                />
                <van-grid-item
                    icon="completed"
                    text="已完成"
                    @click="handleOrderClick('/mobile/order?status=completed')"
                />
                <van-grid-item
                    icon="failure"
                    text="已取消"
                    @click="handleOrderClick('/mobile/order?status=cancelled')"
                />
            </van-grid>
        </van-cell-group>
        
        <van-cell-group class="menu-list">
            <van-cell
                title="收货地址"
                is-link
                @click="handleAddressClick"
            />
            <van-cell
                title="联系客服"
                is-link
                @click="() => showToast({ message: '暂未实现' })"
            />
            <van-cell
                title="关于我们"
                is-link
                @click="() => showToast({ message: '暂未实现' })"
            />
        </van-cell-group>
        
        <div class="logout" v-if="userStore.userInfo">
            <van-button block type="danger" @click="handleLogout">
                退出登录
            </van-button>
        </div>

        <input
            type="file"
            ref="fileInput"
            accept="image/*"
            style="display: none"
            @change="onFileChange"
        />
    </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'
import { uploadAvatar } from '@/api/auth'
import { API_BASE_URL } from '@/config'
import defaultAvatar from '@/assets/default-avatar.png'
import { 
    Button, 
    Tabbar, 
    TabbarItem,
    NavBar,
    Icon,
    Cell,
    CellGroup,
    Form,
    Field,
    Toast,
    Grid,
    GridItem,
    Image as VanImage
} from 'vant'

export default {
    name: 'MinePage',
    setup() {
        const router = useRouter()
        const userStore = useUserStore()
        const fileInput = ref(null)
        
        const getAvatarUrl = (avatar) => {
            if (!avatar) return defaultAvatar
            if (avatar.startsWith('http')) return avatar
            return `${API_BASE_URL}${avatar}`
        }
        
        const goToLogin = () => {
            router.push({
                path: '/mobile/login',
                query: { redirect: '/mobile/mine' }
            })
        }
        
        const handleLogout = () => {
            userStore.logout()
            router.push('/mobile')
        }

        const handleUploadAvatar = () => {
            if (!userStore.userInfo) {
                goToLogin()
                return
            }
            fileInput.value.click()
        }

        const onFileChange = async (event) => {
            const file = event.target.files[0]
            if (!file) return

            const formData = new FormData()
            formData.append('file', file)

            try {
                showToast({
                    type: 'loading',
                    message: '上传中...',
                    forbidClick: true,
                })
                const res = await uploadAvatar(formData)
                await userStore.fetchUserInfo()
                showToast({
                    type: 'success',
                    message: '头像上传成功'
                })
            } catch (error) {
                showToast({
                    type: 'fail',
                    message: '头像上传失败'
                })
            }
        }

        const handleOrderClick = (path) => {
            if (!userStore.token) {
                router.push({
                    path: '/mobile/login',
                    query: { redirect: path }
                })
                return
            }
            router.push(path)
        }

        const handleAddressClick = () => {
            if (!userStore.token) {
                router.push({
                    path: '/mobile/login',
                    query: { redirect: '/mobile/address' }
                })
                return
            }
            router.push('/mobile/address')
        }
        
        return {
            userStore,
            fileInput,
            goToLogin,
            handleLogout,
            handleUploadAvatar,
            onFileChange,
            showToast,
            getAvatarUrl,
            defaultAvatar,
            handleOrderClick,
            handleAddressClick
        }
    }
}
</script>

<style scoped>
.mine {
    min-height: 100vh;
    background: #f7f8fa;
}

.user-info {
    background: #fff;
    padding: 20px;
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.avatar {
    margin-right: 16px;
    position: relative;
}

.avatar::after {
    content: '点击更换头像';
    position: absolute;
    bottom: -20px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 12px;
    color: #999;
    white-space: nowrap;
    opacity: 0;
    transition: opacity 0.3s;
}

.user-info:hover .avatar::after {
    opacity: 1;
}

.info h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #323233;
}

.info p {
    margin: 4px 0 0;
    font-size: 14px;
    color: #969799;
}

.menu-list {
    margin-bottom: 12px;
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
}

.menu-list :deep(.van-grid) {
    padding: 16px 0;
}

.menu-list :deep(.van-grid-item__content) {
    padding: 16px 8px;
}

.menu-list :deep(.van-grid-item__icon) {
    font-size: 24px;
    margin-bottom: 8px;
}

.menu-list :deep(.van-grid-item__text) {
    color: #323233;
    font-size: 12px;
}

.logout {
    padding: 20px;
    margin-top: auto;
}

.logout :deep(.van-button) {
    height: 44px;
    font-size: 16px;
    border-radius: 22px;
}
</style> 