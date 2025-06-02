from flask_restx import Namespace, Resource, fields
from .models import db, SupplierFeedType
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('supplier_feed_types', description='Операции с кормами поставщиков')

supplier_feed_type_model = api.model('SupplierFeedType', {
    'supplier_id': fields.Integer(required=True),
    'feed_type_id': fields.Integer(required=True),
})

@api.route('/')
class SupplierFeedTypeList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = SupplierFeedType.query
        supplier_id = request.args.get('supplier_id', type=int)
        feed_type_id = request.args.get('feed_type_id', type=int)
        if supplier_id:
            query = query.filter_by(supplier_id=supplier_id)
        if feed_type_id:
            query = query.filter_by(feed_type_id=feed_type_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in SupplierFeedType.__table__.columns:
            col = getattr(SupplierFeedType, order_by)
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
    @api.expect(supplier_feed_type_model)
    def post(self):
        data = api.payload
        record = SupplierFeedType(**data)
        db.session.add(record)
        db.session.commit()
        return {'message': 'Связь добавлена'}, 201

@api.route('/<int:supplier_id>/<int:feed_type_id>')
class SupplierFeedTypeResource(Resource):
    @active_user_required
    def get(self, supplier_id, feed_type_id):
        record = SupplierFeedType.query.get_or_404((supplier_id, feed_type_id))
        return record.to_dict()

    @active_user_required
    @api.expect(supplier_feed_type_model)
    def put(self, supplier_id, feed_type_id):
        record = SupplierFeedType.query.get_or_404((supplier_id, feed_type_id))
        data = api.payload
        for key, value in data.items():
            setattr(record, key, value)
        db.session.commit()
        return {'message': 'Связь обновлена'}

    @active_user_required
    def delete(self, supplier_id, feed_type_id):
        record = SupplierFeedType.query.get_or_404((supplier_id, feed_type_id))
        db.session.delete(record)
        db.session.commit()
        return {'message': 'Связь удалена'} 