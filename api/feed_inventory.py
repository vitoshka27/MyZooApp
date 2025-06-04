from flask_restx import Namespace, Resource, fields
from .models import db, FeedInventory
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('feed_inventory', description='Операции со складом корма')

feed_inventory_model = api.model('FeedInventory', {
    'id': fields.Integer(readonly=True),
    'feed_item_id': fields.Integer(required=True),
    'quantity': fields.Float(required=True),
    'received_date': fields.String(required=True),
})

@api.route('/')
class FeedInventoryList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedInventory.query
        feed_item_id = request.args.get('feed_item_id', type=int)
        if feed_item_id:
            query = query.filter_by(feed_item_id=feed_item_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedInventory.__table__.columns:
            col = getattr(FeedInventory, order_by)
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
    @role_required([5])
    @api.expect(feed_inventory_model)
    def post(self):
        data = api.payload
        inventory = FeedInventory(**data)
        db.session.add(inventory)
        db.session.commit()
        return {'message': 'Запись на складе добавлена', 'id': inventory.id}, 201

@api.route('/<int:id>')
class FeedInventoryResource(Resource):
    @active_user_required
    def get(self, id):
        inventory = FeedInventory.query.get_or_404(id)
        return inventory.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(feed_inventory_model)
    def put(self, id):
        inventory = FeedInventory.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(inventory, key, value)
        db.session.commit()
        return {'message': 'Запись на складе обновлена'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        inventory = FeedInventory.query.get_or_404(id)
        db.session.delete(inventory)
        db.session.commit()
        return {'message': 'Запись на складе удалена'} 