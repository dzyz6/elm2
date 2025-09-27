import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from '../views/Index.vue'
import BusinessList from '../views/BusinessList.vue'
import BusinessInfo from '../views/BusinessInfo.vue'
import Login from '../views/Login.vue'
import Orders from '../views/Orders.vue'
import UserAddress from '../views/UserAddress.vue'
import Payment from '../views/Payment.vue'
import OrderList from '../views/OrderList.vue'
import AddUserAddress from '../views/AddUserAddress.vue'
import EditUserAddress from '../views/EditUserAddress.vue'
import Register from '../views/Register.vue'
import MyPage from "@/views/MyPage.vue";
import AddBusinessPage from "@/views/AddBusinessPage.vue";
import AddBusiness from "@/views/AddBusiness.vue";
import AddFood from "@/views/AddFood.vue";
import PutongPage from "@/views/PutongPage.vue";


Vue.use(VueRouter)

const routes = [{
		path: '/',
		name: 'Home',
		component: Index
	},
	{
		path: '/putongPage',
		name: 'PutongPage',
		component: PutongPage
	},
	{
		path: '/index',
		name: 'Index',
		component: Index
	}, {
		path: '/businessList',
		name: 'BusinessList',
		component: BusinessList
	}, {
		path: '/businessInfo',
		name: 'BusinessInfo',
		component: BusinessInfo
	},
	{
		path: '/addBusinessPage',
		name: 'AddBusinessPage',
		component: AddBusinessPage
	},
	{
		path: '/addFood',
		name: 'AddFood',
		component: AddFood
	},
	{
		path: '/addBusiness',
		name: 'AddBusiness',
		component: AddBusiness
	}, {
		path: '/login',
		name: 'Login',
		component: Login
	}, {
		path: '/orders',
		name: 'Orders',
		component: Orders
	}, {
		path: '/userAddress',
		name: 'UserAddress',
		component: UserAddress
	}, {
	path: '/myPage',
	name: 'MyPage',
	component: MyPage
},
	{
		path: '/payment',
		name: 'Payment',
		component: Payment
	}, {
		path: '/orderList',
		name: 'OrderList',
		component: OrderList
	}, {
		path: '/addUserAddress',
		name: 'AddUserAddress',
		component: AddUserAddress
	}, {
		path: '/editUserAddress',
		name: 'EditUserAddress',
		component: EditUserAddress
	}, {
		path: '/register',
		name: 'Register',
		component: Register
	}

]

//解决重复路由报异常问题
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

const router = new VueRouter({
	mode: 'history',
	base: process.env.BASE_URL,
	routes
})

export default router
