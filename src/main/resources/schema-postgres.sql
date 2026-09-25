-- Use DROP TABLE ... IF EXISTS to make the script re-runnable
DROP TABLE IF EXISTS sale_product;
DROP TABLE IF EXISTS sale;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS unit;
DROP TABLE IF EXISTS appconfig;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS discount;

CREATE TABLE discount (
    id SERIAL PRIMARY KEY,
    discount_type VARCHAR(50) NOT NULL
);

CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    vatnumber INT UNIQUE NOT NULL,
    designation VARCHAR(255) NOT NULL,
    phonenumber INT,
    discount_id INT,
    FOREIGN KEY (discount_id) REFERENCES discount(id)
);

CREATE TABLE appconfig (
    id SERIAL PRIMARY KEY,
    totalAmountPercentage DOUBLE PRECISION,
    amountThreshold DOUBLE PRECISION,
    eligiblePercentage DOUBLE PRECISION
);

CREATE TABLE unit (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    prodcod INT UNIQUE NOT NULL,
    description VARCHAR(255),
    facevalue DOUBLE PRECISION,
    qty DOUBLE PRECISION,
    discounteligibility CHAR(1),
    unit_id INT,
    FOREIGN KEY (unit_id) REFERENCES unit(id)
);

CREATE TABLE sale (
    id SERIAL PRIMARY KEY,
    open_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    customer_id INT NOT NULL,
    total_sale DOUBLE PRECISION,
    total_discount DOUBLE PRECISION,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE sale_product (
    sale_id INT,
    product_id INT,
    qty DOUBLE PRECISION,
    PRIMARY KEY (sale_id, product_id),
    FOREIGN KEY (sale_id) REFERENCES sale(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Insert Initial Data
INSERT INTO discount (id, discount_type) VALUES (1, 'NO_DISCOUNT'), (2, 'SALE_AMOUNT'), (3, 'ELIGIBLE_PRODUCTS');
INSERT INTO unit (id, name) VALUES (1, 'units');
INSERT INTO product (prodcod, description, facevalue, qty, discounteligibility, unit_id) VALUES (123, 'Product A', 10.0, 100, 'E', 1), (124, 'Product B', 25.5, 50, 'N', 1);
INSERT INTO appconfig (totalAmountPercentage, amountThreshold, eligiblePercentage) VALUES (0.05, 100.0, 0.10);