<template>
    <div class="admin-users">
        <div class="header">
            <div class="search">
                <el-input
                    v-model="searchForm.keyword"
                    placeholder="请输入用户名"
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
        </div>
        
        <el-table 
            v-loading="loading"
            :data="users"
            style="width: 100%"
        >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="role" label="角色" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
                        {{ row.role === 'admin' ? '管理员' : '用户' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="180" />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-switch
                        v-model="row.status"
                        :disabled="row.role === 'admin'"
                        @change="(val) => handleStatusChange(row, val)"
                    />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="{ row }">
                    <el-button
                        v-if="row.role !== 'admin'"
                        type="danger"
                        link
                        @click="handleDelete(row)"
                    >
                        删除
                    </el-button>
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
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminUsers, updateUserStatus, deleteUser } from '@/api/admin'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const searchForm = ref({
    page: 1,
    size: 10,
    keyword: ''
})

const loadData = async () => {
    loading.value = true
    try {
        const res = await getAdminUsers({
            ...searchForm.value,
            offset: (searchForm.value.page - 1) * searchForm.value.size
        })
        users.value = res.data.list
        total.value = res.data.total
    } catch (error) {
        ElMessage.error(error.message || '获取用户列表失败')
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

const handleStatusChange = async (row, status) => {
    try {
        await updateUserStatus(row.id, status)
        ElMessage.success('更新成功')
        loadData()
    } catch (error) {
        row.status = !status // 恢复状态
        ElMessage.error(error.message || '更新失败')
    }
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await deleteUser(row.id)
            ElMessage.success('删除成功')
            loadData()
        } catch (error) {
            ElMessage.error(error.message || '删除失败')
        }
    })
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.admin-users {
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