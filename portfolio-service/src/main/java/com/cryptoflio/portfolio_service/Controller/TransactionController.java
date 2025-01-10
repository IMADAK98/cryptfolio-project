package com.cryptoflio.portfolio_service.Controller;


import com.cryptoflio.portfolio_service.Exception.AssetCreationException;
import com.cryptoflio.portfolio_service.Exception.NotFound.TransactionNotFoundException;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import com.cryptoflio.portfolio_service.ServiceInterface.TransactionsService;
import com.cryptoflio.portfolio_service.Dto.BaseApiResponse;
import com.cryptoflio.portfolio_service.Dto.Mapper.TransactionMapper;
import com.cryptoflio.portfolio_service.Dto.Requests.CreateTransactionDto;
import com.cryptoflio.portfolio_service.Dto.Requests.UpdateTransactionDTO;
import com.cryptoflio.portfolio_service.Dto.TransactionResponseDTO;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1")
public class TransactionController {


    private final TransactionsService service;
    private final TransactionMapper transactionMapper;
    private final AssetService assetService;

    @GetMapping("/transaction/{id}")
    public ResponseEntity<BaseApiResponse<TransactionResponseDTO>> getTransactionById(@PathVariable Long id) {

        Optional<Transaction> transaction = service.getOptionalTransactionById(id);
        if (transaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(null, "Transaction not found"));
        }

        TransactionResponseDTO dto = transactionMapper.toTransactionDto(transaction.get());

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(dto, "Transaction found"));
    }


    @GetMapping("transactions/asset/{assetId}")
    public ResponseEntity<BaseApiResponse<List<TransactionResponseDTO>>> getTransactionsByAssetId(@PathVariable("assetId") Long assetId) {

        List<Transaction> transactionsList = service.getTransactionsByAssetId(assetId);

        List<TransactionResponseDTO> transactionsDtoList = transactionsList.stream()
                .map(transactionMapper::toTransactionDto)
                .toList();

        if (transactionsDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(transactionsDtoList, "No transactions were found"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(transactionsDtoList, "Transactions found"));
    }


    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(service.getTransactions());
    }

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid CreateTransactionDto transaction, @RequestHeader("loggedInUser") String header)
            throws AssetCreationException {
        //validation
        return ResponseEntity.ok(service.createTransaction(transaction, header));
    }


    @PutMapping("/transaction")
    public ResponseEntity<BaseApiResponse<TransactionResponseDTO>> updateTransaction(@RequestBody @Valid UpdateTransactionDTO transactionDto) {

        Transaction mappedTransaction = transactionMapper.toTransaction(transactionDto);

        if (mappedTransaction == null) {
            throw new TransactionNotFoundException("Invalid transaction data.");
        }

        TransactionResponseDTO dto = transactionMapper.toTransactionDto(service.updateTransaction(mappedTransaction));

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(dto, "Transactions updated"));
    }


    @DeleteMapping("/transaction/{id}")
    public ResponseEntity<BaseApiResponse<String>> deleteTransaction(@PathVariable("id") Long transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>("success", "transaction deleted"));
    }
}
