<template>
  <div class="wrapper">
    <!-- 头部标题栏 -->
    <header class="page-header">
      <span class="header-title">我的信息</span>
    </header>

    <div class="function-list">
      <!-- 添加商铺功能项 -->
      <div class="function-item" @click="push">
        <div class="item-icon">
          <i class="fa fa-plus-circle"></i>
        </div>
        <div class="item-content">
          <div class="item-title">添加商铺</div>
          <div class="item-desc">创建新的商铺信息并管理</div>
        </div>
        <div class="item-arrow">
          <i class="fa fa-angle-right"></i>
        </div>
      </div>

      <!-- 预留其他功能项位置（可根据需求扩展） -->
      <div class="function-item placeholder-item">
        <div class="item-icon placeholder-icon">
          <i class="fa fa-cog"></i>
        </div>
        <div class="item-content">
          <div class="item-title placeholder-text">更多功能开发中</div>
          <div class="item-desc placeholder-text">敬请期待...</div>
        </div>
        <div class="item-arrow placeholder-arrow">
          <i class="fa fa-angle-right"></i>
        </div>
      </div>
    </div>



    <!-- 底部组件 -->
    <Footer class="page-footer" />
  </div>
</template>

<script>
import Footer from "@/components/Footer.vue";
import {getSessionStorage} from "@/common";

export default {
  data(){
    return{
      user:getSessionStorage('user')
    }
  },
  name: "PutongPage",
  components: { Footer },
  methods: {
    push() {
      this.$router.push({ path: "/addBusinessPage" });
    }
  }
};
</script>

<style scoped>
/* 基础容器样式 */
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 12vw;
  padding-bottom: 15vw; /* 预留底部组件空间 */
  box-sizing: border-box;
  font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 头部标题栏 */
.page-header {
  width: 100%;
  height: 12vw;
  background-color: #0097FF;
  color: #ffffff;
  font-size: 4.8vw;
  font-weight: 500;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); /* 轻微阴影增强层次感 */
}

/* 功能列表容器 */
.function-list {
  width: 100%;
  padding: 4vw;
  display: flex;
  flex-direction: column;
  gap: 3vw; /* 功能项之间的间距 */
}

/* 功能项基础样式 */
.function-item {
  width: 100%;
  background-color: #ffffff;
  border-radius: 3vw;
  padding: 4vw;
  display: flex;
  align-items: center;
  gap: 3vw;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

/* 功能项图标 */
.item-icon {
  width: 10vw;
  height: 10vw;
  border-radius: 50%;
  background-color: #e8f4ff;
  color: #0097FF;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 5vw;
  flex-shrink: 0; /* 防止图标被压缩 */
}

/* 功能项内容区 */
.item-content {
  flex: 1; /* 占满剩余空间 */
  overflow: hidden; /* 防止文本溢出 */
}

/* 功能项标题 */
.item-title {
  font-size: 4.2vw;
  color: #333333;
  font-weight: 500;
  margin-bottom: 0.5vw;
}

/* 功能项描述 */
.item-desc {
  font-size: 3.2vw;
  color: #666666;
  opacity: 0.8;
  white-space: nowrap; /* 防止文本换行 */
  overflow: hidden;
  text-overflow: ellipsis; /* 文本溢出显示省略号 */
}

/* 功能项箭头 */
.item-arrow {
  color: #0097FF;
  font-size: 4vw;
  flex-shrink: 0;
}

/* 预留功能项样式（灰色占位） */
.placeholder-item {
  cursor: default;
  opacity: 0.6;
}

.placeholder-icon {
  background-color: #f0f0f0;
  color: #999999;
}

.placeholder-text {
  color: #999999;
}

.placeholder-arrow {
  color: #dddddd;
}

/* 底部组件样式 */
.page-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 999; /* 确保在内容之上，但在头部之下 */
}

/* 响应式适配（平板及以上屏幕） */
@media (min-width: 768px) {
  .wrapper {
    padding-top: 80px;
    padding-bottom: 80px;
    max-width: 600px;
    margin: 0 auto; /* 居中显示 */
  }

  .page-header {
    height: 80px;
    font-size: 24px;
  }

  .function-list {
    padding: 20px;
    gap: 15px;
  }

  .function-item {
    border-radius: 15px;
    padding: 20px;
    gap: 15px;
  }

  .item-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .item-title {
    font-size: 18px;
    margin-bottom: 2px;
  }

  .item-desc {
    font-size: 14px;
  }

  .item-arrow {
    font-size: 20px;
  }


}
</style>