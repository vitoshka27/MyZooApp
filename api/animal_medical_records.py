from flask_restx import Namespace, Resource, fields
from .models import db, AnimalMedicalRecord
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('animal_medical_records', description='Операции с мед. записями животных')

animal_medical_record_model = api.model('AnimalMedicalRecord', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'record_date': fields.String(required=True),
    'weight': fields.Float,
    'height': fields.Float,
    'notes': fields.String,
})

@api.route('/')
class AnimalMedicalRecordList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalMedicalRecord.query
        animal_id = request.args.get('animal_id', type=int)
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalMedicalRecord.__table__.columns:
            col = getattr(AnimalMedicalRecord, order_by)
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
    @role_required([1, 5])
    def post(self):
        data = api.payload
        record = AnimalMedicalRecord(**data)
        db.session.add(record)
        db.session.commit()
        return {'message': 'Медицинская запись добавлена', 'id': record.id}, 201

@api.route('/<int:id>')
class AnimalMedicalRecordResource(Resource):
    @active_user_required
    def get(self, id):
        record = AnimalMedicalRecord.query.get_or_404(id)
        return record.to_dict()

    @active_user_required
    @role_required([1, 5])
    @api.expect(animal_medical_record_model)
    def put(self, id):
        record = AnimalMedicalRecord.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(record, key, value)
        db.session.commit()
        return {'message': 'Медицинская запись обновлена'}

    @active_user_required
    @role_required([1, 5])
    def delete(self, id):
        record = AnimalMedicalRecord.query.get_or_404(id)
        db.session.delete(record)
        db.session.commit()
        return {'message': 'Медицинская запись удалена'} 