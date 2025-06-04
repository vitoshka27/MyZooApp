from flask_restx import Namespace, Resource, fields
from .models import db, AnimalMovementHistory
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('animal_movement_history', description='Операции с историей перемещений животных')

animal_movement_history_model = api.model('AnimalMovementHistory', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'from_enclosure': fields.Integer(required=True),
    'to_enclosure': fields.Integer(required=True),
    'move_date': fields.String(required=True),
})

@api.route('/')
class AnimalMovementHistoryList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalMovementHistory.query
        animal_id = request.args.get('animal_id', type=int)
        from_enclosure = request.args.get('from_enclosure', type=int)
        to_enclosure = request.args.get('to_enclosure', type=int)
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if from_enclosure:
            query = query.filter_by(from_enclosure=from_enclosure)
        if to_enclosure:
            query = query.filter_by(to_enclosure=to_enclosure)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalMovementHistory.__table__.columns:
            col = getattr(AnimalMovementHistory, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        items = [r.to_dict() for r in query.all()]
        return {
            'total': len(items),
            'data': items
        }

    @active_user_required
    @role_required([5])
    @api.expect(animal_movement_history_model)
    def post(self):
        data = api.payload
        record = AnimalMovementHistory(**data)
        db.session.add(record)
        db.session.commit()
        return {'message': 'Перемещение добавлено', 'id': record.id}, 201

@api.route('/<int:id>')
class AnimalMovementHistoryResource(Resource):
    @active_user_required
    def get(self, id):
        record = AnimalMovementHistory.query.get_or_404(id)
        return record.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(animal_movement_history_model)
    def put(self, id):
        record = AnimalMovementHistory.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(record, key, value)
        db.session.commit()
        return {'message': 'Перемещение обновлено'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        record = AnimalMovementHistory.query.get_or_404(id)
        db.session.delete(record)
        db.session.commit()
        return {'message': 'Перемещение удалено'} 