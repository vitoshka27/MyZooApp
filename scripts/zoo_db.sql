CREATE TABLE IF NOT EXISTS staff_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    enclosure_access CHAR(1) CHECK (enclosure_access IN ('Y', 'N')) NOT NULL
);

CREATE TABLE IF NOT EXISTS staff (
    id INT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    gender CHAR(1) CHECK (gender IN ('М', 'Ж')),
    birth_date DATE NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    category_id INT REFERENCES staff_categories(id) ON DELETE CASCADE,
    is_superuser BOOLEAN NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT 1,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    avatar_original_url VARCHAR(255),
    CHECK (hire_date >= birth_date)
);

CREATE TABLE IF NOT EXISTS category_attributes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT REFERENCES staff_categories(id) ON DELETE CASCADE,
    attribute_name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_attribute UNIQUE (category_id, attribute_name)
);

CREATE TABLE IF NOT EXISTS staff_attribute_values (
    staff_id INT REFERENCES staff(id) ON DELETE CASCADE,
    attribute_id INT REFERENCES category_attributes(id) ON DELETE CASCADE,
    attribute_value VARCHAR(255) NOT NULL,
    PRIMARY KEY (staff_id, attribute_id)
);

CREATE TABLE IF NOT EXISTS climate_zones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE CHECK (name IN ('Тропический', 'Субтропический', 'Умеренный', 'Континентальный', 'Арктический', 'Средиземноморский')) NOT NULL
);

CREATE TABLE IF NOT EXISTS feeding_classifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE CHECK (name IN ('Травоядное', 'Хищник')) NOT NULL
);

CREATE TABLE IF NOT EXISTS species (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(255) UNIQUE NOT NULL,
    classification INT REFERENCES feeding_classifications(id) ON DELETE CASCADE,
    climate_zone INT REFERENCES climate_zones(id) ON DELETE CASCADE,
    need_warm CHAR(1) CHECK (need_warm IN ('Y', 'N')) NOT NULL,
    puberty_age INT NOT NULL
);

CREATE TABLE IF NOT EXISTS enclosures (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    is_warm CHAR(1) CHECK (is_warm IN ('Y', 'N')) NOT NULL,
    length DECIMAL(6,2) NOT NULL,
    width DECIMAL(6,2) NOT NULL,
    height DECIMAL(6,2) NOT NULL,
    notes VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS enclosure_neighbors (
    enclosure1_id INT REFERENCES enclosures(id) ON DELETE CASCADE,
    enclosure2_id INT REFERENCES enclosures(id) ON DELETE CASCADE,
    PRIMARY KEY (enclosure1_id, enclosure2_id)
);

CREATE TABLE IF NOT EXISTS animals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    species_id INT REFERENCES species(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    gender CHAR(1) CHECK (gender IN ('М', 'Ж')),
    birth_date DATE NOT NULL,
    arrival_date DATE NOT NULL,
    enclosure_id INT REFERENCES enclosures(id) ON DELETE CASCADE,
    parent1_id INT REFERENCES animals(id) ON DELETE CASCADE,
    parent2_id INT REFERENCES animals(id) ON DELETE CASCADE,
    CHECK (arrival_date >= birth_date)
);

CREATE TABLE IF NOT EXISTS incompatible_species (
    species1_id INT REFERENCES species(id) ON DELETE CASCADE,
    species2_id INT REFERENCES species(id) ON DELETE CASCADE,
    PRIMARY KEY (species1_id, species2_id)
);

CREATE TABLE IF NOT EXISTS animal_movement_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    from_enclosure INT REFERENCES enclosures(id) ON DELETE CASCADE,
    to_enclosure INT REFERENCES enclosures(id) ON DELETE CASCADE,
    move_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS animal_caretakers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    staff_id INT REFERENCES staff(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE
);

CREATE TABLE IF NOT EXISTS diseases (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS vaccines (
    id INT PRIMARY KEY AUTO_INCREMENT,
    disease_id INT REFERENCES diseases(id) ON DELETE CASCADE,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS animal_vaccinations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    vaccine_id INT REFERENCES vaccines(id) ON DELETE CASCADE,
    vaccination_date DATE NOT NULL,
    next_vaccination_date DATE,
    CHECK (next_vaccination_date IS NULL OR next_vaccination_date >= vaccination_date)
);

CREATE TABLE IF NOT EXISTS animal_diseases (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    veterinarian_id INT REFERENCES staff(id) ON DELETE CASCADE,
    disease_id INT REFERENCES diseases(id) ON DELETE CASCADE,
    diagnosed_date DATE NOT NULL,
    recovery_date DATE,
    notes VARCHAR(255),
    CHECK (recovery_date IS NULL OR recovery_date >= diagnosed_date)
);

CREATE TABLE IF NOT EXISTS animal_medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    record_date DATE NOT NULL,
    weight DECIMAL(6,2),
    height DECIMAL(6,2),
    notes VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS feed_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS feed_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    feed_type INT REFERENCES feed_types(id) ON DELETE CASCADE,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS feed_suppliers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS supplier_feed_types (
    supplier_id INT REFERENCES feed_suppliers(id) ON DELETE CASCADE,
    feed_type_id INT REFERENCES feed_types(id) ON DELETE CASCADE,
    PRIMARY KEY (supplier_id, feed_type_id)
);

CREATE TABLE IF NOT EXISTS feed_inventory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    feed_item_id INT REFERENCES feed_items(id) ON DELETE CASCADE,
    quantity DECIMAL(10,2) NOT NULL,
    received_date TIMESTAMP NOT NULL,
    CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS feed_production (
    id INT PRIMARY KEY AUTO_INCREMENT,
    feed_item_id INT REFERENCES feed_items(id) ON DELETE CASCADE,
    production_date DATE NOT NULL,
    quantity DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS animal_diet_requirements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    species_id INT REFERENCES species(id) ON DELETE CASCADE,
    age_group VARCHAR(255) CHECK (age_group IN ('Молодой', 'Взрослый', 'Старый')) NOT NULL,
    body_condition VARCHAR(255) NOT NULL,
    season VARCHAR(255) CHECK (season IN ('Лето', 'Осень', 'Зима', 'Весна', 'Годовой')) NOT NULL,
    feed_type_id INT REFERENCES feed_types(id) ON DELETE CASCADE,
    required_quantity DECIMAL(6,2) NOT NULL,
    feeding_times_per_day INT NOT NULL,
    CHECK (required_quantity > 0 and feeding_times_per_day > 0)
);

CREATE TABLE IF NOT EXISTS daily_feeding_menu (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    diet_id INT REFERENCES animal_diet_requirements(id) ON DELETE CASCADE,
    feeding_number INT NOT NULL,
    feeding_date_time TIMESTAMP NOT NULL,
    feed_item_id INT REFERENCES feed_items(id) ON DELETE CASCADE,
    quantity DECIMAL(6,2) NOT NULL,
    CHECK (feeding_number > 0)
);

CREATE TABLE IF NOT EXISTS feed_orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    feed_supplier_id INT REFERENCES feed_suppliers(id) ON DELETE CASCADE,
    feed_item_id INT REFERENCES feed_items(id) ON DELETE CASCADE,
    ordered_quantity DECIMAL(10,2) NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('Оформлен', 'Доставлен', 'В пути', 'Отменен')),
    CHECK (delivery_date IS NULL OR delivery_date >= order_date)
);

CREATE TABLE IF NOT EXISTS zoo_exchanges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal_id INT REFERENCES animals(id) ON DELETE CASCADE,
    exchange_date DATE NOT NULL,
    exchange_type VARCHAR(50) CHECK (exchange_type IN ('Входящий', 'Исходящий')),
    partner_zoo VARCHAR(255) NOT NULL
);