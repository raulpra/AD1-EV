
CREATE TABLE agencia (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         direccion VARCHAR(200),
                         facturacion_anual FLOAT,
                         codigo_postal INT,
                         abierto_sabados BOOLEAN,
                         fecha_fundacion DATE
);


CREATE TABLE propietario (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             dni VARCHAR(20) NOT NULL UNIQUE,
                             nombre VARCHAR(100) NOT NULL,
                             telefono INT NOT NULL,
                             comision FLOAT,
                             es_empresa BOOLEAN,
                             fecha_contrato DATE
);


CREATE TABLE inmueble (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          titulo VARCHAR(150) NOT NULL,
                          precio FLOAT NOT NULL,
                          metros INT,
                          latitud DOUBLE NOT NULL, -- Clave para Maps
                          longitud DOUBLE NOT NULL, -- Clave para Maps
                          tiene_ascensor BOOLEAN,
                          fecha_publicacion DATE,
                          agencia_id INT,
                          propietario_id INT,
                          FOREIGN KEY (agencia_id) REFERENCES agencia(id),
                          FOREIGN KEY (propietario_id) REFERENCES propietario(id)
);


CREATE TABLE cliente (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         telefono VARCHAR(20),
                         presupuesto_maximo FLOAT,
                         edad INT,
                         recibir_novedades BOOLEAN,
                         fecha_alta DATE
);


CREATE TABLE visita (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        fecha_hora DATETIME NOT NULL,
                        comentarios VARCHAR(255),
                        estado VARCHAR(20) DEFAULT 'PENDIENTE',
                        valoracion FLOAT,
                        duracion_estimada INT,
                        recordatorio_activo BOOLEAN,
                        cliente_id INT,
                        inmueble_id INT,
                        FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
                        FOREIGN KEY (inmueble_id) REFERENCES inmueble(id)
);