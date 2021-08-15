package com.metro.controller;

import com.metro.model.service.card.CardService;

import com.metro.model.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MenuController {
    CardService cardService;
    TransactionService transactionService;

    @Autowired
    @Qualifier("transactionService")
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    @Qualifier("cardService")
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @RequestMapping("menu")
    public ModelAndView menuController() {
        return new ModelAndView("metroMenu", "cardId", cardService.getCurrentCardId());
    }

    @RequestMapping("getCard")
    public ModelAndView cardController() {
        ModelAndView modelAndView = new ModelAndView("cardDetails","cardId", cardService.getCurrentCardId());
        modelAndView.addObject("card",cardService.getCardDetails(cardService.getCurrentCardId()));
        return modelAndView;
    }

    @RequestMapping("getTransactions")
    public ModelAndView transactionController() {
        ModelAndView modelAndView = new ModelAndView("metroTransactions");
        modelAndView.addObject("transactions",transactionService.getAllTransactions(cardService.getCurrentCardId()));
        return modelAndView;
    }

    @RequestMapping("rechargeCard")
    public ModelAndView rechargeController() {
        ModelAndView modelAndView = new ModelAndView("cardRecharge","cardId", cardService.getCurrentCardId());
        modelAndView.addObject("card",cardService.getCardDetails(cardService.getCurrentCardId()));
        return modelAndView;
    }

    @RequestMapping("topUpCard")
    public ModelAndView topUpController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("cardRechargeOutput");
        String amount = request.getParameter("amount");
        if (amount.matches("[0-9]+")) {
            int intAmount = Integer.parseInt(amount);
            if (0 < intAmount && intAmount <= 1000) {
                if(cardService.rechargeCard(cardService.getCurrentCardId(),intAmount)){
                    modelAndView.addObject("message", " Recharge Successful, Current Balance is :" + cardService.getBalance(cardService.getCurrentCardId()));
                    modelAndView.setViewName("cardRechargeOutput");
                    return modelAndView;
                } else return new ModelAndView("cardRechargeOutput","message", "Recharge Failed, Try Again");
            } else return new ModelAndView("cardRechargeOutput","message", "Invalid Range, Permitted Range [1 to 1000]  ");
        } else return new ModelAndView("cardRechargeOutput","message", "Invalid Amount");
    }

}
