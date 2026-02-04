from datetime import datetime
import polars as pl
from src.domain.models import FinancialData
from typing import Any
from typing import Dict
from typing import List
from src.application.ports import DataProcessor

class PolarsDataProcessor(DataProcessor):
    def process_batch(self, raw_data: List[Dict[str, Any]]) -> List[FinancialData]:
        if not raw_data:
            return[]
        df= pl.DataFrame(raw_data)
        df = df.filter(pl.col("price") > 0)
        processed_data = []
        for row in df.iter_rows(named=True):
            processed_data.append(FinancialData(symbol=row["symbol"], 
                price=row["price"],
                timestamp=datetime.now(),
                metadata={"processed_by": "polars_cleaner"}))

        return processed_data