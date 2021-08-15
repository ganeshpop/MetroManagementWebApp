package com.metro.model.service.transaction;

import com.metro.model.exceptions.InsufficientBalanceException;
import com.metro.model.exceptions.InvalidStationException;
import com.metro.model.exceptions.InvalidSwipeInException;
import com.metro.model.exceptions.InvalidSwipeOutException;
import com.metro.model.pojos.Transaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public interface TransactionServiceInterface {
    Collection<Transaction> getAllTransactions(int cardId) throws SQLException, ClassNotFoundException, IOException;
    String swipeIn(int cardId, int sourceStationId) throws SQLException, ClassNotFoundException, IOException, InsufficientBalanceException, InvalidStationException, InvalidSwipeInException;
    Transaction swipeOut(int cardId, int destinationStationId) throws InvalidSwipeInException, InvalidSwipeOutException, InvalidStationException;
}
