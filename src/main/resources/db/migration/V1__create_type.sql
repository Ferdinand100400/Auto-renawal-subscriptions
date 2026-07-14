CREATE TYPE category_enum AS ENUM ('Subscription', 'Warranty', 'Bill', 'Insurance');
CREATE TYPE recurrence_enum AS ENUM ('Monthly', 'Quarterly', 'Yearly', 'Null');
CREATE TYPE status_enum AS ENUM ('Active', 'Cancelled', 'Expired');