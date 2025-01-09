<template>
    <div class="admin-orders">
        <div class="header">
            <h2>订单管理</h2>
        </div>
        
        <el-table :data="orders" style="width: 100%">
            <el-table-column prop="id" label="订单号" width="100" />
            <el-table-column prop="username" label="用户" width="120" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="address" label="地址" show-overflow-tooltip />
            <el-table-column prop="totalAmount" label="金额" width="120">
                <template #default="{ row }">
                    ¥{{ row.totalAmount }}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                        {{ getStatusText(row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button-group>
                        <el-button
                            v-if="row.status === 'paid'"
                            type="primary"
                            @click="handleComplete(row)"
                        >
                            完成订单
                        </el-button>
                        <el-button
                            v-if="row.status === 'pending'"
                            type="danger"
                            @click="handleCancel(row)"
                        >
                            取消订单
                        </el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAdminOrders, updateOrderStatus } from '@/api/admin'

export default {
    name: 'AdminOrders',
    setup() {
        const orders = ref([])
        
        const getStatusType = (status) => {
            const typeMap = {
                pending: 'warning',
                paid: 'primary',
                completed: 'success',
                cancelled: 'info'
            }
            return typeMap[status]
        }
        
        const getStatusText = (status) => {
            const textMap = {
                pending: '待付款',
                paid: '已付款',
                completed: '已完成',
                cancelled: '已取消'
            }
            return textMap[status]
        }
        
        const loadData = async () => {
            try {
                const res = await getAdminOrders()
                orders.value = res.data
            } catch (error) {
                ElMessage.error(error.message)
            }
        }
        
        const handleComplete = (row) => {
            ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    await updateOrderStatus(row.id, 'completed')
                    ElMessage.success('操作成功')
                    loadData()
                } catch (error) {
                    ElMessage.error(error.message)
                }
            })
        }
        
        const handleCancel = (row) => {
            ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
                type: 'warning'
            }).then(async () => {
                try {
                    await updateOrderStatus(row.id, 'cancelled')
                    ElMessage.success('操作成功')
                    loadData()
                } catch (error) {
                    ElMessage.error(error.message)
                }
            })
        }
        
        onMounted(() => {
            loadData()
        })
        
        return {
            orders,
            getStatusType,
            getStatusText,
            handleComplete,
            handleCancel
        }
    }
}
</script>

<style scoped>
.admin-orders {
    padding: 20px;
}

.header {
    margin-bottom: 20px;
}

.header h2 {
    margin: 0;
}
</style> 