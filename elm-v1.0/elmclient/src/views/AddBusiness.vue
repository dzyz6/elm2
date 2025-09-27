<template>
  <div class="wrapper">
    <!-- 后退按钮 -->
    <div class="back-btn" @click="back" aria-label="返回">
      <i class="fa fa-arrow-left"></i>
    </div>

    <!-- 页面标题 -->
    <header class="page-header">
      <p>新增商家</p>
    </header>

    <!-- 表单容器 -->
    <div class="form-container">
      <!-- 商家名称输入项 -->
      <div class="form-item">
        <label class="form-label">商家名称</label>
        <input
            class="form-input"
            v-model="businessName"
            placeholder="请输入商家名称"
            maxlength="50"
        >
      </div>

      <!-- 商家地址输入项 -->
      <div class="form-item">
        <label class="form-label">商家地址</label>
        <input
            class="form-input"
            v-model="businessAddress"
            placeholder="请输入详细地址"
            maxlength="100"
        >
      </div>

      <!-- 商家简介输入项 -->
      <div class="form-item">
        <label class="form-label">商家简介</label>
        <textarea
            class="form-textarea"
            v-model="businessExplain"
            placeholder="请简要介绍商家（如特色、营业时间等）"
            maxlength="200"
            rows="3"
        ></textarea>
      </div>

      <!-- 起送费输入项（小尺寸） -->
      <div class="form-item">
        <label class="form-label">起送费</label>
        <input
            class="form-input form-input-small"
            v-model="minDeliveryFee"
            placeholder="请输入起送费（如：10）"
            maxlength="10"
            type="number"
            step="0.01"
        >
      </div>

      <!-- 配送费输入项（小尺寸） -->
      <div class="form-item">
        <label class="form-label">配送费</label>
        <input
            class="form-input form-input-small"
            v-model="deliveryFee"
            placeholder="请输入配送费（如：3）"
            maxlength="10"
            type="number"
            step="0.01"
        >
      </div>
    </div>

    <!-- 提交按钮 -->
    <button class="submit-btn" @click="submit">
      提交
    </button>
  </div>
</template>

<script>
export default {
  name: "AddBusiness",
  data() {
    return {
      businessName: "",
      businessAddress: "",
      businessExplain: "",
      minDeliveryFee: "", // 新增起送费绑定字段
      deliveryFee: ""     // 新增配送费绑定字段
    };
  },
  methods: {
    back() {
      this.$router.back();
    },

    async submit(){
      // 补充起送费、配送费的非空校验
      if(this.businessName==''){
        alert('名称不能为空！');
        return;
      }
      if(this.businessAddress==''){
        alert('地址不能为空！');
        return;
      }
      if(this.businessExplain==''){
        alert('简介不能为空！');
        return;
      }
      if(this.minDeliveryFee==''){
        alert('起送费不能为空！');
        return;
      }
      if(this.deliveryFee==''){
        alert('配送费不能为空！');
        return;
      }
      // 校验费用为数字（避免输入非数字内容）
      if(isNaN(Number(this.minDeliveryFee))){
        alert('起送费请输入数字！');
        return;
      }
      if(isNaN(Number(this.deliveryFee))){
        alert('配送费请输入数字！');
        return;
      }

      await this.$axios.post('api/businesses',{
        "businessName":this.businessName,
        "businessAddress":this.businessAddress,
        "businessExplain":this.businessExplain,
        "startPrice":Number(this.minDeliveryFee), // 转为数字传给后端
        "deliveryPrice":Number(this.deliveryFee)       // 转为数字传给后端
      }).then(response=>{
        alert("成功")
      }).catch(error=>{
        alert('请求失败：' + (error.response?.data?.message || error.message));
        console.error(error);
      });

      this.$router.go(-1)
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
  padding-bottom: 15vw; /* 预留提交按钮空间 */
  box-sizing: border-box;
  font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 后退按钮 */
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

/* 页面标题栏 */
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 表单容器 */
.form-container {
  width: 100%;
  padding: 6vw 5vw;
  display: flex;
  flex-direction: column;
  gap: 6vw; /* 输入项之间的间距 */
}

/* 表单输入项 */
.form-item {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.5vw; /* 标签与输入框的间距 */
}

/* 表单标签 */
.form-label {
  font-size: 3.8vw;
  color: #333333;
  font-weight: 500;
}

/* 文本输入框（默认尺寸） */
.form-input {
  width: 100%;
  height: 11vw;
  padding: 0 3vw;
  border: 1px solid #dddddd;
  border-radius: 2vw;
  font-size: 3.5vw;
  color: #333333;
  box-sizing: border-box;
  outline: none; /* 移除默认外边框 */
}

/* 后面两个输入框（起送费、配送费）的小尺寸样式 */
.form-input-small {
  width: 60%; /* 宽度缩小为60%，比其他输入框窄 */
  height: 9vw; /* 高度略微减小 */
  font-size: 3.2vw; /* 字体稍小 */
  padding: 0 2.5vw; /* 内边距同步调整 */
}

/* 输入框占位符样式 */
.form-input::placeholder,
.form-textarea::placeholder {
  color: #bbbbbb;
  font-size: 3.2vw;
}
/* 小尺寸输入框的占位符字体同步缩小 */
.form-input-small::placeholder {
  font-size: 3vw;
}

/* 文本域（简介输入） */
.form-textarea {
  width: 100%;
  padding: 3vw;
  border: 1px solid #dddddd;
  border-radius: 2vw;
  font-size: 3.5vw;
  color: #333333;
  box-sizing: border-box;
  outline: none;
  resize: none; /* 禁止拉伸文本域 */
}

/* 提交按钮 */
.submit-btn {
  width: 80%;
  height: 12vw;
  background-color: #0097FF;
  color: white;
  border: none;
  border-radius: 6vw;
  font-size: 4.2vw;
  font-weight: 500;
  cursor: pointer;
  position: fixed;
  bottom: 5vw;
  left: 10%; /* 水平居中 */
  box-shadow: 0 4px 8px rgba(0, 151, 255, 0.2);
  outline: none;
}

/* 适配平板及以上屏幕 */
@media (min-width: 768px) {
  .wrapper {
    padding-top: 80px;
    padding-bottom: 80px;
    max-width: 600px;
    margin: 0 auto; /* 页面居中 */
  }

  .back-btn {
    width: 50px;
    height: 50px;
    font-size: 28px;
    top: 15px;
    left: 20px;
  }

  .page-header {
    height: 80px;
    font-size: 24px;
  }

  .form-container {
    padding: 30px 25px;
    gap: 30px;
  }

  .form-item {
    gap: 8px;
  }

  .form-label {
    font-size: 18px;
  }

  /* 默认尺寸输入框（PC端） */
  .form-input {
    height: 50px;
    padding: 0 15px;
    border-radius: 8px;
    font-size: 16px;
  }

  /* 小尺寸输入框（PC端） */
  .form-input-small {
    width: 40%; /* PC端宽度进一步缩小为40%，比例更协调 */
    height: 42px; /* 高度同步减小 */
    font-size: 15px;
    padding: 0 12px;
  }

  /* 占位符适配（PC端） */
  .form-input::placeholder,
  .form-textarea::placeholder {
    font-size: 14px;
  }
  .form-input-small::placeholder {
    font-size: 13px;
  }

  .form-textarea {
    padding: 15px;
    border-radius: 8px;
    font-size: 16px;
    rows: 3;
  }

  .submit-btn {
    width: 80%;
    height: 50px;
    border-radius: 25px;
    font-size: 18px;
    bottom: 30px;
  }
}
</style>