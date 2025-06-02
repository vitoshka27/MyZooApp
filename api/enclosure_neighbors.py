from flask_restx import Namespace, Resource, fields
from .models import db, EnclosureNeighbor
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('enclosure_neighbors', description='Операции с соседями вольеров')

enclosure_neighbor_model = api.model('EnclosureNeighbor', {
    'enclosure1_id': fields.Integer(required=True),
    'enclosure2_id': fields.Integer(required=True),
})

@api.route('/')
class EnclosureNeighborList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = EnclosureNeighbor.query
        enclosure1_id = request.args.get('enclosure1_id', type=int)
        enclosure2_id = request.args.get('enclosure2_id', type=int)
        if enclosure1_id:
            query = query.filter_by(enclosure1_id=enclosure1_id)
        if enclosure2_id:
            query = query.filter_by(enclosure2_id=enclosure2_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in EnclosureNeighbor.__table__.columns:
            col = getattr(EnclosureNeighbor, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [a.to_dict() for a in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @role_required([4, 5])
    @api.expect(enclosure_neighbor_model)
    def post(self):
        data = api.payload
        record = EnclosureNeighbor(**data)
        db.session.add(record)
        db.session.commit()
        return {'message': 'Связь добавлена'}, 201

@api.route('/<int:enclosure1_id>/<int:enclosure2_id>')
class EnclosureNeighborResource(Resource):
    @active_user_required
    @role_required([4, 5])
    def get(self, enclosure1_id, enclosure2_id):
        record = EnclosureNeighbor.query.get_or_404((enclosure1_id, enclosure2_id))
        return record.to_dict()

    @active_user_required
    @role_required([4, 5])
    @api.expect(enclosure_neighbor_model)
    def put(self, enclosure1_id, enclosure2_id):
        record = EnclosureNeighbor.query.get_or_404((enclosure1_id, enclosure2_id))
        for key, value in api.payload.items():
            setattr(record, key, value)
        db.session.commit()
        return {'message': 'Связь обновлена'}

    @active_user_required
    @role_required([4, 5])
    def delete(self, enclosure1_id, enclosure2_id):
        record = EnclosureNeighbor.query.get_or_404((enclosure1_id, enclosure2_id))
        db.session.delete(record)
        db.session.commit()
        return {'message': 'Связь удалена'} 