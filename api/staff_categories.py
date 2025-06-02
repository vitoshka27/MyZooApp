from flask_restx import Namespace, Resource, fields
from .models import db, StaffCategory
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('staff_categories', description='Операции с категориями сотрудников')

staff_category_model = api.model('StaffCategory', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
    'enclosure_access': fields.String(required=True),
})

@api.route('/')
class StaffCategoryList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = StaffCategory.query
        name = request.args.get('name')
        enclosure_access = request.args.get('enclosure_access')
        if name:
            query = query.filter(StaffCategory.name.ilike(f'%{name}%'))
        if enclosure_access:
            query = query.filter_by(enclosure_access=enclosure_access)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in StaffCategory.__table__.columns:
            col = getattr(StaffCategory, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [c.to_dict() for c in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @api.expect(staff_category_model)
    def post(self):
        data = api.payload
        category = StaffCategory(**data)
        db.session.add(category)
        db.session.commit()
        return {'message': 'Категория добавлена', 'id': category.id}, 201

@api.route('/<int:id>')
class StaffCategoryResource(Resource):
    @active_user_required
    def get(self, id):
        category = StaffCategory.query.get_or_404(id)
        return category.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(staff_category_model)
    def put(self, id):
        category = StaffCategory.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(category, key, value)
        db.session.commit()
        return {'message': 'Категория обновлена'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        category = StaffCategory.query.get_or_404(id)
        db.session.delete(category)
        db.session.commit()
        return {'message': 'Категория удалена'} 