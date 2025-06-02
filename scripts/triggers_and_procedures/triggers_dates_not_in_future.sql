DELIMITER //

CREATE TRIGGER trg_staff_birth_date_insert
BEFORE INSERT ON staff
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения не может быть в будущем');
END;//
CREATE TRIGGER trg_staff_birth_date_update
BEFORE UPDATE ON staff
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения не может быть в будущем');
END;//

CREATE TRIGGER trg_staff_hire_date_insert
BEFORE INSERT ON staff
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.hire_date, 'Дата найма не может быть в будущем');
END;//
CREATE TRIGGER trg_staff_hire_date_update
BEFORE UPDATE ON staff
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.hire_date, 'Дата найма не может быть в будущем');
END;//

CREATE TRIGGER trg_animals_birth_date_insert
BEFORE INSERT ON animals
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения животного не может быть в будущем');
END;//
CREATE TRIGGER trg_animals_birth_date_update
BEFORE UPDATE ON animals
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения животного не может быть в будущем');
END;//

CREATE TRIGGER trg_animals_arrival_date_insert
BEFORE INSERT ON animals
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.arrival_date, 'Дата поступления животного не может быть в будущем');
END;//
CREATE TRIGGER trg_animals_arrival_date_update
BEFORE UPDATE ON animals
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.arrival_date, 'Дата поступления животного не может быть в будущем');
END;//

CREATE TRIGGER trg_animal_caretakers_start_date_insert
BEFORE INSERT ON animal_caretakers
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.start_date, 'Дата начала ухода не может быть в будущем');
END;//
CREATE TRIGGER trg_animal_caretakers_start_date_update
BEFORE UPDATE ON animal_caretakers
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.start_date, 'Дата начала ухода не может быть в будущем');
END;//

CREATE TRIGGER trg_animal_vaccinations_vaccination_date_insert
BEFORE INSERT ON animal_vaccinations
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.vaccination_date, 'Дата вакцинации не может быть в будущем');
END;//
CREATE TRIGGER trg_animal_vaccinations_vaccination_date_update
BEFORE UPDATE ON animal_vaccinations
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.vaccination_date, 'Дата вакцинации не может быть в будущем');
END;//

CREATE TRIGGER trg_animal_diseases_diagnosed_date_insert
BEFORE INSERT ON animal_diseases
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.diagnosed_date, 'Дата диагноза не может быть в будущем');
END;//
CREATE TRIGGER trg_animal_diseases_diagnosed_date_update
BEFORE UPDATE ON animal_diseases
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.diagnosed_date, 'Дата диагноза не может быть в будущем');
END;//

CREATE TRIGGER trg_animal_medical_records_record_date_insert
BEFORE INSERT ON animal_medical_records
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.record_date, 'Дата медосмотра не может быть в будущем');
END;//
CREATE TRIGGER trg_animal_medical_records_record_date_update
BEFORE UPDATE ON animal_medical_records
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.record_date, 'Дата медосмотра не может быть в будущем');
END;//

CREATE TRIGGER trg_feed_production_production_date_insert
BEFORE INSERT ON feed_production
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.production_date, 'Дата производства не может быть в будущем');
END;//
CREATE TRIGGER trg_feed_production_production_date_update
BEFORE UPDATE ON feed_production
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.production_date, 'Дата производства не может быть в будущем');
END;//

CREATE TRIGGER trg_daily_feeding_menu_feeding_date_time_insert
BEFORE INSERT ON daily_feeding_menu
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.feeding_date_time, 'Дата кормления не может быть в будущем');
END;//
CREATE TRIGGER trg_daily_feeding_menu_feeding_date_time_update
BEFORE UPDATE ON daily_feeding_menu
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.feeding_date_time, 'Дата кормления не может быть в будущем');
END;//

CREATE TRIGGER trg_feed_orders_order_date_insert
BEFORE INSERT ON feed_orders
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.order_date, 'Дата заказа не может быть в будущем');
END;//
CREATE TRIGGER trg_feed_orders_order_date_update
BEFORE UPDATE ON feed_orders
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.order_date, 'Дата заказа не может быть в будущем');
END;//

CREATE TRIGGER trg_zoo_exchanges_exchange_date_insert
BEFORE INSERT ON zoo_exchanges
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.exchange_date, 'Дата обмена не может быть в будущем');
END;//
CREATE TRIGGER trg_zoo_exchanges_exchange_date_update
BEFORE UPDATE ON zoo_exchanges
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.exchange_date, 'Дата обмена не может быть в будущем');
END;//

CREATE TRIGGER trg_animal_movement_history_move_date_insert
BEFORE INSERT ON animal_movement_history
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.move_date, 'Дата перемещения не может быть в будущем');
END;//
CREATE TRIGGER trg_animal_movement_history_move_date_update
BEFORE UPDATE ON animal_movement_history
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.move_date, 'Дата перемещения не может быть в будущем');
END;//

CREATE TRIGGER trg_feed_inventory_received_date_insert
BEFORE INSERT ON feed_inventory
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.received_date, 'Дата поступления корма не может быть в будущем');
END;//
CREATE TRIGGER trg_feed_inventory_received_date_update
BEFORE UPDATE ON feed_inventory
FOR EACH ROW
BEGIN
    CALL check_date_not_in_future(NEW.received_date, 'Дата поступления корма не может быть в будущем');
END;//

DELIMITER ; 