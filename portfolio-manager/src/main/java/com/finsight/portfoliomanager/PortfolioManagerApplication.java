package com.finsight.portfoliomanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.finsight.portfoliomanager.infrastructure.grpc.GrpcFinancialDataClient;

@SpringBootApplication
public class PortfolioManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioManagerApplication.class, args);
    }

    @Bean
    CommandLineRunner testGrpcConnection(GrpcFinancialDataClient grpcClient) {
        return args -> {
            System.out.println("------------------------------------------------");
            System.out.println("🧪 INICIANDO PRUEBA DE INTEGRACIÓN: JAVA -> PYTHON");
            System.out.println("------------------------------------------------");

            String symbol = "AAPL";
            System.out.println("📡 Pidiendo precio de: " + symbol);

            Double price = grpcClient.getStockPrice(symbol);

            if (price > 0) {
                System.out.println("✅ ¡ÉXITO! Recibido: " + price);
            } else {
                System.out.println("⚠️ Recibido 0.0 (¿Quizás el símbolo no existe o falló la conexión?)");
            }

            System.out.println("------------------------------------------------");
        };
    }
}
