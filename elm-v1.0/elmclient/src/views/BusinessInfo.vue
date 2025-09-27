<template>
	<div class="wrapper">
				
				<!-- header部分 -->
				<header>
					<p>商家信息</p>
				</header>
				
				<!-- 商家logo部分 -->
				<div class="business-logo">
					<img :src="business.businessImg">
				</div>
				
				<!-- 商家信息部分 -->
				<div class="business-info">
					<h1>{{business.businessName}}</h1>
					<p>&#165;{{business.startPrice}}起送 &#165;{{business.deliveryPrice}}配送</p>
					<p>{{business.businessExplain}}</p>
				</div>
				
				<!-- 食品列表部分 -->
				<ul class="food">
					<li v-for="(item,index) in foodArr">
						<div class="food-left">
							<img :src="item.foodImg">
							<div class="food-left-info">
								<h3>{{item.foodName}}</h3>
								<p>{{item.foodExplain}}</p>
								<p>&#165;{{item.foodPrice}}</p>
							</div>
						</div>
						<div class="food-right">
							<div>
								<i class="fa fa-minus-circle" @click="minus(index)" v-show="item.quantity!=0"></i>
							</div>
							<p><span v-show="item.quantity!=0">{{item.quantity}}</span></p>
							<div>
								<i class="fa fa-plus-circle" @click="add(index)"></i>
							</div>
						</div>
					</li>
				</ul>
				
				<!-- 购物车部分 -->
				<div class="cart">
					<div class="cart-left">
						<div class="cart-left-icon" :style="totalQuantity==0?'background-color:#505051;':'background-color:#3190E8;'">
							<i class="fa fa-shopping-cart"></i>
							<div class="cart-left-icon-quantity" v-show="totalQuantity!=0">{{totalQuantity}}</div>
						</div>
						<div class="cart-left-info">
							<p>&#165;{{totalPrice}}</p>
							<p>另需配送费{{business.deliveryPrice}}元</p>
						</div>
					</div>
					<div class="cart-right">
						<!-- 不够起送费 -->
						<div class="cart-right-item" v-show="totalSettle<(business.startPrice)" style="background-color: #535356;cursor: default;">
							&#165;{{business.startPrice}}起送
						</div>
						<!-- 达到起送费 -->
						<div class="cart-right-item" @click="toOrder" v-show="totalSettle>=(business.startPrice)">
              去结算
						</div>
					</div>
				</div>
				
			</div>
</template>

<script>
	import {getLocalStorage} from "@/common";

  export default{
		name:'BusinessInfo',
		data(){
			return {
				businessId: this.$route.query.businessId,
				business:{},
				foodArr:[],
				user:{}
			}
		},
		async created() {
			this.user = this.$getSessionStorage('user');
			console.log('用户信息:', this.user);
			console.log('商家ID:', this.businessId);
			
			try {
				const businessResponse = await this.$axios.get(`api/businesses/${this.businessId}`);
				this.business = businessResponse.data.data;

				const foodsResponse = await this.$axios.get(`api/foods`, {
					params: {
						business: this.businessId
					}
				});
				
				this.foodArr = foodsResponse.data.data;
				for(let i=0;i<this.foodArr.length;i++){
					this.foodArr[i].quantity=0;
				}

				//如果已登录，那么需要去查询购物车中是否已经选购了某个食品
				if(this.user!=null){
          const hasExecuted = localStorage.getItem(`${this.name}`);

          // 2. 若未执行过，则执行目标代码
          if (!hasExecuted) {

            for(let i=0;i<this.foodArr.length;i++){
              try {
                const cartResponse = await this.$axios.post('api/carts',{
                  "food":{
                    "id":this.foodArr[i].id,
                  },
                  "quantity":0,
                });

              } catch(error) {
                alert(error)
              }
            }
            // 3. 标记为“已执行”，存入本地存储（永久有效，除非手动清除）
            localStorage.setItem(`${this.name}`, 'true');
          } else {
            for(let i=0;i<this.foodArr.length;i++){
              try {
                const cartResponse = await this.$axios.post('api/carts',{
                  "id":i+1,
                  "food":{
                    "id":this.foodArr[i].id,
                  },
                  "quantity":0,
                });

              } catch(error) {
                alert(error)
              }
            }
          }



					await this.listCart();

				} else {
				}
			} catch(error) {
			}
		},
		methods:{

			async listCart(){
				try {
					const response = await this.$axios.get('api/cartlist',{
						params: {  // 使用params传递GET请求参数
							businessId: this.businessId  // 直接传递businessId的值
						}
					});
					
					let cartArr = response.data.data;
					console.log('购物车数据:', cartArr);
					
					//遍历所有食品列表
					for(let foodItem of this.foodArr){
						foodItem.quantity = 0;
						for(let cartItem of cartArr){
							if(cartItem.foodId==foodItem.foodId){
								foodItem.quantity = cartItem.quantity;
							}
						}
					}
					this.foodArr.sort();
					console.log('购物车状态更新完成');
				} catch(error) {
					console.error('查询购物车失败:', error);
				}
			},
			add(index){
				//首先做登录验证
				if(this.user==null){
					this.$router.push({path:'/login'});
					return;
				}

				if(this.foodArr[index].quantity===0){
					//做insert
          this.saveCart(index)
				}else{
					//做update
          this.updateCart(index,1)
				}
			},
			minus(index){
				//首先做登录验证
				if(this.user==null){
					this.$router.push({path:'/login'});
					return;
				}
				
				if(this.foodArr[index].quantity>1){
					//做update
          this.updateCart(index,-1)
				}else{
					//做delete
          this.removeCart(index)
				}
			},
			saveCart(index){
				this.$axios.post('api/carts',{
          "id":index+1,
          "food": {
            "id": this.foodArr[index].id
          },
          "quantity":1
				}).then(response=>{
          this.foodArr[index].quantity=1;
          this.foodArr.sort();
				}).catch(error=>{
          alert(error)
					console.error(error);
				});
			},
			updateCart(index,num){
				this.$axios.post('api/carts',{
          "id":index+1,
					"food":{
            "id":this.foodArr[index].id,
          },
					"quantity":this.foodArr[index].quantity+num,
				}).then(response=>{
					if(response.data.success===true){
						//此食品数量要更新为1或-1；
						this.foodArr[index].quantity+=num;
						this.foodArr.sort();
					}else{
						alert('向购物车中更新食品失败！');
					}
				}).catch(error=>{
					console.error(error);
				});
			},
			removeCart(index){
				this.$axios.post('api/deletecarts',{
          "id":index+1,
					"food":{
            "id":this.foodArr[index].id,
          }
				}).then(response=>{
					if(response.data.success===true){
						//此食品数量要更新为0；视图的减号和数量要消失
						this.foodArr[index].quantity=0;
						this.foodArr.sort();
					}else{
						alert('从购物车中删除食品失败！');
					}
				}).catch(error=>{
					console.error(error);
				});
			},
      async toOrder () {
        // this.foodArr.forEach((_, index) => {
        //   this.saveCart(index);
        // });
        // setTimeout(1000)
        this.$router.push({path:'/orders',query:{businessId:this.business.id}});
			}
		},
		computed:{
			//食品总价格
			totalPrice(){
				let total = 0;
				for(let item of this.foodArr){
					total += item.foodPrice*item.quantity;
				}
				return total;
			},
			//食品总数量
			totalQuantity(){
				let quantity = 0;
				for(let item of this.foodArr){
					quantity += item.quantity;
				}
				return quantity;
			},
			//结算总价格
			totalSettle(){
				return this.totalPrice;
			}
		}
	}
</script>

<style scoped>
	/****************** 总容器 ******************/
	.wrapper{
		width: 100%;
		height: 100%;
	}
	
	/****************** header部分 ******************/
	.wrapper header{
		width: 100%;
		height: 12vw;
		background-color: #0097FF;
		color: #fff;
		font-size: 4.8vw;
		
		position: fixed;
		left: 0;
		top: 0;
		z-index: 1000;
		
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	/****************** 商家logo部分 ******************/
	.wrapper .business-logo{
		width: 100%;
		height: 35vw;
		/*使用上外边距避开header部分*/
		margin-top: 12vw;
		
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.wrapper .business-logo img{
		width: 40vw;
		height: 30vw;
		border-radius: 5px;
	}
	
	/****************** 商家信息部分 ******************/
	.wrapper .business-info{
		width: 100%;
		height: 20vw;
		
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
	.wrapper .business-info h1{
		font-size: 5vw;
	}
	.wrapper .business-info p{
		font-size: 3vw;
		color: #666;
		margin-top: 1vw;
	}
	
	/****************** 食品列表部分 ******************/
	.wrapper .food{
		width: 100%;
		/*使用下外边距避开footer部分*/
		margin-bottom: 14vw;
	}
	.wrapper .food li{
		width: 100%;
		box-sizing: border-box;
		padding: 2.5vw;
		user-select: none;
		
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.wrapper .food li .food-left{
		display: flex;
		align-items: center;
	}
	.wrapper .food li .food-left img{
		width: 20vw;
		height: 20vw;
	}
	.wrapper .food li .food-left .food-left-info{
		margin-left: 3vw;
	}
	.wrapper .food li .food-left .food-left-info h3{
		font-size: 3.8vw;
		color: #555;
	}
	.wrapper .food li .food-left .food-left-info p{
		font-size: 3vw;
		color: #888;
		margin-top: 2vw;
	}
	.wrapper .food li .food-right{
		width: 16vw;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.wrapper .food li .food-right .fa-minus-circle{
		font-size: 5.5vw;
		color: #999;
		cursor: pointer;
	}
	.wrapper .food li .food-right p{
		font-size: 3.6vw;
		color: #333;
	}
	.wrapper .food li .food-right .fa-plus-circle{
		font-size: 5.5vw;
		color: #0097EF;
		cursor: pointer;
	}
	
	/****************** 购物车部分 ******************/
	.wrapper .cart{
		width: 100%;
		height: 14vw;
		
		position: fixed;
		left: 0;
		bottom: 0;
		
		display: flex;
	}
	.wrapper .cart .cart-left{
		flex: 2;
		background-color: #505051;
		display: flex;
	}
	.wrapper .cart .cart-left .cart-left-icon{
		width: 16vw;
		height: 16vw;
		box-sizing: border-box;
		border: solid 1.6vw #444;
		border-radius: 8vw;
		background-color: #3190E8;
		font-size: 7vw;
		color: #fff;
		
		display: flex;
		justify-content: center;
		align-items: center;
		
		margin-top: -4vw;
		margin-left: 3vw;
		
		position: relative;
	}
	.wrapper .cart .cart-left .cart-left-icon-quantity{
		width: 5vw;
		height: 5vw;
		border-radius: 2.5vw;
		background-color: red;
		color: #fff;
		font-size: 3.6vw;
		
		display: flex;
		justify-content: center;
		align-items: center;
		
		position: absolute;
		right: -1.5vw;
		top: -1.5vw;
	}
	.wrapper .cart .cart-left .cart-left-info p:first-child{
		font-size: 4.5vw;
		color: #fff;
		margin-top: 1vw;
	}
	.wrapper .cart .cart-left .cart-left-info p:last-child{
		font-size: 2.8vw;
		color: #AAA;
	}
	
	.wrapper .cart .cart-right{
		flex: 1;
	}
	/*达到起送费时的样式*/
	.wrapper .cart .cart-right .cart-right-item{
		width: 100%;
		height: 100%;
		background-color: #38CA73;
		color: #fff;
		font-size: 4.5vw;
		font-weight: 700;
		user-select: none;
		cursor: pointer;
		
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	/*不够起送费时的样式（只有背景色和鼠标样式的区别）*/
	/*
	.wrapper .cart .cart-right .cart-right-item{
		width: 100%;
		height: 100%;
		background-color: #535356;
		color: #fff;
		font-size: 4.5vw;
		font-weight: 700;
		user-select: none;
		
		display: flex;
		justify-content: center;
		align-items: center;
	}
	*/
</style>
