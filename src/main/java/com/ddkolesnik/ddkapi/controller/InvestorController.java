package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.model.Investor;
import com.ddkolesnik.ddkapi.service.InvestorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ddkolesnik.ddkapi.util.Constants.INVESTORS_PATH;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = INVESTORS_PATH)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvestorController {

    InvestorService investorService;

    @GetMapping(path = "/{id}")
    public Investor getInvestor(@PathVariable long id) {
        return investorService.findById(id);
    }

}
