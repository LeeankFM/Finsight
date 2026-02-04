import grpc
from concurrent import futures
import time
from datetime import datetime
import sys
import os

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from src.infrastructure import financial_data_pb2
from src.infrastructure import financial_data_pb2_grpc
from src.application.price_oracle import PriceOracle
class FinancialDataServicer(financial_data_pb2_grpc.FinancialDataServiceServicer):
    def __init__(self):
        self.oracle = PriceOracle()
    def GetStockPrice(self, request, context):
        symbol = request.symbol
        print(f"💰 gRPC Request received for: {symbol}")
        price = self.oracle.fetch_and_cache(symbol)
        return financial_data_pb2.StockPriceResponse(
            symbol=symbol,
            price=price,
            timestamp=datetime.now().isoformat()
        )
    def GetBatchPrices(self, request, context):
        prices = []
        success = 0
        failed = 0
        
        for symbol in request.symbols:
            price = self.oracle.fetch_and_cache(symbol)
            if price > 0:
                prices.append(financial_data_pb2.StockPriceResponse(
                    symbol=symbol, price=price, timestamp=datetime.now().isoformat()
                ))
                success += 1
            else:
                failed += 1
        
        return financial_data_pb2.BatchStockResponse(
            prices=prices,
            success_count=success,
            failed_count=failed
        )
def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    financial_data_pb2_grpc.add_FinancialDataServiceServicer_to_server(
        FinancialDataServicer(), server
    )
    server.add_insecure_port('[::]:50051')
    print("🚀 gRPC Server starting on port 50051...")
    server.start()
    server.wait_for_termination()
if __name__ == '__main__':
    serve()