CREATE TABLE recommendations (
	id varchar(255) NOT NULL,
    advisor_id INT8 NOT NULL,
    stock_id INT8 NOT NULL,
    rating VARCHAR(255) NULL,
    entry_date TIMESTAMP(6) WITH TIME ZONE NULL,
    entry_price NUMERIC(10, 2) NULL,
    target_date TIMESTAMP(6) WITH TIME ZONE NULL,
    target_price NUMERIC(10, 2) NULL,
    time_period VARCHAR(255) NOT NULL,
    stop_loss NUMERIC(10, 2) NULL,
    recommendation_url VARCHAR(255) NULL,
    rationale TEXT NULL,
    absolute_return NUMERIC(10, 2) NULL,
    closing_recommendation_id VARCHAR(255) NULL,
    closure_price NUMERIC(10, 2) NULL,
    closure_reason VARCHAR(255) NULL,
    closed_at TIMESTAMP(6) WITH TIME ZONE NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_recommendations_id PRIMARY KEY (id),
    CONSTRAINT fk_recommendations_stocks_stock_id FOREIGN KEY (stock_id) REFERENCES stocks(id),
    CONSTRAINT fk_recommendations_advisors_advisor_id FOREIGN KEY (advisor_id) REFERENCES advisors(id)
);
ALTER TABLE recommendations
    ADD CONSTRAINT fk_recommendations_closing_recommendation_id FOREIGN KEY (closing_recommendation_id) REFERENCES recommendations(id);

