<template>
  <div class="wrapper">
    <!-- 后退按钮 -->
    <div class="back-btn" @click="back" aria-label="返回">
      <i class="fa fa-arrow-left"></i>
    </div>

    <!-- 页面标题 -->
    <header>
      <p>管理商家</p>
    </header>

    <!-- 功能卡片容器 -->
    <div class="card-container">
      <!-- 新增商家卡片 -->
      <div class="功能-card" @click="toBusiness">
        <div class="card-icon">
          <i class="fa fa-building"></i>
        </div>
        <div class="card-title">新增商家</div>
        <div class="card-desc">创建并管理新的商家信息</div>
      </div>

      <!-- 新增食品卡片 -->
      <div class="功能-card" @click="toFood">
        <div class="card-icon">
          <i class="fa fa-cutlery"></i>
        </div>
        <div class="card-title">新增食品</div>
        <div class="card-desc">为商家添加新的食品品类</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AddBusinessPage",
  methods: {
    back() {
      this.$router.back();
    },
    toBusiness() {
      this.$router.push({path: "/addBusiness"})
    },
    toFood() {
      this.$router.push({path: "/addFood"})
    }
  }
}
</script>

<style scoped>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 12vw;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 头部样式 */
header {
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
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 后退按钮 - 移除过渡动画，保留基础样式 */
.back-btn {
  cursor: pointer;
  width: 8vw;
  height: 8vw;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  left: 4vw;
  top: 2vw;
  z-index: 1001;
  color: white;
  font-size: 5vw;
}

/* 卡片容器 */
.card-container {
  width: 100%;
  padding: 8vw;
  display: flex;
  flex-direction: column;
  gap: 6vw;
}

/* 功能卡片样式 - 移除过渡、伪元素动画，保留静态视觉 */
.功能-card {
  background-color: white;
  border-radius: 4vw;
  padding: 6vw;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  border: 1px solid transparent;
}

/* 移除卡片悬停动态效果（上浮、阴影变化、边框高亮） */
.功能-card:hover {
  border-color: rgba(0, 151, 255, 0.1); /* 仅保留轻微边框高亮，无动态变化 */
}

/* 卡片图标 - 移除过渡动画，固定样式 */
.card-icon {
  width: 16vw;
  height: 16vw;
  border-radius: 50%;
  background-color: #e8f4ff;
  color: #0097FF;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 8vw;
  margin-bottom: 4vw;
}

/* 移除图标悬停动态变化 */
.功能-card:hover .card-icon {
  background-color: #e8f4ff; /* 固定背景色，不随hover变化 */
  color: #0097FF; /* 固定图标色，不随hover变化 */
}

/* 卡片标题 */
.card-title {
  font-size: 5vw;
  font-weight: 600;
  color: #333;
  margin-bottom: 2vw;
}

/* 卡片描述 */
.card-desc {
  font-size: 3.5vw;
  color: #666;
  opacity: 0.8;
}

/* 适配不同屏幕尺寸 */
@media (min-width: 768px) {
  .wrapper {
    padding-top: 80px;
  }

  header {
    height: 80px;
    font-size: 24px;
  }

  .back-btn {
    width: 50px;
    height: 50px;
    font-size: 28px;
    top: 15px;
    left: 20px;
  }

  .card-container {
    padding: 40px;
    gap: 30px;
    max-width: 800px;
    margin: 0 auto;
  }

  .功能-card {
    padding: 30px;
    border-radius: 20px;
  }

  .card-icon {
    width: 80px;
    height: 80px;
    font-size: 40px;
    margin-bottom: 20px;
  }

  .card-title {
    font-size: 24px;
    margin-bottom: 10px;
  }

  .card-desc {
    font-size: 16px;
  }
}
</style>