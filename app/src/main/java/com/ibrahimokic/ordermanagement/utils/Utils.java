package com.ibrahimokic.ordermanagement.utils;

import com.ibrahimokic.ordermanagement.domain.dto.AddressDto;
import com.ibrahimokic.ordermanagement.domain.entity.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Utils {
    public static BigDecimal calculateTotalProductsPriceAmount(List<OrderItem> orderItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            BigDecimal itemPrice = orderItem.getItemPrice();
            int quantity = orderItem.getQuantity();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }

    public static boolean checkIfUserIdIsDifferent(User user, Order retrievedOrderFromDatabase) {
        return !Objects.equals(user.getUserId(), retrievedOrderFromDatabase.getUser().getUserId());
    }

    public static boolean checkIfAddressIsDifferent(Address address, AddressDto addressDto) {
        return !Objects.equals(address.getStreet(), addressDto.getStreet()) ||
                !Objects.equals(address.getCity(), addressDto.getCity()) ||
                !Objects.equals(address.getZip(), addressDto.getZip()) ||
                !Objects.equals(address.getCountry(), addressDto.getCountry());
    }

    public static boolean checkIfOrderItemsAreDifferent(List<OrderItem> retrievedOrderItems,
            List<OrderItem> updatedOrderItems) {
        boolean differentOrderItems = false;

        if (retrievedOrderItems.size() != updatedOrderItems.size()) {
            return true;
        }

        for (int i = 0; i < retrievedOrderItems.size(); i++) {
            OrderItem retrievedOrderItem = retrievedOrderItems.get(i);
            OrderItem updatedOrderItem = updatedOrderItems.get(i);

            if (!Objects.equals(retrievedOrderItem.getProduct().getProductId(),
                    updatedOrderItem.getProduct().getProductId())) {
                differentOrderItems = true;
                break;
            }
        }

        return differentOrderItems;
    }

    public static void clearConsole(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.println("\n");
        }
    }

    public static void returnBackToTheMainMenu(Scanner scanner) {
        System.out.print(">> To return back to the main menu press 'ENTER': ");

        scanner.nextLine();
        scanner.nextLine();
    }

    public static String promptUserInput(Scanner scanner, String fieldName) {
        System.out.print(">> Please enter the " + fieldName + ": ");
        return scanner.nextLine();
    }

    public static String formatAddress(Address address) {
        return String.format("%s, %s, %s, %s", address.getStreet(), address.getZip(), address.getCity(),
                address.getCountry());
    }

    public static ResponseEntity<?> getBindingResults(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }

    public static boolean checkProductAvailability(Product product) {
        LocalDate currentDate = LocalDate.now();
        LocalDate availableFrom = product.getAvailableFrom();
        LocalDate availableUntil = product.getAvailableUntil();

        return !currentDate.isAfter(availableFrom.minusDays(1)) || !currentDate.isBefore(availableUntil);
    }

    public static boolean checkProductQuantity(Product product, int quantity) {
        return product.getAvailableQuantity() < quantity;
    }

    public static int getValidInput(Scanner scanner, int max) {
        final int MIN_CHOICE = 1;
        int choice;
        do {
            System.out.print(">> Please enter your choice: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please use options " + MIN_CHOICE + " - " + max + ".");
                System.out.print("Enter your choice (" + MIN_CHOICE + " - " + max + "): ");
                scanner.next();
            }
            choice = scanner.nextInt();
            if (choice < MIN_CHOICE || choice > max)
                System.out.println("Invalid choice. Please enter " + MIN_CHOICE + " - " + max + ".");
        } while (choice < MIN_CHOICE || choice > max);
        return choice;
    }
}
