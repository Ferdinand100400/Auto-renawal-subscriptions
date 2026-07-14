ALTER TABLE payments
    ADD CONSTRAINT fk_payments_obligation
        FOREIGN KEY (obligation_id)
            REFERENCES obligations (id)
            ON DELETE CASCADE;