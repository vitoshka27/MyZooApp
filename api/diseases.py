from flask_restx import Namespace, Resource, fields
from .models import db, Disease
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('diseases', description='Операции с болезнями')

disease_model = api.model('Disease', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
})

@api.route('/')
class DiseaseList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Disease.query
        name = request.args.get('name')
        if name:
            query = query.filter(Disease.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Disease.__table__.columns:
            col = getattr(Disease, order_by)
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
    @api.expect(disease_model)
    def post(self):
        data = api.payload
        disease = Disease(**data)
        db.session.add(disease)
        db.session.commit()
        return {'message': 'Болезнь добавлена', 'id': disease.id}, 201

@api.route('/<int:id>')
class DiseaseResource(Resource):
    @active_user_required
    def get(self, id):
        disease = Disease.query.get_or_404(id)
        return disease.to_dict()

    @active_user_required
    @api.expect(disease_model)
    def put(self, id):
        disease = Disease.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(disease, key, value)
        db.session.commit()
        return {'message': 'Болезнь обновлена'}

    @active_user_required
    def delete(self, id):
        disease = Disease.query.get_or_404(id)
        db.session.delete(disease)
        db.session.commit()
        return {'message': 'Болезнь удалена'} 