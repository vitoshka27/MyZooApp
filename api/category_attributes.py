from flask_restx import Namespace, Resource, fields
from .models import db, CategoryAttribute
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('category_attributes', description='Операции с атрибутами категорий сотрудников')

category_attribute_model = api.model('CategoryAttribute', {
    'id': fields.Integer(readonly=True),
    'category_id': fields.Integer(required=True),
    'attribute_name': fields.String(required=True),
})

@api.route('/')
class CategoryAttributeList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = CategoryAttribute.query
        category_id = request.args.get('category_id', type=int)
        attribute_name = request.args.get('attribute_name')
        if category_id:
            query = query.filter_by(category_id=category_id)
        if attribute_name:
            query = query.filter(CategoryAttribute.attribute_name.ilike(f'%{attribute_name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in CategoryAttribute.__table__.columns:
            col = getattr(CategoryAttribute, order_by)
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
    @api.expect(category_attribute_model)
    def post(self):
        data = api.payload
        attr = CategoryAttribute(**data)
        db.session.add(attr)
        db.session.commit()
        return {'message': 'Атрибут добавлен', 'id': attr.id}, 201

@api.route('/<int:id>')
class CategoryAttributeResource(Resource):
    @active_user_required
    def get(self, id):
        attr = CategoryAttribute.query.get_or_404(id)
        return attr.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(category_attribute_model)
    def put(self, id):
        attr = CategoryAttribute.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(attr, key, value)
        db.session.commit()
        return {'message': 'Атрибут обновлен'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        attr = CategoryAttribute.query.get_or_404(id)
        db.session.delete(attr)
        db.session.commit()
        return {'message': 'Атрибут удален'} 