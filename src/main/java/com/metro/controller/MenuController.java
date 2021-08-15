package com.metro.controller;

import com.metro.model.exceptions.InsufficientBalanceException;
import com.metro.model.exceptions.InvalidStationException;
import com.metro.model.exceptions.InvalidSwipeInException;
import com.metro.model.exceptions.InvalidSwipeOutException;
import com.metro.model.pojos.Transaction;
import com.metro.model.service.card.CardService;

import com.metro.model.service.station.StationService;
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
    StationService stationService;

    @Autowired
    @Qualifier("stationService")
    public void setStationService(StationService stationService) {
        this.stationService = stationService;
    }

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

    @RequestMapping("swipeIn")
    public ModelAndView swipeInController() {
        ModelAndView modelAndView = new ModelAndView("metroSwipeIn", "cardId", cardService.getCurrentCardId());
        modelAndView.addObject("card", cardService.getCardDetails(cardService.getCurrentCardId()));
        modelAndView.addObject("stations", stationService.getAllStations());
        return modelAndView;
    }


    @RequestMapping("cardSwipeIn")
    public ModelAndView cardSwipeInController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("metroSwipeInOutput");
        String swipeInStation = request.getParameter("swipeInStation");
        try {
            String sourceStation = transactionService.swipeIn(cardService.getCurrentCardId(),Integer.parseInt(swipeInStation));
            if (sourceStation != null) {
                modelAndView.addObject("message", " Swipe In Successful At Station " + sourceStation);
                modelAndView.setViewName("metroSwipeInOutput");
            } else return new ModelAndView("metroSwipeInOutput","message", "Swipe In Failed, Try Again");
        } catch (InsufficientBalanceException | InvalidStationException | InvalidSwipeInException e) {

            modelAndView.addObject("message", e.getMessage());
            modelAndView.setViewName("metroSwipeInOutput");
            return modelAndView;
        }
        return modelAndView;
    }


    @RequestMapping("swipeOut")
    public ModelAndView swipeOutController() {
        ModelAndView modelAndView = new ModelAndView("metroSwipeOut", "cardId", cardService.getCurrentCardId());
        modelAndView.addObject("card", cardService.getCardDetails(cardService.getCurrentCardId()));
        modelAndView.addObject("stations", stationService.getAllStations());
        return modelAndView;
    }


    @RequestMapping("cardSwipeOut")
    public ModelAndView cardSwipeOutController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("metroSwipeOutOutput");
        String swipeOutStation = request.getParameter("swipeOutStation");
        try {
            Transaction transaction = transactionService.swipeOut(cardService.getCurrentCardId(),Integer.parseInt(swipeOutStation));
            if (transaction != null) {
                modelAndView.addObject("message", " Swipe Out Successful  " );
                modelAndView.addObject("transaction", transaction );
                modelAndView.setViewName("metroSwipeOutOutput");
            } else return new ModelAndView("metroSwipeOutOutput","message", "Swipe Out Failed, Try Again");
        } catch (InvalidStationException | InvalidSwipeOutException e) {

            modelAndView.addObject("message", e.getMessage());
            modelAndView.setViewName("metroSwipeOutOutput");
            return modelAndView;
        }
        return modelAndView;
    }



}
