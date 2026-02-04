from pymongo import MongoClient
from src.domain.models import FinancialData
from src.application.ports import FinancialDataRepository

class MongoFinancialDataRepository(FinancialDataRepository):
    def __init__(self, connection_string: str, database_name: str):
        self.client = MongoClient(connection_string)
        self.db = self.client[database_name]
        self.collection = self.db["market_prices"]

    def save(self, data: FinancialData) -> bool:
        document = {
            "symbol": data.symbol,
            "price": data.price,
            "timestamp": data.timestamp,
            "metadata": data.metadata
        }
        result = self.collection.insert_one(document)
        return result.acknowledged
