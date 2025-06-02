from flask_restx import Namespace, Resource, fields
from .models import db, Vaccine
from flask import request
from sqlalchemy import desc
from .decorators import active_user_required


api = Namespace('vaccines', description='Операции с вакцинами')

vaccine_model = api.model('Vaccine', {
    'id': fields.Integer(readonly=True),
    'disease_id': fields.Integer(required=True),
    'name': fields.String(required=True),
})

@api.route('/')
class VaccineList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Vaccine.query
        name = request.args.get('name')
        if name:
            query = query.filter(Vaccine.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Vaccine.__table__.columns:
            col = getattr(Vaccine, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [v.to_dict() for v in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @api.expect(vaccine_model)
    def post(self):
        data = api.payload
        vaccine = Vaccine(**data)
        db.session.add(vaccine)
        db.session.commit()
        return {'message': 'Вакцина добавлена', 'id': vaccine.id}, 201

@api.route('/<int:id>')
class VaccineResource(Resource):
    @active_user_required
    @api.marshal_with(vaccine_model)
    def get(self, id):
        vaccine = Vaccine.query.get_or_404(id)
        return vaccine

    @active_user_required
    @api.expect(vaccine_model)
    def put(self, id):
        vaccine = Vaccine.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(vaccine, key, value)
        db.session.commit()
        return {'message': 'Вакцина обновлена'}

    @active_user_required
    def delete(self, id):
        vaccine = Vaccine.query.get_or_404(id)
        db.session.delete(vaccine)
        db.session.commit()
        return {'message': 'Вакцина удалена'} 