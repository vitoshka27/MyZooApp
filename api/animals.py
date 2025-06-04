from flask_restx import Namespace, Resource, fields
from .models import db, Animal
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc, text

api = Namespace('animals', description='Операции с животными')

animal_model = api.model('Animal', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
    'species_id': fields.Integer(required=True),
    'gender': fields.String(required=True),
    'birth_date': fields.String(required=True),
    'arrival_date': fields.String(required=True),
    'enclosure_id': fields.Integer(required=True),
    'parent1_id': fields.Integer,
    'parent2_id': fields.Integer,
})

@api.route('/')
class AnimalList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Animal.query
        # Фильтрация
        name = request.args.get('name')
        species_id = request.args.get('species_id', type=int)
        gender = request.args.get('gender')
        enclosure_id = request.args.get('enclosure_id', type=int)
        if name:
            query = query.filter(Animal.name.ilike(f'%{name}%'))
        if species_id:
            query = query.filter_by(species_id=species_id)
        if gender:
            query = query.filter_by(gender=gender)
        if enclosure_id:
            query = query.filter_by(enclosure_id=enclosure_id)
        # Сортировка
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Animal.__table__.columns:
            col = getattr(Animal, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        # Пагинация
        items = [a.to_dict() for a in query.all()]
        return {
            'total': len(items),
            'data': items
        }

    @active_user_required
    @role_required([5])
    @api.expect(animal_model)
    def post(self):
        data = api.payload
        animal = Animal(**data)
        db.session.add(animal)
        db.session.commit()
        return {'message': 'Животное добавлено', 'id': animal.id}, 201

@api.route('/<int:id>')
class AnimalResource(Resource):
    @active_user_required
    def get(self, id):
        animal = Animal.query.get_or_404(id)
        return animal.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(animal_model)
    def put(self, id):
        animal = Animal.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(animal, key, value)
        db.session.commit()
        return {'message': 'Животное обновлено'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        animal = Animal.query.get_or_404(id)
        db.session.delete(animal)
        db.session.commit()
        return {'message': 'Животное удалено'} 

@api.route('/<int:id>/offspring_count')
class AnimalOffspringCountResource(Resource):
    @active_user_required
    def get(self, id):
        # Вызов процедуры
        db.session.execute(text('CALL get_all_offspring_count(:animal_id, @offspring_count)'), {'animal_id': id})
        # Получение результата
        result = db.session.execute(text('SELECT @offspring_count as offspring_count'))
        offspring_count = result.scalar()
        return {'animal_id': id, 'offspring_count': offspring_count}
