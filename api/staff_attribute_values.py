from flask_restx import Namespace, Resource, fields
from .models import db, StaffAttributeValue
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('staff_attribute_values', description='Операции со значениями атрибутов сотрудников')

staff_attribute_value_model = api.model('StaffAttributeValue', {
    'staff_id': fields.Integer(required=True),
    'attribute_id': fields.Integer(required=True),
    'attribute_value': fields.String(required=True),
})

@api.route('/')
class StaffAttributeValueList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = StaffAttributeValue.query
        staff_id = request.args.get('staff_id', type=int)
        attribute_id = request.args.get('attribute_id', type=int)
        attribute_value = request.args.get('attribute_value')
        if staff_id:
            query = query.filter_by(staff_id=staff_id)
        if attribute_id:
            query = query.filter_by(attribute_id=attribute_id)
        if attribute_value:
            query = query.filter(StaffAttributeValue.attribute_value.ilike(f'%{attribute_value}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in StaffAttributeValue.__table__.columns:
            col = getattr(StaffAttributeValue, order_by)
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
    @api.expect(staff_attribute_value_model)
    def post(self):
        data = api.payload
        value = StaffAttributeValue(**data)
        db.session.add(value)
        db.session.commit()
        return {'message': 'Значение добавлено', 'staff_id': value.staff_id, 'attribute_id': value.attribute_id}, 201

@api.route('/<int:staff_id>/<int:attribute_id>')
class StaffAttributeValueResource(Resource):
    @active_user_required
    def get(self, staff_id, attribute_id):
        value = StaffAttributeValue.query.get_or_404((staff_id, attribute_id))
        return value.to_dict()

    @active_user_required
    @api.expect(staff_attribute_value_model)
    def put(self, staff_id, attribute_id):
        value = StaffAttributeValue.query.get_or_404((staff_id, attribute_id))
        for key, value_ in api.payload.items():
            setattr(value, key, value_)
        db.session.commit()
        return {'message': 'Значение обновлено'}

    @active_user_required
    def delete(self, staff_id, attribute_id):
        value = StaffAttributeValue.query.get_or_404((staff_id, attribute_id))
        db.session.delete(value)
        db.session.commit()
        return {'message': 'Значение удалено'} 