package com.ibrahimokic.ordermanagement.controller;

import com.ibrahimokic.ordermanagement.domain.Address;
import com.ibrahimokic.ordermanagement.domain.dto.AddressDto;
import com.ibrahimokic.ordermanagement.repository.AddressRepository;
import com.ibrahimokic.ordermanagement.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/address")
@Tag(name="Address", description = "Oprations related to addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressRepository addressRepository;

    @Autowired
    public AddressController(AddressService addressService, AddressRepository addressRepository) {
        this.addressService = addressService;
        this.addressRepository = addressRepository;
    }

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Get list of all addresses")
    @ApiResponse(responseCode = "200", description = "List of addresses", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Address.class)))
    })
    public ResponseEntity<List<Address>> getAllAddresses(){
        List<Address> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get a address by ID", description = "Get a address by providing its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address found", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Address.class))),
            @ApiResponse(responseCode = "404", description = "Address not found.")
    })
    public ResponseEntity<?> getAddressById(@PathVariable Long addressId){
        Optional<Address> address = addressService.getAddressById(addressId);

        if(address.isPresent()){
            return ResponseEntity.ok(address);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Address not found.");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new address", description = "Create new address based on request body")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Address.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<?> createAddress(
            @RequestBody @Valid AddressDto addressDto
    ) {
        try {
            Address newAddress = new Address();
            newAddress.setCity(addressDto.getCity());
            newAddress.setZip(addressDto.getZip());
            newAddress.setCountry(addressDto.getCountry());
            newAddress.setStreet(addressDto.getStreet());

            Address createdAddress = addressService.createAddress(newAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Internal server error");
        }
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete address", description = "Delete address with provided address id")
    public void deleteAddress(@PathVariable Long addressId){
        addressService.deleteAddress(addressId);
    }

    @PatchMapping("/{addressId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Edit address", description = "Edit address based on request body and address ID")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId, @RequestBody AddressDto updatedAddressDto) {
        Optional<Address> optionalExistingAddress = addressService.getAddressById(addressId);

        if(optionalExistingAddress.isPresent()){
            Address existingAddress = optionalExistingAddress.get();
            BeanUtils.copyProperties(updatedAddressDto, existingAddress);
            try {
                addressRepository.save(existingAddress);
                return ResponseEntity.ok(existingAddress);
            } catch (Exception e){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Internal server error");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Address with that ID does not exist in database");
        }
    }
}
