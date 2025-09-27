<template>
  <div class="wrapper">
    <!-- 核心头部：保持原有风格 -->
    <header class="page-header">
      <span class="header-title">我的信息</span>
    </header>

    <!-- 第一个框：展示用户名 -->
    <div class="info-box username-box">
      <span class="box-label">用户名：</span>
      <span class="box-content">{{ user.username || '未设置用户名' }}</span>
    </div>

    <!-- 第二个框：新增电话展示（使用user.phone） -->
    <div class="info-box phone-box">
      <span class="box-label">注册电话：</span>
      <!-- 手机号脱敏处理：中间4位替换为*，保护隐私 -->
      <span class="box-content">{{ (user.phone) || '未绑定手机号' }}</span>
    </div>

    <!-- 保留底部组件 -->
    <Footer class="page-footer" />
  </div>
</template>

<script>
import Footer from "@/components/Footer.vue";
import { getSessionStorage, removeSessionStorage } from "@/common";

export default {
  name: "MyPage",
  components: { Footer },
  data() {
    return {
      // 解析用户信息，兼容字符串/对象格式
      user: this.parseUserInfo(getSessionStorage('user'))
    };
  },
  methods: {
    // 解析sessionStorage中的用户信息，处理序列化问题
    parseUserInfo(userData) {
      if (!userData) return {};
      return typeof userData === 'string' ? JSON.parse(userData) : userData;
    },
    // 手机号脱敏格式化：138****5678 格式
    formatPhone(phone) {
      if (!phone || phone.length !== 11) return phone; // 非11位手机号不处理
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },
    // 保留退出登录功能（后续可在头部添加按钮触发）
    logout() {
      if (confirm("确定退出登录吗？")) {
        removeSessionStorage("auth") || sessionStorage.removeItem("auth");
        removeSessionStorage("user") || sessionStorage.removeItem("user");
        this.$router.push({ path: "/login" });
      }
    }
  }
};
</script>

<style scoped>
/* 基础容器：控制整体间距与排列 */
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 12vw; /* 头部空间 */
  padding-bottom: 15vw; /* 底部空间 */
  padding-left: 4vw;
  padding-right: 4vw;
  box-sizing: border-box;
  font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  /* 垂直排列两个框，控制间距 */
  display: flex;
  flex-direction: column;
  gap: 3vw; /* 两个框之间的间距 */
}

/* 头部：居中显示标题，保持视觉统一 */
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
  padding: 0 4vw;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 通用信息框样式：两个框保持统一视觉风格 */
.info-box {
  width: 100%;
  height: 15vw;
  background-color: #ffffff;
  border-radius: 4vw;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center; /* 文字垂直居中 */
  padding-left: 6vw; /* 左侧内边距，避免文字贴边 */
  box-sizing: border-box;
}

/* 框内标签样式：次要信息，浅色调区分 */
.box-label {
  font-size: 3.8vw;
  color: #666666;
  font-weight: 500;
  margin-right: 2vw; /* 与内容保持间距 */
}

/* 框内内容样式：统一突出显示，与头部色调呼应 */
.box-content {
  font-size: 4.2vw;
  color: #0097FF; /* 主色调呼应头部，增强整体感 */
  font-weight: 600;
  /* 处理过长内容：换行显示，限制宽度 */
  white-space: normal;
  line-height: 1.4;
  max-width: 60%;
}

/* 底部组件：固定定位，不遮挡内容 */
.page-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 999;
}

/* 响应式适配（平板及以上屏幕） */
@media (min-width: 768px) {
  .wrapper {
    padding-top: 80px;
    padding-bottom: 80px;
    padding-left: 20px;
    padding-right: 20px;
    max-width: 600px;
    margin: 0 auto; /* 页面整体居中 */
    gap: 20px; /* 大屏下两个框间距调整为20px */
  }

  .page-header {
    height: 80px;
    font-size: 24px;
    padding: 0 20px;
  }

  .info-box {
    height: 70px;
    border-radius: 20px;
    padding-left: 30px; /* 大屏左侧内边距调整 */
  }

  .box-label {
    font-size: 16px;
    margin-right: 10px;
  }

  .box-content {
    font-size: 18px;
    max-width: 70%;
  }
}
</style>