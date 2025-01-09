<template>
    <div class="register">
        <h2>注册</h2>
        <el-form :model="form" :rules="rules" ref="formRef">
            <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名" />
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="密码" />
            </el-form-item>
            <el-form-item prop="phone">
                <el-input v-model="form.phone" placeholder="手机号" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSubmit" style="width: 100%">
                    注册
                </el-button>
            </el-form-item>
            <div class="links">
                <router-link to="/login">已有账号？去登录</router-link>
            </div>
        </el-form>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

export default {
    name: 'RegisterPage',
    setup() {
        const router = useRouter()
        const formRef = ref(null)
        const form = ref({
            username: '',
            password: '',
            phone: ''
        })
        
        const rules = {
            username: [
                { required: true, message: '请输入用户名', trigger: 'blur' },
                { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入密码', trigger: 'blur' },
                { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
            ],
            phone: [
                { required: true, message: '请输入手机号', trigger: 'blur' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
            ]
        }
        
        const handleSubmit = async () => {
            try {
                await formRef.value.validate()
                const userStore = useUserStore()
                const res = await userStore.register(form.value)
                if (res.code === 200) {
                    ElMessage.success('注册成功')
                    router.push('/')
                }
            } catch (error) {
                console.error('注册失败:', error)
                ElMessage.error(error.response?.data?.message || '注册失败，请重试')
            }
        }
        
        return {
            form,
            rules,
            formRef,
            handleSubmit
        }
    }
}
</script>

<style scoped>
.register {
    max-width: 400px;
    margin: 100px auto;
    padding: 20px;
}

.links {
    text-align: right;
    margin-top: 10px;
}

.links a {
    color: #409EFF;
    text-decoration: none;
}
</style> 