package com.digitalinnovation.beerstock.service;

import com.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import com.digitalinnovation.beerstock.dto.BeerDTO;
import com.digitalinnovation.beerstock.entities.Beer;
import com.digitalinnovation.beerstock.exceptions.BeerAlreadyRegisteredException;
import com.digitalinnovation.beerstock.exceptions.BeerNotFoundException;
import com.digitalinnovation.beerstock.mapper.BeerMapper;
import com.digitalinnovation.beerstock.repository.BeerRepository;
import com.digitalinnovation.beerstock.services.BeerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private  final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInfermedThenItShuldBeCreated() throws BeerAlreadyRegisteredException {
        // Given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

        // When
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        // Then
        BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
    }

    @Test
    void whenAlreadRegisteredBeerInformedExceptionShouldBeThrown() throws BeerAlreadyRegisteredException {
        // Given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

        // when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

       assertThrows(BeerAlreadyRegisteredException.class, () ->  beerService.createBeer(expectedBeerDTO));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // Given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        // When
        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // Then
        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));

    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // Given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // When
        when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

        // Then
         assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()) );
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        // Given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        // When
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        // Then
        List<BeerDTO> foundListBeerDTO = beerService.listAll();
        assertThat(foundListBeerDTO, is(not(empty())));
        assertThat(foundListBeerDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        // When
        when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        // Then
        List<BeerDTO> foundListBeerDTO = beerService.listAll();

        assertThat(foundListBeerDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCaleedWithValidThenBeerShoulBeDeleted() throws BeerNotFoundException {
        // Given
        BeerDTO expectedDeleteBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeleteBeer = beerMapper.toModel(expectedDeleteBeerDTO);

        // When
        when(beerRepository.findById(expectedDeleteBeerDTO.getId())).thenReturn(Optional.of(expectedDeleteBeer));
        doNothing().when(beerRepository).deleteById(expectedDeleteBeerDTO.getId());

        // Then
        beerService.deleteById(expectedDeleteBeerDTO.getId());

        verify(beerRepository, times(1)).findById(expectedDeleteBeerDTO.getId());
        verify(beerRepository, times(1)).deleteById(expectedDeleteBeerDTO.getId());

    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(INVALID_BEER_ID));


    }
}
