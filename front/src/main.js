import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { 
    Button,
    Tabbar,
    TabbarItem,
    NavBar,
    Icon,
    Cell,
    CellGroup,
    Form,
    Field,
    Toast,
    Card,
    Tag,
    Grid,
    GridItem,
    Swipe,
    SwipeItem,
    Image as VanImage,
    Popup,
    Dialog,
    Badge,
    DropdownMenu,
    DropdownItem,
    Tab,
    Tabs,
    AddressList,
    AddressEdit
} from 'vant'
import 'vant/lib/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册Vant组件
const vantComponents = [
    Button,
    Tabbar,
    TabbarItem,
    NavBar,
    Icon,
    Cell,
    CellGroup,
    Form,
    Field,
    Toast,
    Card,
    Tag,
    Grid,
    GridItem,
    Swipe,
    SwipeItem,
    VanImage,
    Popup,
    Dialog,
    Badge,
    DropdownMenu,
    DropdownItem,
    Tab,
    Tabs,
    AddressList,
    AddressEdit
]
vantComponents.forEach(component => {
    app.use(component)
})

// 使用Element Plus
app.use(ElementPlus)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(createPinia())
app.use(router)

app.mount('#app')
