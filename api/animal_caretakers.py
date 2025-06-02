from flask_restx import Namespace, Resource, fields
from .models import db, AnimalCaretaker
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('animal_caretakers', description='Операции с уходом за животными')

animal_caretaker_model = api.model('AnimalCaretaker', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'staff_id': fields.Integer(required=True),
    'start_date': fields.String(required=True),
    'end_date': fields.String,
})

@api.route('/')
class AnimalCaretakerList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalCaretaker.query
        animal_id = request.args.get('animal_id', type=int)
        staff_id = request.args.get('staff_id', type=int)
        start_date = request.args.get('start_date')
        end_date = request.args.get('end_date')
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if staff_id:
            query = query.filter_by(staff_id=staff_id)
        if start_date:
            query = query.filter_by(start_date=start_date)
        if end_date:
            query = query.filter_by(end_date=end_date)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalCaretaker.__table__.columns:
            col = getattr(AnimalCaretaker, order_by)
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
    @api.expect(animal_caretaker_model)
    def post(self):
        data = api.payload
        caretaker = AnimalCaretaker(**data)
        db.session.add(caretaker)
        db.session.commit()
        return {'message': 'Уход добавлен', 'id': caretaker.id}, 201

@api.route('/<int:id>')
class AnimalCaretakerResource(Resource):
    @active_user_required
    def get(self, id):
        caretaker = AnimalCaretaker.query.get_or_404(id)
        return caretaker.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(animal_caretaker_model)
    def put(self, id):
        caretaker = AnimalCaretaker.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(caretaker, key, value)
        db.session.commit()
        return {'message': 'Уход обновлен'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        caretaker = AnimalCaretaker.query.get_or_404(id)
        db.session.delete(caretaker)
        db.session.commit()
        return {'message': 'Уход удален'} 