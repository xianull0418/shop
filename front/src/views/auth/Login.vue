<template>
    <div class="login-page">
        <van-nav-bar 
            title="登录" 
            left-text="返回"
            left-arrow
            @click-left="$router.back()"
        />
        <div class="form">
            <van-cell-group inset>
                <van-field
                    v-model="form.username"
                    label="用户名"
                    placeholder="请输入用户名"
                />
                <van-field
                    v-model="form.password"
                    type="password"
                    label="密码"
                    placeholder="请输入密码"
                />
            </van-cell-group>
            
            <div class="buttons">
                <van-button type="primary" block @click="handleLogin">
                    登录
                </van-button>
                <van-button plain block to="/mobile/register">
                    注册账号
                </van-button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast } from 'vant'

export default {
    name: 'LoginPage',
    setup() {
        const router = useRouter()
        const route = useRoute()
        const userStore = useUserStore()
        
        const form = ref({
            username: '',
            password: ''
        })
        
        const handleLogin = async () => {
            if (!form.value.username || !form.value.password) {
                showToast('请输入用户名和密码')
                return
            }
            
            try {
                await userStore.login(form.value)
                showToast('登录成功')
                // 获取重定向地址，如果没有则默认跳转到首页
                const redirect = route.query.redirect || '/mobile'
                // 确保重定向路径以 /mobile 开头
                const redirectPath = redirect.startsWith('/mobile') ? redirect : `/mobile${redirect}`
                router.push(redirectPath)
            } catch (error) {
                showToast(error.message || '登录失败')
            }
        }
        
        return {
            form,
            handleLogin
        }
    }
}
</script>

<style scoped>
.login-page {
    height: 100vh;
    background: #f7f8fa;
}
.form {
    padding: 20px;
}
.buttons {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}
</style> 