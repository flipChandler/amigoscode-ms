package com.amigoscode.customer.dtos;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}
