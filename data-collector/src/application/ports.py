from typing import Dict, List, Any, Protocol
from src.domain.models import FinancialData

class FinancialDataRepository(Protocol):
    def save(self, data:FinancialData) -> bool:...

class DataProcessor(Protocol):
    def process_batch(self, raw_data: List[Dict[str, Any]]) -> List[FinancialData]:...
