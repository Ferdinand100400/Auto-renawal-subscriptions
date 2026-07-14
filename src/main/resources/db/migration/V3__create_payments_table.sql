CREATE TABLE payments
(
    id            UUID PRIMARY KEY,
    obligation_id UUID           NOT NULL,
    amount        DECIMAL(19, 2) NOT NULL,
    currency      VARCHAR(3)     NOT NULL,
    status        status_enum,
    paid_at       TIMESTAMP WITH TIME ZONE
);