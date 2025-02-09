package vttp.ws.ws26.controller;

import java.text.ParseException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vttp.ws.ws26.model.ReviewForm;
import vttp.ws.ws26.service.BggService;

@Controller
@RequestMapping("/review")
public class ReviewFormController {
    
    @Autowired
    private BggService bggService;

    @GetMapping("/form")
    public String reviewForm(Model model){
        model.addAttribute("reviewForm", new ReviewForm());
        return "review_form";
    }

    @PostMapping("/add")
    public String submitForm(@Valid @ModelAttribute(value="reviewForm") ReviewForm form, BindingResult bindingResult, Model model) throws ParseException{
        if (bindingResult.hasErrors()){
            return "review_form";
        }
        System.out.println(form.getGid());
        List<Document> games = bggService.getGameById(form.getGid());
        if (games.size()==0){
            model.addAttribute("errorMessage", "Invalid Game Id..");
            
            return "error_page";
        }
        else {
            bggService.createNewReview(form);
            // System.out.println();
            
        }
        return "home";
    }
}
