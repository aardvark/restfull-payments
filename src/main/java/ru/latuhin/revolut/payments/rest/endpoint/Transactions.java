package ru.latuhin.revolut.payments.rest.endpoint;

import static spark.Spark.get;

import io.vavr.control.Either;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Status;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.revolut.payments.rest.endpoint.dao.storage.Storage;
import spark.Request;
import spark.Response;
import spark.Route;

public class Transactions {

  public static final long FROM_ACCOUNT_MISSING = -1L;
  public static final long TO_ACCOUNT_MISSING = -2L;
  public static final long NOT_ENOUGH_AMOUNT = -3L;
  Storage<Transaction, Long> storage;

  public Transactions(Storage<Transaction, Long> storage) {
    this.storage = storage;
  }

  public Either<ErrorMessage, Transaction> create(String fromAccountId, String toAccountId, String transferAmount) {
    long from = Accounts.ensureExists(fromAccountId);
    if (from == Accounts.ACCOUNT_NOT_EXIST) {
      return Either.left(new ErrorMessage(FROM_ACCOUNT_MISSING, accountMissing(fromAccountId)));
    }
    long to = Accounts.ensureExists(toAccountId);
    if (to == Accounts.ACCOUNT_NOT_EXIST) {
      return Either.left(new ErrorMessage(TO_ACCOUNT_MISSING, accountMissing(toAccountId)));
    }

    BigDecimal amount = Accounts.ensureAmount(from, transferAmount);
    if (Objects.equals(amount, BigDecimal.ZERO)) {
      return Either.left(new ErrorMessage(NOT_ENOUGH_AMOUNT, balanceNotEnough(fromAccountId)));
    }
    CompletableFuture<Long> reservedId = CompletableFuture
        .supplyAsync(() -> storage.reserveId());
    Long id = reservedId.join();
    Transaction transaction = new Transaction(id, from, to, amount, Status.Open);
    storage.save(transaction);
    return Either.right(transaction);
  }

  private String balanceNotEnough(String accountId) {
    return "Account with id:" + accountId + " don't have enough balance.";
  }

  private String accountMissing(String id) {
    return "Account with id:"+ id + "is missing.";
  }

  public Transaction find(Long id) {
    return storage.findById(id);
  }

  void transactionGet(Endpoint endpoint) {
    get("/transactions/:id", "application/yaml",
        getRoute(),
        endpoint.transformer
    );
  }

  public Route getRoute() {
    return (Request request, Response response) -> {
      Transaction transaction =
          find(new Long(request.params(":id")));
      if (transaction == null) {
        response.status(422);
        return response;
      }
      return transaction;
    };
  }
}
