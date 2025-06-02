from flask_restx import Namespace, Resource, fields
from .models import db, FeedProduction
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('feed_production', description='Операции с производством корма')

feed_production_model = api.model('FeedProduction', {
    'id': fields.Integer(readonly=True),
    'feed_item_id': fields.Integer(required=True),
    'production_date': fields.String(required=True),
    'quantity': fields.Float(required=True),
})

@api.route('/')
class FeedProductionList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedProduction.query
        feed_item_id = request.args.get('feed_item_id', type=int)
        production_date = request.args.get('production_date')
        if feed_item_id:
            query = query.filter_by(feed_item_id=feed_item_id)
        if production_date:
            query = query.filter_by(production_date=production_date)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedProduction.__table__.columns:
            col = getattr(FeedProduction, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [p.to_dict() for p in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @role_required([5])
    @api.expect(feed_production_model)
    def post(self):
        data = api.payload
        production = FeedProduction(**data)
        db.session.add(production)
        db.session.commit()
        return {'message': 'Производство корма добавлено', 'id': production.id}, 201

@api.route('/<int:id>')
class FeedProductionResource(Resource):
    @active_user_required
    def get(self, id):
        production = FeedProduction.query.get_or_404(id)
        return production.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(feed_production_model)
    def put(self, id):
        production = FeedProduction.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(production, key, value)
        db.session.commit()
        return {'message': 'Производство корма обновлено'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        production = FeedProduction.query.get_or_404(id)
        db.session.delete(production)
        db.session.commit()
        return {'message': 'Производство корма удалено'} 