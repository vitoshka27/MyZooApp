from flask_restx import Namespace, Resource, fields
from .models import db, FeedItem
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

api = Namespace('feed_items', description='Операции с позициями корма')

feed_item_model = api.model('FeedItem', {
    'id': fields.Integer(readonly=True),
    'feed_type': fields.Integer(required=True),
    'name': fields.String(required=True),
})

@api.route('/')
class FeedItemList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedItem.query
        feed_type = request.args.get('feed_type', type=int)
        name = request.args.get('name')
        if feed_type:
            query = query.filter_by(feed_type=feed_type)
        if name:
            query = query.filter(FeedItem.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedItem.__table__.columns:
            col = getattr(FeedItem, order_by)
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
    @api.expect(feed_item_model)
    def post(self):
        data = api.payload
        feed_item = FeedItem(**data)
        db.session.add(feed_item)
        db.session.commit()
        return {'message': 'Позиция корма добавлена', 'id': feed_item.id}, 201

@api.route('/<int:id>')
class FeedItemResource(Resource):
    @active_user_required
    def get(self, id):
        feed_item = FeedItem.query.get_or_404(id)
        return feed_item.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(feed_item_model)
    def put(self, id):
        feed_item = FeedItem.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(feed_item, key, value)
        db.session.commit()
        return {'message': 'Позиция корма обновлена'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        feed_item = FeedItem.query.get_or_404(id)
        db.session.delete(feed_item)
        db.session.commit()
        return {'message': 'Позиция корма удалена'} 