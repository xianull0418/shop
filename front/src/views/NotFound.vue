<template>
    <div class="not-found">
        <div class="content">
            <div class="error-code">404</div>
            <div class="error-desc">抱歉，您访问的页面不存在</div>
            <div class="actions">
                <div class="btn" @click="goBack">返回上一页</div>
                <div class="btn outline" @click="goHome">返回首页</div>
            </div>
        </div>
    </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
    name: 'NotFound',
    setup() {
        const router = useRouter()

        const goBack = () => {
            router.back()
        }

        const goHome = () => {
            router.push('/mobile')
        }

        return {
            goBack,
            goHome
        }
    }
}
</script>

<style scoped>
.not-found {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8f9fa;
    padding: 20px;
}

.content {
    text-align: center;
    padding: 40px 20px;
    background: #fff;
    border-radius: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
    width: 100%;
    max-width: 400px;
}

.error-code {
    font-size: 80px;
    font-weight: bold;
    color: var(--van-primary-color);
    line-height: 1;
    margin-bottom: 20px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-desc {
    font-size: 18px;
    color: #666;
    margin-bottom: 40px;
}

.actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0 20px;
}

.btn {
    height: 44px;
    border-radius: 22px;
    font-size: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    user-select: none;
}

.btn:not(.outline) {
    background: linear-gradient(135deg, var(--van-primary-color), #4dabf7);
    color: #fff;
    box-shadow: 0 4px 12px rgba(var(--van-primary-color-rgb), 0.2);
}

.btn:not(.outline):active {
    transform: scale(0.98);
    box-shadow: 0 2px 8px rgba(var(--van-primary-color-rgb), 0.15);
}

.btn.outline {
    border: 1px solid var(--van-primary-color);
    color: var(--van-primary-color);
}

.btn.outline:active {
    background: rgba(var(--van-primary-color-rgb), 0.05);
    transform: scale(0.98);
}
</style> 