import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: []
    }),
    
    getters: {
        cartItems: (state) => state.items,
        
        totalCount: (state) => {
            return state.items.reduce((total, item) => total + item.count, 0)
        },
        
        totalPrice: (state) => {
            return state.items.reduce((total, item) => total + item.price * item.count, 0)
        }
    },
    
    actions: {
        addToCart(product) {
            const existingItem = this.items.find(item => item.id === product.id)
            if (existingItem) {
                existingItem.count++
            } else {
                this.items.push({
                    ...product,
                    count: 1
                })
            }
        },
        
        decreaseFromCart(product) {
            const existingItem = this.items.find(item => item.id === product.id)
            if (existingItem) {
                if (existingItem.count > 1) {
                    existingItem.count--
                } else {
                    const index = this.items.indexOf(existingItem)
                    this.items.splice(index, 1)
                }
            }
        },
        
        removeFromCart(productId) {
            const index = this.items.findIndex(item => item.id === productId)
            if (index > -1) {
                this.items.splice(index, 1)
            }
        },
        
        clearCart() {
            this.items = []
        },
        
        getProductCount(productId) {
            const item = this.items.find(item => item.id === productId)
            return item ? item.count : 0
        }
    },
    
    persist: true
}) 