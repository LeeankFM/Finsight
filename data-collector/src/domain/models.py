from typing import Dict, Any
from datetime import datetime
from dataclasses import dataclass


@dataclass
class FinancialData:
    symbol: str
    price: float
    timestamp: datetime
    metadata: Dict[str, Any]