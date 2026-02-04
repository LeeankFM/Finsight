from typing import Any, Dict, List
from src.application.ports import DataProcessor
from src.domain.models import FinancialData
from src.application.ports import FinancialDataRepository
from datetime import datetime

class FinancialDataService:
    def __init__(self, repository: FinancialDataRepository, processor: DataProcessor):
        self.repository = repository
        self.processor = processor

    def collect_and_store(self, symbol: str, price: float) -> bool:
        data = FinancialData(symbol=symbol,
        price=price,
        timestamp=datetime.now(),
        metadata={"source": "api_manual"})
        return self.repository.save(data)

    def process_and_store_batch(self, raw_data: List[Dict[str, Any]]) -> int:
        processed_list = self.processor.process_batch(raw_data)
        count = 0
        for data in processed_list:
            if self.repository.save(data):
                count += 1

        return count
