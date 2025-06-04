from flask_restx import Namespace, Resource, fields
from .models import db, AnimalDietRequirement
from .decorators import role_required, active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('animal_diet_requirements', description='Операции с пищевыми требованиями животных')

animal_diet_requirement_model = api.model('AnimalDietRequirement', {
    'id': fields.Integer(readonly=True),
    'species_id': fields.Integer(required=True),
    'age_group': fields.String(required=True),
    'body_condition': fields.String(required=True),
    'season': fields.String(required=True),
    'feed_type_id': fields.Integer(required=True),
    'required_quantity': fields.Float(required=True),
    'feeding_times_per_day': fields.Integer(required=True),
})

@api.route('/')
class AnimalDietRequirementList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = AnimalDietRequirement.query
        species_id = request.args.get('species_id', type=int)
        feed_type_id = request.args.get('feed_type_id', type=int)
        season = request.args.get('season')
        if species_id:
            query = query.filter_by(species_id=species_id)
        if feed_type_id:
            query = query.filter_by(feed_type_id=feed_type_id)
        if season:
            query = query.filter_by(season=season)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in AnimalDietRequirement.__table__.columns:
            col = getattr(AnimalDietRequirement, order_by)
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
    @api.expect(animal_diet_requirement_model)
    def post(self):
        data = api.payload
        req = AnimalDietRequirement(**data)
        db.session.add(req)
        db.session.commit()
        return {'message': 'Пищевое требование добавлено', 'id': req.id}, 201

@api.route('/<int:id>')
class AnimalDietRequirementResource(Resource):
    @active_user_required
    def get(self, id):
        req = AnimalDietRequirement.query.get_or_404(id)
        return req.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(animal_diet_requirement_model)
    def put(self, id):
        req = AnimalDietRequirement.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(req, key, value)
        db.session.commit()
        return {'message': 'Пищевое требование обновлено'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        req = AnimalDietRequirement.query.get_or_404(id)
        db.session.delete(req)
        db.session.commit()
        return {'message': 'Пищевое требование удалено'} 