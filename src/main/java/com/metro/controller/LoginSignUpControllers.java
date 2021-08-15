package com.metro.controller;

import com.metro.model.pojos.Card;
import com.metro.model.service.card.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginSignUpControllers {
    CardService cardService;

    @Autowired
    @Qualifier("cardService")
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @RequestMapping("login")
    public ModelAndView loginController() {
        return new ModelAndView("metroLogin");
    }

    @RequestMapping("signup")
    public ModelAndView signupController() {
        return new ModelAndView("metroSignUp");
    }


    @RequestMapping("verifyCard")
    public ModelAndView verifyController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        String cardId = request.getParameter("card");
        String password = request.getParameter("password");
        if (cardId.matches("[0-9]+")) {
            int intCardId = Integer.parseInt(cardId);
            if (cardService.isACard(intCardId)) {
                if (cardService.validatePassword(intCardId, password)) {
                    cardService.setCurrentCardId(intCardId);
                    modelAndView.addObject("cardId", cardId);
                    modelAndView.addObject("message", "You Are Logged In");
                    modelAndView.setViewName("metroMenu");
                    return modelAndView;
                } else return new ModelAndView("metroLoginOutput","message", "Invalid Password, Try Again");
            } else return new ModelAndView("metroLoginOutput","message", "Invalid Card");
        } else return new ModelAndView("metroLoginOutput","message", "Card ID Only Contains Integers");

    }


    @RequestMapping("createCard")
    public ModelAndView createController(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
            String initialBalance = request.getParameter("cardBalance");
        if (initialBalance.matches("[0-9]+")) {
            if (Integer.parseInt(initialBalance) >= 100) {
                String passwordOne = request.getParameter("initialPassword");
                String passwordTwo = request.getParameter("conformationPassword");
                if (passwordOne.equals(passwordTwo)) {
                    int intCardId = cardService.addCard(new Card("Basic",Integer.parseInt(initialBalance)));
                    if (intCardId > 0) {
                        if (cardService.setPassword(intCardId, passwordOne)) {
                            cardService.setCurrentCardId(intCardId);
                            modelAndView.addObject("message", "Card Created Successfully, Your Card ID is " + intCardId + " Now You Can Use Metro Services");
                            modelAndView.setViewName("metroMenu");
                        }
                        return modelAndView;
                    } else return new ModelAndView("createCardOutput","message", "Card Creation Failed");
                } else return new ModelAndView("createCardOutput","message", "Passwords Didnt Match, Try Again");
            } else return new ModelAndView("createCardOutput","message", "Minimum Card Balance for New Users is 100/- ");
        } else return new ModelAndView("createCardOutput","message", "Only Integers Allowed");
    }


}
