from flask_restx import Namespace, Resource, fields
from .models import db
from flask import request
from sqlalchemy import text
import datetime
from decimal import Decimal
from .decorators import active_user_required

api = Namespace('custom_queries', description='Пользовательские SQL-запросы')

@api.route('/query1')
class Query1Resource(Resource):
    @active_user_required
    @api.param('category_id', 'ID категории сотрудника', type=int)
    @api.param('gender', 'Пол сотрудника', type=str)
    @api.param('salary_min', 'Минимальная зарплата', type=float)
    @api.param('salary_max', 'Максимальная зарплата', type=float)
    @api.param('years_worked_min', 'Минимальный стаж (лет)', type=int)
    @api.param('years_worked_max', 'Максимальный стаж (лет)', type=int)
    @api.param('age_min', 'Минимальный возраст', type=int)
    @api.param('age_max', 'Максимальный возраст', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        # Получаем параметры фильтрации
        category_id = request.args.get('category_id', type=int)
        gender = request.args.get('gender')
        salary_min = request.args.get('salary_min', type=float)
        salary_max = request.args.get('salary_max', type=float)
        years_worked_min = request.args.get('years_worked_min', type=int)
        years_worked_max = request.args.get('years_worked_max', type=int)
        age_min = request.args.get('age_min', type=int)
        age_max = request.args.get('age_max', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        # Формируем SQL с учётом фильтров
        sql = '''
            SELECT 
                s.last_name,
                s.first_name,
                s.middle_name,
                s.gender,
                s.birth_date,
                get_years_diff(s.birth_date) AS age,
                s.hire_date,
                get_years_diff(s.hire_date) AS years_worked,
                s.salary,
                sc.name AS category,
                COUNT(*) OVER () AS total_employees
            FROM staff s
            JOIN staff_categories sc ON s.category_id = sc.id
            WHERE 1=1
        '''
        params = {}
        if category_id:
            sql += ' AND s.category_id = :category_id'
            params['category_id'] = category_id
        if gender:
            sql += ' AND s.gender = :gender'
            params['gender'] = gender
        if salary_min is not None:
            sql += ' AND s.salary >= :salary_min'
            params['salary_min'] = salary_min
        if salary_max is not None:
            sql += ' AND s.salary <= :salary_max'
            params['salary_max'] = salary_max
        if years_worked_min is not None:
            sql += ' AND get_years_diff(s.hire_date) >= :years_worked_min'
            params['years_worked_min'] = years_worked_min
        if years_worked_max is not None:
            sql += ' AND get_years_diff(s.hire_date) <= :years_worked_max'
            params['years_worked_max'] = years_worked_max
        if age_min is not None:
            sql += ' AND get_years_diff(s.birth_date) >= :age_min'
            params['age_min'] = age_min
        if age_max is not None:
            sql += ' AND get_years_diff(s.birth_date) <= :age_max'
            params['age_max'] = age_max

        # Сортировка
        allowed_order = {'years_worked', 'age', 'salary', 'last_name', 'first_name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY s.last_name, s.first_name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query2')
class Query2Resource(Resource):
    @active_user_required
    @api.param('animal_id', 'ID животного', type=int, required=False)
    @api.param('start_date', 'Дата начала ухода (YYYY-MM-DD)', type=str)
    @api.param('end_date', 'Дата окончания ухода (YYYY-MM-DD)', type=str)
    @api.param('gender', 'Пол сотрудника', type=str)
    @api.param('category_id', 'ID категории сотрудника', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        animal_id = request.args.get('animal_id', type=int)
        start_date = request.args.get('start_date')
        end_date = request.args.get('end_date')
        gender = request.args.get('gender')
        category_id = request.args.get('category_id', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
            SELECT 
                s.last_name,
                s.first_name,
                s.middle_name,
                s.gender,
                s.birth_date,
                get_years_diff(s.birth_date) AS age,
                s.hire_date,
                get_years_diff(s.hire_date) AS years_worked,
                s.salary,
                sc.name AS category,
                a.id AS animal_id,
                a.name AS animal_name,
                ac.start_date AS start_date,
                ac.end_date AS end_date,
                COUNT(*) OVER () AS total_caretakers
            FROM staff s
            JOIN staff_categories sc ON s.category_id = sc.id
            JOIN animal_caretakers ac ON s.id = ac.staff_id
            JOIN animals a ON ac.animal_id = a.id
            WHERE 1 = 1
        '''
        params = {}
        if animal_id:
            sql += ' AND ac.animal_id = :animal_id'
            params['animal_id'] = animal_id
        if start_date:
            sql += ' AND ac.start_date >= :start_date'
            params['start_date'] = start_date
        if end_date:
            sql += ' AND (ac.end_date <= :end_date OR ac.end_date IS NULL)'
            params['end_date'] = end_date
        if category_id:
            sql += ' AND s.category_id = :category_id'
            params['category_id'] = category_id

        allowed_order = {'years_worked', 'age', 'salary', 'last_name', 'first_name', 'start_date', 'end_date'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY s.last_name, s.first_name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query4')
class Query4Resource(Resource):
    @active_user_required
    @api.param('species_id', 'ID вида', type=int)
    @api.param('enclosure_id', 'ID вольера', type=int)
    @api.param('gender', 'Пол животного', type=str)
    @api.param('age_min', 'Минимальный возраст', type=int)
    @api.param('age_max', 'Максимальный возраст', type=int)
    @api.param('weight_min', 'Минимальный вес', type=float)
    @api.param('weight_max', 'Максимальный вес', type=float)
    @api.param('height_min', 'Минимальный рост', type=float)
    @api.param('height_max', 'Максимальный рост', type=float)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        species_id = request.args.get('species_id', type=int)
        enclosure_id = request.args.get('enclosure_id', type=int)
        gender = request.args.get('gender')
        age_min = request.args.get('age_min', type=int)
        age_max = request.args.get('age_max', type=int)
        weight_min = request.args.get('weight_min', type=float)
        weight_max = request.args.get('weight_max', type=float)
        height_min = request.args.get('height_min', type=float)
        height_max = request.args.get('height_max', type=float)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH last_medical AS (
          SELECT 
            m1.animal_id,
            m1.weight,
            m1.height
          FROM animal_medical_records m1
          JOIN (
            SELECT animal_id, MAX(record_date) AS max_date
            FROM animal_medical_records
            GROUP BY animal_id
          ) m2 
            ON m1.animal_id = m2.animal_id 
           AND m1.record_date = m2.max_date
        )
        SELECT 
            a.id,
            a.name,
            a.gender,
            a.birth_date,
            get_years_diff(a.birth_date) AS age,
            sp.type_name AS species,
            e.name AS enclosure,
            lm.weight,
            lm.height,
            COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN species sp ON a.species_id = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        LEFT JOIN last_medical lm ON a.id = lm.animal_id
        WHERE 1 = 1
        '''
        params = {}
        if species_id:
            sql += ' AND sp.id = :species_id'
            params['species_id'] = species_id
        if enclosure_id:
            sql += ' AND e.id = :enclosure_id'
            params['enclosure_id'] = enclosure_id
        if gender:
            sql += ' AND a.gender = :gender'
            params['gender'] = gender
        if age_min is not None:
            sql += ' AND get_years_diff(a.birth_date) >= :age_min'
            params['age_min'] = age_min
        if age_max is not None:
            sql += ' AND get_years_diff(a.birth_date) <= :age_max'
            params['age_max'] = age_max
        if weight_min is not None:
            sql += ' AND lm.weight >= :weight_min'
            params['weight_min'] = weight_min
        if weight_max is not None:
            sql += ' AND lm.weight <= :weight_max'
            params['weight_max'] = weight_max
        if height_min is not None:
            sql += ' AND lm.height >= :height_min'
            params['height_min'] = height_min
        if height_max is not None:
            sql += ' AND lm.height <= :height_max'
            params['height_max'] = height_max

        allowed_order = {'age', 'weight', 'height', 'name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query5')
class Query5Resource(Resource):
    @active_user_required
    @api.param('species_id', 'ID вида', type=int)
    @api.param('age_min', 'Минимальный возраст', type=int)
    @api.param('age_max', 'Максимальный возраст', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        species_id = request.args.get('species_id', type=int)
        age_min = request.args.get('age_min', type=int)
        age_max = request.args.get('age_max', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        SELECT 
            a.id,
            a.name,
            a.gender,
            a.birth_date,
            get_years_diff(a.birth_date) AS age,
            get_years_diff(a.arrival_date) AS years_in_zoo,
            sp.type_name AS species,
            e.name AS enclosure,
            COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN species sp ON a.species_id = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        WHERE sp.need_warm = 'Y'
        '''
        params = {}
        if species_id:
            sql += ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        if age_min is not None:
            sql += ' AND get_years_diff(a.birth_date) >= :age_min'
            params['age_min'] = age_min
        if age_max is not None:
            sql += ' AND get_years_diff(a.birth_date) <= :age_max'
            params['age_max'] = age_max

        allowed_order = {'age', 'years_in_zoo', 'name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query6')
class Query6Resource(Resource):
    @active_user_required
    @api.param('vaccine_id', 'ID вакцины', type=int)
    @api.param('disease_id', 'ID болезни', type=int)
    @api.param('species_id', 'ID вида', type=int)
    @api.param('years_in_zoo_min', 'Минимальный стаж в зоопарке (лет)', type=int)
    @api.param('years_in_zoo_max', 'Максимальный стаж в зоопарке (лет)', type=int)
    @api.param('age_min', 'Минимальный возраст', type=int)
    @api.param('age_max', 'Максимальный возраст', type=int)
    @api.param('gender', 'Пол животного', type=str)
    @api.param('offspring_min', 'Минимальное количество потомков', type=int)
    @api.param('offspring_max', 'Максимальное количество потомков', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        vaccine_id = request.args.get('vaccine_id', type=int)
        disease_id = request.args.get('disease_id', type=int)
        species_id = request.args.get('species_id', type=int)
        years_in_zoo_min = request.args.get('years_in_zoo_min', type=int)
        years_in_zoo_max = request.args.get('years_in_zoo_max', type=int)
        age_min = request.args.get('age_min', type=int)
        age_max = request.args.get('age_max', type=int)
        gender = request.args.get('gender')
        offspring_min = request.args.get('offspring_min', type=int)
        offspring_max = request.args.get('offspring_max', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH offspring AS (
          SELECT a.id AS animal_id,
         (SELECT COUNT(*) FROM (
	      SELECT parent1_id FROM animals WHERE parent1_id = a.id
	      UNION ALL
	      SELECT parent2_id FROM animals WHERE parent2_id = a.id
	    ) AS t
          )AS offspring_count
          FROM animals a
        ),
        vaccinations AS (
            SELECT 
                av.animal_id,
                GROUP_CONCAT(
                CONCAT(v.name, ' (', av.vaccination_date, ')') 
                ORDER BY av.vaccination_date 
                SEPARATOR '; '
                ) AS all_vaccines
            FROM animal_vaccinations av
            JOIN vaccines v ON av.vaccine_id = v.id
            GROUP BY av.animal_id
        ),
        diseases AS (
            SELECT 
                ad.animal_id,
                GROUP_CONCAT(
                CONCAT(d.name, ' (', ad.diagnosed_date, ')') 
                ORDER BY ad.diagnosed_date 
                SEPARATOR '; '
                ) AS all_diseases
            FROM animal_diseases ad
            JOIN diseases d ON ad.disease_id = d.id
            GROUP BY ad.animal_id
        )
        SELECT 
            a.id,
            a.name,
            a.gender,
            a.birth_date,
            get_years_diff(a.birth_date) AS age,
            a.arrival_date,
            get_years_diff(a.arrival_date) AS years_in_zoo,
            sp.type_name AS species,
            e.name AS enclosure,
            o.offspring_count,
            vac.all_vaccines AS vaccinations,
            dis.all_diseases AS diseases,
            COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN species sp ON a.species_id = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        JOIN offspring o ON a.id = o.animal_id
        LEFT JOIN vaccinations vac ON a.id = vac.animal_id
        LEFT JOIN diseases dis ON a.id = dis.animal_id
        WHERE 1 = 1
        '''
        params = {}
        if vaccine_id:
            sql += ' AND a.id IN (SELECT animal_id FROM animal_vaccinations WHERE vaccine_id = :vaccine_id)'
            params['vaccine_id'] = vaccine_id
        if disease_id:
            sql += ' AND a.id IN (SELECT animal_id FROM animal_diseases WHERE disease_id = :disease_id)'
            params['disease_id'] = disease_id
        if species_id:
            sql += ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        if years_in_zoo_min is not None:
            sql += ' AND get_years_diff(a.arrival_date) >= :years_in_zoo_min'
            params['years_in_zoo_min'] = years_in_zoo_min
        if years_in_zoo_max is not None:
            sql += ' AND get_years_diff(a.arrival_date) <= :years_in_zoo_max'
            params['years_in_zoo_max'] = years_in_zoo_max
        if age_min is not None:
            sql += ' AND get_years_diff(a.birth_date) >= :age_min'
            params['age_min'] = age_min
        if age_max is not None:
            sql += ' AND get_years_diff(a.birth_date) <= :age_max'
            params['age_max'] = age_max
        if gender:
            sql += ' AND a.gender = :gender'
            params['gender'] = gender
        if offspring_min is not None:
            sql += ' AND o.offspring_count >= :offspring_min'
            params['offspring_min'] = offspring_min
        if offspring_max is not None:
            sql += ' AND o.offspring_count <= :offspring_max'
            params['offspring_max'] = offspring_max

        allowed_order = {'age', 'years_in_zoo', 'offspring_count', 'name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query7')
class Query7Resource(Resource):
    @active_user_required
    @api.param('need_warm', 'Требует тепло (Y/N)', type=str)
    @api.param('compatible_with_species_id', 'ID вида, с которым нужна совместимость', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        need_warm = request.args.get('need_warm')
        compatible_with_species_id = request.args.get('compatible_with_species_id', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH all_incompatible AS (
            SELECT species1_id AS sp1, species2_id AS sp2 FROM incompatible_species
            UNION
            SELECT species2_id AS sp1, species1_id AS sp2 FROM incompatible_species
        )
        SELECT 
            a.id,
            a.name,
            a.gender,
            a.birth_date,
            get_years_diff(a.birth_date) AS age,
            get_years_diff(a.arrival_date) AS years_in_zoo,
            sp.type_name AS species,
            e.name AS enclosure,
            COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN species sp ON a.species_id = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        WHERE 1 = 1
        '''
        params = {}
        if need_warm in ('Y', 'N'):
            sql += ' AND sp.need_warm = :need_warm'
            params['need_warm'] = need_warm
        if compatible_with_species_id:
            sql += ' AND a.species_id NOT IN (SELECT sp2 FROM all_incompatible WHERE sp1 = :compatible_with_species_id)'
            params['compatible_with_species_id'] = compatible_with_species_id

        allowed_order = {'age', 'years_in_zoo', 'name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query8')
class Query8Resource(Resource):
    @active_user_required
    @api.param('feed_type_id', 'ID типа корма', type=int)
    @api.param('order_date_start', 'Дата начала заказа (YYYY-MM-DD)', type=str)
    @api.param('order_date_end', 'Дата конца заказа (YYYY-MM-DD)', type=str)
    @api.param('quantity_min', 'Минимальное количество', type=float)
    @api.param('quantity_max', 'Максимальное количество', type=float)
    @api.param('price_min', 'Минимальная цена', type=float)
    @api.param('price_max', 'Максимальная цена', type=float)
    @api.param('delivery_date_start', 'Дата начала доставки (YYYY-MM-DD)', type=str)
    @api.param('delivery_date_end', 'Дата конца доставки (YYYY-MM-DD)', type=str)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        feed_type_id = request.args.get('feed_type_id', type=int)
        order_date_start = request.args.get('order_date_start')
        order_date_end = request.args.get('order_date_end')
        quantity_min = request.args.get('quantity_min', type=float)
        quantity_max = request.args.get('quantity_max', type=float)
        price_min = request.args.get('price_min', type=float)
        price_max = request.args.get('price_max', type=float)
        delivery_date_start = request.args.get('delivery_date_start')
        delivery_date_end = request.args.get('delivery_date_end')
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        SELECT
            fs.id AS id,
            fs.name,
            fs.phone,
            fs.address,
            COUNT(DISTINCT fo.id) AS order_count,
            SUM(fo.ordered_quantity) AS total_ordered_quantity,
            AVG(fo.price) AS avg_price,
            COUNT(*) OVER () AS total_suppliers
        FROM feed_suppliers fs
        JOIN feed_orders fo ON fs.id = fo.feed_supplier_id
        WHERE 1=1
        '''
        params = {}
        if feed_type_id:
            sql += ' AND fs.id IN (SELECT sft.supplier_id FROM supplier_feed_types sft WHERE sft.feed_type_id = :feed_type_id)'
            params['feed_type_id'] = feed_type_id
        if order_date_start:
            sql += ' AND fo.order_date >= :order_date_start'
            params['order_date_start'] = order_date_start
        if order_date_end:
            sql += ' AND fo.order_date <= :order_date_end'
            params['order_date_end'] = order_date_end
        if quantity_min is not None:
            sql += ' AND fo.ordered_quantity >= :quantity_min'
            params['quantity_min'] = quantity_min
        if quantity_max is not None:
            sql += ' AND fo.ordered_quantity <= :quantity_max'
            params['quantity_max'] = quantity_max
        if price_min is not None:
            sql += ' AND fo.price >= :price_min'
            params['price_min'] = price_min
        if price_max is not None:
            sql += ' AND fo.price <= :price_max'
            params['price_max'] = price_max
        if delivery_date_start:
            sql += ' AND fo.delivery_date >= :delivery_date_start'
            params['delivery_date_start'] = delivery_date_start
        if delivery_date_end:
            sql += ' AND fo.delivery_date <= :delivery_date_end'
            params['delivery_date_end'] = delivery_date_end

        sql += '\nGROUP BY fs.id, fs.name, fs.phone, fs.address'

        allowed_order = {'order_count', 'total_ordered_quantity', 'avg_price', 'name'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY fs.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query9')
class Query9Resource(Resource):
    @active_user_required
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    @api.param('feed_type_id', 'ID типа корма', type=int)
    @api.param('only_actual', 'Только те, что не нуждаются в поставках (fo.id is null)', type=int)
    def get(self):
        order_by = request.args.get('order_by', 'feed_item')
        order_dir = request.args.get('order_dir', 'asc')
        feed_type_id = request.args.get('feed_type_id', type=int)
        only_actual = request.args.get('only_actual', type=int)

        sql = '''
        SELECT
            fi.name AS feed_item,
            ft.name AS feed_type,
            SUM(fp.quantity) AS total_produced,
            COUNT(*) OVER () AS total_feed_items
        FROM feed_items fi
        JOIN feed_production fp ON fi.id = fp.feed_item_id
        JOIN feed_types ft ON fi.feed_type = ft.id
        LEFT JOIN feed_orders fo ON fi.id = fo.feed_item_id
        WHERE 1=1
        '''
        params = {}
        if feed_type_id:
            sql += ' AND ft.id = :feed_type_id'
            params['feed_type_id'] = feed_type_id
        if only_actual == 1:
            sql += ' AND fo.id IS NULL'
        sql += '\nGROUP BY fi.id, fi.name, ft.name'
        allowed_order = {'feed_item', 'feed_type', 'total_produced'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY fi.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query10')
class Query10Resource(Resource):
    @active_user_required
    @api.param('feed_type_id', 'ID типа корма', type=int)
    @api.param('season', 'Сезон', type=str)
    @api.param('age_group', 'Возрастная группа', type=str)
    @api.param('species_id', 'ID вида', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        feed_type_id = request.args.get('feed_type_id', type=int)
        season = request.args.get('season')
        age_group = request.args.get('age_group')
        species_id = request.args.get('species_id', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH animal_menu_info AS (
            SELECT DISTINCT
                dfm.animal_id,
                adr.feed_type_id,
                adr.season,
                adr.age_group,
                a.species_id
            FROM daily_feeding_menu dfm
            JOIN animal_diet_requirements adr
              ON dfm.diet_id = adr.id
            JOIN animals a ON dfm.animal_id = a.id
            WHERE 1 = 1
        '''
        params = {}
        if feed_type_id:
            sql += ' AND adr.feed_type_id = :feed_type_id'
            params['feed_type_id'] = feed_type_id
        if species_id:
            sql += ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        if season:
            sql += ' AND (adr.season = :season OR adr.season = "Годовой")'
            params['season'] = season
        if age_group:
            sql += ' AND adr.age_group = :age_group'
            params['age_group'] = age_group
        sql += '\n)'
        sql += '''
        SELECT
            a.id,
            a.name,
            a.gender,
            a.birth_date,
            get_years_diff(a.birth_date) AS age,
            sp.type_name AS species,
            e.name AS enclosure,
            ft.name     AS feed_type,
            ami.season  AS season,
            ami.age_group AS age_group,
            COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN animal_menu_info ami
          ON a.id = ami.animal_id
        JOIN feed_types ft
          ON ami.feed_type_id = ft.id
        JOIN species sp ON a.species_id = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        WHERE 1 = 1
        '''
        if species_id:
            sql += ' AND sp.id = :species_id'
        allowed_order = {'name', 'age', 'feed_type', 'season', 'age_group'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query11')
class Query11Resource(Resource):
    @active_user_required
    @api.param('species_id', 'ID вида', type=int)
    @api.param('animal_id', 'ID животного', type=int)
    @api.param('enclosure_id', 'ID вольера', type=int)
    @api.param('gender', 'Пол', type=str)
    @api.param('age_min', 'Мин. возраст', type=int)
    @api.param('age_max', 'Макс. возраст', type=int)
    @api.param('weight_min', 'Мин. вес', type=float)
    @api.param('weight_max', 'Макс. вес', type=float)
    @api.param('height_min', 'Мин. рост', type=float)
    @api.param('height_max', 'Макс. рост', type=float)
    @api.param('years_in_zoo_min', 'Мин. лет в зоопарке', type=int)
    @api.param('years_in_zoo_max', 'Макс. лет в зоопарке', type=int)
    @api.param('disease_id', 'ID болезни', type=int)
    @api.param('vaccine_id', 'ID прививки', type=int)
    @api.param('offspring_min', 'Мин. потомков', type=int)
    @api.param('offspring_max', 'Макс. потомков', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        species_id = request.args.get('species_id', type=int)
        animal_id = request.args.get('animal_id', type=int)
        enclosure_id = request.args.get('enclosure_id', type=int)
        gender = request.args.get('gender')
        age_min = request.args.get('age_min', type=int)
        age_max = request.args.get('age_max', type=int)
        weight_min = request.args.get('weight_min', type=float)
        weight_max = request.args.get('weight_max', type=float)
        height_min = request.args.get('height_min', type=float)
        height_max = request.args.get('height_max', type=float)
        years_in_zoo_min = request.args.get('years_in_zoo_min', type=int)
        years_in_zoo_max = request.args.get('years_in_zoo_max', type=int)
        disease_id = request.args.get('disease_id', type=int)
        vaccine_id = request.args.get('vaccine_id', type=int)
        offspring_min = request.args.get('offspring_min', type=int)
        offspring_max = request.args.get('offspring_max', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH offspring AS (
          SELECT 
            a.id AS animal_id,
            (SELECT COUNT(*) 
              FROM (
                SELECT parent1_id AS pid FROM animals WHERE parent1_id = a.id
                UNION ALL
                SELECT parent2_id AS pid FROM animals WHERE parent2_id = a.id
              ) t
            ) AS offspring_count
          FROM animals a
        ),
        last_medical AS (
          SELECT 
            m1.animal_id,
            m1.weight,
            m1.height
          FROM animal_medical_records m1
          JOIN (
            SELECT animal_id, MAX(record_date) AS max_date
            FROM animal_medical_records
            GROUP BY animal_id
          ) m2 
            ON m1.animal_id = m2.animal_id 
           AND m1.record_date = m2.max_date
        ),
        vaccinations AS (
          SELECT 
            av.animal_id,
            GROUP_CONCAT(
              CONCAT(v.name, ' (', av.vaccination_date, ')') 
              ORDER BY av.vaccination_date 
              SEPARATOR '; '
            ) AS all_vaccines
          FROM animal_vaccinations av
          JOIN vaccines v ON av.vaccine_id = v.id
          GROUP BY av.animal_id
        ),
        diseases AS (
          SELECT 
            ad.animal_id,
            GROUP_CONCAT(
              CONCAT(d.name, ' (', ad.diagnosed_date, ')') 
              ORDER BY ad.diagnosed_date 
              SEPARATOR '; '
            ) AS all_diseases
          FROM animal_diseases ad
          JOIN diseases d ON ad.disease_id = d.id
          GROUP BY ad.animal_id
        )
        SELECT
          a.id,
          a.name,
          a.gender,
          a.birth_date,
          get_years_diff(a.birth_date) AS age,
          a.arrival_date,
          get_years_diff(a.arrival_date) AS years_in_zoo,
          sp.type_name AS species,
          e.name AS enclosure,
          o.offspring_count,
          lm.weight,
          lm.height,
          vac.all_vaccines AS vaccinations,
          dis.all_diseases AS diseases,
          COUNT(*) OVER () AS total_animals
        FROM animals a
        JOIN species sp ON a.species_id   = sp.id
        JOIN enclosures e ON a.enclosure_id = e.id
        LEFT JOIN last_medical lm ON a.id = lm.animal_id
        LEFT JOIN offspring o ON a.id = o.animal_id
        LEFT JOIN vaccinations vac ON a.id = vac.animal_id
        LEFT JOIN diseases dis ON a.id = dis.animal_id
        WHERE 1 = 1
        '''
        params = {}
        if species_id:
            sql += ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        if animal_id:
            sql += ' AND a.id = :animal_id'
            params['animal_id'] = animal_id
        if enclosure_id:
            sql += ' AND a.enclosure_id = :enclosure_id'
            params['enclosure_id'] = enclosure_id
        if gender:
            sql += ' AND a.gender = :gender'
            params['gender'] = gender
        if age_min is not None:
            sql += ' AND get_years_diff(a.birth_date) >= :age_min'
            params['age_min'] = age_min
        if age_max is not None:
            sql += ' AND get_years_diff(a.birth_date) <= :age_max'
            params['age_max'] = age_max
        if weight_min is not None:
            sql += ' AND lm.weight >= :weight_min'
            params['weight_min'] = weight_min
        if weight_max is not None:
            sql += ' AND lm.weight <= :weight_max'
            params['weight_max'] = weight_max
        if height_min is not None:
            sql += ' AND lm.height >= :height_min'
            params['height_min'] = height_min
        if height_max is not None:
            sql += ' AND lm.height <= :height_max'
            params['height_max'] = height_max
        if years_in_zoo_min is not None:
            sql += ' AND get_years_diff(a.arrival_date) >= :years_in_zoo_min'
            params['years_in_zoo_min'] = years_in_zoo_min
        if years_in_zoo_max is not None:
            sql += ' AND get_years_diff(a.arrival_date) <= :years_in_zoo_max'
            params['years_in_zoo_max'] = years_in_zoo_max
        if disease_id:
            sql += ' AND a.id IN (SELECT animal_id FROM animal_diseases WHERE disease_id = :disease_id)'
            params['disease_id'] = disease_id
        if vaccine_id:
            sql += ' AND a.id IN (SELECT animal_id FROM animal_vaccinations WHERE vaccine_id = :vaccine_id)'
            params['vaccine_id'] = vaccine_id
        if offspring_min is not None:
            sql += ' AND o.offspring_count >= :offspring_min'
            params['offspring_min'] = offspring_min
        if offspring_max is not None:
            sql += ' AND o.offspring_count <= :offspring_max'
            params['offspring_max'] = offspring_max

        allowed_order = {'name', 'age', 'years_in_zoo', 'offspring_count', 'weight', 'height'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY a.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query12')
class Query12Resource(Resource):
    @active_user_required
    @api.param('species_id', 'ID вида', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        species_id = request.args.get('species_id', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        WITH selection AS (
          SELECT
            a.id,
            a.name,
            a.gender,
            a.species_id,
            s.type_name as species,
            get_years_diff(a.birth_date) AS age
          FROM animals a
          JOIN species s
            ON a.species_id = s.id
          WHERE
            get_years_diff(a.birth_date) >= s.puberty_age
            {species_filter}
        )
        SELECT
          sel.id,
          sel.name,
          sel.gender,
          sel.species,
          sel.age,
          COUNT(*) OVER () AS total_animals
        FROM selection sel
        WHERE EXISTS (
            SELECT 1
            FROM selection p
            WHERE p.species_id = sel.species_id
                AND p.gender <> sel.gender
        )
        '''
        params = {}
        species_filter = ''
        if species_id:
            species_filter = ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        sql = sql.format(species_filter=species_filter)
        allowed_order = {'species', 'name', 'age'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY species, sel.name'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}

@api.route('/query13')
class Query13Resource(Resource):
    @active_user_required
    @api.param('species_id', 'ID вида', type=int)
    @api.param('order_by', 'Поле сортировки', type=str)
    @api.param('order_dir', 'Направление сортировки', type=str)
    def get(self):
        species_id = request.args.get('species_id', type=int)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')

        sql = '''
        SELECT 
            ze.partner_zoo,
            COUNT(*) AS exchange_count,
            COUNT(*) OVER () AS total_zoos
        FROM zoo_exchanges ze
        JOIN animals a ON ze.animal_id = a.id
        WHERE 1 = 1
        '''
        params = {}
        if species_id:
            sql += ' AND a.species_id = :species_id'
            params['species_id'] = species_id
        sql += '\nGROUP BY ze.partner_zoo'
        allowed_order = {'partner_zoo', 'exchange_count'}
        if order_by in allowed_order:
            sql += f' ORDER BY {order_by} '
            if order_dir == 'desc':
                sql += 'DESC'
            else:
                sql += 'ASC'
        else:
            sql += ' ORDER BY ze.partner_zoo'

        def serialize_row(row):
            result = {}
            for k, v in row.items():
                if isinstance(v, (datetime.date, datetime.datetime)):
                    result[k] = v.isoformat()
                elif isinstance(v, Decimal):
                    result[k] = float(v)
                else:
                    result[k] = v
            return result

        result = db.session.execute(text(sql), params)
        rows = [serialize_row(row) for row in result.mappings()]
        return {'data': rows}
