from flask_restx import Namespace, Resource, fields
from .models import db, AnimalDisease
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('animal_diseases', description='Операции с болезнями животных')

animal_disease_model = api.model('AnimalDisease', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'veterinarian_id': fields.Integer(required=True),
    'disease_id': fields.Integer(required=True),
    'diagnosed_date': fields.String(required=True),
    'recovery_date': fields.String,
    'notes': fields.String,
})

@api.route('/')
class AnimalDiseaseList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalDisease.query
        animal_id = request.args.get('animal_id', type=int)
        veterinarian_id = request.args.get('veterinarian_id', type=int)
        disease_id = request.args.get('disease_id', type=int)
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if veterinarian_id:
            query = query.filter_by(veterinarian_id=veterinarian_id)
        if disease_id:
            query = query.filter_by(disease_id=disease_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalDisease.__table__.columns:
            col = getattr(AnimalDisease, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [d.to_dict() for d in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @role_required([1, 5])
    @api.expect(animal_disease_model)
    def post(self):
        data = api.payload
        disease = AnimalDisease(**data)
        db.session.add(disease)
        db.session.commit()
        return {'message': 'Болезнь животного добавлена', 'id': disease.id}, 201

@api.route('/<int:id>')
class AnimalDiseaseResource(Resource):
    @active_user_required
    def get(self, id):
        disease = AnimalDisease.query.get_or_404(id)
        return disease.to_dict()

    @active_user_required
    @role_required([1, 5])
    @api.expect(animal_disease_model)
    def put(self, id):
        disease = AnimalDisease.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(disease, key, value)
        db.session.commit()
        return {'message': 'Болезнь животного обновлена'}

    @active_user_required
    @role_required([1, 5])
    def delete(self, id):
        disease = AnimalDisease.query.get_or_404(id)
        db.session.delete(disease)
        db.session.commit()
        return {'message': 'Болезнь животного удалена'} 