<!-- 地址编辑页面 -->
<template>
    <div class="address-edit">
        <van-nav-bar
            :title="isEdit ? '编辑收货地址' : '新增收货地址'"
            left-text="返回"
            left-arrow
            @click-left="$router.back()"
        />
        
        <van-address-edit
            :area-list="areaList"
            :address-info="addressInfo"
            :show-delete="isEdit"
            show-set-default
            show-search-result
            :search-result="searchResult"
            :area-columns-placeholder="['请选择省', '请选择市', '请选择区']"
            @save="onSave"
            @delete="onDelete"
            @change-detail="onChangeDetail"
        />
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { areaList } from '@vant/area-data'
import { getAddressById, addAddress, updateAddress, deleteAddress } from '@/api/address'

export default {
    name: 'AddressEdit',
    setup() {
        const route = useRoute()
        const router = useRouter()
        const addressId = computed(() => route.params.id)
        const isEdit = computed(() => !!addressId.value)
        const addressInfo = ref({})
        const searchResult = ref([])
        
        // 加载地址详情
        const loadAddress = async () => {
            if (!addressId.value) return
            
            try {
                const res = await getAddressById(addressId.value)
                const addr = res.data
                addressInfo.value = {
                    id: addr.id,
                    name: addr.receiverName,
                    tel: addr.receiverPhone,
                    province: addr.province,
                    city: addr.city,
                    county: addr.district,
                    addressDetail: addr.detailAddress,
                    isDefault: addr.isDefault,
                    tag: addr.tag
                }
            } catch (error) {
                showToast(error.message || '获取地址详情失败')
            }
        }
        
        // 保存地址
        const onSave = async (content) => {
            const addressData = {
                id: addressId.value,
                receiverName: content.name,
                receiverPhone: content.tel,
                province: content.province,
                city: content.city,
                district: content.county,
                detailAddress: content.addressDetail,
                isDefault: content.isDefault,
                tag: content.tag
            }
            
            try {
                if (isEdit.value) {
                    await updateAddress(addressData)
                } else {
                    await addAddress(addressData)
                }
                showToast('保存成功')
                router.back()
            } catch (error) {
                showToast(error.message || '保存失败')
            }
        }
        
        // 删除地址
        const onDelete = async () => {
            try {
                await showDialog({
                    title: '提示',
                    message: '确定要删除这个地址吗？',
                })
                await deleteAddress(addressId.value)
                showToast('删除成功')
                router.back()
            } catch (error) {
                if (error.message !== 'cancel') {
                    showToast(error.message || '删除失败')
                }
            }
        }
        
        // 地址详情改变时的搜索建议
        const onChangeDetail = (val) => {
            if (val) {
                searchResult.value = [
                    {
                        name: '无建议',
                        address: '当前不支持地址联想'
                    }
                ]
            } else {
                searchResult.value = []
            }
        }
        
        onMounted(() => {
            loadAddress()
        })
        
        return {
            isEdit,
            addressInfo,
            areaList,
            searchResult,
            onSave,
            onDelete,
            onChangeDetail
        }
    }
}
</script>

<style scoped>
.address-edit {
    min-height: 100vh;
    background: #f7f8fa;
}

:deep(.van-button--danger) {
    background: var(--van-primary-color);
    border-color: var(--van-primary-color);
}
</style> 