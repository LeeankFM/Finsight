import pytest
from src.infrastructure.polars_processor import PolarsDataProcessor

def test_polars_processor_filters_negative_prices():
    processor = PolarsDataProcessor()
    raw_data = [
        {"symbol": "AAPL", "price": 150.0},
        {"symbol": "BAD", "price": -10.0}
    ]
    result = processor.process_batch(raw_data)
    assert len(result) == 1
    assert result[0].symbol == "AAPL"
    assert result[0].price == 150.0