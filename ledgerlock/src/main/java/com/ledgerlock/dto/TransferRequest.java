package com.ledgerlock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * =====================================================================================
 * DTO: TransferRequest
 * Incoming payload contract for peer-to-peer wallet transfers.
 * 
 * VALIDATION HIGHLIGHTS:
 * 1. Amount must be whole integer cents (BIGINT) and strictly positive (> 0).
 * 2. Currency must be 3-letter ISO code.
 * =====================================================================================
 */
public record TransferRequest(
        @NotBlank(message = "Source account number is required")
        @Size(max = 32, message = "Source account number cannot exceed 32 characters")
        String sourceAccountNumber,

        @NotBlank(message = "Destination account number is required")
        @Size(max = 32, message = "Destination account number cannot exceed 32 characters")
        String destinationAccountNumber,

        @NotNull(message = "Transfer amount is required")
        @Positive(message = "Transfer amount must be strictly greater than zero")
        Long amount,

        @NotBlank(message = "Currency code is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code (e.g. USD)")
        String currency,

        @NotBlank(message = "Transfer description is required")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {}
