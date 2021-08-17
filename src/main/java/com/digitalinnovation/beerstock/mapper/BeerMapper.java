package com.digitalinnovation.beerstock.mapper;

import com.digitalinnovation.beerstock.dto.BeerDTO;
import com.digitalinnovation.beerstock.entities.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}
