package com.finsight.portfoliomanager.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.finsight.portfoliomanager.application.ports.in.PortfolioUseCase;
import com.finsight.portfoliomanager.application.ports.out.MetricRepository;
import com.finsight.portfoliomanager.application.ports.out.PortfolioRepository;
import com.finsight.portfoliomanager.domain.Portfolio;
import com.finsight.portfoliomanager.domain.Position;
import com.finsight.portfoliomanager.domain.Transaction;
import com.finsight.portfoliomanager.domain.TransactionType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService implements PortfolioUseCase {

    private final PortfolioRepository portfolioRepository;
    private final MetricRepository metricRepository;

    @Override
    public Portfolio createPortfolio(Portfolio portfolio) {
        if (portfolio.getId() == null)
            portfolio.setId(UUID.randomUUID());
        if (portfolio.getPositions() == null)
            portfolio.setPositions(new ArrayList<>());
        if (portfolio.getTransactions() == null)
            portfolio.setTransactions(new ArrayList<>());

        portfolio.setBalance(BigDecimal.ZERO);
        portfolio.setCumulativeDeposits(BigDecimal.ZERO);
        portfolio.setCumulativeWithdrawals(BigDecimal.ZERO);
        metricRepository.recordUserActivity(portfolio.getUserId().toString());
        metricRepository.incrementPortfolioCount();

        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio getPortfolio(UUID id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        metricRepository.recordUserActivity(portfolio.getUserId().toString());
        return portfolio;
    }

    @Override
    public Portfolio buyAsset(UUID portfolioId, String symbol, BigDecimal quantity, BigDecimal price) {
        Portfolio portfolio = getPortfolio(portfolioId);
        BigDecimal totalCost = price.multiply(quantity);
        if (portfolio.getBalance().compareTo(totalCost) < 0)
            throw new RuntimeException("Insufficient balance for trade");
        portfolio.setBalance(portfolio.getBalance().subtract(totalCost));
        Optional<Position> existing = portfolio.getPositions()
                .stream().filter(p -> p.getSymbol().equals(symbol)).findFirst();
        if (existing.isPresent()) {
            Position pos = existing.get();
            BigDecimal newQty = pos.getQuantity().add(quantity);
            BigDecimal totalSpent = pos.getQuantity()
                    .multiply(pos.getAveragePurchasePrice()).add(totalCost);
            pos.setAveragePurchasePrice(totalSpent.divide(newQty, 4, RoundingMode.HALF_UP));
            pos.setQuantity(newQty);
        } else {
            portfolio.getPositions().add(Position.builder()
                    .id(UUID.randomUUID())
                    .portfolioId(portfolioId)
                    .symbol(symbol)
                    .quantity(quantity)
                    .averagePurchasePrice(price)
                    .currentPrice(price)
                    .build());
        }

        portfolio.getTransactions().add(Transaction.builder()
                .id(UUID.randomUUID()).portfolioId(portfolioId).type(TransactionType.BUY)
                .symbol(symbol).quantity(quantity).price(price).timestamp(LocalDateTime.now())
                .balanceTransaction(portfolio.getBalance()).build());

        metricRepository.incrementAssetVolume(symbol, quantity.doubleValue());
        updatePerformance(portfolio);

        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio sellAsset(UUID portfolioId, String symbol, BigDecimal quantity, BigDecimal price) {
        Portfolio portfolio = getPortfolio(portfolioId);
        Position pos = portfolio.getPositions().stream()
                .filter(p -> p.getSymbol().equals(symbol)).findFirst()
                .orElseThrow(() -> new RuntimeException("Symbol not found in portfolio"));
        if (pos.getQuantity().compareTo(quantity) < 0)
            throw new RuntimeException("Not enough assets to sell");
        portfolio.setBalance(portfolio.getBalance().add(price.multiply(quantity)));
        pos.setQuantity(pos.getQuantity().subtract(quantity));
        if (pos.getQuantity().compareTo(BigDecimal.ZERO) == 0)
            portfolio.getPositions().remove(pos);

        portfolio.getTransactions().add(Transaction.builder()
                .id(UUID.randomUUID()).portfolioId(portfolioId).type(TransactionType.SELL)
                .symbol(symbol).quantity(quantity).price(price).timestamp(LocalDateTime.now())
                .balanceTransaction(portfolio.getBalance()).build());

        metricRepository.incrementAssetVolume(symbol, quantity.doubleValue());
        updatePerformance(portfolio);

        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio addCash(UUID portfolioId, BigDecimal amount) {
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolio.setBalance(portfolio.getBalance().add(amount));
        portfolio.setCumulativeDeposits(portfolio.getCumulativeDeposits().add(amount));
        portfolio.getTransactions().add(Transaction.builder()
                .id(UUID.randomUUID()).portfolioId(portfolioId).type(TransactionType.DEPOSIT).symbol("USD")
                .quantity(BigDecimal.ZERO).price(amount).timestamp(LocalDateTime.now())
                .balanceTransaction(portfolio.getBalance()).build());

        updatePerformance(portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio withdrawCash(UUID portfolioId, BigDecimal amount) {
        Portfolio portfolio = getPortfolio(portfolioId);
        if (portfolio.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient cash balance");
        portfolio.setBalance(portfolio.getBalance().subtract(amount));
        portfolio.setCumulativeWithdrawals(portfolio.getCumulativeWithdrawals().add(amount));
        portfolio.getTransactions().add(Transaction.builder()
                .id(UUID.randomUUID()).portfolioId(portfolioId).type(TransactionType.WITHDRAWAL).symbol("USD")
                .quantity(BigDecimal.ZERO).price(amount).timestamp(LocalDateTime.now())
                .balanceTransaction(portfolio.getBalance()).build());
        return portfolioRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> getPortfoliosByUser(UUID userId) {
        return portfolioRepository.findByUserId(userId);
    }

    @Override
    public void deletePortfolio(UUID id) {
        portfolioRepository.deleteById(id);
    }

    private void updatePerformance(Portfolio portfolio) {
        double currentVal = portfolio.getTotalAccountValue().doubleValue();
        double totalIn = portfolio.getCumulativeDeposits().doubleValue();
        double totalOut = portfolio.getCumulativeWithdrawals().doubleValue();
        double profit = (currentVal + totalOut) - totalIn;
        double roi = (totalIn > 0) ? (profit / totalIn) * 100 : 0;

        portfolio.setPerformance(roi);
        metricRepository.updatePortfolioPerformance(portfolio.getId().toString(), roi);
    }
}
