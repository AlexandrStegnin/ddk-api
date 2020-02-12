package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.dto.MoneyDTO;
import com.ddkolesnik.ddkapi.service.MoneyService;
import com.ddkolesnik.ddkapi.specification.filter.MoneyFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_MONIES;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(PATH_MONIES)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoneyController {

    MoneyService moneyService;

    @GetMapping
    public List<MoneyDTO> getAll(@RequestBody(required = false) MoneyFilter filter) {
        return moneyService.findAllDTO(filter);
    }

}
