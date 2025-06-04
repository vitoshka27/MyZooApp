from flask_restx import Namespace, Resource, fields
from .models import db, FeedSupplier
from flask import request

from .decorators import role_required, active_user_required
from sqlalchemy import desc

api = Namespace('feed_suppliers', description='Операции с поставщиками корма')

feed_supplier_model = api.model('FeedSupplier', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
    'phone': fields.String(required=True),
    'address': fields.String,
})

@api.route('/')
class FeedSupplierList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedSupplier.query
        name = request.args.get('name')
        if name:
            query = query.filter(FeedSupplier.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedSupplier.__table__.columns:
            col = getattr(FeedSupplier, order_by)
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
    @api.expect(feed_supplier_model)
    def post(self):
        data = api.payload
        supplier = FeedSupplier(**data)
        db.session.add(supplier)
        db.session.commit()
        return {'message': 'Поставщик добавлен', 'id': supplier.id}, 201

@api.route('/<int:id>')
class FeedSupplierResource(Resource):
    @active_user_required
    def get(self, id):
        supplier = FeedSupplier.query.get_or_404(id)
        return supplier.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(feed_supplier_model)
    def put(self, id):
        supplier = FeedSupplier.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(supplier, key, value)
        db.session.commit()
        return {'message': 'Поставщик обновлен'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        supplier = FeedSupplier.query.get_or_404(id)
        db.session.delete(supplier)
        db.session.commit()
        return {'message': 'Поставщик удален'} 