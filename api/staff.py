from flask_restx import Namespace, Resource, fields
from .models import db, Staff
from .decorators import superuser_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('staff', description='Операции с сотрудниками')

staff_model = api.model('Staff', {
    'id': fields.Integer(readonly=True),
    'last_name': fields.String(required=True),
    'first_name': fields.String(required=True),
    'middle_name': fields.String,
    'gender': fields.String(required=True),
    'birth_date': fields.String(required=True),
    'hire_date': fields.String(required=True),
    'salary': fields.Float(required=True),
    'category_id': fields.Integer(required=True),
    'username': fields.String(required=True),
    'is_active': fields.Boolean,
})

@api.route('/')
class StaffList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Staff.query
        category_id = request.args.get('category_id', type=int)
        gender = request.args.get('gender')
        is_active = request.args.get('is_active', type=int)
        if category_id:
            query = query.filter_by(category_id=category_id)
        if gender:
            query = query.filter_by(gender=gender)
        if is_active is not None:
            query = query.filter_by(is_active=bool(is_active))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Staff.__table__.columns:
            col = getattr(Staff, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        items = query.all()
        return {
            'total': len(items),
            'page': page,
            'data': [s.to_dict() for s in items]
        }

    @active_user_required
    @superuser_required
    @api.expect(staff_model)
    def post(self):
        data = api.payload
        staff = Staff(**data)
        db.session.add(staff)
        db.session.commit()
        return {'message': 'Сотрудник добавлен', 'id': staff.id}, 201

@api.route('/<int:id>')
class StaffResource(Resource):
    @active_user_required
    def get(self, id):
        staff = Staff.query.get_or_404(id)
        return staff.to_dict()

    @active_user_required
    @superuser_required
    @api.expect(staff_model)
    def put(self, id):
        staff = Staff.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(staff, key, value)
        db.session.commit()
        return {'message': 'Сотрудник обновлен'}

    @active_user_required
    @superuser_required
    def delete(self, id):
        staff = Staff.query.get_or_404(id)
        db.session.delete(staff)
        db.session.commit()
        return {'message': 'Сотрудник удален'} 