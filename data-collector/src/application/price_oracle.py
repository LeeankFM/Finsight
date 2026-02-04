import yfinance as yf
import redis
import os

class PriceOracle:
    def __init__(self):
        host = os.getenv("DB_REDIS_HOST","localhost")
        password = os.getenv("DB_REDIS_PASSWORD", "mi_redis_pass_seguro")
        self.r = redis.Redis(host=host, port=6379, password=password, db=0, decode_responses=True)

    def fetch_and_cache(self, symbol: str):
        try:
            ticker = yf.Ticker(symbol)
            price = ticker.fast_info['last_price']
            self.r.set(f"price:{symbol}", price, ex=300)
            return float(price)
        except Exception as e:
            print(f"Oracle Error for {symbol}: {e}")
            return 0.0