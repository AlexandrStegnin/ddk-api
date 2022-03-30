package com.ddkolesnik.ddkapi.mapper;

import com.ddkolesnik.ddkapi.configuration.MapStructConfig;
import com.ddkolesnik.ddkapi.dto.cash.DeleteCashDTO;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * @author Alexandr Stegnin
 */
@Component
@Mapper(config = MapStructConfig.class)
public interface InvestorCashMapper {

  @Mapping(target = "delete", constant = "true")
  InvestorCashDTO toCashDTO(DeleteCashDTO deleteCashDTO);

}
