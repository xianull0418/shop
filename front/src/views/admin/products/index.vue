<template>
    <div class="admin-products">
        <div class="header">
            <h2>商品管理</h2>
            <el-button type="primary" @click="handleAdd">
                添加商品
            </el-button>
        </div>
        
        <el-table :data="products" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="图片" width="100">
                <template #default="{ row }">
                    <el-image
                        :src="getImageUrl(row.imageUrl)"
                        :preview-src-list="[getImageUrl(row.imageUrl)]"
                        fit="cover"
                        style="width: 50px; height: 50px"
                    />
                </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="categoryName" label="分类" width="120" />
            <el-table-column prop="price" label="价格" width="120">
                <template #default="{ row }">
                    ¥{{ row.price }}
                </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="120" />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.status ? 'success' : 'info'">
                        {{ row.status ? '上架' : '下架' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button-group>
                        <el-button type="primary" @click="handleEdit(row)">
                            编辑
                        </el-button>
                        <el-button type="danger" @click="handleDelete(row)">
                            删除
                        </el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
        
        <el-dialog
            v-model="dialogVisible"
            :title="form.id ? '编辑商品' : '添加商品'"
            width="500px"
        >
            <el-form :model="form" label-width="80px">
                <el-form-item label="商品名称">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="form.categoryId">
                        <el-option
                            v-for="category in categories"
                            :key="category.id"
                            :label="category.name"
                            :value="category.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="价格">
                    <el-input-number v-model="form.price" :precision="2" :step="0.1" />
                </el-form-item>
                <el-form-item label="库存">
                    <el-input-number v-model="form.stock" :min="0" />
                </el-form-item>
                <el-form-item label="商品图片" prop="imageUrl">
                    <el-upload
                        class="avatar-uploader"
                        action="http://localhost:8080/api/admin/upload"
                        :show-file-list="false"
                        :on-success="handleUploadSuccess"
                        :before-upload="beforeUpload"
                        :headers="{ Authorization: `Bearer ${userStore.token}` }"
                    >
                        <img v-if="form.imageUrl" :src="getImageUrl(form.imageUrl)" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="状态">
                    <el-switch v-model="form.status" />
                </el-form-item>
                <el-form-item label="描述">
                    <el-input
                        v-model="form.description"
                        type="textarea"
                        rows="3"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">
                    确定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAdminProducts, createProduct, updateProduct, deleteProduct, uploadFile } from '@/api/admin'
import { getCategories } from '@/api/product'
import { API_BASE_URL } from '@/config'
import { useUserStore } from '@/stores/user'

export default {
    name: 'AdminProducts',
    setup() {
        // 获取用户store，用于获取token
        const userStore = useUserStore()
        // 商品列表数据
        const products = ref([])
        // 分类列表数据
        const categories = ref([])
        // 控制编辑对话框的显示
        const dialogVisible = ref(false)
        // 表单数据
        const form = ref({
            name: '',
            categoryId: '',
            price: 0,
            stock: 0,
            imageUrl: '',
            status: true,
            description: ''
        })
        
        // 加载商品和分类数据
        const loadData = async () => {
            try {
                // 并行请求商品列表和分类列表
                const [productsRes, categoriesRes] = await Promise.all([
                    getAdminProducts(),
                    getCategories()
                ])
                products.value = productsRes.data
                categories.value = categoriesRes.data
            } catch (error) {
                ElMessage.error(error.message)
            }
        }
        
        // 点击添加商品按钮
        const handleAdd = () => {
            // 重置表单数据
            form.value = {
                name: '',
                categoryId: '',
                price: 0,
                stock: 0,
                imageUrl: '',
                status: true,
                description: ''
            }
            dialogVisible.value = true
        }
        
        // 点击编辑商品按钮
        const handleEdit = (row) => {
            // 复制商品数据到表单
            form.value = { ...row }
            dialogVisible.value = true
        }
        
        // 点击删除商品按钮
        const handleDelete = (row) => {
            ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    await deleteProduct(row.id)
                    ElMessage.success('删除成功')
                    loadData()
                } catch (error) {
                    ElMessage.error(error.message)
                }
            })
        }
        
        // 图片上传成功的回调
        const handleUploadSuccess = (response) => {
            if (response.code === 200) {
                // 设置表单的图片URL
                form.value.imageUrl = response.data;
                ElMessage.success('上传成功');
            } else {
                ElMessage.error(response.message || '上传失败');
            }
        };

        // 图片上传前的验证
        const beforeUpload = (file) => {
            // 验证文件类型是否为图片
            const isImage = file.type.startsWith('image/');
            // 验证文件大小是否小于2MB
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isImage) {
                ElMessage.error('上传文件只能是图片格式!');
                return false;
            }
            if (!isLt2M) {
                ElMessage.error('上传图片大小不能超过 2MB!');
                return false;
            }
            return true;
        };
        
        const handleSubmit = async () => {
            try {
                if (form.value.id) {
                    await updateProduct(form.value.id, form.value)
                } else {
                    await createProduct(form.value)
                }
                ElMessage.success(form.value.id ? '更新成功' : '添加成功')
                dialogVisible.value = false
                loadData()
            } catch (error) {
                ElMessage.error(error.message)
            }
        }
        
        const getImageUrl = (url) => {
            if (!url) return ''
            if (url.startsWith('http')) return url
            return `http://localhost:8080${url}`
        }
        
        onMounted(() => {
            loadData()
        })
        
        return {
            products,
            categories,
            dialogVisible,
            form,
            handleAdd,
            handleEdit,
            handleDelete,
            handleUploadSuccess,
            handleSubmit,
            beforeUpload,
            userStore,
            getImageUrl
        }
    }
}
</script>

<style scoped>
.admin-products {
    padding: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.header h2 {
    margin: 0;
}

.avatar-uploader {
    text-align: center;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
    line-height: 120px;
}

.avatar {
    width: 120px;
    height: 120px;
    display: block;
}
</style> 