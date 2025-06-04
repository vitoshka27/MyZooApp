from flask_restx import Namespace, Resource, fields
from .models import db, FeedOrder
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('feed_orders', description='Операции с заказами на корм')

feed_order_model = api.model('FeedOrder', {
    'id': fields.Integer(readonly=True),
    'feed_supplier_id': fields.Integer(required=True),
    'feed_item_id': fields.Integer(required=True),
    'ordered_quantity': fields.Float(required=True),
    'order_date': fields.String(required=True),
    'delivery_date': fields.String,
    'price': fields.Float(required=True),
    'status': fields.String(required=True),
})

@api.route('/')
class FeedOrderList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedOrder.query
        feed_supplier_id = request.args.get('feed_supplier_id', type=int)
        feed_item_id = request.args.get('feed_item_id', type=int)
        status = request.args.get('status')
        if feed_supplier_id:
            query = query.filter_by(feed_supplier_id=feed_supplier_id)
        if feed_item_id:
            query = query.filter_by(feed_item_id=feed_item_id)
        if status:
            query = query.filter_by(status=status)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedOrder.__table__.columns:
            col = getattr(FeedOrder, order_by)
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
    @api.expect(feed_order_model)
    def post(self):
        data = api.payload
        order = FeedOrder(**data)
        db.session.add(order)
        db.session.commit()
        return {'message': 'Заказ добавлен', 'id': order.id}, 201

@api.route('/<int:id>')
class FeedOrderResource(Resource):
    @active_user_required
    def get(self, id):
        order = FeedOrder.query.get_or_404(id)
        return order.to_dict()

    @active_user_required
    @api.expect(feed_order_model)
    def put(self, id):
        order = FeedOrder.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(order, key, value)
        db.session.commit()
        return {'message': 'Заказ обновлен'}

    @active_user_required
    def delete(self, id):
        order = FeedOrder.query.get_or_404(id)
        db.session.delete(order)
        db.session.commit()
        return {'message': 'Заказ удален'} 