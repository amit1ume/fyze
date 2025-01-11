CREATE TABLE stocks (
    id BIGSERIAL NOT NULL,
    exchange VARCHAR(255) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    short_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    isin VARCHAR(255) NOT NULL,
    market_cap VARCHAR(255) NOT NULL,
    sector VARCHAR(255) NOT NULL,
    screeners JSONB NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_stocks_id PRIMARY KEY (id),
    CONSTRAINT uq_stocks_exchange_symbol UNIQUE (exchange, symbol)
);
