<template>
  <!-- 总容器 -->
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p class="left">我的钱包</p>
      <div class="right" @click="toWalletDetail">
        <i class="fa fa-list-ul"></i>
        明细
      </div>
    </header>
    <div class="wallet-box">
      <!-- 钱包内容 -->
      <div class="wallet">
        <!-- 显示余额 -->
        <div class="balance-box">
          <h3>余额(元)</h3>
          <p>{{balance}}</p>
        </div>
        <div class="rechargeAndWithdraw">
          <!-- 充值 -->
          <button class="recharge" @click="showRechargeModal">
            充值
          </button>
          <!-- 提现 -->
          <button class="withdraw" @click="showWithdrawModal">
            提现
          </button>
        </div>
      </div>
    </div>

    <!-- 交易记录列表 -->
    <div class="transaction-list">
      <h3>交易记录</h3>
      <ul class="transactions">
        <li v-for="item in transactionArr" :key="item.transactionId">
          <div class="transaction-left">
            <p class="transaction-type">{{item.type === 'recharge' ? '充值' : '提现'}}</p>
            <p class="transaction-time">{{item.createTime}}</p>
          </div>
          <div class="transaction-right">
            <p :class="['transaction-amount', item.type === 'recharge' ? 'income' : 'expense']">
              {{item.type === 'recharge' ? '+' : '-'}}{{item.amount}}元
            </p>
          </div>
        </li>
      </ul>
    </div>

    <!-- 充值弹窗 -->
    <div class="modal-overlay" v-if="showRecharge" @click="closeRechargeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>充值</h3>
          <button class="close-btn" @click="closeRechargeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>选择充值金额</label>
            <div class="recharge-options">
              <div v-for="option in rechargeOptions" :key="option.amount"
                   class="recharge-option"
                   :class="{ active: selectedRecharge === option }"
                   @click="selectRecharge(option)">
                <div class="amount">¥{{option.amount}}</div>
                <div class="bonus" v-if="option.bonus > 0">赠送¥{{option.bonus}}</div>
                <div class="total">到账¥{{option.total}}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="confirmRecharge" :disabled="!selectedRecharge">确认充值</button>
        </div>
      </div>
    </div>

    <!-- 提现弹窗 -->
    <div class="modal-overlay" v-if="showWithdraw" @click="closeWithdrawModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>提现</h3>
          <button class="close-btn" @click="closeWithdrawModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>提现金额</label>
            <div class="amount-input">
              <span>¥</span>
              <input type="number" v-model="withdrawAmount" placeholder="请输入金额" @input="validateWithdrawAmount">
            </div>
            <div class="balance-info">当前账户余额：{{balance}}元</div>
            <div class="fee-info" v-if="withdrawAmount && parseFloat(withdrawAmount) > 0">
              手续费：¥{{withdrawFee}}（{{feeRate}}%）
            </div>
            <div class="actual-info" v-if="withdrawAmount && parseFloat(withdrawAmount) > 0">
              实际到账：¥{{actualAmount}}
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="confirmWithdraw" :disabled="!canWithdraw">确认提现</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default{
  name: 'MyWallet',
  data() {
    return {
      walletId: 10010,
      user: {},
      balance: 0,
      transactionArr: [],
      // 弹窗控制
      showRecharge: false,
      showWithdraw: false,
      // 充值相关
      selectedRecharge: null,
      rechargeOptions: [
        { amount: 30, bonus: 3, total: 33 },
        { amount: 68, bonus: 8, total: 76 },
        { amount: 128, bonus: 18, total: 146 },
        { amount: 328, bonus: 58, total: 386 },
        { amount: 648, bonus: 128, total: 776 }
      ],
      // 提现相关
      withdrawAmount: '',
      feeRate: 2, // 2%手续费
      // 银行卡
      selectedBankCard: null
    }
  },
  created() {
    this.user = this.getSessionStorage('user');
    // 加载选中的银行卡
    this.selectedBankCard = this.getLocalStorage(this.walletId);
    // 根据walletId查询balance
    this.axios.get('VirtualWallet/WalletId', {
      params: {
        walletId: this.walletId
      }
    }).then(response => {
      //判断是否登录
      if (this.user != null) {
        // this.walletId = response.data.result.walletId;
        this.balance = response.data.result;
      }
    }).catch(error => {
      console.error(error);
    });

    // 获取交易记录
    this.getTransactions();
  },
  activated() {
    // 页面激活时刷新余额和交易记录
    this.axios.get('VirtualWallet/WalletId', {
      params: {
        walletId: this.walletId
      }
    }).then(response => {
      if (this.user != null) {
        this.balance = response.data.result;
      }
    }).catch(error => {
      console.error(error);
    });
    this.getTransactions();
    // 重新加载银行卡信息
    this.selectedBankCard = this.getLocalStorage(this.walletId);
  },
  computed: {
    canRecharge() {
      return this.selectedRecharge !== null;
    },
    canWithdraw() {
      return this.withdrawAmount &&
          parseFloat(this.withdrawAmount) > 0 &&
          parseFloat(this.withdrawAmount) <= this.balance;
    },
    withdrawFee() {
      if (!this.withdrawAmount || parseFloat(this.withdrawAmount) <= 0) return 0;
      const fee = parseFloat(this.withdrawAmount) * this.feeRate / 100;
      return fee.toFixed(2);
    },
    actualAmount() {
      if (!this.withdrawAmount || parseFloat(this.withdrawAmount) <= 0) return 0;
      const actual = parseFloat(this.withdrawAmount) - parseFloat(this.withdrawFee);
      return actual.toFixed(2);
    }
  },
  methods: {
    getTransactions() {
      // 使用新的交易接口获取交易记录
      this.axios.post('transaction/getTransaction', this.qs.stringify({
        walletId: this.walletId
      })).then(response => {
        if (response.data.success) {
          this.transactionArr = response.data.data || [];
        }
      }).catch(error => {
        console.error('获取交易记录失败:', error);
      });
    },
    toWalletDetail(){
      this.router.push({
        path: '/walletDetail'
      });
    },
    // 弹窗控制方法
    showRechargeModal() {
      this.showRecharge = true;
    },
    closeRechargeModal() {
      this.showRecharge = false;
      this.selectedRecharge = null;
    },
    showWithdrawModal() {
      this.showWithdraw = true;
    },
    closeWithdrawModal() {
      this.showWithdraw = false;
      this.withdrawAmount = '';
    },
    // 选择充值选项
    selectRecharge(option) {
      this.selectedRecharge = option;
    },
    // 金额验证
    validateWithdrawAmount() {
      const value = this.withdrawAmount;
      const regex = /^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/;
      if (!regex.test(value) && value !== '') {
        this.withdrawAmount = value.slice(0, -1);
      }
    },
    // 确认充值
    confirmRecharge() {
      if (!this.selectedRecharge) {
        alert('请选择充值金额！');
        return;
      }

      const rechargeAmount = this.selectedRecharge.amount;
      const actualAmount = this.selectedRecharge.total;

      // 添加交易记录
      this.axios.post('transaction/addTransaction', this.$qs.stringify({
        fromwallet: this.walletId,
        type: '1',
        money: actualAmount,
      })).then(response => {

      }).catch(error => {
        console.error('添加交易记录失败:', error);
        alert('添加交易记录失败！');
      });
    },
    // 确认提现
    confirmWithdraw() {
      if (!this.withdrawAmount || parseFloat(this.withdrawAmount) <= 0) {
        alert('请输入正确的提现金额！');
        return;
      }
      if (parseFloat(this.withdrawAmount) > this.balance) {
        alert('余额不足！');
        return;
      }

      const withdrawAmount = parseFloat(this.withdrawAmount);
      const fee = parseFloat(this.withdrawFee);
      const actualAmount = parseFloat(this.actualAmount);

      // 添加交易记录
      this.axios.post('transaction/addTransaction', this.qs.stringify({
        walletId: this.walletId,
        type: 'withdraw',
        amount: withdrawAmount,
      })).then(response => {
        if (response.data.success) {
          // 执行提现操作（扣除总金额包括手续费）
          this.axios.post('VirtualWallet/FromWalletId', this.qs.stringify({
            walletId: 10010,
            amount: withdrawAmount
          })).then(walletResponse => {
            if (walletResponse.data.result == 1) {
              alert(`提现成功！提现¥${withdrawAmount}，手续费¥${fee}，实际到账¥${actualAmount}`);
              // 更新余额（扣除总金额）
              this.balance -= withdrawAmount;
              // 刷新交易记录
              this.getTransactions();
              // 关闭弹窗
              this.closeWithdrawModal();
            } else {
              alert('提现失败！');
            }
          }).catch(error => {
            console.error('提现操作失败:', error);
            alert('提现操作失败！');
          });
        } else {
          alert('添加交易记录失败！');
        }
      }).catch(error => {
        console.error('添加交易记录失败:', error);
        alert('添加交易记录失败！');
      });
    }
  }
}
</script>

<style scoped>
/* 总容器 */
.wrapper {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
}

/* 头部 */
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #00abf5;
  color: #fff;


  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper header .left {
  flex: 3;
  margin-left: 8vw;
  font-size: 4.8vw;
}

.wrapper header .right {
  margin-right: 4vw;
  font-size: 2.5vw;
}

.wrapper header .right .fa-list-ul {
  margin-right: 1vw;
}

/* 钱包内容 */
.wrapper .wallet-box {
  width: 90%;
  height: 50vw;
  margin: 12vw 4vw 0 4vw;
  background-color: #f5f5f5;
  padding-top: 6vw;
}

.wrapper .wallet-box .wallet {
  width: 100%;
  height: 100%;
  background-color: #fff;
  border-radius: 15px;
  box-sizing: border-box;
  padding: 2vw 4vw;
  display: flex;
  flex-direction: column;
}

.wrapper .wallet-box .wallet .balance-box h3 {
  margin: 3vw 0 5vw 0;

  font-size: 4.5vw;
  font-weight: 400;
  color: #7f7f7f;
}

.wrapper .wallet-box .wallet .balance-box p {
  margin-bottom: 5vw;
  font-size: 8vw;
  font-weight: 600;
}

.wrapper .wallet-box .wallet .rechargeAndWithdraw {
  margin: 4vw 2vw;
  display: flex;
  align-content: center;
  justify-content: center;
  box-sizing: border-box;
}

.wrapper .wallet-box .wallet .rechargeAndWithdraw .recharge {
  width: 45%;
  height: 10vw;
  border: none;
  outline: none;
  border-radius: 6px;
  font-size: 4.5vw;
  font-weight: 500;
  background-color: #00abf5;
  color: #fff;
  margin-right: 4vw;
}

.wrapper .wallet-box .wallet .rechargeAndWithdraw .withdraw {
  width: 45%;
  height: 10vw;
  border: none;
  outline: none;
  border-radius: 6px;
  font-size: 4.5vw;
  font-weight: 500;
  background-color: #00abf5;
  color: #fff;
  margin-left: 4vw;
}

/* 交易记录列表 */
.wrapper .transaction-list {
  width: 90%;
  margin: 4vw auto;
  background-color: #fff;
  border-radius: 10px;
  padding: 4vw;
  box-sizing: border-box;
}

.wrapper .transaction-list h3 {
  font-size: 4.5vw;
  margin-bottom: 3vw;
  color: #333;
}

.wrapper .transaction-list .transactions {
  list-style: none;
  padding: 0;
  margin: 0;
}

.wrapper .transaction-list .transactions li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 3vw 0;
  border-bottom: 1px solid #f0f0f0;
}

.wrapper .transaction-list .transactions li:last-child {
  border-bottom: none;
}

.wrapper .transaction-list .transaction-left {
  flex: 1;
}

.wrapper .transaction-list .transaction-type {
  font-size: 4vw;
  font-weight: 500;
  color: #333;
  margin-bottom: 1vw;
}

.wrapper .transaction-list .transaction-time {
  font-size: 3vw;
  color: #999;
}

.wrapper .transaction-list .transaction-right {
  text-align: right;
}

.wrapper .transaction-list .transaction-amount {
  font-size: 4.5vw;
  font-weight: 600;
}

.wrapper .transaction-list .transaction-amount.income {
  color: #38CA73;
}

.wrapper .transaction-list .transaction-amount.expense {
  color: #ff4444;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.close-btn {
  font-size: 24px;
  color: #999;
  cursor: pointer;
  border: none;
  background: none;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.bank-card-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  background-color: #f9f9f9;
  cursor: pointer;
}

.amount-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  text-align: center;
}

.amount-input:focus {
  border-color: #1890ff;
  outline: none;
}

.confirm-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(to right, #ff6b6b, #ff8e8e);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 20px;
}

.confirm-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.confirm-btn:not(:disabled):hover {
  background: linear-gradient(to right, #ff5252, #ff7979);
}

/* 充值选项样式 */
.recharge-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 10px;
}

.recharge-option {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
}

.recharge-option:hover {
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(24, 144, 255, 0.2);
}

.recharge-option.active {
  border-color: #1890ff;
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.recharge-option .amount {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.recharge-option .bonus {
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 500;
  margin-bottom: 3px;
}

.recharge-option .total {
  font-size: 14px;
  color: #52c41a;
  font-weight: 600;
}

/* 手续费信息样式 */
.fee-info {
  margin-top: 10px;
  padding: 8px 12px;
  background: #fff2e8;
  border: 1px solid #ffd591;
  border-radius: 6px;
  font-size: 14px;
  color: #d46b08;
}

.actual-info {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 6px;
  font-size: 14px;
  color: #389e0d;
  font-weight: 600;
}
</style>