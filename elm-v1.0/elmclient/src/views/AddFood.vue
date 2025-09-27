<script>
export default {
  name: "AddFood",

  methods:{
    back(){
      this.$router.back();
    }
  }
}
</script>

<template>
  <div class="wrapper">
    <div class="b" @click="back">
      <
    </div>
    <header>
      <p>管理商家</p>
    </header>

    <ul>
      <li>
        <div>
          新增商家
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.wrapper {
  width: 100%;
  height: 100%;
}

.wrapper header {
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

.b{
  cursor: pointer;
  font-size: 30px;
  position: fixed;
  left: 20px;
  top: 20px;
  z-index: 1001;
}
</style>