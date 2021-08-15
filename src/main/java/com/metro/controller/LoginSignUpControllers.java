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
        return new ModelAndView("login");
    }

    @RequestMapping("signup")
    public ModelAndView signupController() {
        return new ModelAndView("signup");
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
                    modelAndView.addObject("cardId", cardId);
                    modelAndView.addObject("message", "You Are Logged In");
                    modelAndView.setViewName("menu");
                } else {
                    modelAndView.addObject("message", "Invalid Password, Try Again");
                    modelAndView.setViewName("output");
                }
            } else {
                modelAndView.addObject("message", "Invalid Card");
                modelAndView.setViewName("output");
            }
        } else {
            modelAndView.addObject("message", "Card ID Only Contains Integers");
            modelAndView.setViewName("output");
        }
        return modelAndView;
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
                            modelAndView.addObject("message", "Card Created Successfully, Your Card ID is " + intCardId + " Now You Can Use Metro Services");
                            modelAndView.setViewName("menu");
                        }
                    } else {
                        modelAndView.addObject("message", "Card Creation Failed");
                        modelAndView.setViewName("createCardOutput");
                    }
                } else {
                    modelAndView.addObject("message", "Passwords Didnt Match, Try Again");
                    modelAndView.setViewName("createCardOutput");
                }
            } else {
                modelAndView.addObject("message", "Minimum Card Balance for New Users is 100/- ");
                modelAndView.setViewName("createCardOutput");
            }
        } else {
            modelAndView.addObject("message", "Only Integers Allowed");
            modelAndView.setViewName("createCardOutput");
        }
        return modelAndView;
    }


}
