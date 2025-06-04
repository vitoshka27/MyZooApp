from flask_restx import Namespace, Resource, fields
from .models import db, AnimalVaccination
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc


api = Namespace('animal_vaccinations', description='Операции с вакцинациями животных')

animal_vaccination_model = api.model('AnimalVaccination', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'vaccine_id': fields.Integer(required=True),
    'vaccination_date': fields.String(required=True),
    'next_vaccination_date': fields.String,
})

@api.route('/')
class AnimalVaccinationList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalVaccination.query
        animal_id = request.args.get('animal_id', type=int)
        vaccine_id = request.args.get('vaccine_id', type=int)
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if vaccine_id:
            query = query.filter_by(vaccine_id=vaccine_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalVaccination.__table__.columns:
            col = getattr(AnimalVaccination, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        items = [a.to_dict() for a in query.all()]
        return {
            'total': len(items),
            'data': items
        }

    @active_user_required
    @role_required([1, 5])
    @api.expect(animal_vaccination_model)
    def post(self):
        data = api.payload
        vaccination = AnimalVaccination(**data)
        db.session.add(vaccination)
        db.session.commit()
        return {'message': 'Вакцинация добавлена', 'id': vaccination.id}, 201

@api.route('/<int:id>')
class AnimalVaccinationResource(Resource):
    @active_user_required
    def get(self, id):
        vaccination = AnimalVaccination.query.get_or_404(id)
        return vaccination.to_dict()

    @active_user_required
    @role_required([1, 5])
    @api.expect(animal_vaccination_model)
    def put(self, id):
        vaccination = AnimalVaccination.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(vaccination, key, value)
        db.session.commit()
        return {'message': 'Вакцинация обновлена'}

    @active_user_required
    @role_required([1, 5])
    def delete(self, id):
        vaccination = AnimalVaccination.query.get_or_404(id)
        db.session.delete(vaccination)
        db.session.commit()
        return {'message': 'Вакцинация удалена'} 