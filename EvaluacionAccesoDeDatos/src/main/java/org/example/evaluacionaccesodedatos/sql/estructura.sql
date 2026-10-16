CREATE SCHEMA catalogos;

CREATE TABLE catalogos.producto(

                                   id_producto SERIAL PRIMARY KEY,
                                   codigo VARCHAR(10) NOT NULL UNIQUE,
                                   nombre VARCHAR(80) NOT NULL,
                                   categoria VARCHAR(20) NOT NULL,
                                   precio NUMERIC(10,2)NOT NULL,
                                   stock INTEGER NOT NULL
);
SELECT *FROM catalogos.producto
ORDER BY codigo;
ALTER TABLE catalogos.producto
    ADD CONSTRAINT chk_codigo
        CHECK (
            codigo = UPPER(TRIM(codigo))
                AND LENGTH(codigo) BETWEEN 1 AND 10
                AND codigo <> ''
            );

ALTER TABLE catalogos.producto
    ADD CONSTRAINT chk_nombre
        CHECK (
            LENGTH(TRIM(nombre)) BETWEEN 3 AND 80
            );

ALTER TABLE catalogos.producto
    ADD CONSTRAINT chk_categoria
        CHECK (
            categoria IN ('Papelería', 'Tecnología', 'Accesorios')
            );

ALTER TABLE catalogos.producto
    ADD CONSTRAINT chk_precio
        CHECK (precio > 0);

ALTER TABLE catalogos.producto
    ADD CONSTRAINT chk_stock
        CHECK (stock >= 0);

