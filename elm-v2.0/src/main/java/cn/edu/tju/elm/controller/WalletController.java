package cn.edu.tju.elm.controller;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.model.Wallet;
import cn.edu.tju.elm.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    WalletService walletService;

    @PostMapping("/addWallet")
    public HttpResult<Wallet> addWallet(@RequestBody Wallet wallet) throws Exception {
        return walletService.addWallet(wallet);
    }

    @PostMapping("/setWallet")
    public HttpResult<Wallet> setWallet(@RequestBody Wallet wallet) throws Exception {
        return walletService.setWallet(wallet);
    }

    @GetMapping("/getWallet")
    public HttpResult<Wallet> getWallet(@RequestParam Long userid) throws Exception {
        return walletService.getWallet(userid);
    }

    @PostMapping("/freezeMoney")
    public HttpResult<Wallet> freezeMoney(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.freezeMoney(userid, amount);
    }

    @PostMapping("/unfreezeMoney")
    public HttpResult<Wallet> unfreezeMoney(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.unfreezeMoney(userid, amount);
    }

    @PostMapping("/setVipStatus")
    public HttpResult<Wallet> setVipStatus(@RequestParam Long userid, @RequestParam Boolean isVip, @RequestParam Double overdraftLimit) throws Exception {
        return walletService.setVipStatus(userid, isVip, overdraftLimit);
    }

    @PostMapping("/overdraft")
    public HttpResult<Wallet> overdraft(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.overdraft(userid, amount);
    }

    @PostMapping("/repayOverdraft")
    public HttpResult<Wallet> repayOverdraft(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.repayOverdraft(userid, amount);
    }

    @GetMapping("/calculateInterest")
    public HttpResult<Double> calculateInterest(@RequestParam Long userid) throws Exception {
        return walletService.calculateInterest(userid);
    }

}
