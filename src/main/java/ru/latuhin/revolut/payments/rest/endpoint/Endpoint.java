package ru.latuhin.revolut.payments.rest.endpoint;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import io.vavr.control.Either;
import ru.latuhin.revolut.payments.rest.endpoint.dao.Transaction;
import ru.latuhin.revolut.payments.rest.endpoint.dao.storage.Storage;
import spark.Response;
import spark.ResponseTransformer;


public class Endpoint {

  private Storage<Transaction, Long> transactionStorage;
  private Transactions transactions;
  public ResponseTransformer transformer;

  private Endpoint() {
    transformer = new ToYamlResponseTransformer();
    transactions = new Transactions(new StorageImpl());
  }

  public static void main(String[] args) {
    new Endpoint().engage();
  }

  private void engage() {
    path("/api", () -> {
          transactions.transactionGet(this);
          transactionPost();
        }
    );
  }

  private void transactionPost() {
    post("/transactions/from/:from/to/:to/amount/:amount", "application/yaml",
        (request, response) -> {
          Either<ErrorMessage, Transaction> transaction = this.transactions.create(
              request.params(":from"),
              request.params(":to"),
              request.params(":amount")
          );
          response = unwrap(transaction, response);
          return response;
        });
  }

  private Response unwrap(Either<ErrorMessage, Transaction> transaction, Response response) {
    return null;

  }

}
