<!-- 地址管理页面 -->
<template>
    <div class="address-list">
        <van-nav-bar
            title="我的收货地址"
            left-text="返回"
            left-arrow
            @click-left="$router.back()"
        />
        
        <!-- 地址列表 -->
        <div class="address-items">
            <van-address-list
                v-model="selectedAddressId"
                :list="formattedAddresses"
                default-tag="默认"
                @add="onAdd"
                @edit="onEdit"
                @select="onSelect"
            />
        </div>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getAddressList, deleteAddress, setDefaultAddress } from '@/api/address'

export default {
    name: 'AddressList',
    setup() {
        const router = useRouter()
        const addresses = ref([])
        const selectedAddressId = ref('')
        
        // 格式化地址列表，适配 van-address-list 组件的数据格式
        const formattedAddresses = computed(() => {
            return addresses.value.map(addr => ({
                id: addr.id,
                name: addr.receiverName,
                tel: addr.receiverPhone,
                address: `${addr.province}${addr.city}${addr.district}${addr.detailAddress}`,
                isDefault: addr.isDefault,
                tag: addr.tag
            }))
        })
        
        // 加载地址列表
        const loadAddresses = async () => {
            try {
                const res = await getAddressList()
                addresses.value = res.data
                // 如果有默认地址，选中它
                const defaultAddr = addresses.value.find(addr => addr.isDefault)
                if (defaultAddr) {
                    selectedAddressId.value = defaultAddr.id
                }
            } catch (error) {
                showToast(error.message || '获取地址列表失败')
            }
        }
        
        // 添加新地址
        const onAdd = () => {
            router.push('/mobile/address/edit')
        }
        
        // 编辑地址
        const onEdit = (item) => {
            router.push(`/mobile/address/edit/${item.id}`)
        }
        
        // 选择地址
        const onSelect = async (item) => {
            try {
                await setDefaultAddress(item.id)
                await loadAddresses()
                showToast('设置默认地址成功')
            } catch (error) {
                showToast(error.message || '设置默认地址失败')
            }
        }
        
        onMounted(() => {
            loadAddresses()
        })
        
        return {
            addresses,
            selectedAddressId,
            formattedAddresses,
            onAdd,
            onEdit,
            onSelect
        }
    }
}
</script>

<style scoped>
.address-list {
    min-height: 100vh;
    background: #f7f8fa;
}

.address-items {
    padding-bottom: 50px;
}

:deep(.van-address-list__bottom) {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
}

:deep(.van-button--danger) {
    background: var(--van-primary-color);
    border-color: var(--van-primary-color);
}
</style> 