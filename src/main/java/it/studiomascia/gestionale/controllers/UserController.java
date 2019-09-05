package it.studiomascia.gestionale.controllers;

import it.studiomascia.gestionale.models.Role;
import  it.studiomascia.gestionale.models.User;
import  it.studiomascia.gestionale.service.SecurityService;
import  it.studiomascia.gestionale.service.UserService;
import it.studiomascia.gestionale.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

     @GetMapping("/Admin/SetDefaultUsers")
    public String insertDefaultUsers() {
                
        User u1 = new User();
        u1.setUsername("admin@admin.it");
        u1.setPassword("123123");
        u1.setStato(1);
        userService.save(u1);
        
        return "login";
    }
    
    
    @GetMapping("/Admin/UserRegistration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/Admin/UserRegistration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);
        securityService.autoLogin(userForm.getUsername(), userForm.getPassword());

        return "redirect:/welcome";
    }

//    @GetMapping("/login")
//    public String login(Model model, String error, String logout) {
//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");
//
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");
//
//        return "login2";
//    }
////
//    @GetMapping({"/", "/welcome"})
//    public String welcome(Model model) {
//        return "welcome";   
//    }
}