from flask_restx import Namespace, Resource, fields
from .models import db, Enclosure
from api.decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('enclosures', description='Операции с вольерами')

enclosure_model = api.model('Enclosure', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
    'is_warm': fields.String(required=True),
})

@api.route('/')
class EnclosureList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Enclosure.query
        name = request.args.get('name')
        is_warm = request.args.get('is_warm')
        if name:
            query = query.filter(Enclosure.name.ilike(f'%{name}%'))
        if is_warm:
            query = query.filter_by(is_warm=is_warm)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Enclosure.__table__.columns:
            col = getattr(Enclosure, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [e.to_dict() for e in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @role_required([4, 5])
    @api.expect(enclosure_model)
    def post(self):
        data = api.payload
        enclosure = Enclosure(**data)
        db.session.add(enclosure)
        db.session.commit()
        return {'message': 'Вольер добавлен', 'id': enclosure.id}, 201

@api.route('/<int:id>')
class EnclosureResource(Resource):
    @active_user_required
    def get(self, id):
        enclosure = Enclosure.query.get_or_404(id)
        return enclosure.to_dict()

    @active_user_required
    @role_required([4, 5])
    @api.expect(enclosure_model)
    def put(self, id):
        enclosure = Enclosure.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(enclosure, key, value)
        db.session.commit()
        return {'message': 'Вольер обновлен'}

    @active_user_required
    @role_required([4, 5])
    def delete(self, id):
        enclosure = Enclosure.query.get_or_404(id)
        db.session.delete(enclosure)
        db.session.commit()
        return {'message': 'Вольер удален'} 