<template>
    <div class="admin-categories">
        <div class="header">
            <div class="search">
                <el-input
                    v-model="searchForm.keyword"
                    placeholder="请输入分类名称"
                    clearable
                    @keyup.enter="handleSearch"
                >
                    <template #append>
                        <el-button @click="handleSearch">
                            搜索
                        </el-button>
                    </template>
                </el-input>
            </div>
            <el-button type="primary" @click="handleAdd">
                添加分类
            </el-button>
        </div>
        
        <el-table 
            v-loading="loading"
            :data="categories"
            style="width: 100%"
        >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="sortOrder" label="排序" width="120" />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.status ? 'success' : 'info'">
                        {{ row.status ? '启用' : '禁用' }}
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
        
        <div class="pagination">
            <el-pagination
                v-model:current-page="searchForm.page"
                v-model:page-size="searchForm.size"
                :total="total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
        
        <el-dialog
            v-model="dialogVisible"
            :title="form.id ? '编辑分类' : '添加分类'"
            width="500px"
        >
            <el-form
                :model="form"
                :rules="rules"
                ref="formRef"
                label-width="80px"
            >
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="排序" prop="sortOrder">
                    <el-input-number v-model="form.sortOrder" :min="0" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-switch v-model="form.status" />
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
import {
    getAdminCategories,
    createCategory,
    updateCategory,
    deleteCategory
} from '@/api/admin'

export default {
    name: 'AdminCategories',
    setup() {
        const loading = ref(false)
        const categories = ref([])
        const total = ref(0)
        const searchForm = ref({
            page: 1,
            size: 10,
            keyword: ''
        })
        const dialogVisible = ref(false)
        const form = ref({
            name: '',
            sortOrder: 0,
            status: true
        })
        
        const formRef = ref(null)
        const rules = {
            name: [
                { required: true, message: '请输入分类名称', trigger: 'blur' },
                { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
            ],
            sortOrder: [
                { required: true, message: '请输入排序', trigger: 'blur' },
                { type: 'number', min: 0, message: '必须大于等于0', trigger: 'blur' }
            ],
            status: [
                { required: true, message: '请选择状态', trigger: 'change' }
            ]
        }
        
        const loadData = async () => {
            loading.value = true
            try {
                const res = await getAdminCategories(searchForm.value)
                categories.value = res.data.list
                total.value = res.data.total
            } catch (error) {
                ElMessage.error(error.message)
            } finally {
                loading.value = false
            }
        }
        
        const handleSearch = () => {
            searchForm.value.page = 1
            loadData()
        }
        
        const handleSizeChange = (val) => {
            searchForm.value.size = val
            loadData()
        }
        
        const handleCurrentChange = (val) => {
            searchForm.value.page = val
            loadData()
        }
        
        const handleAdd = () => {
            form.value = {
                name: '',
                sortOrder: 0,
                status: true
            }
            dialogVisible.value = true
        }
        
        const handleEdit = (row) => {
            form.value = { ...row }
            dialogVisible.value = true
        }
        
        const handleDelete = (row) => {
            ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    await deleteCategory(row.id)
                    ElMessage.success('删除成功')
                    loadData()
                } catch (error) {
                    ElMessage.error(error.message)
                }
            })
        }
        
        const handleSubmit = async () => {
            try {
                await formRef.value.validate()
                if (form.value.id) {
                    await updateCategory(form.value.id, form.value)
                } else {
                    await createCategory(form.value)
                }
                ElMessage.success(form.value.id ? '更新成功' : '添加成功')
                dialogVisible.value = false
                loadData()
            } catch (error) {
                if (error.message) {
                    ElMessage.error(error.message)
                }
            }
        }
        
        onMounted(() => {
            loadData()
        })
        
        return {
            loading,
            categories,
            total,
            searchForm,
            dialogVisible,
            form,
            handleSearch,
            handleSizeChange,
            handleCurrentChange,
            handleAdd,
            handleEdit,
            handleDelete,
            handleSubmit,
            formRef,
            rules
        }
    }
}
</script>

<style scoped>
.admin-categories {
    padding: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.search {
    width: 300px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style> 