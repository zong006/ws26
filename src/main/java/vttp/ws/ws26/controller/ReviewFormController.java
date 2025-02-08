package vttp.ws.ws26.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vttp.ws.ws26.model.ReviewForm;

@Controller
@RequestMapping("/review")
public class ReviewFormController {
    
    @GetMapping("/form")
    public String reviewForm(Model model){
        model.addAttribute("reviewForm", new ReviewForm());
        return "review_form";
    }

    @PostMapping("/add")
    public String submitForm(@Valid @ModelAttribute(value="reviewForm") ReviewForm form, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "review_form";
        }

        System.out.println(form);

        return "home";
    }
}
