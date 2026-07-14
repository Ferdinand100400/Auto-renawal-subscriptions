CREATE TABLE obligations
(
    id                UUID PRIMARY KEY,
    title             VARCHAR(255)    NOT NULL,
    amount            DECIMAL(19, 2)  NOT NULL,
    currency          VARCHAR(3)      NOT NULL,
    category          category_enum   NOT NULL,
    recurrence        recurrence_enum NOT NULL,
    next_payment_date DATE,
    status            status_enum,
    created_at        TIMESTAMP WITH TIME ZONE,
    updated_at        TIMESTAMP WITH TIME ZONE
);