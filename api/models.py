from flask_sqlalchemy import SQLAlchemy
from decimal import Decimal

db = SQLAlchemy()

class ToDictMixin:
    def to_dict(self):
        result = {}
        for column in self.__table__.columns:
            value = getattr(self, column.name)
            # Преобразуем даты и Decimal в строку
            if hasattr(value, 'isoformat'):
                value = value.isoformat()
            elif isinstance(value, Decimal):
                value = float(value)
            result[column.name] = value
        return result

class StaffCategory(db.Model, ToDictMixin):
    __tablename__ = 'staff_categories'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)
    enclosure_access = db.Column(db.String(1), nullable=False)

class Staff(db.Model, ToDictMixin):
    __tablename__ = 'staff'
    id = db.Column(db.Integer, primary_key=True)
    last_name = db.Column(db.String(255), nullable=False)
    first_name = db.Column(db.String(255), nullable=False)
    middle_name = db.Column(db.String(255), nullable=False)
    gender = db.Column(db.String(1))
    birth_date = db.Column(db.Date, nullable=False)
    hire_date = db.Column(db.Date, nullable=False)
    salary = db.Column(db.Numeric(10,2), nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey('staff_categories.id'))
    category = db.relationship('StaffCategory', backref='staff')
    username = db.Column(db.String(64), unique=True, nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    is_active = db.Column(db.Boolean, default=True)
    avatar_url = db.Column(db.String(255))
    avatar_original_url = db.Column(db.String(255))

class Species(db.Model, ToDictMixin):
    __tablename__ = 'species'
    id = db.Column(db.Integer, primary_key=True)
    type_name = db.Column(db.String(255), unique=True, nullable=False)
    need_warm = db.Column(db.String(1), nullable=False)

class Enclosure(db.Model, ToDictMixin):
    __tablename__ = 'enclosures'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)
    is_warm = db.Column(db.String(1), nullable=False)

class Animal(db.Model, ToDictMixin):
    __tablename__ = 'animals'
    id = db.Column(db.Integer, primary_key=True)
    species_id = db.Column(db.Integer, db.ForeignKey('species.id'))
    name = db.Column(db.String(255), nullable=False)
    gender = db.Column(db.String(1))
    birth_date = db.Column(db.Date, nullable=False)
    arrival_date = db.Column(db.Date, nullable=False)
    enclosure_id = db.Column(db.Integer, db.ForeignKey('enclosures.id'))
    parent1_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    parent2_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    species = db.relationship('Species', backref='animals')
    enclosure = db.relationship('Enclosure', backref='animals')

class ClimateZone(db.Model, ToDictMixin):
    __tablename__ = 'climate_zones'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)

class FeedingClassification(db.Model, ToDictMixin):
    __tablename__ = 'feeding_classifications'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)

class FeedType(db.Model, ToDictMixin):
    __tablename__ = 'feed_types'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)

class FeedItem(db.Model, ToDictMixin):
    __tablename__ = 'feed_items'
    id = db.Column(db.Integer, primary_key=True)
    feed_type = db.Column(db.Integer, db.ForeignKey('feed_types.id'))
    name = db.Column(db.String(255), unique=True, nullable=False)

class Disease(db.Model, ToDictMixin):
    __tablename__ = 'diseases'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)

class Vaccine(db.Model, ToDictMixin):
    __tablename__ = 'vaccines'
    id = db.Column(db.Integer, primary_key=True)
    disease_id = db.Column(db.Integer, db.ForeignKey('diseases.id'))
    name = db.Column(db.String(255), unique=True, nullable=False)

class AnimalVaccination(db.Model, ToDictMixin):
    __tablename__ = 'animal_vaccinations'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    vaccine_id = db.Column(db.Integer, db.ForeignKey('vaccines.id'))
    vaccination_date = db.Column(db.Date, nullable=False)
    next_vaccination_date = db.Column(db.Date)

class AnimalDisease(db.Model, ToDictMixin):
    __tablename__ = 'animal_diseases'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    veterinarian_id = db.Column(db.Integer, db.ForeignKey('staff.id'))
    disease_id = db.Column(db.Integer, db.ForeignKey('diseases.id'))
    diagnosed_date = db.Column(db.Date, nullable=False)
    recovery_date = db.Column(db.Date)
    notes = db.Column(db.String(255))

class AnimalMedicalRecord(db.Model, ToDictMixin):
    __tablename__ = 'animal_medical_records'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    record_date = db.Column(db.Date, nullable=False)
    weight = db.Column(db.Numeric(6,2))
    height = db.Column(db.Numeric(6,2))
    notes = db.Column(db.String(255))

class FeedSupplier(db.Model, ToDictMixin):
    __tablename__ = 'feed_suppliers'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), unique=True, nullable=False)
    phone = db.Column(db.String(20), nullable=False)
    address = db.Column(db.String(255))

class FeedInventory(db.Model, ToDictMixin):
    __tablename__ = 'feed_inventory'
    id = db.Column(db.Integer, primary_key=True)
    feed_item_id = db.Column(db.Integer, db.ForeignKey('feed_items.id'))
    quantity = db.Column(db.Numeric(10,2))
    received_date = db.Column(db.DateTime)

class FeedProduction(db.Model, ToDictMixin):
    __tablename__ = 'feed_production'
    id = db.Column(db.Integer, primary_key=True)
    feed_item_id = db.Column(db.Integer, db.ForeignKey('feed_items.id'))
    production_date = db.Column(db.Date)
    quantity = db.Column(db.Numeric(10,2))

class AnimalDietRequirement(db.Model, ToDictMixin):
    __tablename__ = 'animal_diet_requirements'
    id = db.Column(db.Integer, primary_key=True)
    species_id = db.Column(db.Integer, db.ForeignKey('species.id'))
    age_group = db.Column(db.String(255))
    body_condition = db.Column(db.String(255))
    season = db.Column(db.String(255))
    feed_type_id = db.Column(db.Integer, db.ForeignKey('feed_types.id'))
    required_quantity = db.Column(db.Numeric(6,2))
    feeding_times_per_day = db.Column(db.Integer)

class DailyFeedingMenu(db.Model, ToDictMixin):
    __tablename__ = 'daily_feeding_menu'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    diet_id = db.Column(db.Integer, db.ForeignKey('animal_diet_requirements.id'))
    feeding_number = db.Column(db.Integer)
    feeding_date_time = db.Column(db.DateTime)
    feed_item_id = db.Column(db.Integer, db.ForeignKey('feed_items.id'))
    quantity = db.Column(db.Numeric(6,2))

class FeedOrder(db.Model, ToDictMixin):
    __tablename__ = 'feed_orders'
    id = db.Column(db.Integer, primary_key=True)
    feed_supplier_id = db.Column(db.Integer, db.ForeignKey('feed_suppliers.id'))
    feed_item_id = db.Column(db.Integer, db.ForeignKey('feed_items.id'))
    ordered_quantity = db.Column(db.Numeric(10,2))
    order_date = db.Column(db.Date)
    delivery_date = db.Column(db.Date)
    price = db.Column(db.Numeric(10,2))
    status = db.Column(db.String(50))

class ZooExchange(db.Model, ToDictMixin):
    __tablename__ = 'zoo_exchanges'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    exchange_date = db.Column(db.Date)
    exchange_type = db.Column(db.String(50))
    partner_zoo = db.Column(db.String(255))

class SupplierFeedType(db.Model, ToDictMixin):
    __tablename__ = 'supplier_feed_types'
    supplier_id = db.Column(db.Integer, primary_key=True)
    feed_type_id = db.Column(db.Integer, primary_key=True)

class EnclosureNeighbor(db.Model, ToDictMixin):
    __tablename__ = 'enclosure_neighbors'
    enclosure1_id = db.Column(db.Integer, primary_key=True)
    enclosure2_id = db.Column(db.Integer, primary_key=True)

class IncompatibleSpecies(db.Model, ToDictMixin):
    __tablename__ = 'incompatible_species'
    species1_id = db.Column(db.Integer, primary_key=True)
    species2_id = db.Column(db.Integer, primary_key=True)

class AnimalMovementHistory(db.Model, ToDictMixin):
    __tablename__ = 'animal_movement_history'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    from_enclosure = db.Column(db.Integer, db.ForeignKey('enclosures.id'))
    to_enclosure = db.Column(db.Integer, db.ForeignKey('enclosures.id'))
    move_date = db.Column(db.Date)

class AnimalCaretaker(db.Model, ToDictMixin):
    __tablename__ = 'animal_caretakers'
    id = db.Column(db.Integer, primary_key=True)
    animal_id = db.Column(db.Integer, db.ForeignKey('animals.id'))
    staff_id = db.Column(db.Integer, db.ForeignKey('staff.id'))
    start_date = db.Column(db.Date)
    end_date = db.Column(db.Date)

class CategoryAttribute(db.Model, ToDictMixin):
    __tablename__ = 'category_attributes'
    id = db.Column(db.Integer, primary_key=True)
    category_id = db.Column(db.Integer, db.ForeignKey('staff_categories.id'))
    attribute_name = db.Column(db.String(255))

class StaffAttributeValue(db.Model, ToDictMixin):
    __tablename__ = 'staff_attribute_values'
    staff_id = db.Column(db.Integer, db.ForeignKey('staff.id'), primary_key=True)
    attribute_id = db.Column(db.Integer, db.ForeignKey('category_attributes.id'), primary_key=True)
    attribute_value = db.Column(db.String(255)) 