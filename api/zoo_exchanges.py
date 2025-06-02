from flask_restx import Namespace, Resource, fields
from .models import db, ZooExchange
from api.decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('zoo_exchanges', description='Операции с обменами животных между зоопарками')

zoo_exchange_model = api.model('ZooExchange', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'exchange_date': fields.String(required=True),
    'exchange_type': fields.String(required=True),
    'partner_zoo': fields.String(required=True),
})

@api.route('/')
class ZooExchangeList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = ZooExchange.query
        animal_id = request.args.get('animal_id', type=int)
        exchange_type = request.args.get('exchange_type')
        partner_zoo = request.args.get('partner_zoo')
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if exchange_type:
            query = query.filter_by(exchange_type=exchange_type)
        if partner_zoo:
            query = query.filter(ZooExchange.partner_zoo.ilike(f'%{partner_zoo}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in ZooExchange.__table__.columns:
            col = getattr(ZooExchange, order_by)
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
    @role_required([5])
    @api.expect(zoo_exchange_model)
    def post(self):
        data = api.payload
        exchange = ZooExchange(**data)
        db.session.add(exchange)
        db.session.commit()
        return {'message': 'Обмен добавлен', 'id': exchange.id}, 201

@api.route('/<int:id>')
class ZooExchangeResource(Resource):
    @active_user_required
    def get(self, id):
        exchange = ZooExchange.query.get_or_404(id)
        return exchange.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(zoo_exchange_model)
    def put(self, id):
        exchange = ZooExchange.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(exchange, key, value)
        db.session.commit()
        return {'message': 'Обмен обновлен'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        exchange = ZooExchange.query.get_or_404(id)
        db.session.delete(exchange)
        db.session.commit()
        return {'message': 'Обмен удален'} 