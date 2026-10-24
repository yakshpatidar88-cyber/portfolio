package com.ledgerlock.dto;

import java.time.Instant;
import java.util.UUID;

import com.ledgerlock.domain.TransactionStatus;

/**
 * =====================================================================================
 * DTO: TransferResponse
 * Outgoing payload contract confirming settlement of a double-entry transaction.
 * =====================================================================================
 */
public record TransferResponse(
        UUID transactionId,
        String idempotencyKey,
        TransactionStatus status,
        String sourceAccountNumber,
        String destinationAccountNumber,
        Long amount,
        String currency,
        Instant postedAt,
        String message
) {}
